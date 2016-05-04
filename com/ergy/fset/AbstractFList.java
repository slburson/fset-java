/*
 * AbstractFList.java
 *
 * Copyright (c) 2013, 2014 Scott L. Burson.
 *
 * This file is licensed under the Library GNU Public License (LGPL), v. 2.1.
 */


package com.ergy.fset;
import java.util.*;

/**
 * This class provides a skeletal pure (functional) implementation of the List
 * interface.
 * 
 * <p>The purpose of this class is to provide methods for all the mutating operations
 * which throw <code>UnsupportedOperationException</code>, and one for
 * <code>clone</code> which simply returns <code>this</code>.
 *
 * @author Scott L. Burson
 */

public abstract class AbstractFList<Elt>
    // can't extend AbstractFList<Elt> because of subList return type
    extends AbstractCollection<Elt>
    implements FList<Elt> {

    /**
     * Returns this map.
     */
    public final AbstractFList<Elt> clone() {
	return this;
    }

    public Elt first() {
	return get(0);
    }

    public Elt last() {
	return get(size() - 1);
    }

    public FList<Elt> lessFirst() {
	return less(0);
    }

    public FList<Elt> lessLast() {
	return less(size() - 1);
    }

    public FList<Elt> prefix(int len) {
	return subseq(0, len);
    }

    public FList<Elt> suffix(int len) {
	return subseq(size() - len, size());
    }

    public FList<Elt> suffixFrom(int fromIndex) {
	return subseq(fromIndex, size());
    }

    /**
     * Unsupported.
     */
    public final boolean add(Elt e) {
	throw new UnsupportedOperationException();
    }

    /**
     * Unsupported.
     */
    public final void add(int index, Elt e) {
	throw new UnsupportedOperationException();
    }

    /**
     * Unsupported.
     */
    public final boolean addAll(int index, Collection<? extends Elt> c) {
	throw new UnsupportedOperationException();
    }

    /**
     * Unsupported.
     */
    public final void clear() {
	throw new UnsupportedOperationException();
    }

    /**
     * Unsupported.
     */
    public final Elt remove(int index) {
	throw new UnsupportedOperationException();
    }

    /**
     * Unsupported.
     */
    public final boolean removeRange(int fromIndex, int toIndex) {
	throw new UnsupportedOperationException();
    }

    /**
     * Unsupported.
     */
    public final Elt set(int index, Elt e) {
	throw new UnsupportedOperationException();
    }

    /**
     * Unsupported.  This is new in Java 8; in previous versions of FSet, this
     * interface had a 'sort' method, but we've been forced to rename it to 'sorted'
     * so it doesn't conflict with this one.
     */
    public void sort(Comparator<? super Elt> comp) {
	throw new UnsupportedOperationException();
    }

}
