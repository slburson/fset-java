/*
 * FLinkedHashMap.java
 *
 * Copyright (c) 2013 Scott L. Burson.
 *
 * This file is licensed under the Library GNU Public License (LGPL) v. 2.1.
 */


package com.ergy.fset;

import java.util.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Just like <code>FHashMap</code> except that the iterator returns entries
 * in the same order in which the keys were first added.  Also, <code>keySet</code>,
 * <code>values</code>, and <code>entrySet</code> return collections whose iterators
 * return objects in the same order.
 *
 * <p>WARNING: <code>less</code> takes O(n) time in this implementation.  Avoid it.
 *
 * <p>Still unimplemented: <code>restrictedTo</code>, <code>restrictedFrom</code>.
 */

public class FLinkedHashMap<Key, Val>
    extends AbstractFMap<Key, Val>
    implements Comparable<FLinkedHashMap<Key, Val>>, Serializable
{
    /**
     * Returns an empty <code>FLinkedHashMap</code>.  Slightly more efficient than calling
     * the constructor, because it returns a canonical instance.
     */
    public static <Key, Val> FLinkedHashMap<Key, Val> emptyMap() {
	return (FLinkedHashMap<Key, Val>)EMPTY_INSTANCE;
    }

    /*
     * Constructs an empty <code>FLinkedHashMap</code>.
     */
    public FLinkedHashMap() {
	map_tree = null;
	list_tree = null;
	dflt = null;
    }

    private FLinkedHashMap(Object _map_tree, Object _list_tree, Val _dflt) {
	map_tree = _map_tree;
	list_tree = _list_tree;
	dflt = _dflt;
    }

    public boolean isEmpty() {
	return map_tree == null;
    }

    public int size() {
	return FHashMap.treeSize(map_tree);
    }

    public Map.Entry<Key, Val> arb() {
	return (Map.Entry<Key, Val>)FHashMap.arb(map_tree);
    }

    public boolean contains(Map.Entry<Key, Val> entry) {
	Key key = entry.getKey();
	Object val = FHashMap.get(map_tree, key, hashCode(key));
	return val != FHashMap.NO_ELEMENT && eql(val, entry.getValue());
    }

    public boolean containsKey(Object key) {
	return FHashMap.get(map_tree, key, hashCode(key)) != FHashMap.NO_ELEMENT;
    }

    public Val get(Object key) {
	Object val = FHashMap.get(map_tree, key, hashCode(key));
	if (val == FHashMap.NO_ELEMENT) return dflt;
	else return (Val)val;
    }

    public FLinkedHashMap<Key, Val> with(Key key, Val value) {
	int khash = hashCode(key);
	Object new_map_tree = FHashMap.with(map_tree, key, khash, value);
	if (new_map_tree == map_tree) return this;
	if (FHashMap.treeSize(new_map_tree) == FHashMap.treeSize(map_tree))   // existing key?
	    return new FLinkedHashMap<Key, Val>(new_map_tree, list_tree, dflt);
	else {
	    Object new_list_tree = FTreeList.insert(list_tree, FTreeList.treeSize(list_tree), key);
	    return new FLinkedHashMap<Key, Val>(new_map_tree, new_list_tree, dflt);
	}
    }

    public FLinkedHashMap<Key, Val> less(Key key) {
	Object new_map_tree = FHashMap.less(map_tree, key, hashCode(key));
	if (new_map_tree == map_tree) return this;
	// O(n) because we have to linearly search the list for the key.
	Iterator<Key> it = new FTreeList.FTLIterator(list_tree);
	int i = 0;
	while (!eql(key, it.next())) i++;
	Object new_list_tree = FTreeList.less(list_tree, i);
	return new FLinkedHashMap<Key, Val>(new_map_tree, new_list_tree, dflt);
    }

    public Set<Key> keySet() {
	return new AbstractSet<Key>() {
	    public Iterator<Key> iterator() {
		return new FTreeList.FTLIterator<Key>(list_tree);
	    }
	    public int size() {
		return FLinkedHashMap.this.size();
	    }
	    public boolean contains(Object key) {
		return FLinkedHashMap.this.containsKey(key);
	    }
	};
    }

    public Collection<Val> values() {
	return new AbstractCollection<Val>() {
	    public Iterator<Val> iterator() {
		return new FLHMValueIterator<Val>(map_tree, list_tree);
	    }
	    public int size() {
		return FLinkedHashMap.this.size();
	    }
	};
    }

    public Set<Map.Entry<Key, Val>> entrySet() {
	return new AbstractSet<Map.Entry<Key, Val>>() {
	    public Iterator<Map.Entry<Key, Val>> iterator() {
		return new FLHMIterator<Key, Val>(map_tree, list_tree);
	    }
	    public int size() {
		return FLinkedHashMap.this.size();
	    }
	    public boolean remove(Object x) {
		throw new UnsupportedOperationException();
	    }
	    public void clear() {
		throw new UnsupportedOperationException();
	    }
	};
    }

    // &&& Or FLinkedHashSet, when that exists
    public FHashSet<Key> domain() {
	return FHashSet.<Key>make(FHashMap.domain(map_tree));
    }

    public FSet<Val> range() {
	return (FSet<Val>)FHashMap.range(map_tree, new FHashSet<Val>());
    }

    public FSet<Val> range(FSet<Val> initial_set) {
	initial_set = initial_set.difference(initial_set);
	return (FSet<Val>)FHashMap.range(map_tree, initial_set);
    }

    // &&& Or FLinkedHashSet
    public FHashSet<Map.Entry<Key, Val>> toSet() {
	return (FHashSet<Map.Entry<Key, Val>>)toSet(new FHashSet<Map.Entry<Key, Val>>());
    }

    public FSet<Map.Entry<Key, Val>> toSet(FSet<Map.Entry<Key, Val>> initial_set) {
	FSet<Map.Entry<Key, Val>> s = initial_set.difference(initial_set);
	for (Iterator<Map.Entry<Key, Val>> it = new FHashMap.FHMIterator<Key, Val>(map_tree);
	     it.hasNext();)
	    s = s.with(it.next());
	return s;
    }

    public FLinkedHashMap<Key, Val> union(FMap<? extends Key, ? extends Val> with_map) {
	// O(n log n) rather than O(n), and probably with a worse constant factor too.
	FLinkedHashMap<Key, Val> m = this;
	for (Map.Entry<? extends Key, ? extends Val> ent : with_map)
	    m = m.with(ent.getKey(), ent.getValue());
	return m;
    }

    public FLinkedHashMap<Key, Val> restrictedTo(FSet<Key> set) {
	// Maybe later -- I'm lazy
	throw new UnsupportedOperationException();
    }

    public FLinkedHashMap<Key, Val> restrictedFrom(FSet<Key> set) {
	// Maybe later -- I'm lazy
	throw new UnsupportedOperationException();
    }

    public Val getDefault() {
	return dflt;
    }

    public Iterator<Map.Entry<Key, Val>> iterator() {
	return new FLHMIterator<Key, Val>(map_tree, list_tree);
    }

    public int compareTo(FLinkedHashMap<Key, Val> other) {
	return FHashMap.compareTo(map_tree, other.map_tree);
    }

    public boolean equals(Object obj) {
	if (obj == this) return true;
	else if (obj instanceof FLinkedHashMap) {
	    FLinkedHashMap flhm = (FLinkedHashMap)obj;
	    return FHashMap.equals(map_tree, flhm.map_tree);
	} else if (obj instanceof FHashMap) {
	    FHashMap fhm = (FHashMap)obj;
	    return FHashMap.equals(map_tree, fhm.tree);
	} else if (!(obj instanceof Map)) return false;
	else {
	    Map<Object, Object> map = (Map<Object, Object>)obj;
	    if (size() != map.size()) return false;
	    for (Map.Entry<Object, Object> ent : map.entrySet())
		if (!FHashMap.contains(map_tree, ent)) return false;
	    return true;
	}
    }

    public int hashCode() {
	if (hash_code == Integer.MIN_VALUE) hash_code = FHashMap.myHashCode(map_tree);
	return hash_code;
    }

    /******************************************************************************/
    /* Internals */

    // The empty map can be a singleton.
    private static final FLinkedHashMap<?, ?> EMPTY_INSTANCE = new FLinkedHashMap<Object, Object>();

    // The map tree is managed by FHashMap and contains the same pairs.
    /*pkg*/ transient Object map_tree;
    // The list tree contains the keys in the order in which they were first added.
    // It is managed by FTreeList.
    private transient Object list_tree;

    private Val dflt;

    private transient int hash_code = Integer.MIN_VALUE;

    private static boolean eql(Object x, Object y) {
	return x == null ? y == null : x.equals(y);
    }

    private static int hashCode(Object x) {
	return FHashMap.hashCode(x);
    }

    /****************/
    // Iterator classes

    private static final class FLHMIterator<Key, Val> implements Iterator<Map.Entry<Key, Val>> {
	Object map_tree;
	FTreeList.FTLIterator<Key> list_it;

	private FLHMIterator(Object _map_tree, Object _list_tree) {
	    map_tree = _map_tree;
	    list_it = new FTreeList.FTLIterator<Key>(_list_tree);
	}

	public boolean hasNext() {
	    return list_it.hasNext();
	}

	public Map.Entry<Key, Val> next() {
	    Key key = list_it.next();
	    // &&& This could be improved with a 'getEntry' method -- it would cons an
	    // Entry only about half the time.  Worth the trouble?
	    Val val = (Val)FHashMap.get(map_tree, key, FHashMap.hashCode(key));
	    return (Map.Entry<Key, Val>)new FHashMap.Entry(key, val);
	}

	public void remove() {
	    throw new UnsupportedOperationException();
	}
    }

    private static final class FLHMValueIterator<Val> implements Iterator<Val> {
	Object map_tree;
	FTreeList.FTLIterator<Object> list_it;

	private FLHMValueIterator(Object _map_tree, Object _list_tree) {
	    map_tree = _map_tree;
	    list_it = new FTreeList.FTLIterator<Object>(_list_tree);
	}

	public boolean hasNext() {
	    return list_it.hasNext();
	}

	public Val next() {
	    Object key = list_it.next();
	    return (Val)FHashMap.get(map_tree, key, FHashMap.hashCode(key));
	}

	public void remove() {
	    throw new UnsupportedOperationException();
	}
    }

    /**
     * Saves the state of this <code>FLinkedHashMap</code> to a stream.
     *
     * @serialData Emits the internal data of the map, including the default it uses;
     * the size of the map [<code>int</code>]; and the key/value pairs in the order
     * they were added [<code>Object</code>s].
     */
    private void writeObject(ObjectOutputStream strm) throws IOException {
	strm.defaultWriteObject();	// writes `dflt'
        strm.writeInt(size());
	for (Map.Entry ment : this) {
            FHashMap.Entry ent = (FHashMap.Entry)ment;
	    strm.writeObject(ent.key);
	    strm.writeObject(ent.value);
	}
    }

    /**
     * Reconstitutes the <code>FLinkedHashMap</code> instance from a stream.
     */
    private void readObject(ObjectInputStream strm) throws IOException, ClassNotFoundException {
	hash_code = Integer.MIN_VALUE;
	strm.defaultReadObject();	// reads `dflt'
        int size = strm.readInt();
	map_tree = null;
	Object[] keys = new Object[size];
	for (int i = 0; i < size; ++i) {
	    Object key = strm.readObject();
	    Object val = strm.readObject();
	    map_tree = FHashMap.with(map_tree, key, hashCode(key), val);
	    keys[i] = key;
	}
	list_tree = FTreeList.fromCollection(keys);
    }

}
