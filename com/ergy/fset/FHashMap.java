/*
 * FHashMap.java
 *
 * Copyright (c) 2013, 2014 Scott L. Burson.
 *
 * This file is licensed under the Library GNU Public License (LGPL), v. 2.1.
 */


package com.ergy.fset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * A functional map that uses hash codes to order objects.  The name notwithstanding, it
 * is implemented as a tree, not a hash table, but its ordering function is implemented
 * by calling <code>hashCode</code>.  Thus the key objects need not implement {@link
 * Comparable}; they need not even have any superclass in common other than
 * <code>Object</code>.  In the case where distinct keys have the same hash code, the
 * implementation is correct, but less efficient; so it is (as with ordinary hash
 * tables) important to make the hash codes distinct whenever possible.  The iterator
 * returns entries in increasing order of the hash codes of the keys; in the case of
 * keys with equal hash codes, the order is not specified, but it will be
 * deterministic (it depends only on the sequence of operations used to construct the
 * map).
 *
 * <p>Time costs: <code>isEmpty</code>, <code>size</code>, <code>arb</code>, and
 * <code>entrySet</code> take O(1) (constant) time.  <code>containsKey</code>,
 * <code>with</code>, and <code>less</code> take O(log <i>n</i>) time.
 * <code>domain</code>, <code>keySet</code>, and <code>containsValue</code> take O(n)
 * (linear) time.  <code>toSet</code>, <code>range</code>, and <code>values</code>
 * take O(<i>n</i> log <i>n</i>) time.  <code>union</code> takes O(n) (linear) time
 * if the other map involved is also a <code>FHashMap</code>; otherwise, it takes
 * O(<i>n</i> log <i>n</i>) time.  <code>compareTo</code> (called, for instance, if
 * this map is an element in a containing <code>FTreeSet</code>) takes O(n)
 * (linear) time.
 *
 * <p>Space costs: <code>FHashMap</code> uses a heterogeneous binary tree
 * structure with bounded-length arrays at the leaves.  It uses much less space than
 * traditional homogeneous binary trees; typical space consumption is roughly twice
 * that of a pair of plain arrays.
 *
 * Operations on a <code>FHashMap</code> repeatedly call <code>hashCode</code> on
 * the keys in the map.  It therefore is best for performance if this takes O(1)
 * time.  For classes whose hash code may depend on the hash codes of many other
 * objects, I recommend they cache their hash code after computing it.  If such
 * caching is not practical for some reason, <code>FCachedHashMap</code> may be a
 * better choice.
 *
 * <p><code>FHashMap</code> accepts the null key and the null value.
 *
 * <p><code>FHashMap</code> also provides, corresponding to each constructor, a
 * static factory method <code>withDefault</code> which, in addition to the
 * functionality of the constructor, also allows the specification of a default
 * value to be returned by the <code>get</code> method when it is called with a key
 * which is not in the map.  (Otherwise, <code>get</code> returns <code>null</code>
 * in that case.)  This is particularly handy for map <i>chaining</i>, in which the
 * range values in one map are themselves maps.  For example, if the outer map is
 * created like this:
 *
 * <pre>
 *     FMap<K1, FMap<K2, V>> map =
 *       FHashMap.<K1, FMap<K2, V>>withDefault(new FHashMap<K2, V>());
 * </pre>
 *
 * the chained mapping <code>key1 -> key2 -> val</code> can then be added like this:
 *
 * <pre>
 *     map = map.with(key1, map.get(key1).with(key2, val));
 * </pre>
 *
 * which works even if <code>map</code> does not already contain an entry for
 * <code>key1</code>.  (Of course, the outer and inner maps do not have to be of the
 * same class.)
 *
 * <p>If <code>FHashMap</code> instances are used as elements of a
 * <code>FTreeSet</code>, or as <i>keys</i> (not values) of a containing
 * <code>FTreeMap</code>, the <code>FHashMap.compareTo</code> method will be
 * called to order the maps relative to one another.  Under some circumstances, this
 * method compares not just keys, but also values.  It uses the same comparison
 * method for the values that it does for the keys: either the supplied
 * <code>Comparator</code>, or if none was given, the natural ordering.  Thus, under
 * these circumstances, the user must be sure that the value objects are acceptable
 * to the <code>Comparator</code>, or that they implement <code>Comparable</code>.
 *
 * <p><code>FHashMap</code> implements {@link Serializable}; an instance
 * of it is serializable provided that all keys and values it contains, the
 * <code>Comparator</code> it uses if any, and the default value if nonnull, are
 * serializable.
 *
 * @author Scott L. Burson
 * @see FMap
 * @see FCachedHashMap
 */

public class FHashMap<Key, Val>
    extends AbstractFMap<Key, Val>
    implements Comparable<FHashMap<Key, Val>>, Serializable
{

    /**
     * Returns an empty FHashMap.  Slightly more efficient than calling the constructor,
     * because it returns a canonical instance.
     */
    public static <Key, Val> FHashMap<Key, Val> emptyMap() {
	return (FHashMap<Key, Val>)EMPTY_INSTANCE;
    }

    /**
     * Constructs an empty <code>FHashMap</code>.
     */
    public FHashMap() {
	tree = null;
	dflt = null;
    }

    /**
     * Constructs a <code>FHashMap</code> containing the same entries as
     * <code>map</code>.
     *
     * @param map the map to use the entries of
     */
    public FHashMap(Map<? extends Key, ? extends Val> map) {
	tree = fromMap(map);
	dflt = null;
    }

    private static <Key, Val> Object fromMap(Map<? extends Key, ? extends Val> map) {
	if (map instanceof FHashMap)
	    return ((FHashMap)map).tree;
	else {
	    Object t = null;
	    for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
		Map.Entry ent = (Map.Entry)it.next();
		Object k = ent.getKey();
		t = with(t, k, hashCode(k), ent.getValue());
	    }
	    return t;
	}
    }

    /**
     * Constructs a <code>FTreeMap</code> mapping each element of <code>keys</code>
     * to the corresponding element of <code>vals</code>.  If a key is duplicated, the
     * value it will be mapped to in the result will be the one corresponding to its
     * last occurrence.
     *
     * @throws IllegalArgumentException if keys.length != vals.length
     */
    public FHashMap(Key[] keys, Val[] vals) {
	if (keys.length != vals.length) throw new IllegalArgumentException();
	Object t = null;
	dflt = null;
	if (keys.length != vals.length)
	    throw new IllegalArgumentException("array lengths must be equal");
	for (int i = 0; i < keys.length; ++i)
	    t = with(t, keys[i], hashCode(keys[i]), vals[i]);
	tree = t;
    }

    /**
     * Constructs and returns an empty <code>FHashMap</code> with default
     * <code>dflt</code>.  The resulting map's <code>get</code> method returns
     * <code>dflt</code> when called with a key which is not in the map.
     *
     * @param dflt the default value
     * @return the new <code>FHashMap</code>
     */
    public static <Key, Val> FHashMap<Key, Val> withDefault(Val dflt) {
	return new FHashMap<Key, Val>(null, dflt);
    }

    /**
     * Constructs and returns a <code>FHashMap</code> with default
     * <code>dflt</code>, containing the same entries as <code>map</code>.  The
     * resulting map's <code>get</code> method returns <code>dflt</code> when called
     * with a key which is not in the map.
     *
     * @param map the map to use the entries of
     * @param dflt the default value
     * @return the new <code>FHashMap</code>
     */
    public static <Key, Val> FHashMap<Key, Val> withDefault(Map<? extends Key, ? extends Val> map,
							    Val dflt) {
	return new FHashMap<Key, Val>(fromMap(map), dflt);
    }

    public boolean isEmpty() {
	return tree == null;
    }

    public int size() {
	return treeSize(tree);
    }

    public Map.Entry<Key, Val> arb() {
	return (Map.Entry<Key, Val>)arb(tree);
    }

    /**
     * Returns true iff this map contains an entry with key <code>entry.getKey()</code>,
     * with corresponding value <code>entry.getValue()</code>.  (Returns false when there
     * is no entry for the key, even if the value is equal to the map's default.)
     */
    public boolean contains(Map.Entry<Key, Val> entry) {
	return contains(tree, entry);
    }

    public boolean containsKey(Object key) {
	return get(tree, key, hashCode(key)) != NO_ELEMENT;
    }

    /**
     * Returns the value to which this map maps <code>key</code>.  If this map
     * contains no entry for <code>key</code>, returns this map's default value,
     * which is normally <code>null</code>, but may be a different value if the map
     * was given a default using <code>withDefault</code>.
     * */
    public Val get(Object key) {
	Object val = get(tree, key, hashCode(key));
	if (val == NO_ELEMENT) return dflt;
	else return (Val)val;
    }

    public FHashMap<Key, Val> with(Key key, Val value) {
	Object t = with(tree, key, hashCode(key), value);
	if (t == tree) return this;
	else return new FHashMap<Key, Val>(t, dflt);
    }

    public FHashMap<Key, Val> less(Key key) {
	Object t = less(tree, key, hashCode(key));
	if (t == tree) return this;
	else if (t == null && dflt == null) return emptyMap();
	else return new FHashMap<Key, Val>(t, dflt);
    }

    public Set<Key> keySet() {
	return new AbstractSet<Key>() {
	    public Iterator<Key> iterator() {
		return new FHMKeyIterator<Key>(tree);
	    }
	    public int size() {
		return FHashMap.this.size();
	    }
	    public boolean contains(Object key) {
		return containsKey(key);
	    }
	};
    }

    public Collection<Val> values() {
	return new AbstractCollection<Val>() {
	    public Iterator<Val> iterator() {
		return new FHMValueIterator<Val>(tree);
	    }
	    public int size() {
		return FHashMap.this.size();
	    }
	};
    }

    // Note, you never have to call this; you can iterate over the map directly.
    // But in case you're in the habit of doing so, it's provided.
    public Set<Map.Entry<Key, Val>> entrySet() {
	return new AbstractSet<Map.Entry<Key, Val>>() {
	    public Iterator<Map.Entry<Key, Val>> iterator() {
		return FHashMap.this.iterator();
	    }
	    public int size() {
		return FHashMap.this.size();
	    }
	    public boolean contains(Object x) {
		if (!(x instanceof Map.Entry)) return false;
		else {
		    Map.Entry<Key, Val> ent = (Map.Entry)x;
		    return FHashMap.this.contains(ent);
		}
	    }
	    public boolean remove(Object x) {
		throw new UnsupportedOperationException();
	    }
	    public void clear() {
		throw new UnsupportedOperationException();
	    }
	};
    }

    public FHashSet<Key> domain() {
	Object dom = domain(tree);
	return FHashSet.<Key>make(dom);
    }

    /**
     * Returns the range of the map (the set of values it contains).  The returned set
     * is a {@link FHashSet}.
     *
     * @return the range set of this map
     */
    public FSet<Val> range() {
	return (FSet<Val>)range(tree, new FHashSet<Val>());
    }

    public FSet<Val> range(FSet<Val> initial_set) {
	// Gives us an empty set of the right class and comparator.
	initial_set = initial_set.difference(initial_set);
	return (FSet<Val>)range(tree, initial_set);
    }

    public FHashSet<Map.Entry<Key, Val>> toSet() {
	return (FHashSet<Map.Entry<Key, Val>>)toSet(new FHashSet<Map.Entry<Key, Val>>());
    }

    public FSet<Map.Entry<Key, Val>> toSet(FSet<Map.Entry<Key, Val>> initial_set) {
	// Gives us an empty set of the right class and comparator.
	FSet<Map.Entry<Key, Val>> s = initial_set.difference(initial_set);
	for (Map.Entry<Key, Val> ent : this)
	    s = s.with(ent);
	return s;
    }

    public FHashMap<Key, Val> union(FMap<? extends Key, ? extends Val> with_map) {
	FHashMap<Key, Val> with_fhm = new FHashMap<Key, Val>(with_map);
	Object t = union(tree, with_fhm.tree);
	return new FHashMap<Key, Val>(t, dflt);
    }

    public FHashMap<Key, Val> restrictedTo(FSet<Key> set) {
	FHashSet<Key> fhs = new FHashSet<Key>(set);
	Object t = restrictedTo(tree, fhs.tree);
	return new FHashMap(t, dflt);
    }

    public FHashMap<Key, Val> restrictedFrom(FSet<Key> set) {
	FHashSet<Key> fhs = new FHashSet<Key>(set);
	Object t = restrictedFrom(tree, fhs.tree);
	return new FHashMap(t, dflt);
    }

    public Val getDefault() {
	return dflt;
    }

    public Iterator<Map.Entry<Key, Val>> iterator() {
	return new FHMIterator<Key, Val>(tree);
    }

    // &&& Better to implement 'Comparable<Map<Key, Val>>' ?
    public int compareTo(FHashMap<Key, Val> other) {
	return compareTo(tree, other.tree);
    }

    public boolean equals(Object obj) {
	if (obj == this) return true;
	else if (obj instanceof FHashMap) {
	    FHashMap fhm = (FHashMap)obj;
	    return equals(tree, fhm.tree);
	} else if (obj instanceof FLinkedHashMap) {
	    FLinkedHashMap plhm = (FLinkedHashMap)obj;
	    return equals(tree, plhm.map_tree);
	} else if (!(obj instanceof Map)) return false;
	else {
	    Map<Object, Object> map = (Map<Object, Object>)obj;
	    if (size() != map.size()) return false;
	    for (Map.Entry ent : map.entrySet())
		if (!contains(ent)) return false;
	    return true;
	}
    }

    public int hashCode() {
	if (hash_code == Integer.MIN_VALUE) hash_code = myHashCode(tree);
	return hash_code;
    }

    // For debugging.
    /*pkg*/ String dump() {
	return dump(tree);
    }

    /*pkg*/ boolean verify() {
	return verify(tree, NEGATIVE_INFINITY, POSITIVE_INFINITY);
    }

    /******************************************************************************/
    /* Internals */

    // Inspired by Stephen Adams' paper on weight-balanced binary trees.  As an additional
    // development, these trees are heterogeneous: instead of consisting entirely of nodes,
    // the lowest two to three levels of the tree are stored in bounded-length vectors.
    // This cuts space requirements roughly in half without costing much (if any) time.

    // The empty map can be a singleton.
    private static final FHashMap<?, ?> EMPTY_INSTANCE = new FHashMap<Object, Object>();

    /* Instance variables */

    /*pkg*/ transient final Object tree;	// a subtree (see below)

    private final Val dflt;

    // We use Integer.MIN_VALUE to indicate that the hash code has not been computed yet.
    private transient int hash_code = Integer.MIN_VALUE;

    /* The threshold length above which tree nodes will be built.  This includes
     * both keys and values (i.e. 16 = 8 pairs). */
    private static final int MAX_LEAF_ARRAY_LENGTH = 16;

    /* The factor by which one subtree may outweigh another.  See Adams.  Don't
     * change this unless you understand his analysis. */
    private static final int BALANCE_FACTOR = 4;

    /* To represent negative and positive infinity, we use the smallest and largest
     * available integers.  We arrange to make sure `hashCode(Object)' never returns
     * them.  (For the benefit of `restricted{to,from}', these must be the same values
     * `FHashSet' uses.)
     */
    private static final int NEGATIVE_INFINITY = Integer.MIN_VALUE;
    private static final int POSITIVE_INFINITY = Integer.MAX_VALUE;

    /*pkg*/ static int hashCode(Object x) {
	if (x instanceof EquivalentMap)
	    x = ((Entry)((EquivalentMap)x).contents.get(0)).key;
	if (x == null) return 0;
	int h = x.hashCode();
	if (h == NEGATIVE_INFINITY) return NEGATIVE_INFINITY + 1;
	else if (h == POSITIVE_INFINITY) return POSITIVE_INFINITY - 1;
	else return h;
    }

    /*pkg*/ static class Entry implements Map.Entry<Object, Object> {
	Entry(Object _key, Object _value) {
	    key = _key;
	    value = _value;
	}
	final Object key;
	final Object value;
	public Object getKey() { return key; }
	public Object getValue() { return value; }
	public Object setValue(Object newval) {
	    throw new UnsupportedOperationException();
	}
	public int hashCode() {
	    return ((key == null ? 0 : key.hashCode()) ^
		    (value == null ? 0 : value.hashCode()));
	}
	public boolean equals(Object obj) {
	    if (obj == null) return false;
	    else if (!(obj instanceof Map.Entry)) return false;
	    else {
		Map.Entry<Object, Object> ent = (Map.Entry<Object, Object>)obj;
		return eql(key, ent.getKey()) && eql(value, ent.getValue());
	    }
	}
    }

    /* A subtree can be either null, a `Node', or a leaf.  A leaf is an `Object[]'
     * containing first the keys, then the values (i.e. its length is twice the
     * number of pairs).  The `Entry' key type is `Object' because it might hold an
     * `EquivalentMap'. */
    /*pkg*/ static final class Node extends Entry {
	Node(int _size, Object _key, Object _value, Object _left, Object _right) {
	    super(_key, _value);
	    size = _size;
	    left = _left;
	    right = _right;
	}
	/*pkg*/ final int size;		// the number of pairs in the subtree
	/*pkg*/ final Object left;	// a subtree
	/*pkg*/ final Object right;	// a subtree
    }

    private static Node makeNode(Object key, Object value, Object left, Object right) {
	if (key instanceof Entry) {	// convenience for some callers
	    Entry ent = (Entry)key;
	    value = ent.value;
	    key = ent.key;
	}
	return new Node(treeSize(left) + treeSize(right) + keySize(key),
			key, value, left, right);
    }

    /*pkg*/ static int treeSize(Object subtree) {
	if (subtree == null) return 0;
	else if (!(subtree instanceof Node)) return ((Object[])subtree).length >> 1;
	else return ((Node)subtree).size;
    }

    private static int keySize(Object key) {
	if (key instanceof EquivalentMap)
	    return ((EquivalentMap)key).contents.size();
	else return 1;
    }

    private FHashMap(Object _tree, Val _dflt) {
	tree = _tree;
	dflt = _dflt;
    }

    /*pkg*/ static Object arb(Object tree) {
	if (tree == null) throw new NoSuchElementException();
	else if (!(tree instanceof Node)) {
	    Object[] ary = (Object[])tree;
	    int len = ary.length, nkeys = len >> 1, idx = nkeys >> 1;
	    return new Entry(ary[idx], ary[idx + nkeys]);
	} else {
	    Node node = (Node) tree;
	    if (node.key instanceof EquivalentMap)
		return ((EquivalentMap)node.key).contents.get(0);
	    else return node;
	}
    }

    /*pkg*/ static <Key, Val> boolean contains(Object tree, Map.Entry<Key, Val> entry) {
	Key key = entry.getKey();
	Object val = get(tree, key, hashCode(key));
	return val != NO_ELEMENT && eql(val, entry.getValue());
    }

    /*pkg*/ static final Object NO_ELEMENT = new Object();

    // Returns NO_ELEMENT if there is no entry for the key.
    /*pkg*/ static Object get(Object subtree, Object key, int khash) {
	if (subtree == null) return NO_ELEMENT;
	else if (!(subtree instanceof Node)) {
	    Object[] ary = (Object[])subtree;
	    int bin_srch_res = binarySearch(ary, khash);
	    int idx = bin_srch_res >> BIN_SEARCH_INDEX_SHIFT;
	    if ((bin_srch_res & BIN_SEARCH_FOUND_MASK) == BIN_SEARCH_FOUND && eql(key, ary[idx]))
		return ary[idx + (ary.length >> 1)];
	    else return NO_ELEMENT;
	} else {
	    Node node = (Node)subtree;
	    Object nkey = node.key;
	    int nhash = hashCode(node.key);
	    if (khash == nhash) {
		if (nkey instanceof EquivalentMap) {
		    ArrayList<Entry> al = ((EquivalentMap)nkey).contents;
		    for (int i = 0, len = al.size(); i < len; ++i) {
			Entry ent = al.get(i);
			if (eql(key, ent.key)) return ent.value;
		    }
		    return NO_ELEMENT;
		} else if (eql(key, nkey)) return node.value;
		else return NO_ELEMENT;
	    } else if (khash < nhash) return get(node.left, key, khash);
	    else return get(node.right, key, khash);
	}
    }

    /* `key' may be an `EquivalentMap', or an `Entry'. */
    /*pkg*/ static Object with(Object subtree, Object key, int khash, Object value) {
	if (subtree == null) {
	    if (!(key instanceof EquivalentMap)) {
		Object[] a = new Object[2];
		a[0] = key;
		a[1] = value;
		return a;
	    } else return makeNode(key, value, null, null);
	} else if (!(subtree instanceof Node)) {
	    Object[] ary = (Object[])subtree;
	    int len = ary.length, nkeys = len >> 1;
	    int bin_srch_res = binarySearch(ary, khash);
	    int found = bin_srch_res & BIN_SEARCH_FOUND_MASK;
	    int idx = bin_srch_res >> BIN_SEARCH_INDEX_SHIFT;
	    if (found == BIN_SEARCH_FOUND && !(key instanceof EquivalentMap) && eql(key, ary[idx]))
		if (eql(value, ary[idx + nkeys])) return subtree;
		else return update2(ary, idx, value);
	    else if (found == BIN_SEARCH_NOT_FOUND  &&
		     len + 1 < MAX_LEAF_ARRAY_LENGTH  &&
		     !(key instanceof EquivalentMap))
		return insert2(ary, idx, key, value);
	    else return makeNode((found == BIN_SEARCH_FOUND
				  ? equivUnion(ary[idx], ary[idx + nkeys], key, value)
				  : key),
				 value, subseq2(ary, 0, idx),
				 subseq2(ary, (found == BIN_SEARCH_FOUND ? idx + 1 : idx),
					 nkeys));
	} else {
	    Node node = (Node)subtree;
	    Object nkey = node.key;
	    int nhash = hashCode(nkey);
	    if (khash == nhash) {
		if (!(key instanceof EquivalentMap) && !(nkey instanceof EquivalentMap) &&
		    eql(key, nkey) && eql(value, node.value))
		    return subtree;
		else return makeNode(equivUnion(nkey, node.value, key, value), value,
				     node.left, node.right);
	    } else if (khash < nhash) {
		Object new_left = with(node.left, key, khash, value);
		if (new_left == node.left) return subtree;
		else return buildNode(nkey, node.value, new_left, node.right);
	    } else {
		Object new_right = with(node.right, key, khash, value);
		if (new_right == node.right) return subtree;
		else return buildNode(nkey, node.value, node.left, new_right);
	    }
	}
    }

    /*pkg*/ static Object less(Object subtree, Object key, int khash) {
	if (subtree == null) return null;
	else if (!(subtree instanceof Node)) {
	    Object[] ary = (Object[])subtree;
	    int bin_srch_res = binarySearch(ary, khash);
	    int found = bin_srch_res & BIN_SEARCH_FOUND_MASK;
	    int idx = bin_srch_res >> BIN_SEARCH_INDEX_SHIFT;
	    if (found == BIN_SEARCH_FOUND) {
		if (eql(key, ary[idx])) return remove2(ary, idx);
		else return subtree;
	    } else return subtree;
	} else {
	    Node node = (Node)subtree;
	    Object nkey = node.key;
	    int nhash = hashCode(nkey);
	    if (khash == nhash) {
		if (!(nkey instanceof EquivalentMap)) {
		    if (!eql(key, nkey)) return subtree;
		    else return join(node.left, node.right);
		} else return buildNode(equivLess(nkey, key), null,
					node.left, node.right);
	    } else if (khash < nhash) {
		Object new_left = less(node.left, key, khash);
		if (new_left == node.left) return subtree;
		else return buildNode(nkey, node.value, new_left, node.right);
	    } else {
		Object new_right = less(node.right, key, khash);
		if (new_right == node.right) return subtree;
		else return buildNode(nkey, node.value, node.left, new_right);
	    }
	}
    }

    /*pkg*/ static Object domain(Object subtree) {
	if (subtree == null) return null;
	else if (!(subtree instanceof Node)) {
	    Object[] ary = (Object[])subtree;
	    int nkeys = ary.length >> 1;
	    Object[] dom = new Object[nkeys];
	    for (int i = 0; i < nkeys; ++i) dom[i] = ary[i];
	    return dom;
	} else {
	    Node node = (Node)subtree;
	    Object ldom = domain(node.left), rdom = domain(node.right);
	    if (node.key instanceof EquivalentMap) {
		ArrayList<Entry> al = ((EquivalentMap)node.key).contents;
		ArrayList<Object> dom = new ArrayList<Object>(al.size());
		for (int i = 0; i < al.size(); ++i) dom.add(al.get(i).key);
		return FHashSet.makeNode(new FHashSet.EquivalentSet(dom), ldom, rdom);
	    } else return FHashSet.makeNode(node.key, ldom, rdom);
	}
    }

    /*pkg*/ static <Val> FSet<Object> range(Object subtree, FSet<Val> initial) {
	if (subtree == null) return (FSet<Object>)initial;
	else if (!(subtree instanceof Node)) {
	    Object[] ary = (Object[])subtree;
	    int nkeys = ary.length >> 1;
	    for (int i = 0; i < nkeys; ++i)
		initial = initial.with((Val)ary[nkeys + i]);
	    return (FSet<Object>)initial;
	} else {
	    Node node = (Node)subtree;
	    if (node.key instanceof EquivalentMap) {
		ArrayList<Entry> al = ((EquivalentMap)node.key).contents;
		for (int i = 0; i < al.size(); ++i)
		    initial = initial.with((Val)al.get(i).value);
		return (FSet<Object>)initial;
	    } else return (range(node.left, initial).with(node.value)
			   .union(range(node.right, initial)));
	}
    }

    private static Object union(Object subtree1, Object subtree2) {
	return union(subtree1, subtree2, NEGATIVE_INFINITY, POSITIVE_INFINITY);
    }

    private static Object union(Object subtree1, Object subtree2, int lo, int hi) {
	if (subtree1 == subtree2 || subtree1 == null) return split(subtree2, lo, hi);
	else if (subtree2 == null) return split(subtree1, lo, hi);
	else if (!(subtree1 instanceof Node)) {
	    Object[] ary1 = (Object[])subtree1;
	    if (!(subtree2 instanceof Node))
		return union2(ary1, (Object[])subtree2, lo, hi);
	    else {
		Node node2 = (Node)subtree2;
		Object key2 = node2.key;
		int hash2 = hashCode(key2);
		Object new_left = union(trim(subtree1, lo, hash2),
					trim(node2.left, lo, hash2),
					lo, hash2);
		Object new_right = union(trim(subtree1, hash2, hi),
					 trim(node2.right, hash2, hi),
					 hash2, hi);
		Entry entry1 = findEquiv(subtree1, hash2);
		if (entry1 == null) return concat(key2, hash2, node2.value, new_left, new_right);
		else {
		    Object k = equivUnion(entry1.key, entry1.value, key2, node2.value);
		    if (k instanceof EquivalentMap)
			return concat(k, hash2, null, new_left, new_right);
		    else {
			Entry ent = ((Entry)k);
			return concat(ent.key, hash2, ent.value, new_left, new_right);
		    }
		}
	    }
	} else {
	    Node node1 = (Node)subtree1;
	    Object key1 = node1.key;
	    int hash1 = hashCode(key1);
	    Object new_left = union(trim(node1.left, lo, hash1),
				    trim(subtree2, lo, hash1),
				    lo, hash1);
	    Object new_right = union(trim(node1.right, hash1, hi),
				     trim(subtree2, hash1, hi),
				     hash1, hi);
	    Entry entry2 = findEquiv(subtree2, hash1);
	    if (entry2 == null) return concat(key1, hash1, node1.value, new_left, new_right);
	    else {
		Object e = equivUnion(key1, node1.value, entry2.key, entry2.value);
		if (e instanceof EquivalentMap)
		    return concat(e, hash1, null, new_left, new_right);
		else {
		    Entry ent = ((Entry)e);
		    return concat(ent.key, hash1, ent.value, new_left, new_right);
		}
	    }
	}
    }

    private static Object restrictedTo(Object map_subtree, Object set_subtree) {
	return restrictedTo(map_subtree, set_subtree,
			    NEGATIVE_INFINITY, POSITIVE_INFINITY);
    }

    private static Object restrictedTo(Object map_subtree, Object set_subtree,
				       int lo, int hi) {
	if (map_subtree == null) return null;
	else if (set_subtree == null) return null;
	else if (!(map_subtree instanceof Node)) {
	    Object[] map_ary = (Object[])map_subtree;
	    if (!(set_subtree instanceof FHashSet.Node))
		return restrictedTo2(map_ary, (Object[])set_subtree, lo, hi);
	    else {
		FHashSet.Node set_node = (FHashSet.Node)set_subtree;
		Object raw_elt = set_node.element;
		Object set_elt;
		if (raw_elt instanceof FHashSet.EquivalentSet)
		    set_elt = ((FHashSet.EquivalentSet)raw_elt).contents.get(0);
		else set_elt = raw_elt;
		int se_hash = hashCode(set_elt);
		Object new_left = restrictedTo(trim(map_subtree, lo, se_hash),
					       set_node.left, lo, se_hash);
		Object new_right = restrictedTo(trim(map_subtree, se_hash, hi),
						set_node.right, se_hash, hi);
		Entry entry = findEquiv(map_subtree, se_hash);
		if (entry == null) return join(new_left, new_right);
		else {
		    Object k = equivRestrictedTo(entry.key, entry.value, raw_elt);
		    if (k == null) return join(new_left, new_right);
		    else {
			Entry ent = (Entry)k;
			return concat(ent.key, se_hash, ent.value, new_left, new_right);
		    }
		}
	    }
	} else {
	    Node map_node = (Node)map_subtree;
	    Object raw_key = map_node.key;
	    Object map_key;
	    if (raw_key instanceof EquivalentMap)
		map_key = ((EquivalentMap)raw_key).contents.get(0).key;
	    else map_key = raw_key;
	    int mk_hash = hashCode(map_key);
	    Object new_left = restrictedTo(map_node.left,
					   FHashSet.trim(set_subtree, lo, mk_hash),
					   lo, mk_hash);
	    Object new_right = restrictedTo(map_node.right,
					    FHashSet.trim(set_subtree, mk_hash, hi),
					    mk_hash, hi);
	    Object set_elt = FHashSet.findEquiv(set_subtree, mk_hash);
	    if (set_elt == FHashSet.NO_ELEMENT) return join(new_left, new_right);
	    else {
		Object k = equivRestrictedTo(raw_key, map_node.value, set_elt);
		if (k == null) return join(new_left, new_right);
		else if (k instanceof EquivalentMap)
		    return concat(k, mk_hash, null, new_left, new_right);
		else {
		    Entry ent = (Entry)k;
		    return concat(ent.key, mk_hash, ent.value, new_left, new_right);
		}
	    }
	}
    }

    private static Object restrictedFrom(Object map_subtree, Object set_subtree) {
	return restrictedFrom(map_subtree, set_subtree,
			      NEGATIVE_INFINITY, POSITIVE_INFINITY);
    }

    private static Object restrictedFrom(Object map_subtree, Object set_subtree,
					 int lo, int hi) {
	if (map_subtree == null) return null;
	else if (set_subtree == null) return split(map_subtree, lo, hi);
	else if (!(map_subtree instanceof Node)) {
	    Object[] map_ary = (Object[])map_subtree;
	    if (!(set_subtree instanceof FHashSet.Node))
		return restrictedFrom2(map_ary, (Object[])set_subtree, lo, hi);
	    else {
		FHashSet.Node set_node = (FHashSet.Node)set_subtree;
		Object raw_elt = set_node.element;
		Object set_elt;
		if (raw_elt instanceof FHashSet.EquivalentSet)
		    set_elt = ((FHashSet.EquivalentSet)raw_elt).contents.get(0);
		else set_elt = raw_elt;
		int se_hash = hashCode(set_elt);
		Object new_left = restrictedFrom(trim(map_subtree, lo, se_hash),
						 FHashSet.trim(set_node.left, lo, se_hash),
						 lo, se_hash);
		Object new_right = restrictedFrom(trim(map_subtree, se_hash, hi),
						  FHashSet.trim(set_node.right, se_hash, hi),
						  se_hash, hi);
		Entry entry = findEquiv(map_subtree, se_hash);
		if (entry == null) return join(new_left, new_right);
		else {
		    Object k = equivRestrictedFrom(entry.key, entry.value, raw_elt);
		    if (k == null) return join(new_left, new_right);
		    else {
			Entry ent = (Entry)k;
			return concat(ent.key, se_hash, ent.value, new_left, new_right);
		    }
		}
	    }
	} else {
	    Node map_node = (Node)map_subtree;
	    Object raw_key = map_node.key;
	    Object map_key;
	    if (raw_key instanceof EquivalentMap)
		map_key = ((EquivalentMap)raw_key).contents.get(0).key;
	    else map_key = raw_key;
	    int mk_hash = hashCode(map_key);
	    Object new_left = restrictedFrom(map_node.left,
					     FHashSet.trim(set_subtree, lo, mk_hash),
					     lo, mk_hash);
	    Object new_right = restrictedFrom(map_node.right,
					      FHashSet.trim(set_subtree, mk_hash, hi),
					      mk_hash, hi);
	    Object set_elt = FHashSet.findEquiv(set_subtree, mk_hash);
	    if (set_elt == FHashSet.NO_ELEMENT)
		return concat(raw_key, mk_hash, map_node.value, new_left, new_right);
	    else {
		Object k = equivRestrictedFrom(raw_key, map_node.value, set_elt);
		if (k == null) return join(new_left, new_right);
		else if (k instanceof EquivalentMap)
		    return concat(k, mk_hash, null, new_left, new_right);
		else {
		    Entry ent = (Entry)k;
		    return concat(ent.key, mk_hash, ent.value, new_left, new_right);
		}
	    }
	}
    }

    /*pkg*/ static int compareTo(Object tree1, Object tree2) {
	if (tree1 == tree2) return 0;
	int size1 = treeSize(tree1), size2 = treeSize(tree2);
	// Start by comparing the sizes; smaller sets are considered less than
	// larger ones.  Only if the sizes are equal do we have to do the lexicographic
	// comparison.
	if (size1 < size2) return -1;
	else if (size1 > size2) return 1;
	else return compareTo(tree1, 0, tree2, 0, 0, size1);
    }

    private static int compareTo(Object subtree1, int base1, Object subtree2, int base2,
				 int lo, int hi) {
	if ((subtree1 == subtree2 && base1 == base2) || lo == hi) return 0;
	else if (!(subtree1 instanceof Node)) {
	    if (!(subtree2 instanceof Node)) {
		Object[] ary1 = (Object[])subtree1, ary2 = (Object[])subtree2;
		int nkeys1 = ary1.length >> 1, nkeys2 = ary2.length >> 1;
		for (int i = lo; i < hi; ++i) {
		    int hash1 = hashCode(ary1[i - base1]);
		    int hash2 = hashCode(ary2[i - base2]);
		    if (hash1 < hash2) return -1;
		    else if (hash1 > hash2) return 1;
		    else {
			Object val1 = ary1[i - base1 + nkeys1];
			Object val2 = ary2[i - base2 + nkeys2];
			int comp_res =
			    ((Comparable<Object>)val1).compareTo((Comparable<Object>)val2);
			if (comp_res != 0) return comp_res;
		    }
		}
		return 0;
	    } else return - compareTo(subtree2, base2, subtree1, base1, lo, hi);
	} else {
	    Node node1 = (Node)subtree1;
	    Object left1 = node1.left;
	    int l1size = treeSize(left1);
	    int new_hi = base1 + l1size;
	    RankTrimResult rtr1 = rankTrim(left1, base1, lo, new_hi);
	    RankTrimResult rtr2 = rankTrim(subtree2, base2, lo, new_hi);
	    int left_comp_res = compareTo(rtr1.subtree, rtr1.base,
					  rtr2.subtree, rtr2.base, lo, new_hi);
	    if (left_comp_res != 0) return left_comp_res;
	    else {
		Object key1 = node1.key;
		Entry ent2 = rankEntry(subtree2, new_hi - base2);
		int hash1 = hashCode(key1);
		int hash2 = hashCode(ent2.key);
		if (hash1 < hash2) return -1;
		else if (hash1 > hash2) return 1;
		else {
		    int comp_res = equivCompare(key1, node1.value, ent2.key, ent2.value);
		    if (comp_res != 0) return comp_res;
		    else {
			int new_lo = base1 + l1size + keySize(key1);
			RankTrimResult rtr3 = rankTrim(node1.right, new_lo,
						       new_lo, hi);
			RankTrimResult rtr4 = rankTrim(subtree2, base2, new_lo, hi);
			return compareTo(rtr3.subtree, rtr3.base,
					 rtr4.subtree, rtr4.base, new_lo, hi);
		    }
		}
	    }
	}
    }

    /*pkg*/ static boolean equals(Object tree1, Object tree2) {
	if (tree1 == tree2) return true;
	int size1 = treeSize(tree1), size2 = treeSize(tree2);
	if (size1 != size2) return false;
	else return equals(tree1, 0, tree2, 0, 0, size1);
    }

    private static boolean equals(Object subtree1, int base1, Object subtree2, int base2,
				  int lo, int hi) {
	if ((subtree1 == subtree2 && base1 == base2) || lo == hi) return true;
	else if (!(subtree1 instanceof Node)) {
	    if (!(subtree2 instanceof Node)) {
		Object[] ary1 = (Object[])subtree1, ary2 = (Object[])subtree2;
		for (int i = lo; i < hi; ++i) {
		    Object key1 = ary1[i - base1], key2 = ary2[i - base2];
		    if (!eql(key1, key2)) return false;
		    int nkeys1 = ary1.length >> 1, nkeys2 = ary2.length >> 1;
		    Object val1 = ary1[i - base1 + nkeys1];
		    Object val2 = ary2[i - base2 + nkeys2];
		    if (!eql(val1, val2)) return false;
		}
		return true;
	    } else return equals(subtree2, base2, subtree1, base1, lo, hi);
	} else {
	    Node node1 = (Node)subtree1;
	    Object left1 = node1.left;
	    int l1size = treeSize(left1);
	    int new_hi = base1 + l1size;
	    RankTrimResult rtr1 = rankTrim(left1, base1, lo, new_hi);
	    RankTrimResult rtr2 = rankTrim(subtree2, base2, lo, new_hi);
	    if (!equals(rtr1.subtree, rtr1.base, rtr2.subtree, rtr2.base, lo, new_hi))
		return false;
	    else {
		Object key1 = node1.key;
		Object val1 = node1.value;
		Entry ent2 = rankEntry(subtree2, new_hi - base2);
		Object val2 = ent2.value;
		if (!equivEquals(key1, ent2.key))
		    return false;
		else if (!(key1 instanceof EquivalentMap) && !eql(val1, val2)) return false;
		else {
		    int key1_size = keySize(key1);
		    int new_lo = base1 + l1size + key1_size;
		    RankTrimResult rtr3 = rankTrim(node1.right, new_lo, new_lo, hi);
		    RankTrimResult rtr4 = rankTrim(subtree2, base2, new_lo, hi);
		    return equals(rtr3.subtree, rtr3.base,
				  rtr4.subtree, rtr4.base, new_lo, hi);
		}
	    }
	}
    }

    private static final class RankTrimResult {
	RankTrimResult (Object _subtree, int _base) {
	    subtree = _subtree;
	    base = _base;
	}
	Object subtree;
	int base;
    }

    private static RankTrimResult rankTrim(Object subtree, int base, int lo, int hi) {
	while (subtree != null && subtree instanceof Node) {
	    Node node = (Node)subtree;
	    int nrank = base + treeSize(node.left);
	    if (nrank >= lo) {
		if (nrank < hi) break;
		else subtree = node.left;
	    } else {
		int rbase = nrank + keySize(node.key);
		if (rbase > lo) break;
		else {
		    subtree = node.right;
		    base = rbase;
		}
	    }
	}
	return new RankTrimResult(subtree, base);
    }

    private static Entry rankEntry(Object subtree, int rank) {
	if (subtree == null) throw new NullPointerException();	// shouldn't happen
	else if (!(subtree instanceof Node)) {
	    Object[] ary = (Object[])subtree;
	    return new Entry(ary[rank], ary[rank + (ary.length >> 1)]);
	} else {
	    Node node = (Node)subtree;
	    int left_size = treeSize(node.left);
	    if (rank < left_size) return rankEntry(node.left, rank);
	    else {
		int key_size = keySize(node.key);
		if (rank < left_size + key_size) return node;
		else return rankEntry(node.right, rank - (left_size + key_size));
	    }
	}
    }

    private static Entry findEquiv(Object subtree, int hash) {
	if (subtree == null) return null;
	else if (!(subtree instanceof Node)) {
	    Object[] ary = (Object[])subtree;
	    int bin_srch_res = binarySearch(ary, hash);
	    int found = bin_srch_res & BIN_SEARCH_FOUND_MASK;
	    int idx = bin_srch_res >> BIN_SEARCH_INDEX_SHIFT;
	    if (found == BIN_SEARCH_FOUND)
		return new Entry(ary[idx], ary[idx + (ary.length >> 1)]);
	    else return null;
	} else {
	    Node node = (Node)subtree;
	    int nhash = hashCode(node.key);
	    if (hash == nhash) return node;
	    else if (hash < nhash) return findEquiv(node.left, hash);
	    else return findEquiv(node.right, hash);
	}
    }

    // Returns a new tree all of whose keys are greater than `lo' and less than
    // `hi'.  (Contrast `trim'.)
    private static Object split(Object subtree, int lo, int hi) {
	if (subtree == null) return null;
	else if (lo == NEGATIVE_INFINITY && hi == POSITIVE_INFINITY) return subtree;
	else if (!(subtree instanceof Node)) {
	    Object[] ary = (Object[])subtree;
	    int nkeys = ary.length >> 1;
	    int lo_split = (lo == NEGATIVE_INFINITY ? 0 : binarySearchLo(ary, lo));
	    int hi_split = (hi == POSITIVE_INFINITY ? nkeys : binarySearchHi(ary, hi));
	    if (lo_split >= hi_split) return null;
	    else if (lo_split == 0 && hi_split == nkeys) return subtree;
	    else return subseq2(ary, lo_split, hi_split);
	} else {
	    Node node = (Node)subtree;
	    int khash = hashCode(node.key);
	    int lo_comp, hi_comp;
	    if (lo != NEGATIVE_INFINITY && khash <= lo) {
		if (hi == POSITIVE_INFINITY && khash == lo) return node.right;
		else return split(node.right, lo, hi);
	    } else if (hi != POSITIVE_INFINITY && khash >= hi) {
		if (lo == NEGATIVE_INFINITY && khash == hi) return node.left;
		else return split(node.left, lo, hi);
	    } else {
		Object new_left = split(node.left, lo, POSITIVE_INFINITY);
		Object new_right = split(node.right, NEGATIVE_INFINITY, hi);
		if (new_left == node.left && new_right == node.right) return subtree;
		else return concat(node.key, khash, node.value, new_left, new_right);
	    }
	}
    }


    // Returns the largest subtree of `subtree' whose root key is greater than `lo'
    // and less than `hi'.  (Contrast `split'.)
    private static Object trim (Object subtree, int lo, int hi) {
	if (subtree == null) return null;
	else if (!(subtree instanceof Node)) {
	    Object[] ary = (Object[])subtree;
	    int nkeys = ary.length >> 1;
	    // If the array is completely out of range, drop it.
	    if ((lo != NEGATIVE_INFINITY && hashCode(ary[nkeys - 1]) <= lo) ||
		(hi != POSITIVE_INFINITY && hashCode(ary[0]) >= hi))
		return null;
	    else return subtree;
	} else {
	    Node node = (Node)subtree;
	    int khash = hashCode(node.key);
	    if (lo == NEGATIVE_INFINITY || khash > lo) {
		if (hi == POSITIVE_INFINITY || khash < hi)
		    return subtree;
		else return trim(node.left, lo, hi);
	    } else return trim(node.right, lo, hi);
	}
    }

    // Assumes that all keys of `left' are less than `key', and all keys of `right'
    // are greater than `key'; returns a new tree containing all these.  This does
    // more rebalancing than `buildNode', which otherwise has the same contract.
    // `key' may be an `EquivalentMap'.
    private static Object concat(Object key, int khash, Object value,
				 Object left, Object right) {
	if (left == null) return with(right, key, khash, value);
	else if (right == null) return with(left, key, khash, value);
	else {
	    int sizl = treeSize(left);
	    int sizr = treeSize(right);
	    if (left instanceof Node && sizl > sizr * BALANCE_FACTOR) {
		Node l = (Node)left;
		return buildNode(l.key, l.value, l.left,
				 concat(key, khash, value, l.right, right));
	    } else if (right instanceof Node && sizr > sizl * BALANCE_FACTOR) {
		Node r = (Node)right;
		return buildNode(r.key, r.value,
				 concat(key, khash, value, left, r.left), r.right);
	    } else return buildNode(key, value, left, right);
	}
    }

    private static Object buildNode(Object key, Object value,
				    Object left, Object right) {
	if (key instanceof Entry) {	// convenience for some callers
	    Entry ent = (Entry)key;
	    value = ent.value;
	    key = ent.key;
	}
	if ((left == null || !(left instanceof Node)) &&
	    (right == null || !(right instanceof Node))) {
	    if (!(key instanceof EquivalentMap) &&
		(left == null ? 0 : ((Object[])left).length) + 1 +
		(right == null ? 0 : ((Object[])right).length) < MAX_LEAF_ARRAY_LENGTH)
		return makeArray2(key, value, ((Object[])left), ((Object[])right));
	    else return makeNode(key, value, left, right);
	} else {
	    int sizl = treeSize(left);
	    int sizr = treeSize(right);
	    // This code is subtly different from Adams' in order to create more
	    // opportunities to coalesce adjacent leaf arrays.
	    if (right instanceof Node && sizr > sizl * BALANCE_FACTOR) {
		Node r = (Node)right;
		Object rl = r.left;
		Object rr = r.right;
		if (!(rl instanceof Node) || treeSize(rl) <= treeSize(rr))
		    return makeNode(r.key, r.value, buildNode(key, value, left, rl), rr);
		else {
		    Node rln = (Node)rl;
		    return makeNode(rln.key, rln.value,
				    buildNode(key, value, left, rln.left),
				    buildNode(r.key, r.value, rln.right, rr));
		}
	    } else if (left instanceof Node && sizl > sizr * BALANCE_FACTOR) {
		Node l = (Node)left;
		Object ll = l.left;
		Object lr = l.right;
		if (!(lr instanceof Node) || treeSize(lr) <= treeSize(ll))
		    return makeNode(l.key, l.value, ll, buildNode(key, value, lr, right));
		else {
		    Node lrn = (Node)lr;
		    return makeNode(lrn.key, lrn.value,
				    buildNode(l.key, l.value, ll, lrn.left),
				    buildNode(key, value, lrn.right, right));
		}
	    } else return makeNode(key, value, left, right);
	}
    }

    private static Object join(Object left, Object right) {
	if (left == null) return right;
	else if (right == null) return left;
	else {
	    Object m = min(right), k, v = null;
	    if (m instanceof Entry) {
		Entry e = ((Entry)m);
		m = e.key;
		v = e.value;
	    }
	    return concat(m, hashCode(m), v, left, lessMin(right));
	}
    }

    /* Differs from `first' in that it may return an `EquivalentMap'.
     * Assumes `subtree' is nonempty. */
    private static Object min(Object subtree) {
	if (!(subtree instanceof Node)) {
	    Object[] ary = (Object[])subtree;
	    return new Entry(ary[0], ary[ary.length >> 1]);
	} else {
	    Node node = (Node)subtree;
	    if (node.left == null) {
		if (node.key instanceof EquivalentMap) return node.key;
		else return node;
	    } else return min(node.left);
	}
    }

    /* Assumes `subtree' is nonempty. */
    private static Object lessMin(Object subtree) {
	if (!(subtree instanceof Node)) {
	    Object[] ary = (Object[])subtree;
	    return subseq2(ary, 1, ary.length >> 1);
	} else {
	    Node node = (Node)subtree;
	    if (node.left == null) return node.right;
	    else return concat(node.key, hashCode(node.key), node.value,
			       lessMin(node.left), node.right);
	}
    }

    /*pkg*/ static int myHashCode(Object subtree) {
	if (subtree == null) return 0;
	else if (!(subtree instanceof Node)) {
	    Object[] ary = (Object[])subtree;
	    int nkeys = ary.length >> 1;
	    int hash = 0;
	    for (int i = 0; i < nkeys; ++i) {
		Object key = ary[i], value = ary[i + nkeys];
		int enthash = ((key == null ? 0 : key.hashCode()) ^
			       (value == null ? 0 : value.hashCode()));
		hash += enthash;
	    }
	    return hash;
	} else {
	    Node node = (Node)subtree;
	    int hash = myHashCode(node.left) + myHashCode(node.right);
	    Object key = node.key;
	    if (key instanceof EquivalentMap) {
		ArrayList<Entry> al = ((EquivalentMap)key).contents;
		for (int i = 0, siz = al.size(); i < siz; ++i)
		    hash += al.get(i).hashCode();
	    } else hash += (key == null ? 0 : key.hashCode()) ^
			   (node.value == null ? 0 : node.value.hashCode());
	    return hash;
	}
    }

    private static String dump(Object thing) {
	if (thing == null) return "null";
	else if (thing instanceof EquivalentMap) {
	    ArrayList<Entry> al = ((EquivalentMap)thing).contents;
	    String res = "[";
	    for (int i = 0, size = al.size(); i < size; ++i) {
		if (i > 0) res = res + ", ";
		res = res + dump(al.get(i));
	    }
	    return res + "]";
	} else if (thing instanceof Object[]) {
	    StringBuffer str_buf = new StringBuffer("{");
	    Object[] ary = (Object[])thing;
	    int nkeys = ary.length >> 1;
	    for (int i = 0; i < nkeys; ++i) {
		str_buf.append(dump(ary[i]));
		str_buf.append(" -> ");
		str_buf.append(dump(ary[i + nkeys]));
		if (i < nkeys - 1) str_buf.append(", ");
	    }
	    str_buf.append("}");
	    return str_buf.toString();
	} else if (thing instanceof Node) {
	    Node node = (Node)thing;
	    return "(" + node.size + ", " + dump(node.key) +
		(node.key instanceof EquivalentMap ? "" : " -> " + dump(node.value)) +
		";\n" +
		indent(dump(node.left), "  ") + ",\n" +
		indent(dump(node.right), "  ") + ")";
	} else if (thing instanceof Entry) {
	    Entry ent = (Entry)thing;
	    return dump(ent.key) + " -> " + dump(ent.value);
	} else return thing.toString();
    }

    private static String indent(String str, String prefix) {
	StringBuffer res = new StringBuffer(prefix);
	for (int i = 0, len = str.length(); i < len; ++i) {
	    char c = str.charAt(i);
	    res.append(c);
	    if (c == '\n' && i < len - 1) res.append(prefix);
	}
	return res.toString();
    }

    private boolean verify(Object subtree, int lo, int hi) {
	if (subtree == null) return true;
	else if (!(subtree instanceof Node)) {
	    Object[] ary = (Object[])subtree;
	    boolean res = true;
	    int prev = lo;
	    int nkeys = ary.length >> 1;
	    for (int i = 0, len = nkeys; i < len; ++i) {
		Object key = ary[i];
		if (key instanceof EquivalentMap) return false;
		int hash = hashCode(key);
		if (prev != NEGATIVE_INFINITY && prev >= hash) res = false;
		prev = hash;
	    }
	    if (hi != POSITIVE_INFINITY && prev >= hi) res = false;
	    return res;
	} else {
	    Node node = (Node)subtree;
	    int hash = hashCode(node.key);
	    int sizl = treeSize(node.left);
	    int sizr = treeSize(node.right);
	    if (node.size != sizl + sizr + keySize(node.key)) return false;
	    if (node.key instanceof Entry) return false;
	    if (node.key instanceof EquivalentMap &&
		((EquivalentMap)node.key).contents.size() < 2) return false;
	    // Small subtrees may be unbalanced, because of our array-splitting heuristic
	    // and because of EquivalentMaps.  As long as the size of any subtree that
	    // can be unbalanced is strictly bounded, we're still okay.
	    if ((sizr > 4 && sizl > sizr * BALANCE_FACTOR) ||
		(sizl > 4 && sizr > sizl * BALANCE_FACTOR))
		return false;
	    return verify(node.left, lo, hash) &&
		   verify(node.right, hash, hi);
	}
    }


    /****************/
    /* Equivalent maps */

    /* In the case where the map contains two or more keys which are distinct but
     * are equivalent according to the comparison method, the `key' field of a
     * `Node' will contain one of these.  We always build a `Node' in this case; we
     * never put equivalent elements in an array.  Here we don't worry about the
     * extra level(s) of structure because this is supposed to be a rare case.
     */
    /* The name is too cryptic, but `MapWithEquivalentKeys' is too long. */
    private static final class EquivalentMap {
	private EquivalentMap(ArrayList<Entry> _contents) {
	    contents = _contents;
	}
	private ArrayList<Entry> contents;
    }

    // The "2" pair takes precedence.  Returns either an `EquivalentMap' or an `Entry'.
    private static Object equivUnion(Object key1, Object value1,
				     Object key2, Object value2) {
	if (key1 instanceof EquivalentMap) {
	    ArrayList<Entry> al1 = ((EquivalentMap)key1).contents;
	    if (key2 instanceof EquivalentMap) {
		ArrayList<Entry> al2 = ((EquivalentMap)key2).contents;
		ArrayList<Entry> al = (ArrayList<Entry>)al2.clone();
		for (int i1 = 0, siz1 = al1.size(); i1 < siz1; ++i1) {
		    Entry ent1 = (Entry)al1.get(i1);
		    boolean found = false;
		    for (int i = 0, siz = al.size(); i < siz && !found; ++i) {
			Entry e = (Entry)al.get(i);
			if (eql(e.key, ent1.key)) found = true;
		    }
		    if (!found) al.add(ent1);
		}
		al.trimToSize();
		return new EquivalentMap(al);
	    } else {
		ArrayList<Entry> al = (ArrayList<Entry>)al1.clone();
		int found_at = -1;
		for (int i = 0, siz = al.size(); i < siz && found_at < 0; ++i) {
		    Entry e = (Entry)al.get(i);
		    if (eql(key2, e.key)) found_at = i;
		}
		if (found_at >= 0) al.set(found_at, new Entry(key2, value2));
		else al.add(new Entry(key2, value2));
		al.trimToSize();
		return new EquivalentMap(al);
	    }
	} else if (key2 instanceof EquivalentMap) {
	    ArrayList<Entry> al2 = ((EquivalentMap)key2).contents;
	    boolean found = false;
	    for (int i = 0, siz = al2.size(); i < siz && !found; ++i) {
		Entry e = (Entry)al2.get(i);
		if (eql(key1, e.key)) found = true;
	    }
	    if (!found) {
		ArrayList<Entry> al = (ArrayList<Entry>)al2.clone();
		al.add(new Entry(key1, value1));
		al.trimToSize();
		return new EquivalentMap(al);
	    } else return new EquivalentMap(al2);
	} else if (eql(key1, key2)) return new Entry(key2, value2);
	else {
	    ArrayList<Entry> al = new ArrayList<Entry>(2);
	    al.add(new Entry(key1, value1));
	    al.add(new Entry(key2, value2));
	    return new EquivalentMap(al);
	}
    }

    private static Object equivRestrictedTo(Object map_key, Object map_val,
					    Object set_elt) {
	if (map_key instanceof EquivalentMap) {
	    ArrayList<Entry> map_al = ((EquivalentMap)map_key).contents;
	    if (set_elt instanceof FHashSet.EquivalentSet) {
		ArrayList<Object> set_al = ((FHashSet.EquivalentSet)set_elt).contents;
		ArrayList<Entry> al = new ArrayList<Entry>();
		for (int i = 0, siz = map_al.size(); i < siz; ++i) {
		    Entry e = (Entry)map_al.get(i);
		    if (set_al.contains(e.key)) al.add(e);
		}
		if (al.size() == 0) return null;
		else if (al.size() == 1) return al.get(0);
		else {
		    al.trimToSize();
		    return new EquivalentMap(al);
		}
	    } else {
		for (int i = 0, siz = map_al.size(); i < siz; ++i) {
		    Entry e = (Entry)map_al.get(i);
		    if (eql(set_elt, e.key)) return e;
		}
		return null;
	    }
	} else {
	    if (set_elt instanceof FHashSet.EquivalentSet) {
		ArrayList<Object> set_al = ((FHashSet.EquivalentSet)set_elt).contents;
		if (set_al.contains(map_key))
		    return new Entry(map_key, map_val);
		else return null;
	    } else {
		if (eql(map_key, set_elt)) return new Entry(map_key, map_val);
		else return null;
	    }
	}
    }

    private static Object equivRestrictedFrom(Object map_key, Object map_val,
					      Object set_elt) {
	if (map_key instanceof EquivalentMap) {
	    ArrayList<Entry> map_al = ((EquivalentMap)map_key).contents;
	    ArrayList<Entry> al = new ArrayList<Entry>();
	    if (set_elt instanceof FHashSet.EquivalentSet) {
		ArrayList<Object> set_al = ((FHashSet.EquivalentSet)set_elt).contents;
		for (int i = 0, siz = map_al.size(); i < siz; ++i) {
		    Entry e = (Entry)map_al.get(i);
		    if (!set_al.contains(e.key)) al.add(e);
		}
	    } else {
		for (int i = 0, siz = map_al.size(); i < siz; ++i) {
		    Entry e = (Entry)map_al.get(i);
		    if (!eql(set_elt, e.key)) al.add(e);
		}
	    }
	    if (al.size() == 0) return null;
	    else if (al.size() == 1) return al.get(0);
	    else {
		al.trimToSize();
		return new EquivalentMap(al);
	    }
	} else {
	    if (set_elt instanceof FHashSet.EquivalentSet) {
		ArrayList<Object> set_al = ((FHashSet.EquivalentSet)set_elt).contents;
		if (!set_al.contains(map_key))
		    return new Entry(map_key, map_val);
		else return null;
	    } else {
		if (!eql(map_key, set_elt))
		    return new Entry(map_key, map_val);
		else return null;
	    }
	}
    }

    private static Object equivLess(Object eqm, Object key) {
	ArrayList<Entry> al = ((EquivalentMap)eqm).contents;
	int found_at = -1;
	for (int i = 0, siz = al.size(); i < siz && found_at < 0; ++i) {
	    Entry e = (Entry)al.get(i);
	    if (eql(key, e.key)) found_at = i;
	}
	if (found_at >= 0) {
	    al = (ArrayList<Entry>)al.clone();
	    al.remove(found_at);
	    if (al.size() == 1) return al.get(0);
	    else return new EquivalentMap(al);
	} else return eqm;
    }

    static private int equivCompare(Object key1, Object val1,
				    Object key2, Object val2) {
	if (key1 instanceof EquivalentMap) {
	    ArrayList<Entry> al1 = ((EquivalentMap)key1).contents;
	    if (key2 instanceof EquivalentMap) {
		ArrayList<Entry> al2 = ((EquivalentMap)key2).contents;
		int siz1 = al1.size(), siz2 = al2.size();
		if (siz1 < siz2) return 1;
		else if (siz1 > siz2) return -1;
		else {
		    FHashSet<Object> vals1 = new FHashSet<Object>();
		    FHashSet<Object> vals2 = new FHashSet<Object>();
		    for (int i = 0; i < siz1; ++i)
			vals1 = vals1.with(((Entry)al1.get(i)).value);
		    for (int i = 0; i < siz2; ++i)
			vals2 = vals2.with(((Entry)al2.get(i)).value);
		    return vals1.compareTo(vals2);
		}
	    } else return -1;
	} else if (key2 instanceof EquivalentMap) return 1;
	else return ((Comparable<Object>)val1).compareTo((Comparable<Object>)val2);
    }

    private static boolean equivEquals(Object key1, Object key2) {
	if (key1 instanceof EquivalentMap) {
	    ArrayList<Entry> al1 = ((EquivalentMap)key1).contents;
	    if (key2 instanceof EquivalentMap) {
		ArrayList<Entry> al2 = ((EquivalentMap)key2).contents;
		int siz1 = al1.size(), siz2 = al2.size();
		if (siz1 != siz2) return false;
		else {
		    for (int i1 = 0; i1 < siz1; ++i1) {
			boolean found = false;
			Entry ent1 = (Entry)al1.get(i1);
			for (int i2 = 0; i2 < siz2 && !found; ++i2) {
			    Entry ent2 = (Entry)al2.get(i2);
			    if (eql(ent1.key, ent2.key) && eql(ent1.value, ent2.value))
				found = true;
			}
			if (!found) return false;
		    }
		    return true;
		}
	    } else return false;
	} else if (key2 instanceof EquivalentMap) return false;
	else return eql(key1, key2);
    }


    /****************/
    /* Internal array manipulation routines.  These all assume their index
     * parameters are within bounds.  Of course, despite what the names of some
     * might suggest, they all make new arrays.  The "2" suffix means these all
     * operate on double-length arrays, containing keys followed by values. */

    private static Object[] makeArray2(Object key, Object value,
				       Object[] left, Object[] right) {
	int lnkeys = (left == null ? 0 : left.length >> 1);
	int rnkeys = (right == null ? 0 : right.length >> 1);
	int nkeys = lnkeys + 1 + rnkeys;
	Object[] a = new Object[nkeys << 1];
	for (int i = 0; i < lnkeys; ++i) {
	    a[i] = left[i];
	    a[i + nkeys] = left[i + lnkeys];
	}
	a[lnkeys] = key;
	a[lnkeys + nkeys] = value;
	for (int i = 0; i < rnkeys; ++i) {
	    a[i + lnkeys + 1] = right[i];
	    a[i + lnkeys + 1 + nkeys] = right[i + rnkeys];
	}
	return a;
    }

    private static Object[] insert2(Object[] ary, int idx, Object key, Object value) {
	int nkeys = ary.length >> 1;
	Object[] a = new Object[ary.length + 2];
	for (int i = 0; i < idx; ++i) {
	    a[i] = ary[i];
	    a[i + nkeys + 1] = ary[i + nkeys];
	}
	a[idx] = key;
	a[idx + nkeys + 1] = value;
	for (int i = idx; i < nkeys; ++i) {
	    a[i + 1] = ary[i];
	    a[i + nkeys + 2] = ary[i + nkeys];
	}
	return a;
    }

    private static Object[] remove2(Object[] ary, int idx) {
	int nkeys = ary.length >> 1;
	if (nkeys == 1) return null;
	else {
	    Object[] a = new Object[ary.length - 2];
	    for (int i = 0; i < idx; ++i) {
		a[i] = ary[i];
		a[i + nkeys - 1] = ary[i + nkeys];
	    }
	    for (int i = idx + 1; i < nkeys; ++i) {
		a[i - 1] = ary[i];
		a[i + nkeys - 2] = ary[i + nkeys];
	    }
	    return a;
	}
    }

    private static Object[] subseq2(Object[] ary, int lo, int hi) {
	if (lo >= hi) return null;
	else {
	    int ary_nkeys = ary.length >> 1;
	    int nkeys = hi - lo;
	    Object[] a = new Object[nkeys << 1];
	    for (int i = 0; i < nkeys; ++i) {
		a[i] = ary[i + lo];
		a[i + nkeys] = ary[i + lo + ary_nkeys];
	    }
	    return a;
	}
    }

    // Updates the value for the key at `idx'.
    private static Object[] update2(Object[] ary, int idx, Object value) {
	int len = ary.length;
	Object[] a = new Object[len];
	for (int i = 0; i < len; ++i) a[i] = ary[i];
	a[idx + (len >> 1)] = value;
	return a;
    }

    // Does a union on `ary1' and `ary2', omitting any pairs whose keys are not
    // greater than `lo' and less than `hi'.  If the result is too long to be a
    // leaf, splits it and makes a node.  Also, if any equivalent keys are found,
    // makes a node.
    private static Object union2(Object[] ary1, Object[] ary2, int lo, int hi) {
	int i1 = 0, i2 = 0;
	int nkeys1 = ary1.length >> 1, nkeys2 = ary2.length >> 1;
	int len1 = nkeys1, len2 = nkeys2;
	if (lo != NEGATIVE_INFINITY) {
	    while (i1 < len1 && lo >= hashCode(ary1[i1])) ++i1;
	    while (i2 < len2 && lo >= hashCode(ary2[i2])) ++i2;
	}
	if (hi != POSITIVE_INFINITY) {
	    while (i1 < len1 && hi <= hashCode(ary1[len1 - 1])) --len1;
	    while (i2 < len2 && hi <= hashCode(ary2[len2 - 1])) --len2;
	}
	int maxprs = (len1 - i1) + (len2 - i2);
	ArrayList<Object> keys = new ArrayList<Object>(maxprs);
	ArrayList<Object> vals = new ArrayList<Object>(maxprs);
	boolean any_equiv = false;
	Object k1 = null, k2 = null;
	int hash1 = 0, hash2 = 0;
	if (i1 < len1) hash1 = hashCode(k1 = ary1[i1]);
	if (i2 < len2) hash2 = hashCode(k2 = ary2[i2]);
	while (true) {
	    if (i1 == len1) {
		while (i2 < len2) {
		    keys.add(ary2[i2]);
		    vals.add(ary2[i2 + nkeys2]);
		    ++i2;
		}
		break;
	    } else if (i2 == len2) {
		while (i1 < len1) {
		    keys.add(ary1[i1]);
		    vals.add(ary1[i1 + nkeys1]);
		    ++i1;
		}
		break;
	    } else {
		if (hash1 < hash2) {
		    keys.add(k1);
		    vals.add(ary1[i1 + nkeys1]);
		    ++i1;
		    if (i1 < len1) hash1 = hashCode(k1 = ary1[i1]);
		} else if (hash1 > hash2) {
		    keys.add(k2);
		    vals.add(ary2[i2 + nkeys2]);
		    ++i2;
		    if (i2 < len2) hash2 = hashCode(k2 = ary2[i2]);
		} else {
		    if (eql(k1, k2)) {
			keys.add(k2);
			vals.add(ary2[i2 + nkeys2]);
		    } else {
			keys.add(equivUnion(k1, ary1[i1 + nkeys1],
					    k2, ary2[i2 + nkeys2]));
			vals.add(null);
			any_equiv = true;
		    }
		    ++i1;
		    ++i2;
		    if (i1 < len1) hash1 = hashCode(k1 = ary1[i1]);
		    if (i2 < len2) hash2 = hashCode(k2 = ary2[i2]);
		}
	    }
	}
	if (any_equiv) {
	    Object t = null;
	    // We could attempt a better algorithm, but this shouldn't happen often.
	    for (Iterator kit = keys.iterator(), vit = vals.iterator(); kit.hasNext(); ) {
		Object k = kit.next();
		t = with(t, k, hashCode(k), vit.next());
	    }
	    return t;
	} else {
	    int nkeys = keys.size();
	    keys.addAll(vals);
	    Object[] res_ary = keys.toArray();
	    if (res_ary.length > MAX_LEAF_ARRAY_LENGTH) {
		int idx = nkeys >> 1;
		return makeNode(keys.get(idx), vals.get(idx),
				subseq2(res_ary, 0, idx),
				subseq2(res_ary, idx + 1, nkeys));
	    } else return res_ary;
	}
    }

    private static Object[] restrictedTo2(Object[] map_ary, Object[] set_ary,
					  int lo, int hi) {
	int i1 = 0, i2 = 0;
	int nkeys = map_ary.length >> 1, nelts = set_ary.length;
	int len1 = nkeys, len2 = nelts;
	if (lo != NEGATIVE_INFINITY) {
	    while (i1 < len1 && lo >= hashCode(map_ary[i1])) ++i1;
	    while (i2 < len2 && lo >= hashCode(set_ary[i2])) ++i2;
	}
	if (hi != POSITIVE_INFINITY) {
	    while (i1 < len1 && hi <= hashCode(map_ary[len1 - 1])) --len1;
	    while (i2 < len2 && hi <= hashCode(set_ary[len2 - 1])) --len2;
	}
	ArrayList<Object> keys = new ArrayList<Object>(len1 - i1);
	ArrayList<Object> vals = new ArrayList<Object>(len1 - i1);
	Object k = null, e = null;
	int khash = 0, ehash = 0;
	if (i1 < len1) khash = hashCode(k = map_ary[i1]);
	if (i2 < len2) ehash = hashCode(e = set_ary[i2]);
	while (i1 < len1 && i2 < len2) {
	    if (khash < ehash) {
		++i1;
		if (i1 < len1) khash = hashCode(k = map_ary[i1]);
	    } else if (khash > ehash) {
		++i2;
		if (i2 < len2) ehash = hashCode(e = set_ary[i2]);
	    } else {
		if (eql(k, e)) {
		    keys.add(k);
		    vals.add(map_ary[i1 + nkeys]);
		}
		++i1;
		++i2;
		if (i1 < len1) khash = hashCode(k = map_ary[i1]);
		if (i2 < len2) ehash = hashCode(e = set_ary[i2]);
	    }
	}
	if (keys.isEmpty()) return null;
	else {
	    keys.addAll(vals);
	    return keys.toArray();
	}
    }

    private static Object[] restrictedFrom2(Object[] map_ary, Object[] set_ary,
					    int lo, int hi) {
	int i1 = 0, i2 = 0;
	int nkeys = map_ary.length >> 1, nelts = set_ary.length;
	int len1 = nkeys, len2 = nelts;
	if (lo != NEGATIVE_INFINITY) {
	    while (i1 < len1 && lo >= hashCode(map_ary[i1])) ++i1;
	    while (i2 < len2 && lo >= hashCode(set_ary[i2])) ++i2;
	}
	if (hi != POSITIVE_INFINITY) {
	    while (i1 < len1 && hi <= hashCode(map_ary[len1 - 1])) --len1;
	    while (i2 < len2 && hi <= hashCode(set_ary[len2 - 1])) --len2;
	}
	ArrayList<Object> keys = new ArrayList<Object>(len1 - i1);
	ArrayList<Object> vals = new ArrayList<Object>(len1 - i1);
	Object k = null, e = null;
	int khash = 0, ehash = 0;
	if (i1 < len1) khash = hashCode(k = map_ary[i1]);
	if (i2 < len2) ehash = hashCode(e = set_ary[i2]);
	while (i1 < len1 && i2 < len2) {
	    if (khash < ehash) {
		keys.add(k);
		vals.add(map_ary[i1 + nkeys]);
		++i1;
		if (i1 < len1) khash = hashCode(k = map_ary[i1]);
	    } else if (khash > ehash) {
		++i2;
		if (i2 < len2) ehash = hashCode(e = set_ary[i2]);
	    } else {
		if (!eql(k, e)) {
		    keys.add(k);
		    vals.add(map_ary[i1 + nkeys]);
		}
		++i1;
		++i2;
		if (i1 < len1) khash = hashCode(k = map_ary[i1]);
		if (i2 < len2) ehash = hashCode(e = set_ary[i2]);
	    }
	}
	while (i1 < len1) {
	    keys.add(map_ary[i1]);
	    vals.add(map_ary[i1 + nkeys]);
	    ++i1;
	}
	if (keys.isEmpty()) return null;
	else {
	    keys.addAll(vals);
	    return keys.toArray();
	}
    }

    /* Binary search returns two pieces of information: (1) whether the key or an
     * equivalent one was found; and (2) the index at which the key (or an
     * equivalent one) either was found or should be inserted.  Since these are both
     * small integers, we pack them into a single `int', to save us from having to
     * allocate a result object. */
    private static final int BIN_SEARCH_NOT_FOUND = 0;
    private static final int BIN_SEARCH_FOUND = 1;
    private static final int BIN_SEARCH_FOUND_MASK = 0x1;
    private static final int BIN_SEARCH_INDEX_SHIFT = 1;

    private static int binarySearch(Object[] ary, int hash) {
	int nkeys = ary.length >> 1;
	int lo = 0, hi = nkeys - 1;
	while (lo <= hi) {
	    int mid = (lo + hi) / 2;
	    Object akey = ary[mid];
	    int akhash = hashCode(akey);
	    if (hash == akhash)
		return (mid << BIN_SEARCH_INDEX_SHIFT) | BIN_SEARCH_FOUND;
	    else if (hash < akhash) hi = mid - 1;
	    else lo = mid + 1;
	}
	return (lo << BIN_SEARCH_INDEX_SHIFT) | BIN_SEARCH_NOT_FOUND;
    }

    // Returns the index of the left edge of the first member of `ary' that is
    // above `lo'.
    private static int binarySearchLo(Object[] ary, int lo) {
	int bin_srch_res = binarySearch(ary, lo);
	int idx = bin_srch_res >> BIN_SEARCH_INDEX_SHIFT;
	if ((bin_srch_res & BIN_SEARCH_FOUND_MASK) == BIN_SEARCH_FOUND)
	    return idx + 1;
	else return idx;
    }

    // Returns the index of the right edge of the last member of `ary' that is
    // below `hi'.
    private static int binarySearchHi(Object[] ary, int hi) {
	int bin_srch_res = binarySearch(ary, hi);
	return bin_srch_res >> BIN_SEARCH_INDEX_SHIFT;
    }

    private static boolean eql(Object x, Object y) {
	return x == null ? y == null : x.equals(y);
    }

    /****************/
    // Iterator class

    /*pkg*/ static final class FHMIterator<Key, Val> implements Iterator<Map.Entry<Key, Val>> {

	private static final class IteratorNode {
	    IteratorNode(Object _subtree, int _index, IteratorNode _parent) {
		subtree = _subtree;
		index = _index;
		parent = _parent;
	    }
	    private final Object subtree;
	    private int index;
	    private final IteratorNode parent;
	}

	private IteratorNode inode;

	/*pkg*/ FHMIterator(Object subtree) {
	    inode = new IteratorNode(subtree, 0, null);
	    canonicalize();
	}

	private void canonicalize() {
	    while (true) {
		if (inode == null) break;
		else if (inode.subtree == null) {
		    inode = inode.parent;
		    if (inode == null) break;
		    else ++inode.index;
		} else if (!(inode.subtree instanceof Node)) {
		    if (inode.index < ((Object[])inode.subtree).length >> 1) break;
		    else {
			inode = inode.parent;
			if (inode == null) break;
			else ++inode.index;
		    }
		} else {
		    Node node = (Node)inode.subtree;
		    if (inode.index == 0) inode = new IteratorNode(node.left, 0, inode);
		    else if (inode.index == keySize(node.key) + 1)
			inode = new IteratorNode(node.right, 0, inode.parent);
		    else break;
		}
	    }
	}

	public boolean hasNext() {
	    return inode != null;
	}

	public Map.Entry<Key, Val> next() {
	    Object entry;
	    if (inode == null) throw new NoSuchElementException();
	    else if (!(inode.subtree instanceof Node)) {
		Object[] ary = (Object[])inode.subtree;
		entry = new Entry(ary[inode.index],
				  ary[inode.index + (ary.length >> 1)]);
	    } else {
		Node node = (Node)inode.subtree;
		if (node.key instanceof EquivalentMap) {
		    ArrayList<Entry> al = ((EquivalentMap)node.key).contents;
		    entry = al.get(inode.index - 1);
		} else entry = node;
	    }
	    inode.index++;
	    canonicalize();
	    return (Map.Entry<Key, Val>)entry;
	}

	public void remove() {
	    throw new UnsupportedOperationException();
	}
    }

    // Used by 'keySet'.
    private static class FHMKeyIterator<Key> implements Iterator<Key> {
	private FHMIterator<Key, Object> fhmIter;

	FHMKeyIterator(Object subtree) {
	    fhmIter = new FHMIterator(subtree);
	}

	public boolean hasNext() {
	    return fhmIter.hasNext();
	}

	// Duplicate code, but saves consing.
	public Key next() {
	    Key key;
	    if (fhmIter.inode == null) throw new NoSuchElementException();
	    else if (!(fhmIter.inode.subtree instanceof Node)) {
		Object[] ary = (Object[])fhmIter.inode.subtree;
		key = (Key)ary[fhmIter.inode.index];
	    } else {
		Node node = (Node)fhmIter.inode.subtree;
		if (node.key instanceof EquivalentMap) {
		    ArrayList<Entry> al = ((EquivalentMap)node.key).contents;
		    key = (Key)al.get(fhmIter.inode.index - 1).key;
		} else key = (Key)node.key;
	    }
	    fhmIter.inode.index++;
	    fhmIter.canonicalize();
	    return key;
	}

	public void remove() {
	    throw new UnsupportedOperationException();
	}
    }

    // Used by 'values'.
    private static class FHMValueIterator<Val> implements Iterator<Val> {
	private FHMIterator<Object, Val> fhmIter;

	FHMValueIterator(Object subtree) {
	    fhmIter = new FHMIterator(subtree);
	}

	public boolean hasNext() {
	    return fhmIter.hasNext();
	}

	// I don't think 'values' is commonly used, so I haven't bothered to optimize
	// this like 'FHMKeyIterator.next'.
	public Val next() {
	    return fhmIter.next().getValue();
	}

	public void remove() {
	    throw new UnsupportedOperationException();
	}
    }

    /**
     * Saves the state of this <code>FHashMap</code> to a stream.
     *
     * @serialData Emits the internal data of the map, including the default it uses;
     * the size of the map [<code>int</code>]; and the key/value pairs in key order
     * [<code>Object</code>s].
     */
    private void writeObject(ObjectOutputStream strm) throws IOException {
	strm.defaultWriteObject();	// writes `comp' and `dflt'
        strm.writeInt(size());
	for (Map.Entry ment : this) {
	    Entry ent = (Entry)ment;
            strm.writeObject(ent.key);
	    strm.writeObject(ent.value);
	}
    }

    // http://docs.oracle.com/javase/specs/jls/se7/html/jls-17.html#jls-17.5.3
    private static Field TreeField;
    static {
	try {
	    TreeField = FHashMap.class.getDeclaredField("tree");
	    TreeField.setAccessible(true);
	} catch (NoSuchFieldException nsf) {
	    throw new RuntimeException("Static initialization failed", nsf);
	}
    }

    /**
     * Reconstitutes the <code>FHashMap</code> instance from a stream.
     */
    private void readObject(ObjectInputStream strm) throws IOException, ClassNotFoundException {
	hash_code = Integer.MIN_VALUE;
	strm.defaultReadObject();	// reads `comp' and `dflt'
        int size = strm.readInt();
	Object t = null;
	for (int i = 0; i < size; ++i) {
	    Object key = strm.readObject();
	    Object val = strm.readObject();
	    t = with(t, key, hashCode(key), val);
	}
	try {
	    TreeField.set(this, t);
	} catch (IllegalAccessException ia) {
	    throw new RuntimeException("FHashMap deserialization failed", ia);
	}
    }

}


