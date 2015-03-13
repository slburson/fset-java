/*
 * BinaryOp.java
 *
 * Copyright (c) 2013, 2014 Scott L. Burson.
 *
 * This file is licensed under the Library GNU Public License (LGPL), v. 2.1.
 */


package com.ergy.fset;

/**
 * A binary operation of type (T, T) -> T.
 */
public interface BinaryOp<T> {

	T apply(T x, T y);

}
