/*
 * PureLinkedHashMap.java
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
 * Just like <code>PureHashMap</code> except that the iterator returns entries
 * in the same order in which the keys were first added.  Also, <code>keySet</code>,
 * <code>values</code>, and <code>entrySet</code> return collections whose iterators
 * return objects in the same order.
 *
 * WARNING: <code>less</code> takes O(n) time in this implementation.  Avoid it.
 *
 * Still unimplemented: <code>restrictedTo</code>, <code>restrictedFrom</code>.
 */

public class PureLinkedHashMap<Key, Val>
    extends AbstractPureMap<Key, Val>
    implements Comparable<PureLinkedHashMap<Key, Val>>, Serializable
{
    /**
     * Returns an empty PureLinkedHashMap.  Slightly more efficient than calling the constructor,
     * because it returns a canonical instance.
     */
    public static <Key, Val> PureLinkedHashMap<Key, Val> emptyMap() {
	return (PureLinkedHashMap<Key, Val>)EMPTY_INSTANCE;
    }

    public PureLinkedHashMap() {
	map_tree = null;
	list_tree = null;
	dflt = null;
    }

    private PureLinkedHashMap(Object _map_tree, Object _list_tree, Val _dflt) {
	map_tree = _map_tree;
	list_tree = _list_tree;
	dflt = _dflt;
    }

    public boolean isEmpty() {
	return map_tree == null;
    }

    public int size() {
	return PureHashMap.treeSize(map_tree);
    }

    public Map.Entry<Key, Val> arb() {
	return (Map.Entry<Key, Val>)PureHashMap.arb(map_tree);
    }

    public Key firstKey() {
	return (Key)PureHashMap.firstKey(map_tree);
    }

    public Key lastKey() {
	return (Key)PureHashMap.lastKey(map_tree);
    }

    public boolean containsKey(Object key) {
	return PureHashMap.containsKey(map_tree, key, PureHashMap.hashCode(key));
    }

    public Val get(Object key) {
	return (Val)PureHashMap.get(map_tree, key, PureHashMap.hashCode(key), dflt);
    }

    public PureLinkedHashMap<Key, Val> with(Key key, Val value) {
	int khash = PureHashMap.hashCode(key);
	Object new_map_tree = PureHashMap.with(map_tree, key, khash, value);
	if (new_map_tree == map_tree) return this;
	if (PureHashMap.containsKey(map_tree, key, khash))
	    return new PureLinkedHashMap<Key, Val>(new_map_tree, list_tree, dflt);
	else {
	    Object new_list_tree = PureTreeList.insert(list_tree, PureTreeList.treeSize(list_tree),
						       key);
	    return new PureLinkedHashMap<Key, Val>(new_map_tree, new_list_tree, dflt);
	}
    }

    public PureLinkedHashMap<Key, Val> less(Key key) {
	int khash = PureHashMap.hashCode(key);
	Object new_map_tree = PureHashMap.less(map_tree, key, khash);
	if (new_map_tree == map_tree) return this;
	// O(n) because we have to linearly search the list for the key.  (If we used 'get'
	// instead of an iterator, it would be O(n log n).)
	Iterator<Key> it = new PureTreeList.PTLIterator(list_tree);
	int i = 0;
	while (!eql(key, it.next())) i++;
	Object new_list_tree = PureTreeList.less(list_tree, i);
	return new PureLinkedHashMap<Key, Val>(new_map_tree, new_list_tree, dflt);
    }

    public Set<Key> keySet() {
	return new AbstractSet<Key>() {
	    public Iterator<Key> iterator() {
		return new PureTreeList.PTLIterator<Key>(list_tree);
	    }
	    public int size() {
		return PureLinkedHashMap.this.size();
	    }
	    public boolean contains(Object key) {
		return PureHashMap.containsKey(map_tree, key, PureHashMap.hashCode(key));
	    }
	};
    }

    public Collection<Val> values() {
	return new AbstractCollection<Val>() {
	    public Iterator<Val> iterator() {
		return new PLHMValueIterator<Val>(map_tree, list_tree);
	    }
	    public int size() {
		return PureLinkedHashMap.this.size();
	    }
	};
    }

    public Set<Map.Entry<Key, Val>> entrySet() {
	return new AbstractSet<Map.Entry<Key, Val>>() {
	    public Iterator<Map.Entry<Key, Val>> iterator() {
		return new PLHMIterator<Key, Val>(map_tree, list_tree);
	    }
	    public int size() {
		return PureLinkedHashMap.this.size();
	    }
	    public boolean remove(Object x) {
		throw new UnsupportedOperationException();
	    }
	    public void clear() {
		throw new UnsupportedOperationException();
	    }
	};
    }

    // &&& Or PureLinkedHashSet, when that exists
    public PureHashSet<Key> domain() {
	return new PureHashSet<Key>(PureHashMap.domain(map_tree));
    }

    public PureSet<Val> range() {
	return (PureSet<Val>)PureHashMap.range(map_tree, new PureHashSet<Val>());
    }

    public PureSet<Val> range(PureSet<Val> initial_set) {
	initial_set = initial_set.difference(initial_set);
	return (PureSet<Val>)PureHashMap.range(map_tree, initial_set);
    }

    // &&& Or PureLinkedHashSet
    public PureHashSet<Map.Entry<Key, Val>> toSet() {
	return (PureHashSet<Map.Entry<Key, Val>>)toSet(new PureHashSet<Map.Entry<Key, Val>>());
    }

    public PureSet<Map.Entry<Key, Val>> toSet(PureSet<Map.Entry<Key, Val>> initial_set) {
	PureSet<Map.Entry<Key, Val>> s = initial_set.difference(initial_set);
	for (Iterator<Map.Entry<Key, Val>> it = new PureHashMap.PHMIterator<Key, Val>(map_tree);
	     it.hasNext();)
	    s = s.with(it.next());
	return s;
    }

    public PureLinkedHashMap<Key, Val> union(PureMap<? extends Key, ? extends Val> with_map) {
	// O(n log n) rather than O(n), and probably with a worse constant factor too.
	PureLinkedHashMap<Key, Val> m = this;
	for (Map.Entry<? extends Key, ? extends Val> ent : with_map)
	    m = m.with(ent.getKey(), ent.getValue());
	return m;
    }

    public PureHashMap<Key, Val> restrictedTo(PureSet<Key> set) {
	// Maybe later -- I'm lazy
	throw new UnsupportedOperationException();
    }

    public PureHashMap<Key, Val> restrictedFrom(PureSet<Key> set) {
	// Maybe later -- I'm lazy
	throw new UnsupportedOperationException();
    }

    public Val getDefault() {
	return dflt;
    }

    public Iterator<Map.Entry<Key, Val>> iterator() {
	return new PLHMIterator<Key, Val>(map_tree, list_tree);
    }

    public int compareTo(PureLinkedHashMap<Key, Val> other) {
	return PureHashMap.compareTo(map_tree, other.map_tree);
    }

    public boolean equals(Object obj) {
	if (obj == this) return true;
	else if (obj instanceof PureLinkedHashMap) {
	    PureLinkedHashMap plhm = (PureLinkedHashMap)obj;
	    return PureHashMap.equals(map_tree, plhm.map_tree);
	} else if (obj instanceof PureHashMap) {
	    PureHashMap phm = (PureHashMap)obj;
	    return PureHashMap.equals(map_tree, phm.tree);
	} else if (!(obj instanceof Map)) return false;
	else {
	    Map<Object, Object> map = (Map<Object, Object>)obj;
	    if (size() != map.size()) return false;
	    for (Map.Entry<Object, Object> ent : map.entrySet()) {
		Object ekey = ent.getKey();
		Object eval = ent.getValue();
		int ekhash = PureHashMap.hashCode(ekey);
		if (!PureHashMap.containsKey(map_tree, ekey, ekhash)) return false;
		if (!eql(eval, PureHashMap.get(map_tree, ekey, ekhash, dflt))) return false;
	    }
	    return true;
	}
    }

    public int hashCode() {
	if (hash_code == Integer.MIN_VALUE) hash_code = PureHashMap.myHashCode(map_tree);
	return hash_code;
    }

    /******************************************************************************/
    /* Internals */

    // The empty map can be a singleton.
    private static final PureLinkedHashMap EMPTY_INSTANCE = new PureLinkedHashMap();

    // The map tree is managed by PureHashMap and contains the same pairs.
    /*package*/ transient Object map_tree;
    // The list tree contains the keys in the order in which they were first added.
    // It is managed by PureTreeList.
    private transient Object list_tree;

    private Val dflt;

    private transient int hash_code = Integer.MIN_VALUE;

    private static boolean eql(Object x, Object y) {
	return x == null ? y == null : x.equals(y);
    }

    /****************/
    // Iterator classes

    private static final class PLHMIterator<Key, Val> implements Iterator<Map.Entry<Key, Val>> {
	Object map_tree;
	PureTreeList.PTLIterator<Key> list_it;

	private PLHMIterator(Object _map_tree, Object _list_tree) {
	    map_tree = _map_tree;
	    list_it = new PureTreeList.PTLIterator<Key>(_list_tree);
	}

	public boolean hasNext() {
	    return list_it.hasNext();
	}

	public Map.Entry<Key, Val> next() {
	    Key key = list_it.next();
	    // &&& This could be improved with a 'getEntry' method -- it would cons an
	    // Entry only about half the time.  Worth the trouble?
	    Val val = (Val)PureHashMap.get(map_tree, key, PureHashMap.hashCode(key), null); 
	    return (Map.Entry<Key, Val>)new PureHashMap.Entry(key, val);
	}

	public void remove() {
	    throw new UnsupportedOperationException();
	}
    }

    private static final class PLHMValueIterator<Val> implements Iterator<Val> {
	Object map_tree;
	PureTreeList.PTLIterator<Object> list_it;

	private PLHMValueIterator(Object _map_tree, Object _list_tree) {
	    map_tree = _map_tree;
	    list_it = new PureTreeList.PTLIterator<Object>(_list_tree);
	}

	public boolean hasNext() {
	    return list_it.hasNext();
	}

	public Val next() {
	    Object key = list_it.next();
	    return (Val)PureHashMap.get(map_tree, key, PureHashMap.hashCode(key), null);
	}

	public void remove() {
	    throw new UnsupportedOperationException();
	}
    }

    /**
     * Saves the state of this <code>PureLinkedHashMap</code> to a stream.
     *
     * @serialData Emits the internal data of the map, including the default it uses;
     * the size of the map [<code>int</code>]; and the key/value pairs in the order
     * they were added [<code>Object</code>s].
     */
    private void writeObject(ObjectOutputStream strm) throws IOException {
	strm.defaultWriteObject();	// writes `dflt'
        strm.writeInt(size());
	for (Map.Entry ment : this) {
            PureHashMap.Entry ent = (PureHashMap.Entry)ment;
	    strm.writeObject(ent.key);
	    strm.writeObject(ent.value);
	}
    }

    /**
     * Reconstitutes the <code>PureLinkedHashMap</code> instance from a stream.
     */
    private void readObject(ObjectInputStream strm) throws IOException, ClassNotFoundException {
	strm.defaultReadObject();	// reads `dflt'
        int size = strm.readInt();
	Object[][] pairs = new Object[size][2];
	Object[] vals = new Object[size];
	for (int i = 0; i < size; ++i) {
	    pairs[i][0] = strm.readObject();
	    pairs[i][1] = vals[i] = strm.readObject();
	}
	map_tree = PureHashMap.fromArrayNoCopy(pairs, 0, size);
	list_tree = PureTreeList.fromCollection(vals);
    }

}
