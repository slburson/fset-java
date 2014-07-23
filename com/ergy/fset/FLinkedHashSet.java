/*
 * FLinkedHashSet.java
 *
 * Copyright (c) 2014 Scott L. Burson.
 *
 * This file is licensed under the Library GNU Public License (LGPL) v. 2.1.
 */


package com.ergy.fset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;


/**
 * Just like <code>FHashSet</code> except that the iterator returns entries
 * in the same order in which the keys were first added.
 *
 * <p>WARNING: <code>less</code> takes O(n) time, in general, in this implementation.
 * Avoid it, except in the special case of the element just returned by <code>arb</code>.
 *
 * Still unimplemented: <code>intersection</code>, <code>difference</code>.
 */

public class FLinkedHashSet<Elt>
    extends AbstractFSet<Elt>
    implements Comparable<FLinkedHashSet<Elt>>, Serializable
{

    /**
     * Returns an empty <code>FLinkedHashSet</code>.  Slightly more efficient than calling
     * the constructor, because it returns a canonical instance.
     */
    public static <Elt> FLinkedHashSet<Elt> emptySet() {
	return (FLinkedHashSet<Elt>)EMPTY_INSTANCE;
    }

    /*
     * Constructs an empty <code>FLinkedHashSet</code>.
     */
    public FLinkedHashSet() {
	set_tree = null;
	list_tree = null;
    }

    /**
     * Constructs a <code>FLinkedHashSet</code> containing only <code>elt</code>.
     */
    public FLinkedHashSet(Elt elt) {
	set_tree = FHashSet.with(null, elt, 0);	// hashCode(elt) not needed -- just use 0
	list_tree = FTreeList.insert(null, 0, elt);
    }

    public boolean isEmpty() {
	return set_tree == null;
    }

    public int size() {
	return FHashSet.treeSize(set_tree);
    }

    /**
     * On an <code>FLinkedHashSet</code>, always returns the element that was added first.
     * (A subsequent call to <code>less</code> passing this element will be efficient.)
     */
    public Elt arb() {
	if (list_tree == null) throw new NoSuchElementException();
	else return (Elt)FTreeList.get(list_tree, 0);
    }

    public boolean contains(Object elt) {
	return FHashSet.contains(set_tree, elt, hashCode(elt));
    }

    public FLinkedHashSet<Elt> with(Elt elt) {
	Object new_set_tree = FHashSet.with(set_tree, elt, hashCode(elt));
	if (new_set_tree == set_tree) return this;
	else return new FLinkedHashSet(new_set_tree,
				       FTreeList.insert(list_tree, FTreeList.treeSize(list_tree),
							elt));
    }

    public FLinkedHashSet<Elt> less(Elt elt) {
	Object new_set_tree = FHashSet.less(set_tree, elt, hashCode(elt));
	if (new_set_tree == set_tree) return this;
	// O(n) because we have to linearly search the list for the key.
	Iterator<Elt> it = new FTreeList.FTLIterator<Elt>(list_tree);
	int i = 0;
	while (!eql(elt, it.next())) i++;
	Object new_list_tree = FTreeList.less(list_tree, i);
	return new FLinkedHashSet<Elt>(new_set_tree, new_list_tree);
    }

    public Iterator<Elt> iterator() {
	return new FTreeList.FTLIterator(list_tree);
    }

    /**
     * This works by repeated <code>with</code>, and so is O(n log m); it doesn't use
     * the spiffy linear-time algorithm used by the other implementations.
     */
    public FLinkedHashSet<Elt> union(Collection<? extends Elt> coll) {
	Object new_set_tree = set_tree;
	Object new_list_tree = list_tree;
	for (Elt elt : coll) {
	    Object prev_set_tree = new_set_tree;
	    new_set_tree = FHashSet.with(new_set_tree, elt, hashCode(elt));
	    if (new_set_tree != prev_set_tree)
		new_list_tree = FTreeList.insert(new_list_tree, FTreeList.treeSize(new_list_tree),
						 elt);
	}
	if (new_set_tree == set_tree) return this;
	else return new FLinkedHashSet<Elt>(new_set_tree, new_list_tree);
    }

    public FLinkedHashSet<Elt> intersection(Collection<? extends Elt> coll) {
	// Maybe later -- I'm lazy
	throw new UnsupportedOperationException();
    }

    public FLinkedHashSet<Elt> difference(Collection<? extends Elt> coll) {
	// Maybe later -- I'm lazy
	throw new UnsupportedOperationException();
    }

    public int compareTo(FLinkedHashSet<Elt> other) {
	return FHashSet.compareTo(set_tree, other.set_tree);
    }

    public boolean equals(Object obj) {
	if (obj == this) return true;
	else if (obj instanceof FLinkedHashSet) {
	    FLinkedHashSet flhs = (FLinkedHashSet)obj;
	    return FHashSet.equals(set_tree, flhs.set_tree);
	} else if (obj instanceof FHashSet) {
	    FHashSet fhs = (FHashSet)obj;
	    return FHashSet.equals(set_tree, fhs.tree);
	} else if (!(obj instanceof Set)) return false;
	else {
	    Set<Object> set = (Set<Object>)obj;
	    if (size() != set.size()) return false;
	    for (Object elt : set)
		if (!FHashSet.contains(set_tree, elt, hashCode(elt))) return false;
	    return true;
	}
    }

    public boolean isSubset(Collection<?> coll) {
	if (coll == this) return true;
	else if (size() > coll.size()) return false;
	else if (coll instanceof FLinkedHashSet) {
	    FLinkedHashSet<Object> flhs = (FLinkedHashSet<Object>)coll;
	    return FHashSet.isSubset(set_tree, flhs.set_tree);
	} else if (coll instanceof FHashSet) {
	    FHashSet fhs = (FHashSet)coll;
	    return FHashSet.isSubset(set_tree, fhs.tree);
	} else if (!(coll instanceof Set)) return false;
	else {
	    for (Elt elt : this)
		if (!coll.contains(elt)) return false;
	    return true;
	}
    }

    public boolean isSuperset(Collection<?> coll) {
	if (coll == this) return true;
	else if (size() < coll.size()) return false;
	else if (coll instanceof FLinkedHashSet) {
	    FLinkedHashSet<Object> flhs = (FLinkedHashSet<Object>)coll;
	    return FHashSet.isSubset(flhs.set_tree, set_tree);
	} else if (coll instanceof FHashSet) {
	    FHashSet fhs = (FHashSet)coll;
	    return FHashSet.isSubset(fhs.tree, set_tree);
	} else if (!(coll instanceof Set)) return false;
	else {
	    for (Object elt : coll)
		if (!FHashSet.contains(set_tree, elt, hashCode(elt))) return false;
	    return true;
	}
    }

    public int hashCode() {
	if (hash_code == Integer.MIN_VALUE) hash_code = FHashSet.myHashCode(set_tree);
	return hash_code;
    }

    /******************************************************************************/
    /* Internals */

    // The empty set can be a singleton.
    private static final FLinkedHashSet<?> EMPTY_INSTANCE = new FLinkedHashSet<Object>();

    // The set tree is managed by FHashSet and contains the same pairs.
    /*pkg*/ transient Object set_tree;
    // The list tree contains the keys in the order in which they were first added.
    // It is managed by FTreeList.
    private transient Object list_tree;

    private FLinkedHashSet(Object _set_tree, Object _list_tree) {
	set_tree = _set_tree;
	list_tree = _list_tree;
    }

    private transient int hash_code = Integer.MIN_VALUE;

    private static boolean eql(Object x, Object y) {
	return x == null ? y == null : x.equals(y);
    }

    private static int hashCode(Object x) {
	return FHashSet.hashCode(x);
    }

    /**
     * Saves the state of this <code>FLinkedHashSet</code> to a stream.
     *
     * @serialData Emits the internal data of the set, including the size of the set
     * [<code>int</code>]; and the elements in order [<code>Object</code>s].
     */
    private void writeObject(ObjectOutputStream strm) throws IOException {
	strm.defaultWriteObject();	// writes `comp'
        strm.writeInt(size());
	for (Object e : this)
            strm.writeObject(e);
    }

    /**
     * Reconstitutes the <code>FLinkedHashSet</code> instance from a stream.
     */
    private void readObject(ObjectInputStream strm) throws IOException, ClassNotFoundException {
	hash_code = Integer.MIN_VALUE;
	strm.defaultReadObject();	// reads `comp'
        int size = strm.readInt();
	set_tree = null;
	Object[] elts = new Object[size];
	for (int i = 0; i < size; ++i) {
	    Object e = strm.readObject();
	    set_tree = FHashSet.with(set_tree, e, hashCode(e));
	    elts[i] = e;
	}
	list_tree = FTreeList.fromCollection(elts);
    }

}
