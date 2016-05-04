/*
 * FList.java
 *
 * Copyright (c) 2013, 2014 Scott L. Burson.
 *
 * This file is licensed under the Library GNU Public License (LGPL), v. 2.1.
 */


package com.ergy.fset;
import java.util.*;

/**
 * A list (sequence) for which the update operators are all functional: they
 * return a new list rather than modifying the existing one.
 *
 * <p>Although this interface extends {@link List} of the Java Collections Framework,
 * and thus is somewhat integrated into that framework, it is not used the same way
 * as the <code>java.util</code> classes that implement <code>List</code>.  It does
 * not support the update operators declared by <code>List</code> (which are
 * documented as optional, anyway); in their place it adds several new operators
 * which are functional, in the sense that rather than modifying the list in place,
 * they construct and return a new list.
 * 
 * @author Scott L. Burson.
 */

public interface FList<Elt> extends List<Elt> {

    /**
     * Returns the first element of the list.
     *
     * @return the first element of the list
     * @throws IndexOutOfBoundsException if the list is empty
     */
    Elt first();

    /**
     * Returns the last element of the list.
     *
     * @return the last element of the list
     * @throws IndexOutOfBoundsException if the list is empty
     */
    Elt last();

    /**
     * Returns a new list which has <code>elt</code> at position <code>index</code>,
     * in place of the previous element.  The the other elements are the same as
     * those of this list.  If <code>index</code> is equal to the size of the list,
     * the returned list is one element longer.
     *
     * @param index the index of the element to substitute
     * @param elt the new element
     * @return the result list
     * @throws IndexOutOfBoundsException if <code>index < 0</code> or
     * <code>index > size()</code>
     */
    FList<Elt> with(int index, Elt elt);

    /**
     * Returns a new list which has <code>elt</code> inserted at position
     * <code>index</code>, which may be equal to <code>size()</code>.  The size is
     * one greater than that of this list; the elements at positions
     * <code>index</code> and above are moved one position to the right.
     *
     * @param index the index at which to insert
     * @param elt the new element
     * @return the result list
     * @throws IndexOutOfBoundsException if <code>index < 0</code> or
     * <code>index > size()</code>
     */
    FList<Elt> withInserted(int index, Elt elt);

    /**
     * Returns a new list which has <code>elt</code> prepended to the the contents
     * of this list.  The size is one greater than that of this list;
     * <code>elt</code> appears at position 0, and the elements of this list are
     * moved one position to the right.
     *
     * @param elt the element to be prepended
     * @return the result list
     */
    FList<Elt> withFirst(Elt elt);

    /**
     * Returns a new list which has <code>elt</code> appended to the contents of
     * this list.  The size is one greater than that of this list; <code>elt</code>
     * appears at the end.
     *
     * @param elt the element to be appended
     * @return the result list
     */
    FList<Elt> withLast(Elt elt);

    /**
     * Returns a new list which is this list less its first element.
     *
     * @return the result list
     */
    FList<Elt> lessFirst();

    /**
     * Returns a new list which is this list less its last element.
     *
     * @return the result list
     */
    FList<Elt> lessLast();

    /**
     * Returns a new list from which the element at position <code>index</code> has
     * been omitted.  The size is one less than that of this list; the elements at
     * positions <code>index + 1</code> and above are moved one position to the
     * left.
     *
     * @param index the index of the element to omit
     * @return the result list
     * @throws IndexOutOfBoundsException if <code>index < 0</code> or
     * <code>index >= size()</code>
     */
    FList<Elt> less(int index);

    /**
     * Returns the concatenation of this list with the argument list.  The size is
     * the sum of the sizes of this list and <code>list</code>; the elements of this
     * list appear first, followed by the elements of <code>list</code>.
     *
     * @param list the list to be concatenated
     * @return the result list
     */
    FList<Elt> concat(List<? extends Elt> list);

    /**
     * Returns this list in reverse order.
     *
     * @return the reversed list
     */
    FList<Elt> reverse();

    /**
     * Returns the portion of this list between position <code>fromIndex</code>,
     * inclusive, to position <code>toIndex</code>, exclusive.
     *
     * @throws IndexOutOfBoundsException, IllegalArgumentException
     */
    FList<Elt> subList(int fromIndex, int toIndex);

    /**
     * Returns the portion of this list between position <code>fromIndex</code>,
     * inclusive, to position <code>toIndex</code>, exclusive.
     *
     * <p>This is just like <code>subList</code> except that this method never throws
     * <code>IllegalArgumentException</code>; if the arguments are out of order, it
     * simply returns an empty list. 
     *
     * @throws IndexOutOfBoundsException
     */
    FList<Elt> subseq(int fromIndex, int toIndex);

    /**
     * Returns the initial subsequence of this list, of length <code>len</code>.
     *
     * @throws IndexOutOfBoundsException
     */
    FList<Elt> prefix(int len);

    /**
     * Returns the final subsequence of this list, of length <code>len</code>.  (Compare
     * <code>suffixFrom</code>.)
     *
     * @throws IndexOutOfBoundsException
     */
    FList<Elt> suffix(int len);

    /**
     * Returns the final subsequence of this list, starting at index <code>fromIndex</code>.
     * (Compare <code>suffix</code>.)
     *
     * @throws IndexOutOfBoundsException
     */
    FList<Elt> suffixFrom(int fromIndex);

    /**
     * Returns true iff this list is a prefix of <code>other</code>, that is, if every element
     * of this list occurs in <code>other</code> at the same index (including the case where the
     * lists are equal).
     *
     * Equivalent to <code>other.prefix(size()).equals(this)</code>, but more efficient.
     */
    boolean isPrefix(FList<Elt> other);

    /**
     * Returns true iff this list is a suffix of <code>other</code>, that is, if every element
     * of this list occurs in <code>other</code> at the same distance from the end (including
     * the case where the lists are equal).
     *
     * Equivalent to <code>other.suffix(size()).equals(this)</code>, but more efficient.
     */
    boolean isSuffix(FList<Elt> other);

    /**
     * Returns a new list in which the elements of this list are sorted by their
     * natural ordering.  The elements of this list must implement
     * <code>Comparable</code>, and must be mutually acceptable to one another's
     * <code>compareTo</code> methods.  The sort is stable.
     *
     * @return the sorted list
     */
    FList<Elt> sorted();

    /**
     * Returns a new list in which the elements of this list are sorted according to
     * <code>comp</code>.  The sort is stable.
     *
     * @param comp the comparator to use for sorting
     * @return the sorted list
     */
    FList<Elt> sorted(Comparator<? super Elt> comp);


    /* ======== Deprecated List Methods ========
     *
     * We mark these deprecated to remind people not to use them on an FList.
     */

    @Deprecated
    boolean add(Elt e);

    @Deprecated
    void add(int index, Elt e);

    @Deprecated
    boolean addAll(int index, Collection<? extends Elt> c);

    @Deprecated
    boolean addAll(Collection<? extends Elt> c);

    @Deprecated
    void clear();

    @Deprecated
    Elt remove(int index);

    @Deprecated
    boolean removeRange(int fromIndex, int toIndex);

    @Deprecated
    Elt set(int index, Elt e);
}
