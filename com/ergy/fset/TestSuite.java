/*
 * TestSuite.java
 *
 * Copyright (c) 2013, 2014 Scott L. Burson.
 *
 * This file is licensed under the Library GNU Public License (LGPL), v. 2.1.
 */


package com.ergy.fset;
import java.util.*;
import java.io.*;

public class TestSuite {

    public static void main(String[] args) {
	if (false) {
	    /* Some basic tests to see if anything is working at all */
	    int[] test0 = { 130, 115, 90, 1025, 330, 475, 190, 515, 290, 1770, 1505, 25,
			    180, 115, 325, 660, 1920, 1440, 960, 1280 };
	    FTreeSet<MyInteger> set0 = new FTreeSet<MyInteger>(conv(test0));
	    println(set0.dump());
	    println(set0);
	    int[] test1 = { 215, 775, 180, 625, 1960, 25, 525, 415, 325, 705, 800, 360,
			    485, 270, 1025, 890, 830, 715, 665, 305, 240, 695, 215 };
	    FTreeSet<MyInteger> set1 = new FTreeSet<MyInteger>(conv(test1));
	    println(set1.dump());
	    println(set1);
	    FTreeSet<MyInteger> set0u1 = set0.union(set1);
	    println(set0u1.dump());
	    println(set0u1);
	    FTreeSet<MyInteger> set0i1 = set0.intersection(set1);
	    println(set0i1.dump());
	    println(set0i1);
	    FTreeSet<MyInteger> set0d1 = set0.difference(set1);
	    println(set0d1.dump());
	    println(set0d1);
	}
	if (args.length != 1) {
	    println("Usage: java TestSuite [n_iterations]");
	    exit();
	}
	int n_iterations = Integer.decode(args[0]).intValue();
	// For now, use a fixed seed for repeatability.
	Random rand = new Random(0xdeadbeefcafeL);
	for (int i = 0; i < n_iterations; ++i) {
	    FTreeSet<MyInteger> fts = testFTreeSet(rand, i);
	    FHashSet<MyInteger> fhs = testFHashSet(rand, i);
	    //FSet pchs = testFCachedHashSet(rand, i);
	    testFLinkedHashSet(rand, i);
	    testFLinkedHashMap(rand, i);
	    testFTreeMap(rand, i, fts);
	    testFHashMap(rand, i, fhs);
	    //testFCachedHashMap(rand, i, pchs);
	    testFTreeList(rand, i);
	}
	println("All tests passed.");
    }

    static FTreeSet<MyInteger> testFTreeSet(Random rand, int i) {
	FTreeSet<MyInteger> fts0 = new FTreeSet<MyInteger>(TestComparator.Instance);
	TreeSet<MyInteger> ts0 = new TreeSet<MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = new MyInteger(r);
	    FTreeSet<MyInteger> tmp = fts0.with(R);
	    ts0.add(R);
	    if (!tmp.verify()) {
		println("FTreeSet Verification failure on iteration " + i);
		println(fts0.dump());
		println("Adding " + R);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.size() != ts0.size()) {
		println("FTreeSet size failed on iteration " + i);
		exit();
	    }
	    if (!fts0.isSubset(tmp) || !tmp.isSuperset(fts0) ||
		(!fts0.contains(R) && (tmp.isSubset(fts0) || fts0.isSuperset(tmp)))) {
		println("FTreeSet is{Sub,Super}set failed on iteration " + i);
		println(fts0.isSubset(tmp) + ", " + tmp.isSuperset(fts0) + ", " +
			fts0.contains(R) + ", " + tmp.isSubset(fts0) + ", " +
			fts0.isSuperset(tmp));
		exit();
	    }
	    fts0 = tmp;
	}
	FTreeSet<MyInteger> fts1 = new FTreeSet<MyInteger>(TestComparator.Instance);
	TreeSet<MyInteger> ts1 = new TreeSet<MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = new MyInteger(r);
	    FTreeSet<MyInteger> tmp = fts1.with(R);
	    ts1.add(R);
	    if (!tmp.verify()) {
		println("FTreeSet Verification failure on iteration " + i);
		println(fts1.dump());
		println("Adding " + R);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.size() != ts1.size()) {
		println("FTreeSet size failed on iteration " + i);
		exit();
	    }
	    if (!fts1.isSubset(tmp) || !tmp.isSuperset(fts1) ||
		(!fts1.contains(R) && (tmp.isSubset(fts1) || fts1.isSuperset(tmp)))) {
		println("FTreeSet is{Sub,Super}set failed on iteration " + i);
		println(fts1.isSubset(tmp) + ", " + tmp.isSuperset(fts1) + ", " +
			fts1.contains(R) + ", " + tmp.isSubset(fts1) + ", " +
			fts1.isSuperset(tmp));
		exit();
	    }
	    fts1 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = new MyInteger(r);
	    if (fts0.contains(R) != ts0.contains(R)) {
		println("FTreeSet contains failed (fts0) on iteration " + i);
		exit();
	    }
	    FTreeSet<MyInteger> tmp = fts0.less(R);
	    ts0.remove(R);
	    if (!tmp.verify()) {
		println("FTreeSet Verification failure on iteration " + i);
		println(fts0.dump());
		println("Removing " + R);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.size() != ts0.size()) {
		println("FTreeSet size failed on iteration " + i);
		exit();
	    }
	    fts0 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = new MyInteger(r);
	    if (fts1.contains(R) != ts1.contains(R)) {
		println("FTreeSet contains failed (fts1) on iteration " + i);
		exit();
	    }
	    FTreeSet<MyInteger> tmp = fts1.less(R);
	    ts1.remove(R);
	    if (!tmp.verify()) {
		println("FTreeSet Verification failure on iteration " + i);
		println(fts1.dump());
		println("Removing " + R);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.size() != ts1.size()) {
		println("FTreeSet size failed on iteration " + i);
		exit();
	    }
	    fts1 = tmp;
	}
	if (i == 0) {
	    FTreeSet<MyInteger> tmp = fts0.with(null);
	    if (!tmp.verify() || !tmp.contains(null) || tmp.first() != null) {
		println("FTreeSet Verification failure on iteration " + i);
		println(fts0.dump());
		println("Adding null");
		println(tmp.dump());
		exit();
	    }
	    tmp = tmp.less(null);
	    if (!tmp.verify() || tmp.contains(null)) {
		println("FTreeSet Verification failure on iteration " + i);
		println(fts0.dump());
		println("Removing null");
		println(tmp.dump());
		exit();
	    }
	}		
	if (fts0.hashCode() != ts0.hashCode()) {
	    println("FTreeSet hashCode failed on fts0 on iteration " + i);
	    println(fts0);
	    println(ts0);
	    exit();
	}
	if (fts1.hashCode() != ts1.hashCode()) {
	    println("FTreeSet hashCode failed on fts1 on iteration " + i);
	    exit();
	}
	if (!fts0.equals(ts0)) {
	    println("FTreeSet Equality failed (fts0, A) on iteration " + i);
	    exit();
	}
	if (!fts0.equals(new FTreeSet<MyInteger>(ts0))) {
	    println("FTreeSet Equality failed (fts0, B) on iteration " + i);
	    exit();
	}
	if (!fts0.equals(new FTreeSet<MyInteger>(ts0, TestComparator.Instance))) {
	    println("FTreeSet Equality failed (fts0, C) on iteration " + i);
	    println(fts0);
	    FTreeSet<MyInteger> nfts0 = new FTreeSet<MyInteger>(ts0, TestComparator.Instance);
	    println(nfts0);
	    println(nfts0.dump());
	    exit();
	}
	if (!fts0.equals(new FTreeSet<MyInteger>(new ArrayList<MyInteger>(ts0)))) {
	    println("FTreeSet construction from ArrayList failed (fts0) on iteration "
		    + i);
	    exit();
	}
	if (!fts0.equals(new FTreeSet<MyInteger>(ts0.toArray(new MyInteger[0])))) {
	    println("FTreeSet construction from array failed (fts0) on iteration "
		    + i);
	    exit();
	}
	if (!fts1.equals(ts1)) {
	    println("FTreeSet Equality failed (fts1, A) on iteration " + i);
	    exit();
	}
	// Next line also tests constructor from `MyInteger[]'
	if (!fts1.equals(new FTreeSet<MyInteger>(ts1.toArray(new MyInteger[0])))) {
	    println("FTreeSet Equality failed (fts1, B) on iteration " + i);
	    exit();
	}
	// Next line also tests constructor from `MyInteger[]'
	if (!fts1.equals(new FTreeSet<MyInteger>(TestComparator.Instance,
						 ts1.toArray(new MyInteger[0])))) {
	    println("FTreeSet Equality failed (fts1, C) on iteration " + i);
	    exit();
	}
	if (fts0.first().intValue() / 2 != ts0.first().intValue() / 2) {
	    println("FTreeSet `first' failed (fts0) on iteration " + i);
	    exit();
	}
	if (fts1.first().intValue() / 2 != ts1.first().intValue() / 2) {
	    println("FTreeSet `first' failed (fts1) on iteration " + i);
	    exit();
	}
	if (fts0.last().intValue() / 2 != ts0.last().intValue() / 2) {
	    println("FTreeSet `last' failed (fts0) on iteration " + i);
	    exit();
	}
	if (fts1.last().intValue() / 2 != ts1.last().intValue() / 2) {
	    println("FTreeSet `last' failed (fts1) on iteration " + i);
	    exit();
	}
	FTreeSet<MyInteger> ftsu = fts0.union(fts1);
	TreeSet<MyInteger> tsu = (TreeSet<MyInteger>)ts0.clone();
	tsu.addAll(ts1);
	if (!((FTreeSet<MyInteger>)ftsu).verify() || !ftsu.equals(tsu)) {
	    println("FTreeSet Union failed on iteration " + i);
	    println(fts0);
	    println(fts1);
	    if (!ftsu.verify())
		println(ftsu.dump());
	    println(ftsu.size() + ", " + tsu.size());
	    println(ftsu);
	    println(tsu);
	    exit();
	}
	if (!ftsu.equals(new FTreeSet<MyInteger>(tsu))) {
	    println("FTreeSet Equality failed (ftsu) on iteration " + i);
	}
	FTreeSet<MyInteger> ftsi = fts0.intersection(fts1);
	TreeSet<MyInteger> tsi = (TreeSet<MyInteger>)ts0.clone();
	tsi.retainAll(ts1);
	if (!ftsi.verify() || !ftsi.equals(tsi)) {
	    println("FTreeSet Intersection failed on iteration " + i);
	    println(fts0);
	    println(fts1);
	    if (!ftsi.verify())
		println(ftsi.dump());
	    println(ftsi.size() + ", " + tsi.size());
	    println(ftsi);
	    println(tsi);
	    exit();
	}
	if (!ftsi.isSubset(fts0) || !ftsi.isSubset(fts1)) {
	    println("FTreeSet isSubset failed on iteration " + i);
	    exit();
	}
	if (!ftsi.equals(new FTreeSet<MyInteger>(tsi))) {
	    println("FTreeSet Equality failed (ftsi) on iteration " + i);
	}
	FTreeSet<MyInteger> ftsd = fts0.difference(fts1);
	TreeSet<MyInteger> tsd = (TreeSet<MyInteger>)ts0.clone();
	tsd.removeAll(ts1);
	if (!ftsd.verify() || !ftsd.equals(tsd)) {
	    println("FTreeSet Difference failed on iteration " + i);
	    println(fts0);
	    println(fts0.dump());
	    println(fts1);
	    println(fts1.dump());
	    //if (!((FTreeSet)ftsd).verify())
	    println(ftsd.size() + ", " + tsd.size());
	    println(ftsd);
	    println(ftsd.dump());
	    println(tsd);
	    exit();
	}
	if (!ftsd.equals(new FTreeSet<MyInteger>(tsd))) {
	    println("FTreeSet Equality failed (ftsd) on iteration " + i);
	}
	FTreeSet<MyInteger> nfts0 = new FTreeSet<MyInteger>(fts0, TestComparator.Instance);
	nfts0 = nfts0.less(pick(rand, nfts0));
	FTreeSet<MyInteger> fts0a = fts0.less(pick(rand, fts0));
	if (sgn(fts0a.compareTo(nfts0)) != compare(fts0a, nfts0)) {
	    println("FTreeSet Compare failed (fts0) on iteration " + i);
	    println(fts0a.dump());
	    println(nfts0.dump());
	    println(fts0a);
	    println(nfts0);
	    println(fts0a.compareTo(nfts0));
	    println(compare(fts0a, nfts0));
	    exit();
	}
	if (fts0a.equals(nfts0) != equals(fts0a, nfts0)) {
	    println("FTreeSet equality failed (fts0a) on iteration " + i);
	    exit();
	}
	FTreeSet<MyInteger> nfts1 = new FTreeSet<MyInteger>(fts1, TestComparator.Instance);
	nfts1 = nfts1.less(pick(rand, nfts1));
	FTreeSet<MyInteger> fts1a = fts1.less(pick(rand, fts1));
	if (sgn(fts1a.compareTo(nfts1)) != compare(fts1a, nfts1)) {
	    println("FTreeSet Compare failed (fts1) on iteration " + i);
	    println(fts1a.dump());
	    println(nfts1.dump());
	    println(fts1a.compareTo(nfts1));
	    println(compare(fts1a, nfts1));
	    exit();
	}
	if (fts1a.equals(nfts1) != equals(fts1a, nfts1)) {
	    println("FTreeSet equality failed (fts1a) on iteration " + i);
	    exit();
	}
	int lo = rand.nextInt(150) - 25;
	int hi = rand.nextInt(125 - lo) + lo;
	lo *= 2;	// they have to be even because of the comparator behavior
	hi *= 2;
	MyInteger Lo = new MyInteger(lo);
	MyInteger Hi = new MyInteger(hi);
	SortedSet<MyInteger> ftss = fts0.subSet(Lo, Hi);
	SortedSet<MyInteger> tss = ts0.subSet(Lo, Hi);
	if (!ftss.equals(tss)) {
	    println("FTreeSet subSet failed on iteration " + i);
	    println("[" + lo + ", " + hi + ")");
	    println(ftss);
	    println(tss);
	    exit();
	}
	if (!fts0.headSet(Hi).equals(ts0.headSet(Hi))) {
	    println("FTreeSet headSet failed on iteration " + i);
	    exit();
	}
	if (!fts0.tailSet(Lo).equals(ts0.tailSet(Lo))) {
	    println("FTreeSet tailSet failed on iteration " + i);
	    exit();
	}
	while (!fts0.isEmpty()) {
	    MyInteger x = fts0.arb();
	    if (!fts0.contains(x) || !ts0.contains(x)) {
		println("FTreeSet arb/contains failed on iteration " + i);
		exit();
	    }
	    fts0 = fts0.less(x);
	    ts0.remove(x);
	    if (ts0.isEmpty() != fts0.isEmpty()) {
		println("FTreeSet less/isEmpty failed on iteration " + i);
		exit();
	    }
	}
	if (i % 50 == 0) {
	    // Check handling of null set
	    try {
		FSet<MyInteger> ftsser = (i == 0 ? fts0 : fts1);
		FileOutputStream fos = new FileOutputStream("fts.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(ftsser);
		oos.close();
		FileInputStream fis = new FileInputStream("fts.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FSet<MyInteger> nftsser = (FSet<MyInteger>)ois.readObject();
		ois.close();
		if (!ftsser.equals(nftsser) || ftsser.hashCode() != nftsser.hashCode()) {
		    println("FTreeSet read/write failed on iteration " + i);
		    exit();
		}
	    } catch (IOException e) {
		println("FTreeSet read/write: exception " + e);
		exit();
	    } catch (ClassNotFoundException e) {
		println("FTreeSet read/write: exception " + e);
	    }
	}
	return fts1;
    }

    static FHashSet<MyInteger> testFHashSet(Random rand, int i) {
	FHashSet<MyInteger> fhs0 = new FHashSet<MyInteger>();
	HashSet<MyInteger> hs0 = new HashSet<MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FHashSet<MyInteger> tmp = fhs0.with(R);
	    hs0.add(R);
	    if (!tmp.verify()) {
		println("FHashSet Verification failure on iteration " + i);
		println(fhs0.dump());
		println("Adding " + (R == null ? "null" : "" + R));
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != hs0.hashCode()) {
		println("FHashSet hashCode failed on fhs0 on iteration " + i);
		println(tmp);
		println(hs0);
		println("Adding " + R + "; " + tmp.hashCode() + ", " + hs0.hashCode());
		exit();
	    }
	    if (!fhs0.isSubset(tmp) || !tmp.isSuperset(fhs0) ||
		(!fhs0.contains(R) && (tmp.isSubset(fhs0) || fhs0.isSuperset(tmp)))) {
		println("FHashSet is{Sub,Super}set failed (fhs0) on iteration " + i);
		println(fhs0.isSubset(tmp) + ", " + tmp.isSuperset(fhs0) + ", " +
			fhs0.contains(R) + ", " + tmp.isSubset(fhs0) + ", " +
			fhs0.isSuperset(tmp) + "; " + R);
		println(fhs0);
		println(tmp);
		//FHashSet.debug = true;
		//fhs0.isSubset(tmp);
		exit();
	    }
	    fhs0 = tmp;
	}
	FHashSet<MyInteger> fhs1 = new FHashSet<MyInteger>();
	HashSet<MyInteger> hs1 = new HashSet<MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FHashSet<MyInteger> tmp = fhs1.with(R);
	    hs1.add(R);
	    if (!tmp.verify()) {
		println("FHashSet Verification failure on iteration " + i);
		println(fhs1.dump());
		println("Adding " + R);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != hs1.hashCode()) {
		println("FHashSet hashCode failed on fhs1 on iteration " + i);
		println(tmp);
		println(hs1);
		println("Adding " + R + "; " + tmp.hashCode() + ", " + hs1.hashCode());
		exit();
	    }
	    if (!fhs1.isSubset(tmp) || !tmp.isSuperset(fhs1) ||
		(!fhs1.contains(R) && (tmp.isSubset(fhs1) || fhs1.isSuperset(tmp)))) {
		println("FHashSet is{Sub,Super}set failed (fhs1) on iteration " + i);
		println(fhs1.isSubset(tmp) + ", " + tmp.isSuperset(fhs1) + ", " +
			fhs1.contains(R) + ", " + tmp.isSubset(fhs1) + ", " +
			fhs1.isSuperset(tmp) + "; " + R);
		println(fhs1);
		println(tmp);
		exit();
	    }
	    fhs1 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FHashSet<MyInteger> tmp = fhs0.less(R);
	    hs0.remove(R);
	    if (!tmp.verify()) {
		println("FHashSet Verification failure on iteration " + i);
		println(fhs0.dump());
		println("Removing " + (R == null ? "null" : "" + R));
		println(tmp.dump());
		exit();
	    }
	    fhs0 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FHashSet<MyInteger> tmp = fhs1.less(R);
	    hs1.remove(R);
	    if (!tmp.verify()) {
		println("FHashSet Verification failure on iteration " + i);
		println(fhs1.dump());
		println("Removing " + (R == null ? "null" : "" + R));
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != hs1.hashCode()) {
		println("FHashSet hashCode failed on fhs1 on iteration " + i);
		println(tmp);
		println(hs1);
		println("Removing " + R + "; " + tmp.hashCode() + ", " + hs1.hashCode());
		exit();
	    }
	    if (!tmp.equals(hs1)) {
		println("FHashSet equality failed on fhs1 on iteration " + i);
		println(tmp);
		println(hs1);
		println(new FHashSet<MyInteger>(hs1));
		println("Removing " + R + "; " + tmp.hashCode() + ", " + hs1.hashCode());
		exit();
	    }
	    fhs1 = tmp;
	}
	if (fhs0.hashCode() != hs0.hashCode()) {
	    println("FHashSet hashCode failed on fhs0 on iteration " + i);
	    println(fhs0);
	    println(hs0);
	    exit();
	}
	if (fhs1.hashCode() != hs1.hashCode()) {
	    println("FHashSet hashCode failed on fhs1 on iteration " + i);
	    exit();
	}
	if (!fhs0.equals(hs0)) {
	    println("FHashSet Equality failed (fhs0, A) on iteration " + i);
	    println(fhs0);
	    println(fhs0.dump());
	    println(new TreeSet<MyInteger>(hs0));
	    exit();
	}
	if (!fhs0.equals(new FHashSet<MyInteger>(hs0))) {
	    println("FHashSet Equality failed (fhs0, B) on iteration " + i);
	    println(fhs0);
	    println(fhs0.dump());
	    FHashSet<MyInteger> nfhs0 = new FHashSet<MyInteger>(hs0);
	    println(nfhs0);
	    println(nfhs0.dump());
	    exit();
	}
	if (!fhs0.equals(new FHashSet<MyInteger>(new ArrayList<MyInteger>(hs0)))) {
	    println("FHashSet construction from ArrayList failed (fhs0) on iteration " + i);
	    exit();
	}
	if (!fhs0.equals(new FHashSet<MyInteger>(hs0.toArray(new MyInteger[0])))) {
	    println("FHashSet construction from array failed (fhs0) on iteration " + i);
	    exit();
	}
	if (!fhs1.equals(hs1)) {
	    println("FHashSet Equality failed (fhs1, A) on iteration " + i);
	    println(fhs1);
	    println(hs1);
	    println(new FHashSet<MyInteger>(hs1));
	    exit();
	}
	// Next line also tests constructor from `Object[]'
	if (!fhs1.equals(new FHashSet<MyInteger>(hs1.toArray(new MyInteger[0])))) {
	    println("FHashSet Equality failed (fhs1, B) on iteration " + i);
	    exit();
	}
	FHashSet<MyInteger> fhsu = fhs0.union(fhs1);
	HashSet<MyInteger> hsu = (HashSet<MyInteger>)hs0.clone();
	hsu.addAll(hs1);
	if (!fhsu.verify() || !fhsu.equals(hsu)) {
	    println("FHashSet Union failed on iteration " + i);
	    println(fhs0);
	    println(fhs1);
	    if (!fhsu.verify()) println(fhsu.dump());
	    println(fhsu.size() + ", " + hsu.size());
	    println(fhsu);
	    println(hsu);
	    exit();
	}
	if (!fhsu.equals(new FHashSet<MyInteger>(hsu))) {
	    println("FHashSet Equality failed (fhsu) on iteration " + i);
	}
	FHashSet<MyInteger> fhsi = fhs0.intersection(fhs1);
	HashSet<MyInteger> hsi = (HashSet<MyInteger>)hs0.clone();
	hsi.retainAll(hs1);
	if (!fhsi.verify() || !fhsi.equals(hsi)) {
	    println("FHashSet Intersection failed on iteration " + i);
	    println(fhs0);
	    println(fhs0.dump());
	    println(fhs1);
	    println(fhs1.dump());
	    if (!fhsi.verify()) println(fhsi.dump());
	    println(fhsi.size() + ", " + hsi.size());
	    println(fhsi);
	    println(new TreeSet<MyInteger>(hsi));
	    exit();
	}
	if (!fhsi.isSubset(fhs0) || !fhsi.isSubset(fhs1)) {
	    println("FHashSet isSubset failed on iteration " + i);
	    exit();
	}
	if (!fhsi.equals(new FHashSet<MyInteger>(hsi))) {
	    println("FHashSet Equality failed (fhsi) on iteration " + i);
	}
	FHashSet<MyInteger> fhsd = fhs0.difference(fhs1);
	HashSet<MyInteger> hsd = (HashSet<MyInteger>)hs0.clone();
	hsd.removeAll(hs1);
	if (!fhsd.verify() || !fhsd.equals(hsd)) {
	    println("FHashSet Difference failed on iteration " + i);
	    println(fhs0);
	    println((fhs0).dump());
	    println(fhs1);
	    println(fhs1.dump());
	    //if (!fhsd.verify())
	    println(fhsd.size() + ", " + hsd.size());
	    println(fhsd);
	    println(fhsd.dump());
	    println(hsd);
	    exit();
	}
	if (!fhsd.equals(new FHashSet<MyInteger>(hsd))) {
	    println("FHashSet Equality failed (fhsd) on iteration " + i);
	}
	FHashSet<MyInteger> nfhs0 = new FHashSet<MyInteger>(fhs0);
	nfhs0 = nfhs0.less(pick(rand, nfhs0));
	FHashSet<MyInteger> fhs0a = fhs0.less(pick(rand, fhs0));
	if (sgn(fhs0a.compareTo(nfhs0)) != compare(fhs0a, nfhs0)) {
	    println("FHashSet Compare failed (fhs0) on iteration " + i);
	    println(fhs0a.compareTo(nfhs0) + ", " + compare(fhs0a, nfhs0));
	    println(fhs0a);
	    println(fhs0a.dump());
	    println(nfhs0);
	    println(nfhs0.dump());
	    exit();
	}
	if (fhs0a.equals(nfhs0) != equals(fhs0a, nfhs0)) {
	    println("FHashSet equality failed (fhs0a) on iteration " + i);
	    exit();
	}
	FHashSet<MyInteger> nfhs1 = new FHashSet<MyInteger>(fhs1);
	nfhs1 = nfhs1.less(pick(rand, nfhs1));
	FHashSet<MyInteger> fhs1a = fhs1.less(pick(rand, fhs1));
	if (sgn(fhs1a.compareTo(nfhs1)) != compare(fhs1a, nfhs1)) {
	    println("FHashSet Compare failed (fhs1) on iteration " + i);
	    exit();
	}
	if (fhs1a.equals(nfhs1) != equals(fhs1a, nfhs1)) {
	    println("FHashSet equality failed (fhs1a) on iteration " + i);
	    exit();
	}
	while (!fhs0.isEmpty()) {
	    MyInteger x = fhs0.arb();
	    if (!fhs0.contains(x) || !hs0.contains(x)) {
		println("FHashSet arb/contains failed on iteration " + i);
		exit();
	    }
	    fhs0 = fhs0.less(x);
	    hs0.remove(x);
	    if (hs0.isEmpty() != fhs0.isEmpty()) {
		println("FHashSet less/isEmpty failed on iteration " + i);
		exit();
	    }
	}
	if (i % 50 == 0) {
	    try {
		// Check handling of null set
		FSet<MyInteger> fhsser = (i == 0 ? fhs0 : fhs1);
		FileOutputStream fos = new FileOutputStream("fhs.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(fhsser);
		oos.close();
		FileInputStream fis = new FileInputStream("fhs.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FSet<MyInteger> nfhsser = (FSet<MyInteger>)ois.readObject();
		ois.close();
		if (!fhsser.equals(nfhsser) || fhsser.hashCode() != nfhsser.hashCode()) {
		    println("FHashSet read/write failed on iteration " + i);
		    exit();
		}
	    } catch (IOException e) {
		println("FHashSet read/write: exception " + e);
		exit();
	    } catch (ClassNotFoundException e) {
		println("FHashSet read/write: exception " + e);
	    }
	}
	return fhs1;
    }

    static void testFLinkedHashSet(Random rand, int i) {
	// Just some quick sanity checks.  The tricky parts are shared with FHashSet.
	FLinkedHashSet<Integer> flhs0 = new FLinkedHashSet<Integer>();
	LinkedHashSet<Integer> lhs0 = new LinkedHashSet<Integer>();
	for (int j = 0; j < 10; ++j) {
	    int r = rand.nextInt(200);
	    flhs0 = flhs0.with(r);
	    lhs0.add(r);
	}
	if (flhs0.size() != lhs0.size()) {
	    println("FLinkedHashSet size failed on iteration " + i);
	    exit();
	}
	if (!flhs0.equals(lhs0)) {
	    println("FLinkedHashSet equals failed on iteration " + i);
	    exit();
	}
	Iterator<Integer> flhs0_it = flhs0.iterator();
	Iterator<Integer> lhs0_it = lhs0.iterator();
	while (flhs0_it.hasNext()) {
	    if (!lhs0_it.hasNext() || !flhs0_it.next().equals(lhs0_it.next())) {
		println("FLinkedHashSet ordering failed on iteration " + i);
		exit();
	    }
	}
	if (lhs0_it.hasNext()) {
	    println("FLinkedHashSet ordering failed on iteration " + i);
	    exit();
	}
	FLinkedHashSet<Integer> flhs1 = new FLinkedHashSet<Integer>();
	LinkedHashSet<Integer> lhs1 = new LinkedHashSet<Integer>();
	for (int j = 0; j < 10; ++j) {
	    int r = rand.nextInt(200);
	    flhs1 = flhs1.with(r);
	    lhs1.add(r);
	}
	flhs0 = flhs0.union(flhs1);
	lhs0.addAll(lhs1);
	if (!flhs0.equals(lhs0)) {
	    println("FLinkedHashSet equals failed on iteration " + i);
	    exit();
	}
	flhs0_it = flhs0.iterator();
	lhs0_it = lhs0.iterator();
	while (flhs0_it.hasNext()) {
	    if (!lhs0_it.hasNext() || !flhs0_it.next().equals(lhs0_it.next())) {
		println("FLinkedHashSet ordering failed on iteration " + i);
		exit();
	    }
	}
	if (lhs0_it.hasNext()) {
	    println("FLinkedHashSet ordering failed on iteration " + i);
	    exit();
	}
	if (i % 50 == 0) {
	    try {
		FSet<Integer> fhsser = flhs0;
		FileOutputStream fos = new FileOutputStream("flhs.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(fhsser);
		oos.close();
		FileInputStream fis = new FileInputStream("flhs.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FSet<Integer> nfhsser = (FSet<Integer>)ois.readObject();
		ois.close();
		if (!fhsser.equals(nfhsser) || fhsser.hashCode() != nfhsser.hashCode()) {
		    println("FLinkedHashSet read/write failed on iteration " + i);
		    exit();
		}
		flhs0_it = flhs0.iterator();
		lhs0_it = lhs0.iterator();
		while (flhs0_it.hasNext()) {
		    if (!lhs0_it.hasNext() || !flhs0_it.next().equals(lhs0_it.next())) {
			println("FLinkedHashSet ordering failed on iteration " + i);
			exit();
		    }
		}
		if (lhs0_it.hasNext()) {
		    println("FLinkedHashSet ordering failed on iteration " + i);
		    exit();
		}
	    } catch (IOException e) {
		println("FLinkedHashSet read/write: exception " + e);
		exit();
	    } catch (ClassNotFoundException e) {
		println("FLinkedHashSet read/write: exception " + e);
	    }
	}
    }

/********
    static FSet testFCachedHashSet(Random rand, int i) {
	FSet fhs0 = new FCachedHashSet();
	HashSet hs0 = new HashSet();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FSet tmp = fhs0.with(R);
	    hs0.add(R);
	    if (!((FCachedHashSet)tmp).verify()) {
		println("FCachedHashSet Verification failure on iteration " + i);
		println(((FCachedHashSet)fhs0).dump());
		println("Adding " + (R == null ? "null" : "" + R));
		println(((FCachedHashSet)tmp).dump());
		exit();
	    }
	    if (tmp.hashCode() != hs0.hashCode()) {
		println("FCachedHashSet hashCode failed on fhs0 on iteration " + i);
		println(tmp);
		println(hs0);
		println("Adding " + R + "; " + tmp.hashCode() + ", " + hs0.hashCode());
		exit();
	    }
	    if (!fhs0.isSubset(tmp) || !tmp.isSuperset(fhs0) ||
		(!fhs0.contains(R) && (tmp.isSubset(fhs0) || fhs0.isSuperset(tmp)))) {
		println("FCachedHashSet is{Sub,Super}set failed (fhs0) on iteration " + i);
		println(fhs0.isSubset(tmp) + ", " + tmp.isSuperset(fhs0) + ", " +
			fhs0.contains(R) + ", " + tmp.isSubset(fhs0) + ", " +
			fhs0.isSuperset(tmp) + "; " + R);
		println(fhs0);
		println(tmp);
		//FCachedHashSet.debug = true;
		//fhs0.isSubset(tmp);
		exit();
	    }
	    fhs0 = tmp;
	}
	FSet fhs1 = new FCachedHashSet();
	HashSet hs1 = new HashSet();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FSet tmp = fhs1.with(R);
	    hs1.add(R);
	    if (!((FCachedHashSet)tmp).verify()) {
		println("FCachedHashSet Verification failure on iteration " + i);
		println(((FCachedHashSet)fhs1).dump());
		println("Adding " + R);
		println(((FCachedHashSet)tmp).dump());
		exit();
	    }
	    if (tmp.hashCode() != hs1.hashCode()) {
		println("FCachedHashSet hashCode failed on fhs1 on iteration " + i);
		println(tmp);
		println(hs1);
		println("Adding " + R + "; " + tmp.hashCode() + ", " + hs1.hashCode());
		exit();
	    }
	    if (!fhs1.isSubset(tmp) || !tmp.isSuperset(fhs1) ||
		(!fhs1.contains(R) && (tmp.isSubset(fhs1) || fhs1.isSuperset(tmp)))) {
		println("FCachedHashSet is{Sub,Super}set failed (fhs1) on iteration " + i);
		println(fhs1.isSubset(tmp) + ", " + tmp.isSuperset(fhs1) + ", " +
			fhs1.contains(R) + ", " + tmp.isSubset(fhs1) + ", " +
			fhs1.isSuperset(tmp) + "; " + R);
		println(fhs1);
		println(tmp);
		exit();
	    }
	    fhs1 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FSet tmp = fhs0.less(R);
	    hs0.remove(R);
	    if (!((FCachedHashSet)tmp).verify()) {
		println("FCachedHashSet Verification failure on iteration " + i);
		println(((FCachedHashSet)fhs0).dump());
		println("Removing " + (R == null ? "null" : "" + R));
		println(((FCachedHashSet)tmp).dump());
		exit();
	    }
	    fhs0 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FSet tmp = fhs1.less(R);
	    hs1.remove(R);
	    if (!((FCachedHashSet)tmp).verify()) {
		println("FCachedHashSet Verification failure on iteration " + i);
		println(((FCachedHashSet)fhs1).dump());
		println("Removing " + (R == null ? "null" : "" + R));
		println(((FCachedHashSet)tmp).dump());
		exit();
	    }
	    if (tmp.hashCode() != hs1.hashCode()) {
		println("FCachedHashSet hashCode failed on fhs1 on iteration " + i);
		println(tmp);
		println(hs1);
		println("Removing " + R + "; " + tmp.hashCode() + ", " + hs1.hashCode());
		exit();
	    }
	    if (!tmp.equals(hs1)) {
		println("FCachedHashSet equality failed on fhs1 on iteration " + i);
		println(tmp);
		println(hs1);
		println(new FCachedHashSet(hs1));
		println("Removing " + R + "; " + tmp.hashCode() + ", " + hs1.hashCode());
		exit();
	    }
	    fhs1 = tmp;
	}
	if (fhs0.hashCode() != hs0.hashCode()) {
	    println("FCachedHashSet hashCode failed on fhs0 on iteration " + i);
	    println(fhs0);
	    println(hs0);
	    exit();
	}
	if (fhs1.hashCode() != hs1.hashCode()) {
	    println("FCachedHashSet hashCode failed on fhs1 on iteration " + i);
	    exit();
	}
	if (!fhs0.equals(hs0)) {
	    println("FCachedHashSet Equality failed (fhs0, A) on iteration " + i);
	    println(fhs0);
	    println(((FCachedHashSet)fhs0).dump());
	    println(new TreeSet(hs0));
	    exit();
	}
	if (!fhs0.equals(new FCachedHashSet(hs0))) {
	    println("FCachedHashSet Equality failed (fhs0, B) on iteration " + i);
	    println(fhs0);
	    println(((FCachedHashSet)fhs0).dump());
	    FCachedHashSet nfhs0 = new FCachedHashSet(hs0);
	    println(nfhs0);
	    println(nfhs0.dump());
	    exit();
	}
	if (!fhs0.equals(new FCachedHashSet(new ArrayList(hs0)))) {
	    println("FCachedHashSet construction from ArrayList failed (fhs0) on iteration "
		    + i);
	    exit();
	}
	if (!fhs0.equals(new FCachedHashSet(hs0.toArray()))) {
	    println("FCachedHashSet construction from array failed (fhs0) on iteration "
		    + i);
	    exit();
	}
	if (!fhs1.equals(hs1)) {
	    println("FCachedHashSet Equality failed (fhs1, A) on iteration " + i);
	    println(fhs1);
	    println(hs1);
	    println(new FCachedHashSet(hs1));
	    exit();
	}
	// Next line also tests constructor from `Object[]'
	if (!fhs1.equals(new FCachedHashSet(hs1.toArray()))) {
	    println("FCachedHashSet Equality failed (fhs1, B) on iteration " + i);
	    exit();
	}
	FSet fhsu = fhs0.union(fhs1);
	HashSet hsu = (HashSet)hs0.clone();
	hsu.addAll(hs1);
	if (!((FCachedHashSet)fhsu).verify() || !fhsu.equals(hsu)) {
	    println("FCachedHashSet Union failed on iteration " + i);
	    println(fhs0);
	    println(fhs1);
	    if (!((FCachedHashSet)fhsu).verify())
		println(((FCachedHashSet)fhsu).dump());
	    println(fhsu.size() + ", " + hsu.size());
	    println(fhsu);
	    println(hsu);
	    exit();
	}
	if (!fhsu.equals(new FCachedHashSet(hsu))) {
	    println("FCachedHashSet Equality failed (fhsu) on iteration " + i);
	}
	FSet fhsi = fhs0.intersection(fhs1);
	HashSet hsi = (HashSet)hs0.clone();
	hsi.retainAll(hs1);
	if (!((FCachedHashSet)fhsi).verify() || !fhsi.equals(hsi)) {
	    println("FCachedHashSet Intersection failed on iteration " + i);
	    println(fhs0);
	    println(((FCachedHashSet)fhs0).dump());
	    println(fhs1);
	    println(((FCachedHashSet)fhs1).dump());
	    if (!((FCachedHashSet)fhsi).verify())
		println(((FCachedHashSet)fhsi).dump());
	    println(fhsi.size() + ", " + hsi.size());
	    println(fhsi);
	    println(new TreeSet(hsi));
	    exit();
	}
	if (!fhsi.isSubset(fhs0) || !fhsi.isSubset(fhs1)) {
	    println("FCachedHashSet isSubset failed on iteration " + i);
	    exit();
	}
	if (!fhsi.equals(new FCachedHashSet(hsi))) {
	    println("FCachedHashSet Equality failed (fhsi) on iteration " + i);
	}
	FSet fhsd = fhs0.difference(fhs1);
	HashSet hsd = (HashSet)hs0.clone();
	hsd.removeAll(hs1);
	if (!((FCachedHashSet)fhsd).verify() || !fhsd.equals(hsd)) {
	    println("FCachedHashSet Difference failed on iteration " + i);
	    println(fhs0);
	    println(((FCachedHashSet)fhs0).dump());
	    println(fhs1);
	    println(((FCachedHashSet)fhs1).dump());
	    //if (!((FCachedHashSet)fhsd).verify())
	    println(fhsd.size() + ", " + hsd.size());
	    println(fhsd);
	    println(((FCachedHashSet)fhsd).dump());
	    println(hsd);
	    exit();
	}
	if (!fhsd.equals(new FCachedHashSet(hsd))) {
	    println("FCachedHashSet Equality failed (fhsd) on iteration " + i);
	}
	FSet nfhs0 = new FCachedHashSet(fhs0);
	nfhs0 = nfhs0.less(pick(rand, nfhs0));
	FSet fhs0a = fhs0.less(pick(rand, fhs0));
	if (sgn(((FCachedHashSet)fhs0a).compareTo(nfhs0)) !=
	      compare(fhs0a, nfhs0)) {
	    println("FCachedHashSet Compare failed (fhs0) on iteration " + i);
	    println(((FCachedHashSet)fhs0a).compareTo(nfhs0) + ", " +
		    compare(fhs0a, nfhs0));
	    println(fhs0a);
	    println(((FCachedHashSet)fhs0a).dump());
	    println(nfhs0);
	    println(((FCachedHashSet)nfhs0).dump());
	    exit();
	}
	if (fhs0a.equals(nfhs0) != equals(fhs0a, nfhs0)) {
	    println("FCachedHashSet equality failed (fhs0a) on iteration " + i);
	    exit();
	}
	FSet nfhs1 = new FCachedHashSet(fhs1);
	nfhs1 = nfhs1.less(pick(rand, nfhs1));
	FSet fhs1a = fhs1.less(pick(rand, fhs1));
	if (sgn(((FCachedHashSet)fhs1a).compareTo(nfhs1)) !=
	      compare(fhs1a, nfhs1)) {
	    println("FCachedHashSet Compare failed (fhs1) on iteration " + i);
	    exit();
	}
	if (fhs1a.equals(nfhs1) != equals(fhs1a, nfhs1)) {
	    println("FCachedHashSet equality failed (fhs1a) on iteration " + i);
	    exit();
	}
	while (!fhs0.isEmpty()) {
	    MyInteger x = fhs0.arb();
	    if (!fhs0.contains(x) || !hs0.contains(x)) {
		println("FCachedHashSet arb/contains failed on iteration " + i);
		exit();
	    }
	    fhs0 = fhs0.less(x);
	    hs0.remove(x);
	    if (hs0.isEmpty() != fhs0.isEmpty()) {
		println("FCachedHashSet less/isEmpty failed on iteration " + i);
		exit();
	    }
	}
	if (i % 50 == 0) {
	    try {
		// Check handling of null set
		FSet fhsser = (i == 0 ? fhs0 : fhs1);
		FileOutputStream fos = new FileOutputStream("fhs.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(fhsser);
		oos.close();
		FileInputStream fis = new FileInputStream("fhs.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FSet nfhsser = (FSet)ois.readObject();
		ois.close();
		if (!fhsser.equals(nfhsser) || fhsser.hashCode() != nfhsser.hashCode()) {
		    println("FCachedHashSet read/write failed on iteration " + i);
		    exit();
		}
	    } catch (IOException e) {
		println("FCachedHashSet read/write: exception " + e);
		exit();
	    } catch (ClassNotFoundException e) {
		println("FCachedHashSet read/write: exception " + e);
	    }
	}
	return fhs1;
    }
*/

    static void testFTreeMap(Random rand, int i, FTreeSet<MyInteger> set) {
	FTreeMap<MyInteger, MyInteger> ftm0 =
	    new FTreeMap<MyInteger, MyInteger>(TestComparator.Instance);
	TreeMap<MyInteger, MyInteger> tm0 = new TreeMap<MyInteger, MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200), v = rand.nextInt(3);
	    MyInteger R = new MyInteger(r), V = new MyInteger(v);
	    FTreeMap<MyInteger, MyInteger> tmp = ftm0.with(R, V);
	    tm0.put(R, V);
	    if (!tmp.verify()) {
		println("FTreeMap Verification failure on iteration " + i);
		println(ftm0.dump());
		println("Adding " + R + ", " + V);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != tm0.hashCode()) {
		println("FTreeMap hashCode failed on ftm0 on iteration " + i);
		println(ftm0);
		println(ftm0.dump());
		println("Adding " + r + " -> " + v);
		println(tmp.dump());
		println(tm0);
		exit();
	    }
	    ftm0 = tmp;
	}
	FTreeMap<MyInteger, MyInteger> ftm1 = new FTreeMap<MyInteger, MyInteger>(TestComparator.Instance);
	TreeMap<MyInteger, MyInteger> tm1 = new TreeMap<MyInteger, MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200), v = rand.nextInt(3);
	    MyInteger R = new MyInteger(r), V = new MyInteger(v);
	    FTreeMap<MyInteger, MyInteger> tmp = ftm1.with(R, V);
	    tm1.put(R, V);
	    if (!tmp.verify()) {
		println("FTreeMap Verification failure on iteration " + i);
		println(ftm1.dump());
		println("Adding " + R + ", " + V);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != tm1.hashCode()) {
		println("FTreeMap hashCode failed on ftm1 on iteration " + i);
		println(ftm1);
		println(ftm1.dump());
		println("Adding " + r + " -> " + v);
		println(tmp.dump());
		println(tm1);
		exit();
	    }
	    ftm1 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = new MyInteger(r);
	    if (!equals(ftm0.get(R), tm0.get(R))) {
		println("FTreeMap get (ftm0) failed on iteration " + i);
		exit();
	    }
	    FTreeMap<MyInteger, MyInteger> tmp = ftm0.less(R);
	    tm0.remove(R);
	    if (!tmp.verify()) {
		println("FTreeMap Verification failure on iteration " + i);
		println(ftm0.dump());
		println("Removing " + R);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != tm0.hashCode()) {
		println("FTreeMap hashCode failed on ftm0 on iteration " + i);
		println(ftm0);
		println(ftm0.dump());
		println("Removing " + r);
		println(tmp.dump());
		println(tm0);
		exit();
	    }
	    ftm0 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = new MyInteger(r);
	    if (!equals(ftm1.get(R), tm1.get(R))) {
		println("FTreeMap get failed (ftm1) on iteration " + i);
		exit();
	    }
	    FTreeMap<MyInteger, MyInteger> tmp = ftm1.less(R);
	    tm1.remove(R);
	    if (!tmp.verify()) {
		println("FTreeMap Verification failure on iteration " + i);
		println(ftm1.dump());
		println("Removing " + R);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != tm1.hashCode()) {
		println("FTreeMap hashCode failed on ftm1 on iteration " + i);
		println(ftm1);
		println(ftm1.dump());
		println("Removing " + r);
		println(tmp.dump());
		println(tm1);
		exit();
	    }
	    ftm1 = tmp;
	}
	if (i == 0) {
	    FTreeMap<MyInteger, MyInteger> tmp = ftm0.with(null, null);
	    if (!tmp.verify() || !tmp.containsKey(null) || tmp.firstKey() != null) {
		println("FTreeMap Verification failure on iteration " + i);
		println(ftm0.dump());
		println("Adding null");
		println(tmp.dump());
		exit();
	    }
	    tmp = tmp.less(null);
	    if (!tmp.verify() || tmp.containsKey(null)) {
		println("FTreeMap Verification failure on iteration " + i);
		println(ftm0.dump());
		println("Removing null");
		println(tmp.dump());
		exit();
	    }
	}		
	if (ftm0.hashCode() != tm0.hashCode()) {
	    println("FTreeMap hashCode failed on ftm0 on iteration " + i);
	    println(ftm0);
	    println(tm0);
	    exit();
	}
	if (ftm1.hashCode() != tm1.hashCode()) {
	    println("FTreeMap hashCode failed on ftm1 on iteration " + i);
	    exit();
	}
	if (!ftm0.equals(tm0)) {
	    println("FTreeMap Equality failed (ftm0, A) on iteration " + i);
	    println(ftm0.dump());
	    println(tm0);
	    exit();
	}
	if (!ftm0.equals(new FTreeMap<MyInteger, MyInteger>(tm0))) {
	    println("FTreeMap Equality failed (ftm0, B) on iteration " + i);
	    println(ftm0.dump());
	    println(tm0);
	    exit();
	}
	if (!ftm0.equals(new FTreeMap<MyInteger, MyInteger>(tm0, TestComparator.Instance))) {
	    println("FTreeMap Equality failed (ftm0, C) on iteration " + i);
	    println(ftm0.dump());
	    println(tm0);
	    exit();
	}
	if (!ftm1.equals(tm1)) {
	    println("FTreeMap Equality failed (ftm1, A) on iteration " + i);
	    println(ftm1.dump());
	    println(tm1);
	    exit();
	}
	if (!ftm1.equals(new FTreeMap<MyInteger, MyInteger>(tm1))) {
	    println("FTreeMap Equality failed (ftm1, B) on iteration " + i);
	    println(ftm1.dump());
	    exit();
	}
	if (!ftm1.equals(new FTreeMap<MyInteger, MyInteger>(tm1, TestComparator.Instance))) {
	    println("FTreeMap Equality failed (ftm1, C) on iteration " + i);
	    println(ftm1.dump());
	    exit();
	}
	if (ftm0.firstKey().intValue() / 2 != tm0.firstKey().intValue() / 2) {
	    println("FTreeMap `firstKey' failed (ftm0) on iteration " + i);
	    exit();
	}
	if (ftm1.firstKey().intValue() / 2 != tm1.firstKey().intValue() / 2) {
	    println("FTreeMap `firstKey' failed (ftm1) on iteration " + i);
	    exit();
	}
	if (ftm0.lastKey().intValue() / 2 != tm0.lastKey().intValue() / 2) {
	    println("FTreeMap `lastKey' failed (ftm0) on iteration " + i);
	    exit();
	}
	if (ftm1.lastKey().intValue() / 2 != tm1.lastKey().intValue() / 2) {
	    println("FTreeMap `lastKey' failed (ftm1) on iteration " + i);
	    exit();
	}
	FTreeMap<MyInteger, MyInteger> ftmm = ftm0.union(ftm1);
	TreeMap<MyInteger, MyInteger> tmm = (TreeMap<MyInteger, MyInteger>)tm0.clone();
	tmm.putAll(tm1);
	if (!ftmm.verify() || !ftmm.equals(tmm)) {
	    println("FTreeMap Union failed on iteration " + i);
	    println(ftm0);
	    println(ftm0.dump());
	    println(ftm1);
	    println(ftm1.dump());
	    //if (!((FTreeMap)ftmm).verify())
	    println(ftmm.size() + ", " + tmm.size());
	    println(ftmm);
	    println(ftmm.dump());
	    println(tmm);
	    exit();
	}
	if (!ftmm.equals(new FTreeMap<MyInteger, MyInteger>(tmm))) {
	    println("FTreeMap Equality failed (ftmm) on iteration " + i);
	    exit();
	}
	FTreeMap<MyInteger, MyInteger> ftmr = ftm0.restrictedTo(set);
	TreeMap<MyInteger, MyInteger> tmr = (TreeMap<MyInteger, MyInteger>)tm0.clone();
	for (Iterator it = tmr.keySet().iterator(); it.hasNext(); ) {
	    Object k = it.next();
	    if (!set.contains(k)) it.remove();
	}
	if (!ftmr.verify() || !ftmr.equals(tmr)) {
	    println("FTreeMap restrictedTo failed on iteration " + i);
	    exit();
	}
	ftmr = ftm0.restrictedFrom(set);
	tmr = (TreeMap<MyInteger, MyInteger>)tm0.clone();
	for (Iterator it = tmr.keySet().iterator(); it.hasNext(); ) {
	    Object k = it.next();
	    if (set.contains(k)) it.remove();
	}
	if (!ftmr.verify() || !ftmr.equals(tmr)) {
	    println("FTreeMap restrictedFrom failed on iteration " + i);
	    exit();
	}
	ftm0 = ftm0.less(null);		// for benefit of `compare' below
	FSet<MyInteger> ftm0_dom = ftm0.domain();
	FTreeMap<MyInteger, MyInteger> ftm0a =
	    ftm0.less(pick(rand, ftm0_dom)).with(pick(rand, ftm0_dom),
						 new MyInteger(rand.nextInt(3)));
	FTreeMap<MyInteger, MyInteger> ftm0b =
	    ftm0.less(pick(rand, ftm0_dom)).with(pick(rand, ftm0_dom),
						 new MyInteger(rand.nextInt(3)));
	if (sgn(ftm0a.compareTo(ftm0b)) != compare(ftm0a, ftm0b)) {
	    println("FTreeMap Compare failed (ftm0) on iteration " + i);
	    println(ftm0a.dump());
	    println(ftm0b.dump());
	    println(ftm0a);
	    println(ftm0b);
	    println(ftm0a.compareTo(ftm0b));
	    println(compare(ftm0a, ftm0b));
	    exit();
	}
	ftm1 = ftm1.less(null);
	FSet<MyInteger> ftm1_dom = ftm1.domain();
	FTreeMap<MyInteger, MyInteger> ftm1a =
	    ftm1.less(pick(rand, ftm1_dom)).with(pick(rand, ftm1_dom),
						 new MyInteger(rand.nextInt(3)));
	FTreeMap<MyInteger, MyInteger> ftm1b =
	    ftm1.less(pick(rand, ftm1_dom)).with(pick(rand, ftm1_dom),
						 new MyInteger(rand.nextInt(3)));
	if (sgn(ftm1a.compareTo(ftm1b)) != compare(ftm1a, ftm1b)) {
	    println("FTreeMap Compare failed (ftm1) on iteration " + i);
	    println(ftm1a.dump());
	    println(ftm1b.dump());
	    println(ftm1a);
	    println(ftm1b);
	    println(ftm1a.compareTo(ftm1b));
	    println(compare(ftm1a, ftm1b));
	    exit();
	}
	int lo = rand.nextInt(150) - 25;
	int hi = rand.nextInt(125 - lo) + lo;
	lo *= 2;	// they have to be even because of the comparator behavior
	hi *= 2;
	MyInteger Lo = new MyInteger(lo);
	MyInteger Hi = new MyInteger(hi);
	SortedMap<MyInteger, MyInteger> ftsm = ftm0.subMap(Lo, Hi);
	SortedMap<MyInteger, MyInteger> tsm = tm0.subMap(Lo, Hi);
	if (!ftsm.equals(tsm)) {
	    println("FTreeMap subMap failed on iteration " + i);
	    println("[" + lo + ", " + hi + ")");
	    println(ftsm);
	    println(tsm);
	    exit();
	}
	if (!ftm0.headMap(Hi).equals(tm0.headMap(Hi))) {
	    println("FTreeMap headMap failed on iteration " + i);
	    exit();
	}
	if (!ftm0.tailMap(Lo).equals(tm0.tailMap(Lo))) {
	    println("FTreeMap tailMap failed on iteration " + i);
	    exit();
	}
	if (i % 50 == 0) {
	    try {
		// Check handling of null map
		FMap<MyInteger, MyInteger> ftmser =
		    (i == 0 ? new FTreeMap<MyInteger, MyInteger>(TestComparator.Instance)
		     : ftm0);
		FileOutputStream fos = new FileOutputStream("ftm.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(ftmser);
		oos.close();
		FileInputStream fis = new FileInputStream("ftm.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FMap<MyInteger, MyInteger> nftmser =
		    (FMap<MyInteger, MyInteger>)ois.readObject();
		ois.close();
		if (!ftmser.equals(nftmser) || ftmser.hashCode() != nftmser.hashCode()) {
		    println("FTreeMap read/write failed on iteration " + i);
		    exit();
		}
	    } catch (IOException e) {
		println("FTreeMap read/write: exception " + e);
		exit();
	    } catch (ClassNotFoundException e) {
		println("FTreeMap read/write: exception " + e);
	    }
	}
    }

    static void testFHashMap(Random rand, int i, FHashSet<MyInteger> set) {
	FHashMap<MyInteger, MyInteger> fhm0 = new FHashMap<MyInteger, MyInteger>();
	HashMap<MyInteger, MyInteger> hm0 = new HashMap<MyInteger, MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200), v = rand.nextInt(3);
	    MyInteger R = r == 57 ? null : new MyInteger(r), V = new MyInteger(v);
	    FHashMap<MyInteger, MyInteger> tmp = fhm0.with(R, V);
	    hm0.put(R, V);
	    if (!tmp.verify()) {
		println("FHashMap Verification failure on iteration " + i);
		println(fhm0.dump());
		println("Adding " + R + ", " + V);
		println(tmp.dump());
		exit();
	    }
	    fhm0 = tmp;
	}
	FHashMap<MyInteger, MyInteger> fhm1 = new FHashMap<MyInteger, MyInteger>();
	HashMap<MyInteger, MyInteger> hm1 = new HashMap<MyInteger, MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200), v = rand.nextInt(3);
	    MyInteger R = r == 57 ? null : new MyInteger(r), V = new MyInteger(v);
	    FHashMap<MyInteger, MyInteger> tmp = fhm1.with(R, V);
	    hm1.put(R, V);
	    if (!tmp.verify()) {
		println("FHashMap Verification failure on iteration " + i);
		println(fhm1.dump());
		println("Adding " + R + ", " + V);
		println(tmp.dump());
		exit();
	    }
	    fhm1 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FHashMap<MyInteger, MyInteger> tmp = fhm0.less(R);
	    hm0.remove(R);
	    if (!tmp.verify()) {
		println("FHashMap Verification failure on iteration " + i);
		println(fhm0.dump());
		println("Removing " + R);
		println(tmp.dump());
		exit();
	    }
	    fhm0 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FHashMap<MyInteger, MyInteger> tmp = fhm1.less(R);
	    hm1.remove(R);
	    if (!tmp.verify()) {
		println("FHashMap Verification failure on iteration " + i);
		println(fhm1.dump());
		println("Removing " + R);
		println(tmp.dump());
		exit();
	    }
	    fhm1 = tmp;
	}
	if (!fhm0.equals(hm0)) {
	    println("FHashMap Equality failed (fhm0, A) on iteration " + i);
	    println(fhm0.dump());
	    println(fhm0);
	    println(hm0);
	    exit();
	}
	if (!fhm0.equals(new FHashMap<MyInteger, MyInteger>(hm0))) {
	    println("FHashMap Equality failed (fhm0, B) on iteration " + i);
	    println(fhm0.dump());
	    println(hm0);
	    exit();
	}
	if (!fhm1.equals(hm1)) {
	    println("FHashMap Equality failed (fhm1, A) on iteration " + i);
	    println(fhm1.dump());
	    println(fhm1);
	    println(hm1);
	    exit();
	}
	if (!fhm1.equals(new FHashMap<MyInteger, MyInteger>(hm1))) {
	    println("FHashMap Equality failed (fhm1, B) on iteration " + i);
	    println(fhm1.dump());
	    exit();
	}
	FHashMap<MyInteger, MyInteger> fhmm = fhm0.union(fhm1);
	HashMap<MyInteger, MyInteger> hmm = (HashMap<MyInteger, MyInteger>)hm0.clone();
	hmm.putAll(hm1);
	if (!fhmm.verify() || !fhmm.equals(hmm)) {
	    println("FHashMap Union failed on iteration " + i);
	    println(fhm0);
	    println(fhm0.dump());
	    println(fhm1);
	    println(fhm1.dump());
	    //if (!fhmm.verify())
	    println(fhmm.size() + ", " + hmm.size());
	    println(fhmm);
	    println(fhmm.dump());
	    println(hmm);
	    exit();
	}
	if (!fhmm.equals(new FHashMap<MyInteger, MyInteger>(hmm))) {
	    println("FHashMap Equality failed (fhmm) on iteration " + i);
	}
	FHashMap<MyInteger, MyInteger> fhmr = fhm0.restrictedTo(set);
	HashMap<MyInteger, MyInteger> hmr = (HashMap<MyInteger, MyInteger>)hm0.clone();
	for (Iterator it = hmr.keySet().iterator(); it.hasNext(); ) {
	    Object k = it.next();
	    if (!set.contains(k)) it.remove();
	}
	if (!fhmr.verify() || !fhmr.equals(hmr)) {
	    println("FHashMap restrictedTo failed on iteration " + i);
	    println(fhmr);
	    println(hmr);
	    exit();
	}
	fhmr = fhm0.restrictedFrom(set);
	hmr = (HashMap<MyInteger, MyInteger>)hm0.clone();
	for (Iterator it = hmr.keySet().iterator(); it.hasNext(); ) {
	    Object k = it.next();
	    if (set.contains(k)) it.remove();
	}
	if (!fhmr.verify() || !fhmr.equals(hmr)) {
	    println("FHashMap restrictedFrom failed on iteration " + i);
	    println(fhmr);
	    println(hmr);
	    exit();
	}
	fhm0 = fhm0.less(null);		// for benefit of `compare' below
	FSet<MyInteger> fhm0_dom = fhm0.domain();
	FHashMap<MyInteger, MyInteger> fhm0a =
	    fhm0.less(pick(rand, fhm0_dom)).with(pick(rand, fhm0_dom),
						 new MyInteger(rand.nextInt(3)));
	FHashMap<MyInteger, MyInteger> fhm0b =
	    fhm0.less(pick(rand, fhm0_dom)).with(pick(rand, fhm0_dom),
						 new MyInteger(rand.nextInt(3)));
	if (sgn(fhm0a.compareTo(fhm0b)) != compare(fhm0a, fhm0b)) {
	    println("FHashMap Compare failed (fhm0) on iteration " + i);
	    println(fhm0a.dump());
	    println(fhm0b.dump());
	    println(fhm0a);
	    println(fhm0b);
	    println(fhm0a.compareTo(fhm0b));
	    println(compare(fhm0a, fhm0b));
	    exit();
	}
	fhm1 = fhm1.less(null);
	FSet<MyInteger> fhm1_dom = fhm1.domain();
	FHashMap<MyInteger, MyInteger> fhm1a =
	    fhm1.less(pick(rand, fhm1_dom)).with(pick(rand, fhm1_dom),
						 new MyInteger(rand.nextInt(3)));
	FHashMap<MyInteger, MyInteger> fhm1b =
	    fhm1.less(pick(rand, fhm1_dom)).with(pick(rand, fhm1_dom),
						 new MyInteger(rand.nextInt(3)));
	if (sgn(fhm1a.compareTo(fhm1b)) != compare(fhm1a, fhm1b)) {
	    println("FHashMap Compare failed (fhm1) on iteration " + i);
	    println(fhm1a.dump());
	    println(fhm1b.dump());
	    println(fhm1a);
	    println(fhm1b);
	    println(fhm1a.compareTo(fhm1b));
	    println(compare(fhm1a, fhm1b));
	    exit();
	}
	if (i % 50 == 0) {
	    try {
		// Check handling of null map
		FMap<MyInteger, MyInteger> fhmser =
		    (i == 0 ? new FHashMap<MyInteger, MyInteger>() : fhm0);
		FileOutputStream fos = new FileOutputStream("fhm.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(fhmser);
		oos.close();
		FileInputStream fis = new FileInputStream("fhm.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FMap<MyInteger, MyInteger> nfhmser = (FMap<MyInteger, MyInteger>)ois.readObject();
		ois.close();
		if (!fhmser.equals(nfhmser) || fhmser.hashCode() != nfhmser.hashCode()) {
		    println("FHashMap read/write failed on iteration " + i);
		    exit();
		}
	    } catch (IOException e) {
		println("FHashMap read/write: exception " + e);
		exit();
	    } catch (ClassNotFoundException e) {
		println("FHashMap read/write: exception " + e);
	    }
	}
    }

/********
    static void testFCachedHashMap(Random rand, int i, FSet set) {
	FMap fhm0 = new FCachedHashMap();
	HashMap hm0 = new HashMap();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200), v = rand.nextInt(3);
	    MyInteger R = r == 57 ? null : new MyInteger(r), V = new MyInteger(v);
	    FMap tmp = fhm0.with(R, V);
	    hm0.put(R, V);
	    if (!((FCachedHashMap)tmp).verify()) {
		println("FCachedHashMap Verification failure on iteration " + i);
		println(((FCachedHashMap)fhm0).dump());
		println("Adding " + R + ", " + V);
		println(((FCachedHashMap)tmp).dump());
		exit();
	    }
	    fhm0 = tmp;
	}
	FMap fhm1 = new FCachedHashMap();
	HashMap hm1 = new HashMap();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200), v = rand.nextInt(3);
	    MyInteger R = r == 57 ? null : new MyInteger(r), V = new MyInteger(v);
	    FMap tmp = fhm1.with(R, V);
	    hm1.put(R, V);
	    if (!((FCachedHashMap)tmp).verify()) {
		println("FCachedHashMap Verification failure on iteration " + i);
		println(((FCachedHashMap)fhm1).dump());
		println("Adding " + R + ", " + V);
		println(((FCachedHashMap)tmp).dump());
		exit();
	    }
	    fhm1 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FMap tmp = fhm0.less(R);
	    hm0.remove(R);
	    if (!((FCachedHashMap)tmp).verify()) {
		println("FCachedHashMap Verification failure on iteration " + i);
		println(((FCachedHashMap)fhm0).dump());
		println("Removing " + R);
		println(((FCachedHashMap)tmp).dump());
		exit();
	    }
	    fhm0 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FMap tmp = fhm1.less(R);
	    hm1.remove(R);
	    if (!((FCachedHashMap)tmp).verify()) {
		println("FCachedHashMap Verification failure on iteration " + i);
		println(((FCachedHashMap)fhm1).dump());
		println("Removing " + R);
		println(((FCachedHashMap)tmp).dump());
		exit();
	    }
	    fhm1 = tmp;
	}
	if (!fhm0.equals(hm0)) {
	    println("FCachedHashMap Equality failed (fhm0, A) on iteration " + i);
	    println(((FCachedHashMap)fhm0).dump());
	    println(fhm0);
	    println(hm0);
	    exit();
	}
	if (!fhm0.equals(new FCachedHashMap(hm0))) {
	    println("FCachedHashMap Equality failed (fhm0, B) on iteration " + i);
	    println(((FCachedHashMap)fhm0).dump());
	    println(hm0);
	    exit();
	}
	if (!fhm1.equals(hm1)) {
	    println("FCachedHashMap Equality failed (fhm1, A) on iteration " + i);
	    println(((FCachedHashMap)fhm1).dump());
	    println(hm1);
	    exit();
	}
	if (!fhm1.equals(new FCachedHashMap(hm1))) {
	    println("FCachedHashMap Equality failed (fhm1, B) on iteration " + i);
	    println(((FCachedHashMap)fhm1).dump());
	    exit();
	}
	FMap fhmm = fhm0.union(fhm1);
	HashMap hmm = (HashMap)hm0.clone();
	hmm.putAll(hm1);
	if (!((FCachedHashMap)fhmm).verify() || !fhmm.equals(hmm)) {
	    println("FCachedHashMap Union failed on iteration " + i);
	    println(fhm0);
	    println(((FCachedHashMap)fhm0).dump());
	    println(fhm1);
	    println(((FCachedHashMap)fhm1).dump());
	    //if (!((FCachedHashMap)fhmm).verify())
	    println(fhmm.size() + ", " + hmm.size());
	    println(fhmm);
	    println(((FCachedHashMap)fhmm).dump());
	    println(hmm);
	    exit();
	}
	if (!fhmm.equals(new FCachedHashMap(hmm))) {
	    println("FCachedHashMap Equality failed (fhmm) on iteration " + i);
	}
	FMap fhmr = fhm0.restrictedTo(set);
	HashMap hmr = (HashMap)hm0.clone();
	for (Iterator it = hmr.keySet().iterator(); it.hasNext(); ) {
	    Object k = it.next();
	    if (!set.contains(k)) it.remove();
	}
	if (!((FCachedHashMap)fhmr).verify() || !fhmr.equals(hmr)) {
	    println("FCachedHashMap restrictedTo failed on iteration " + i);
	    println(fhmr);
	    println(hmr);
	    exit();
	}
	fhmr = fhm0.restrictedFrom(set);
	hmr = (HashMap)hm0.clone();
	for (Iterator it = hmr.keySet().iterator(); it.hasNext(); ) {
	    Object k = it.next();
	    if (set.contains(k)) it.remove();
	}
	if (!((FCachedHashMap)fhmr).verify() || !fhmr.equals(hmr)) {
	    println("FCachedHashMap restrictedFrom failed on iteration " + i);
	    println(fhmr);
	    println(hmr);
	    exit();
	}
	fhm0 = fhm0.less(null);		// for benefit of `compare' below
	FSet fhm0_dom = fhm0.domain();
	FMap fhm0a = fhm0.less(pick(rand, fhm0_dom))
			.with(pick(rand, fhm0_dom), new MyInteger(rand.nextInt(3)));
	FMap fhm0b = fhm0.less(pick(rand, fhm0_dom))
			.with(pick(rand, fhm0_dom), new MyInteger(rand.nextInt(3)));
	if (sgn(((FCachedHashMap)fhm0a).compareTo(fhm0b)) !=
	      compare(fhm0a, fhm0b)) {
	    println("FHashMap Compare failed (fhm0) on iteration " + i);
	    println(((FHashMap)fhm0a).dump());
	    println(((FHashMap)fhm0b).dump());
	    println(fhm0a);
	    println(fhm0b);
	    println(((FHashMap)fhm0a).compareTo(fhm0b));
	    println(compare(fhm0a, fhm0b));
	    exit();
	}
	fhm1 = fhm1.less(null);
	FSet fhm1_dom = fhm1.domain();
	FMap fhm1a = fhm1.less(pick(rand, fhm1_dom))
			.with(pick(rand, fhm1_dom), new MyInteger(rand.nextInt(3)));
	FMap fhm1b = fhm1.less(pick(rand, fhm1_dom))
			.with(pick(rand, fhm1_dom), new MyInteger(rand.nextInt(3)));
	if (sgn(((FCachedHashMap)fhm1a).compareTo(fhm1b)) !=
	      compare(fhm1a, fhm1b)) {
	    println("FHashMap Compare failed (fhm1) on iteration " + i);
	    println(((FHashMap)fhm1a).dump());
	    println(((FHashMap)fhm1b).dump());
	    println(fhm1a);
	    println(fhm1b);
	    println(((FHashMap)fhm1a).compareTo(fhm1b));
	    println(compare(fhm1a, fhm1b));
	    exit();
	}
	if (i % 50 == 0) {
	    try {
		// Check handling of null map
		FMap fhmser = (i == 0 ? new FCachedHashMap() : fhm0);
		FileOutputStream fos = new FileOutputStream("fhm.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(fhmser);
		oos.close();
		FileInputStream fis = new FileInputStream("fhm.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FMap nfhmser = (FMap)ois.readObject();
		ois.close();
		if (!fhmser.equals(nfhmser) || fhmser.hashCode() != nfhmser.hashCode()) {
		    println("FCachedHashMap read/write failed on iteration " + i);
		    exit();
		}
	    } catch (IOException e) {
		println("FCachedHashMap read/write: exception " + e);
		exit();
	    } catch (ClassNotFoundException e) {
		println("FCachedHashMap read/write: exception " + e);
	    }
	}
    }
*/

    static void testFLinkedHashMap(Random rand, int i) {
	// Just some quick sanity checks.  The tricky parts are shared with FHashMap.
	FLinkedHashMap<Integer, Integer> flhm0 = new FLinkedHashMap<Integer, Integer>();
	LinkedHashMap<Integer, Integer> lhm0 = new LinkedHashMap<Integer, Integer>();
	for (int j = 0; j < 10; ++j) {
	    int r = rand.nextInt(200), v = rand.nextInt(100);
	    flhm0 = flhm0.with(r, v);
	    lhm0.put(r, v);
	}
	if (flhm0.size() != lhm0.size()) {
	    println("FLinkedHashMap size failed on iteration " + i);
	    exit();
	}
	if (!flhm0.equals(lhm0)) {
	    println("FLinkedHashMap equals failed on iteration " + i);
	    exit();
	}
	Iterator<Map.Entry<Integer, Integer>> flhm0_it = flhm0.iterator();
	Iterator<Map.Entry<Integer, Integer>> lhm0_it = lhm0.entrySet().iterator();
	while (flhm0_it.hasNext()) {
	    if (!lhm0_it.hasNext() || !flhm0_it.next().equals(lhm0_it.next())) {
		println("FLinkedHashMap ordering failed on iteration " + i);
		exit();
	    }
	}
	if (lhm0_it.hasNext()) {
	    println("FLinkedHashMap ordering failed on iteration " + i);
	    exit();
	}
	FLinkedHashMap<Integer, Integer> flhm1 = new FLinkedHashMap<Integer, Integer>();
	LinkedHashMap<Integer, Integer> lhm1 = new LinkedHashMap<Integer, Integer>();
	for (int j = 0; j < 10; ++j) {
	    int r = rand.nextInt(200), v = rand.nextInt(100);
	    flhm1 = flhm1.with(r, v);
	    lhm1.put(r, v);
	}
	if (i % 50 == 0) {
	    try {
		FMap<Integer, Integer> fhmser = flhm0;
		FileOutputStream fos = new FileOutputStream("flhm.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(fhmser);
		oos.close();
		FileInputStream fis = new FileInputStream("flhm.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FMap<Integer, Integer> nfhmser = (FMap<Integer, Integer>)ois.readObject();
		ois.close();
		if (!fhmser.equals(nfhmser) || fhmser.hashCode() != nfhmser.hashCode()) {
		    println("FLinkedHashMap read/write failed on iteration " + i);
		    exit();
		}
		flhm0_it = flhm0.iterator();
		lhm0_it = lhm0.entrySet().iterator();
		while (flhm0_it.hasNext()) {
		    if (!lhm0_it.hasNext() || !flhm0_it.next().equals(lhm0_it.next())) {
			println("FLinkedHashMap ordering failed on iteration " + i);
			exit();
		    }
		}
		if (lhm0_it.hasNext()) {
		    println("FLinkedHashMap ordering failed on iteration " + i);
		    exit();
		}
	    } catch (IOException e) {
		println("FLinkedHashMap read/write: exception " + e);
		exit();
	    } catch (ClassNotFoundException e) {
		println("FLinkedHashMap read/write: exception " + e);
	    }
	}
    }

    static void testFTreeList(Random rand, int i) {
	FTreeList<MyInteger> ftl0 = new FTreeList<MyInteger>();
	// No `java.util' List class has both log-time-or-better indexing and
	// log-time-or-better insertion.  So we're going to have a quadraticism
	// somewhere.
	ArrayList<MyInteger> al0 = new ArrayList<MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    int pos = al0.isEmpty() ? 0 : rand.nextInt(al0.size());
	    int which = rand.nextInt(6);
	    FTreeList<MyInteger> tmp;
	    if (ftl0.indexOf(R) != al0.indexOf(R)) {
		println("FTreeList indexOf failed (ftl0) on iteration " + i);
		exit();
	    }
	    if (which == 0 && !al0.isEmpty()) {
		if (!equals(ftl0.get(pos), al0.get(pos))) {
		    println("FTreeList get failed (ftl0) on iteration " + i);
		    exit();
		}
		tmp = ftl0.with(pos, R);
		al0.set(pos, R);
	    } else if (which == 1 && !al0.isEmpty()) {
		tmp = ftl0.less(pos);
		al0.remove(pos);
	    } else {
		tmp = ftl0.withInserted(pos, R);
		al0.add(pos, R);
	    }
	    if (!tmp.verify()) {
		println("FTreeList Verification failed on iteration " + i);
		println(ftl0.dump());
		if (which == 1)
		    println("Deleting at " + pos);
		else
		    println((which == 0 ? "Writing " : "Inserting ") + R + " at " + pos);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != al0.hashCode()) {
		println("FTreeList hashCode failed on iteration " + i);
		println(ftl0);
		println(ftl0.dump());
		if (which == 1)
		    println("Deleting at " + pos);
		else
		    println((which == 0 ? "Writing " : "Inserting ") + R + " at " + pos);
		println(tmp);
		println(tmp.dump());
		println(al0);
		println(tmp.hashCode() + ", " + al0.hashCode());
		exit();
	    }
	    ftl0 = tmp;
	}
	FTreeList<MyInteger> ftl1 = new FTreeList<MyInteger>();
	ArrayList<MyInteger> al1 = new ArrayList<MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    int pos = al1.isEmpty() ? 0 : rand.nextInt(al1.size());
	    int which = rand.nextInt(5);
	    FTreeList<MyInteger> tmp;
	    if (ftl1.lastIndexOf(R) != al1.lastIndexOf(R)) {
		println("FTreeList lastIndexOf failed (ftl1) on iteration " + i);
		exit();
	    }
	    if (which == 0 && !al1.isEmpty()) {
		if (!equals(ftl1.get(pos), al1.get(pos))) {
		    println("FTreeList get failed (ftl1) on iteration " + i);
		    exit();
		}
		tmp = ftl1.with(pos, R);
		al1.set(pos, R);
	    } else if (which == 1 && !al1.isEmpty()) {
		tmp = ftl1.less(pos);
		al1.remove(pos);
	    } else {
		tmp = ftl1.withInserted(pos, R);
		al1.add(pos, R);
	    }
	    if (!tmp.verify()) {
		println("FTreeList Verification failed on iteration " + i);
		println(ftl1.dump());
		if (which == 1)
		    println("Deleting at " + pos);
		else
		    println((which == 0 ? "Writing " : "Inserting ") + R + " at " + pos);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != al1.hashCode()) {
		println("FTreeList hashCode failed on iteration " + i);
		println(tmp);
		println(tmp.hashCode() + ", " + al1.hashCode());
		exit();
	    }
	    ftl1 = tmp;
	}
	if (!ftl0.equals(al0)) {
	    println("FTreeList Equality failed (ftl0, A) on iteration " + i);
	    println(ftl0.dump());
	    println(ftl0);
	    println(al0);
	    exit();
	}
	if (!ftl0.equals(new FTreeList<MyInteger>(al0))) {
	    println("FTreeList Equality failed (ftl0, B) on iteration " + i);
	    println(ftl0.dump());
	    println(al0);
	    exit();
	}
	if (!ftl1.equals(al1)) {
	    println("FTreeList Equality failed (ftl1, A) on iteration " + i);
	    println(ftl1.dump());
	    println(al1);
	    exit();
	}
	if (!ftl1.equals(new FTreeList<MyInteger>(al1))) {
	    println("FTreeList Equality failed (ftl1, B) on iteration " + i);
	    println(ftl1.dump());
	    exit();
	}
	FTreeList<MyInteger> ftlc = ftl0.concat(ftl1);
	ArrayList<MyInteger> alc = (ArrayList<MyInteger>)al0.clone();
	alc.addAll(alc.size(), al1);
	if (!ftlc.equals(alc)) {
	    println("FTreeList concat failed on iteration " + i);
	    exit();
	}
	int lo = rand.nextInt(al0.size()), hi = rand.nextInt(al0.size() - lo) + lo;
	FTreeList<MyInteger> sftl0 = ftl0.subseq(lo, hi);
	List<MyInteger> sal0 = al0.subList(lo, hi);
	if (!sftl0.equals(sal0)) {
	    println("FTreeList subseq failed on iteration " + i);
	    println(ftl0);
	    println(ftl0.dump());
	    println(lo + ", " + hi);
	    println(sftl0);
	    println(sal0);
	    exit();
	}
	int delpos = rand.nextInt(ftl0.size());
	FTreeList<MyInteger> ftl0a = ftl0.less(delpos);
	List<MyInteger> al0a = (List<MyInteger>)al0.clone();
	al0a.remove(delpos);
	if (!ftl0a.equals(al0a)) {
	    println("FTreeList less failed on iteration " + i);
	    println(ftl0);
	    println(ftl0.dump());
	    println("Removing at " + delpos);
	    println(ftl0a);
	    println(ftl0a.dump());
	    exit();
	}
	FTreeList<MyInteger> ftl0b = ftl0.less(rand.nextInt(ftl0.size()));
	if (sgn(ftl0a.compareTo(ftl0b)) !=
	    compare(ftl0a, ftl0b)) {
	    println("FTreeList compareTo failed on iteration " + i);
	    println(ftl0a);
	    println(ftl0b);
	    println(ftl0a.compareTo(ftl0b));
	    println(compare(ftl0a, ftl0b));
	    exit();
	}
	FTreeList<MyInteger> ftl0s = ftl0.sorted(TestComparator.Instance);
	ArrayList<MyInteger> al0s = (ArrayList<MyInteger>)al0.clone();
	al0s.sort(TestComparator.Instance);
	if (!ftl0s.equals(al0s)) {
	    println("FTreeList sort failed on iteration " + i);
	    println(ftl0s);
	    println(al0s);
	    exit();
	}
	ListIterator<MyInteger> ftli = ftl0.listIterator();
	ListIterator<MyInteger> ali = al0.listIterator();
	//FTreeList.debug = true;
	for (int j = 0; j < 400; ++j) {
	    int which = rand.nextInt(2);
	    if (ftli.nextIndex() != ali.nextIndex()) {
		println("FTreeList nextIndex failed on iteration "+ i + "." + j);
		exit();
	    }
	    if (which == 0 && ali.hasPrevious()) {
		//println("Rev");
		if (!ftli.hasPrevious()) {
		    println("FTreeList hasPrevious failed false on iteration " +
			    i + "." + j);
		    exit();
		}
		MyInteger x = ftli.previous();
		MyInteger y = ali.previous();
		if (x == null ? y != null : !x.equals(y)) {
		    println("FTreeList previous failed on iteration " + i + "." + j);
		    exit();
		}
	    } else if (ali.hasNext()) {
		//println("Fwd");
		if (!ali.hasPrevious() && ftli.hasPrevious()) {
		    println("FTreeList hasPrevious failed true on iteration " +
			    i + "." + j);
		    exit();
		}
		if (!ftli.hasNext()) {
		    println("FTreeList hasNext failed false on iteration " +
			    i + "." + j);
		    exit();
		}
		MyInteger x = ftli.next();
		MyInteger y = ali.next();
		if (x == null ? y != null : !x.equals(y)) {
		    println("FTreeList next failed on iteration " + i + "." + j);
		    exit();
		}
	    } else if (ftli.hasNext()) {
		println("FTreeList hasNext failed true on iteration " + i + "." + j);
		exit();
	    }
	}
	if (i % 50 == 0) {
	    try {
		// Check handling of null list
		FList<MyInteger> ftlser = (i == 0 ? new FTreeList<MyInteger>() : ftl0);
		FileOutputStream fos = new FileOutputStream("ftl.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(ftlser);
		oos.writeObject(ftlser);
		oos.close();
		FileInputStream fis = new FileInputStream("ftl.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FList<MyInteger> nftlser0 = (FList<MyInteger>)ois.readObject();
		FList<MyInteger> nftlser1 = (FList<MyInteger>)ois.readObject();
		ois.close();
		if (!ftlser.equals(nftlser0) || !ftlser.equals(nftlser1) ||
		    ftlser.hashCode() != nftlser0.hashCode()) {
		    println("FTreeList read/write failed on iteration " + i);
		    exit();
		}
	    } catch (IOException e) {
		println("FTreeList read/write: exception " + e);
		exit();
	    } catch (ClassNotFoundException e) {
		println("FTreeList read/write: exception " + e);
	    }
	}
    }

    static class MyInteger implements Comparable<MyInteger>, Serializable {
	MyInteger(int val) { value = val; }
	private int value;
	public int intValue() { return value; }
	public boolean equals(Object x) {
	    if (x == this) return true;
	    else if (!(x instanceof MyInteger)) return false;
	    else return value == ((MyInteger)x).value;
	}
	public int hashCode() { return value >> 1; }
	public int compareTo(MyInteger x) {
	    return value < x.value ? -1 : value > x.value ? 1 : 0;
	}
	public String toString() { return "" + value; }
    }

    // For convenience of testing, we use integers; to make it easy to test
    // handling of equivalent values, we use a comparator that divides by 2
    // before comparing.
    static class TestComparator implements Comparator<MyInteger>, Serializable {
	private TestComparator() { }
	public static final TestComparator Instance = new TestComparator();
	public int compare(MyInteger a, MyInteger b) {
	    int ia = a == null ? 0 : a.intValue();
	    int ib = b == null ? 0 : b.intValue();
	    return (ia / 2) - (ib / 2);
	}
    }

    static MyInteger[] conv(int[] ary) {
	MyInteger[] res = new MyInteger[ary.length];
	for (int i = 0; i < ary.length; ++i) res[i] = new MyInteger(ary[i]);
	return res;
    }

    static MyInteger pick(Random rand, FSet<MyInteger> s) {
	if (s.isEmpty()) throw new IllegalStateException();
	while (true) {
	    int r = rand.nextInt(200);
	    MyInteger R = new MyInteger(r);
	    if (s.contains(R)) return R;
	}
    }

    static int compare(FSet<MyInteger> a, FSet<MyInteger> b) {
	if (a.size() < b.size()) return -1;
	else if (a.size() > b.size()) return 1;
	else {
	    Iterator<MyInteger> bi = b.iterator();
	    for (Iterator<MyInteger> ai = a.iterator(); ai.hasNext(); ) {
		if (!bi.hasNext()) {
		    println("Set iterator disagreement");
		    exit();
		}
		MyInteger a_elt = ai.next();
		MyInteger b_elt = bi.next();
		// 0 is the right value to use for null in `FHashSet', but for
		// `FTreeSet' the right value would be -1.  However, we don't
		// leave null in `FTreeSet's very long, because `TreeSet' doesn't
		// accept it.
		int a_val = a_elt == null ? 0 : a_elt.intValue();
		int b_val = b_elt == null ? 0 : b_elt.intValue();
		int a_tmp = a_val / 2;
		int b_tmp = b_val / 2;
		if (a_tmp < b_tmp) return -1;
		else if (a_tmp > b_tmp) return 1;
	    }
	    if (bi.hasNext()) {
		println("Set iterator disagreement");
		exit();
	    }
	    return 0;
	}
    }

    static boolean equals(FSet<MyInteger> a, FSet<MyInteger> b) {
	if (a.size() != b.size()) return false;
	else {
	    for (Iterator ai = a.iterator(); ai.hasNext(); )
		if (!b.contains(ai.next())) return false;
	    return true;
	}
    }

    static int compare(FList<MyInteger> a, FList<MyInteger> b) {
	if (a.size() < b.size()) return -1;
	else if (a.size() > b.size()) return 1;
	else {
	    Iterator<MyInteger> bi = b.iterator();
	    for (Iterator<MyInteger> ai = a.iterator(); ai.hasNext(); ) {
		MyInteger a_elt = ai.next();
		MyInteger b_elt = bi.next();
		int a_val = a_elt == null ? -1 : a_elt.intValue();
		int b_val = b_elt == null ? -1 : b_elt.intValue();
		if (a_val < b_val) return -1;
		else if (a_val > b_val) return 1;
	    }
	    return 0;
	}
    }

    static int compare(FMap<MyInteger, MyInteger> a, FMap<MyInteger, MyInteger> b) {
	if (a.size() < b.size()) return -1;
	else if (a.size() > b.size()) return 1;
	else {
	    Iterator<Map.Entry<MyInteger, MyInteger>> ai = a.iterator();
	    Iterator<Map.Entry<MyInteger, MyInteger>> bi = b.iterator();
	    Map.Entry<MyInteger, MyInteger> a_prev = null, b_prev = null;
	    while (ai.hasNext() || a_prev != null) {
		Map.Entry<MyInteger, MyInteger> a_ent =
		    a_prev != null ? a_prev : (Map.Entry<MyInteger, MyInteger>)ai.next();
		Map.Entry<MyInteger, MyInteger> b_ent =
		    b_prev != null ? b_prev : (Map.Entry<MyInteger, MyInteger>)bi.next();
		a_prev = b_prev = null;
		int aki = a_ent.getKey().intValue();
		int bki = b_ent.getKey().intValue();
		Map.Entry<MyInteger, MyInteger> a_next = null, b_next = null;
		if ((aki >> 1) < (bki >> 1)) return -1;
		else if ((aki >> 1) > (bki >> 1)) return 1;
		else {
		    if (ai.hasNext()) a_next = (Map.Entry)ai.next();
		    if (bi.hasNext()) b_next = (Map.Entry)bi.next();
		    if (a_next != null &&
			(((MyInteger)a_next.getKey()).intValue() >> 1) > (aki >> 1)) {
			a_prev = a_next;
			a_next = null;
		    }
		    if (b_next != null &&
			(((MyInteger)b_next.getKey()).intValue() >> 1) > (bki >> 1)) {
			b_prev = b_next;
			b_next = null;
		    }
		    if (a_next != null && b_next == null) return -1;
		    else if (a_next == null && b_next != null) return 1;
		    else if (a_next == null && b_next == null) {
			int av = a_ent.getValue().intValue();
			int bv = b_ent.getValue().intValue();
			if (av < bv) return -1;
			else if (av > bv) return 1;
		    } else if (a instanceof FTreeMap) {
			// We have to use the same kind of set as 'a' and 'b' (which are
			// assumed to be the same as each other), because they use different
			// value comparators.
			FTreeSet<MyInteger> avs = FTreeSet.<MyInteger>emptySet();
			avs = avs.with(a_ent.getValue()).with(a_next.getValue());
			FTreeSet<MyInteger> bvs = FTreeSet.<MyInteger>emptySet();
			bvs = bvs.with(b_ent.getValue()).with(b_next.getValue());
			int comp_res = avs.compareTo(bvs);
			if (comp_res != 0) return comp_res;
		    } else {
			FHashSet<MyInteger> avs = FHashSet.<MyInteger>emptySet();
			avs = avs.with(a_ent.getValue()).with(a_next.getValue());
			FHashSet<MyInteger> bvs = FHashSet.<MyInteger>emptySet();
			bvs = bvs.with(b_ent.getValue()).with(b_next.getValue());
			int comp_res = avs.compareTo(bvs);
			if (comp_res != 0) return comp_res;
		    }
		}
	    }
	    return 0;
	}
    }

    static boolean equals(Object x, Object y) {
	return x == null ? y == null : x.equals(y);
    }

    static int sgn(int n) { return n < 0 ? -1 : n > 0 ? 1 : 0; }

    static void println(String str) { System.out.println(str); }
    static void println(Object obj) { System.out.println(obj); }
    static void println(int i) { System.out.println(i); }
    static void println(boolean b) { System.out.println(b); }

    static void exit() { System.exit(1); }

}
