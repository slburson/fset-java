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
	    FTreeSet<MyInteger> pts = testFTreeSet(rand, i);
	    FHashSet<MyInteger> phs = testFHashSet(rand, i);
	    //FSet pchs = testFCachedHashSet(rand, i);
	    testFTreeMap(rand, i, pts);
	    testFHashMap(rand, i, phs);
	    //testFCachedHashMap(rand, i, pchs);
	    testFTreeList(rand, i);
	}
	println("All tests passed.");
    }

    static FTreeSet<MyInteger> testFTreeSet(Random rand, int i) {
	FTreeSet<MyInteger> pts0 = new FTreeSet<MyInteger>(TestComparator.Instance);
	TreeSet<MyInteger> ts0 = new TreeSet<MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = new MyInteger(r);
	    FTreeSet<MyInteger> tmp = pts0.with(R);
	    ts0.add(R);
	    if (!tmp.verify()) {
		println("FTreeSet Verification failure on iteration " + i);
		println(pts0.dump());
		println("Adding " + R);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.size() != ts0.size()) {
		println("FTreeSet size failed on iteration " + i);
		exit();
	    }
	    if (!pts0.isSubset(tmp) || !tmp.isSuperset(pts0) ||
		(!pts0.contains(R) && (tmp.isSubset(pts0) || pts0.isSuperset(tmp)))) {
		println("FTreeSet is{Sub,Super}set failed on iteration " + i);
		println(pts0.isSubset(tmp) + ", " + tmp.isSuperset(pts0) + ", " +
			pts0.contains(R) + ", " + tmp.isSubset(pts0) + ", " +
			pts0.isSuperset(tmp));
		exit();
	    }
	    pts0 = tmp;
	}
	FTreeSet<MyInteger> pts1 = new FTreeSet<MyInteger>(TestComparator.Instance);
	TreeSet<MyInteger> ts1 = new TreeSet<MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = new MyInteger(r);
	    FTreeSet<MyInteger> tmp = pts1.with(R);
	    ts1.add(R);
	    if (!tmp.verify()) {
		println("FTreeSet Verification failure on iteration " + i);
		println(pts1.dump());
		println("Adding " + R);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.size() != ts1.size()) {
		println("FTreeSet size failed on iteration " + i);
		exit();
	    }
	    if (!pts1.isSubset(tmp) || !tmp.isSuperset(pts1) ||
		(!pts1.contains(R) && (tmp.isSubset(pts1) || pts1.isSuperset(tmp)))) {
		println("FTreeSet is{Sub,Super}set failed on iteration " + i);
		println(pts1.isSubset(tmp) + ", " + tmp.isSuperset(pts1) + ", " +
			pts1.contains(R) + ", " + tmp.isSubset(pts1) + ", " +
			pts1.isSuperset(tmp));
		exit();
	    }
	    pts1 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = new MyInteger(r);
	    if (pts0.contains(R) != ts0.contains(R)) {
		println("FTreeSet contains failed (pts0) on iteration " + i);
		exit();
	    }
	    FTreeSet<MyInteger> tmp = pts0.less(R);
	    ts0.remove(R);
	    if (!tmp.verify()) {
		println("FTreeSet Verification failure on iteration " + i);
		println(pts0.dump());
		println("Removing " + R);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.size() != ts0.size()) {
		println("FTreeSet size failed on iteration " + i);
		exit();
	    }
	    pts0 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = new MyInteger(r);
	    if (pts1.contains(R) != ts1.contains(R)) {
		println("FTreeSet contains failed (pts1) on iteration " + i);
		exit();
	    }
	    FTreeSet<MyInteger> tmp = pts1.less(R);
	    ts1.remove(R);
	    if (!tmp.verify()) {
		println("FTreeSet Verification failure on iteration " + i);
		println(pts1.dump());
		println("Removing " + R);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.size() != ts1.size()) {
		println("FTreeSet size failed on iteration " + i);
		exit();
	    }
	    pts1 = tmp;
	}
	if (i == 0) {
	    FTreeSet<MyInteger> tmp = pts0.with(null);
	    if (!tmp.verify() || !tmp.contains(null) || tmp.first() != null) {
		println("FTreeSet Verification failure on iteration " + i);
		println(pts0.dump());
		println("Adding null");
		println(tmp.dump());
		exit();
	    }
	    tmp = tmp.less(null);
	    if (!tmp.verify() || tmp.contains(null)) {
		println("FTreeSet Verification failure on iteration " + i);
		println(pts0.dump());
		println("Removing null");
		println(tmp.dump());
		exit();
	    }
	}		
	if (pts0.hashCode() != ts0.hashCode()) {
	    println("FTreeSet hashCode failed on pts0 on iteration " + i);
	    println(pts0);
	    println(ts0);
	    exit();
	}
	if (pts1.hashCode() != ts1.hashCode()) {
	    println("FTreeSet hashCode failed on pts1 on iteration " + i);
	    exit();
	}
	if (!pts0.equals(ts0)) {
	    println("FTreeSet Equality failed (pts0, A) on iteration " + i);
	    exit();
	}
	if (!pts0.equals(new FTreeSet<MyInteger>(ts0))) {
	    println("FTreeSet Equality failed (pts0, B) on iteration " + i);
	    exit();
	}
	if (!pts0.equals(new FTreeSet<MyInteger>(ts0, TestComparator.Instance))) {
	    println("FTreeSet Equality failed (pts0, C) on iteration " + i);
	    println(pts0);
	    FTreeSet<MyInteger> npts0 = new FTreeSet<MyInteger>(ts0, TestComparator.Instance);
	    println(npts0);
	    println(npts0.dump());
	    exit();
	}
	if (!pts0.equals(new FTreeSet<MyInteger>(new ArrayList<MyInteger>(ts0)))) {
	    println("FTreeSet construction from ArrayList failed (pts0) on iteration "
		    + i);
	    exit();
	}
	if (!pts0.equals(new FTreeSet<MyInteger>(ts0.toArray(new MyInteger[0])))) {
	    println("FTreeSet construction from array failed (pts0) on iteration "
		    + i);
	    exit();
	}
	if (!pts1.equals(ts1)) {
	    println("FTreeSet Equality failed (pts1, A) on iteration " + i);
	    exit();
	}
	// Next line also tests constructor from `MyInteger[]'
	if (!pts1.equals(new FTreeSet<MyInteger>(ts1.toArray(new MyInteger[0])))) {
	    println("FTreeSet Equality failed (pts1, B) on iteration " + i);
	    exit();
	}
	// Next line also tests constructor from `MyInteger[]'
	if (!pts1.equals(new FTreeSet<MyInteger>(ts1.toArray(new MyInteger[0]),
						    TestComparator.Instance))) {
	    println("FTreeSet Equality failed (pts1, C) on iteration " + i);
	    exit();
	}
	if (pts0.first().intValue() / 2 != ts0.first().intValue() / 2) {
	    println("FTreeSet `first' failed (pts0) on iteration " + i);
	    exit();
	}
	if (pts1.first().intValue() / 2 != ts1.first().intValue() / 2) {
	    println("FTreeSet `first' failed (pts1) on iteration " + i);
	    exit();
	}
	if (pts0.last().intValue() / 2 != ts0.last().intValue() / 2) {
	    println("FTreeSet `last' failed (pts0) on iteration " + i);
	    exit();
	}
	if (pts1.last().intValue() / 2 != ts1.last().intValue() / 2) {
	    println("FTreeSet `last' failed (pts1) on iteration " + i);
	    exit();
	}
	FTreeSet<MyInteger> ptsu = pts0.union(pts1);
	TreeSet<MyInteger> tsu = (TreeSet<MyInteger>)ts0.clone();
	tsu.addAll(ts1);
	if (!((FTreeSet<MyInteger>)ptsu).verify() || !ptsu.equals(tsu)) {
	    println("FTreeSet Union failed on iteration " + i);
	    println(pts0);
	    println(pts1);
	    if (!ptsu.verify())
		println(ptsu.dump());
	    println(ptsu.size() + ", " + tsu.size());
	    println(ptsu);
	    println(tsu);
	    exit();
	}
	if (!ptsu.equals(new FTreeSet<MyInteger>(tsu))) {
	    println("FTreeSet Equality failed (ptsu) on iteration " + i);
	}
	FTreeSet<MyInteger> ptsi = pts0.intersection(pts1);
	TreeSet<MyInteger> tsi = (TreeSet<MyInteger>)ts0.clone();
	tsi.retainAll(ts1);
	if (!ptsi.verify() || !ptsi.equals(tsi)) {
	    println("FTreeSet Intersection failed on iteration " + i);
	    println(pts0);
	    println(pts1);
	    if (!ptsi.verify())
		println(ptsi.dump());
	    println(ptsi.size() + ", " + tsi.size());
	    println(ptsi);
	    println(tsi);
	    exit();
	}
	if (!ptsi.isSubset(pts0) || !ptsi.isSubset(pts1)) {
	    println("FTreeSet isSubset failed on iteration " + i);
	    exit();
	}
	if (!ptsi.equals(new FTreeSet<MyInteger>(tsi))) {
	    println("FTreeSet Equality failed (ptsi) on iteration " + i);
	}
	FTreeSet<MyInteger> ptsd = pts0.difference(pts1);
	TreeSet<MyInteger> tsd = (TreeSet<MyInteger>)ts0.clone();
	tsd.removeAll(ts1);
	if (!ptsd.verify() || !ptsd.equals(tsd)) {
	    println("FTreeSet Difference failed on iteration " + i);
	    println(pts0);
	    println(pts0.dump());
	    println(pts1);
	    println(pts1.dump());
	    //if (!((FTreeSet)ptsd).verify())
	    println(ptsd.size() + ", " + tsd.size());
	    println(ptsd);
	    println(ptsd.dump());
	    println(tsd);
	    exit();
	}
	if (!ptsd.equals(new FTreeSet<MyInteger>(tsd))) {
	    println("FTreeSet Equality failed (ptsd) on iteration " + i);
	}
	FTreeSet<MyInteger> npts0 = new FTreeSet<MyInteger>(pts0, TestComparator.Instance);
	npts0 = npts0.less(pick(rand, npts0));
	FTreeSet<MyInteger> pts0a = pts0.less(pick(rand, pts0));
	if (sgn(pts0a.compareTo(npts0)) != compare(pts0a, npts0)) {
	    println("FTreeSet Compare failed (pts0) on iteration " + i);
	    println(pts0a.dump());
	    println(npts0.dump());
	    println(pts0a);
	    println(npts0);
	    println(pts0a.compareTo(npts0));
	    println(compare(pts0a, npts0));
	    exit();
	}
	if (pts0a.equals(npts0) != equals(pts0a, npts0)) {
	    println("FTreeSet equality failed (pts0a) on iteration " + i);
	    exit();
	}
	FTreeSet<MyInteger> npts1 = new FTreeSet<MyInteger>(pts1, TestComparator.Instance);
	npts1 = npts1.less(pick(rand, npts1));
	FTreeSet<MyInteger> pts1a = pts1.less(pick(rand, pts1));
	if (sgn(pts1a.compareTo(npts1)) != compare(pts1a, npts1)) {
	    println("FTreeSet Compare failed (pts1) on iteration " + i);
	    println(pts1a.dump());
	    println(npts1.dump());
	    println(pts1a.compareTo(npts1));
	    println(compare(pts1a, npts1));
	    exit();
	}
	if (pts1a.equals(npts1) != equals(pts1a, npts1)) {
	    println("FTreeSet equality failed (pts1a) on iteration " + i);
	    exit();
	}
	int lo = rand.nextInt(150) - 25;
	int hi = rand.nextInt(125 - lo) + lo;
	lo *= 2;	// they have to be even because of the comparator behavior
	hi *= 2;
	MyInteger Lo = new MyInteger(lo);
	MyInteger Hi = new MyInteger(hi);
	SortedSet<MyInteger> ptss = pts0.subSet(Lo, Hi);
	SortedSet<MyInteger> tss = ts0.subSet(Lo, Hi);
	if (!ptss.equals(tss)) {
	    println("FTreeSet subSet failed on iteration " + i);
	    println("[" + lo + ", " + hi + ")");
	    println(ptss);
	    println(tss);
	    exit();
	}
	if (!pts0.headSet(Hi).equals(ts0.headSet(Hi))) {
	    println("FTreeSet headSet failed on iteration " + i);
	    exit();
	}
	if (!pts0.tailSet(Lo).equals(ts0.tailSet(Lo))) {
	    println("FTreeSet tailSet failed on iteration " + i);
	    exit();
	}
	while (!pts0.isEmpty()) {
	    MyInteger x = pts0.arb();
	    if (!pts0.contains(x) || !ts0.contains(x)) {
		println("FTreeSet arb/contains failed on iteration " + i);
		exit();
	    }
	    pts0 = pts0.less(x);
	    ts0.remove(x);
	    if (ts0.isEmpty() != pts0.isEmpty()) {
		println("FTreeSet less/isEmpty failed on iteration " + i);
		exit();
	    }
	}
	if (i % 50 == 0) {
	    // Check handling of null set
	    try {
		FSet<MyInteger> ptsser = (i == 0 ? pts0 : pts1);
		FileOutputStream fos = new FileOutputStream("pts.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(ptsser);
		oos.close();
		FileInputStream fis = new FileInputStream("pts.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FSet<MyInteger> nptsser = (FSet<MyInteger>)ois.readObject();
		ois.close();
		if (!ptsser.equals(nptsser)) {
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
	return pts1;
    }

    static FHashSet<MyInteger> testFHashSet(Random rand, int i) {
	FHashSet<MyInteger> phs0 = new FHashSet<MyInteger>();
	HashSet<MyInteger> hs0 = new HashSet<MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FHashSet<MyInteger> tmp = phs0.with(R);
	    hs0.add(R);
	    if (!tmp.verify()) {
		println("FHashSet Verification failure on iteration " + i);
		println(phs0.dump());
		println("Adding " + (R == null ? "null" : "" + R));
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != hs0.hashCode()) {
		println("FHashSet hashCode failed on phs0 on iteration " + i);
		println(tmp);
		println(hs0);
		println("Adding " + R + "; " + tmp.hashCode() + ", " + hs0.hashCode());
		exit();
	    }
	    if (!phs0.isSubset(tmp) || !tmp.isSuperset(phs0) ||
		(!phs0.contains(R) && (tmp.isSubset(phs0) || phs0.isSuperset(tmp)))) {
		println("FHashSet is{Sub,Super}set failed (phs0) on iteration " + i);
		println(phs0.isSubset(tmp) + ", " + tmp.isSuperset(phs0) + ", " +
			phs0.contains(R) + ", " + tmp.isSubset(phs0) + ", " +
			phs0.isSuperset(tmp) + "; " + R);
		println(phs0);
		println(tmp);
		//FHashSet.debug = true;
		//phs0.isSubset(tmp);
		exit();
	    }
	    phs0 = tmp;
	}
	FHashSet<MyInteger> phs1 = new FHashSet<MyInteger>();
	HashSet<MyInteger> hs1 = new HashSet<MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FHashSet<MyInteger> tmp = phs1.with(R);
	    hs1.add(R);
	    if (!tmp.verify()) {
		println("FHashSet Verification failure on iteration " + i);
		println(phs1.dump());
		println("Adding " + R);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != hs1.hashCode()) {
		println("FHashSet hashCode failed on phs1 on iteration " + i);
		println(tmp);
		println(hs1);
		println("Adding " + R + "; " + tmp.hashCode() + ", " + hs1.hashCode());
		exit();
	    }
	    if (!phs1.isSubset(tmp) || !tmp.isSuperset(phs1) ||
		(!phs1.contains(R) && (tmp.isSubset(phs1) || phs1.isSuperset(tmp)))) {
		println("FHashSet is{Sub,Super}set failed (phs1) on iteration " + i);
		println(phs1.isSubset(tmp) + ", " + tmp.isSuperset(phs1) + ", " +
			phs1.contains(R) + ", " + tmp.isSubset(phs1) + ", " +
			phs1.isSuperset(tmp) + "; " + R);
		println(phs1);
		println(tmp);
		exit();
	    }
	    phs1 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FHashSet<MyInteger> tmp = phs0.less(R);
	    hs0.remove(R);
	    if (!tmp.verify()) {
		println("FHashSet Verification failure on iteration " + i);
		println(phs0.dump());
		println("Removing " + (R == null ? "null" : "" + R));
		println(tmp.dump());
		exit();
	    }
	    phs0 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FHashSet<MyInteger> tmp = phs1.less(R);
	    hs1.remove(R);
	    if (!tmp.verify()) {
		println("FHashSet Verification failure on iteration " + i);
		println(phs1.dump());
		println("Removing " + (R == null ? "null" : "" + R));
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != hs1.hashCode()) {
		println("FHashSet hashCode failed on phs1 on iteration " + i);
		println(tmp);
		println(hs1);
		println("Removing " + R + "; " + tmp.hashCode() + ", " + hs1.hashCode());
		exit();
	    }
	    if (!tmp.equals(hs1)) {
		println("FHashSet equality failed on phs1 on iteration " + i);
		println(tmp);
		println(hs1);
		println(new FHashSet<MyInteger>(hs1));
		println("Removing " + R + "; " + tmp.hashCode() + ", " + hs1.hashCode());
		exit();
	    }
	    phs1 = tmp;
	}
	if (phs0.hashCode() != hs0.hashCode()) {
	    println("FHashSet hashCode failed on phs0 on iteration " + i);
	    println(phs0);
	    println(hs0);
	    exit();
	}
	if (phs1.hashCode() != hs1.hashCode()) {
	    println("FHashSet hashCode failed on phs1 on iteration " + i);
	    exit();
	}
	if (!phs0.equals(hs0)) {
	    println("FHashSet Equality failed (phs0, A) on iteration " + i);
	    println(phs0);
	    println(phs0.dump());
	    println(new TreeSet<MyInteger>(hs0));
	    exit();
	}
	if (!phs0.equals(new FHashSet<MyInteger>(hs0))) {
	    println("FHashSet Equality failed (phs0, B) on iteration " + i);
	    println(phs0);
	    println(phs0.dump());
	    FHashSet<MyInteger> nphs0 = new FHashSet<MyInteger>(hs0);
	    println(nphs0);
	    println(nphs0.dump());
	    exit();
	}
	if (!phs0.equals(new FHashSet<MyInteger>(new ArrayList<MyInteger>(hs0)))) {
	    println("FHashSet construction from ArrayList failed (phs0) on iteration " + i);
	    exit();
	}
	if (!phs0.equals(new FHashSet<MyInteger>(hs0.toArray(new MyInteger[0])))) {
	    println("FHashSet construction from array failed (phs0) on iteration " + i);
	    exit();
	}
	if (!phs1.equals(hs1)) {
	    println("FHashSet Equality failed (phs1, A) on iteration " + i);
	    println(phs1);
	    println(hs1);
	    println(new FHashSet<MyInteger>(hs1));
	    exit();
	}
	// Next line also tests constructor from `Object[]'
	if (!phs1.equals(new FHashSet<MyInteger>(hs1.toArray(new MyInteger[0])))) {
	    println("FHashSet Equality failed (phs1, B) on iteration " + i);
	    exit();
	}
	FHashSet<MyInteger> phsu = phs0.union(phs1);
	HashSet<MyInteger> hsu = (HashSet<MyInteger>)hs0.clone();
	hsu.addAll(hs1);
	if (!phsu.verify() || !phsu.equals(hsu)) {
	    println("FHashSet Union failed on iteration " + i);
	    println(phs0);
	    println(phs1);
	    if (!phsu.verify()) println(phsu.dump());
	    println(phsu.size() + ", " + hsu.size());
	    println(phsu);
	    println(hsu);
	    exit();
	}
	if (!phsu.equals(new FHashSet<MyInteger>(hsu))) {
	    println("FHashSet Equality failed (phsu) on iteration " + i);
	}
	FHashSet<MyInteger> phsi = phs0.intersection(phs1);
	HashSet<MyInteger> hsi = (HashSet<MyInteger>)hs0.clone();
	hsi.retainAll(hs1);
	if (!phsi.verify() || !phsi.equals(hsi)) {
	    println("FHashSet Intersection failed on iteration " + i);
	    println(phs0);
	    println(phs0.dump());
	    println(phs1);
	    println(phs1.dump());
	    if (!phsi.verify()) println(phsi.dump());
	    println(phsi.size() + ", " + hsi.size());
	    println(phsi);
	    println(new TreeSet<MyInteger>(hsi));
	    exit();
	}
	if (!phsi.isSubset(phs0) || !phsi.isSubset(phs1)) {
	    println("FHashSet isSubset failed on iteration " + i);
	    exit();
	}
	if (!phsi.equals(new FHashSet<MyInteger>(hsi))) {
	    println("FHashSet Equality failed (phsi) on iteration " + i);
	}
	FHashSet<MyInteger> phsd = phs0.difference(phs1);
	HashSet<MyInteger> hsd = (HashSet<MyInteger>)hs0.clone();
	hsd.removeAll(hs1);
	if (!phsd.verify() || !phsd.equals(hsd)) {
	    println("FHashSet Difference failed on iteration " + i);
	    println(phs0);
	    println((phs0).dump());
	    println(phs1);
	    println(phs1.dump());
	    //if (!phsd.verify())
	    println(phsd.size() + ", " + hsd.size());
	    println(phsd);
	    println(phsd.dump());
	    println(hsd);
	    exit();
	}
	if (!phsd.equals(new FHashSet<MyInteger>(hsd))) {
	    println("FHashSet Equality failed (phsd) on iteration " + i);
	}
	FHashSet<MyInteger> nphs0 = new FHashSet<MyInteger>(phs0);
	nphs0 = nphs0.less(pick(rand, nphs0));
	FHashSet<MyInteger> phs0a = phs0.less(pick(rand, phs0));
	if (sgn(phs0a.compareTo(nphs0)) != compare(phs0a, nphs0)) {
	    println("FHashSet Compare failed (phs0) on iteration " + i);
	    println(phs0a.compareTo(nphs0) + ", " + compare(phs0a, nphs0));
	    println(phs0a);
	    println(phs0a.dump());
	    println(nphs0);
	    println(nphs0.dump());
	    exit();
	}
	if (phs0a.equals(nphs0) != equals(phs0a, nphs0)) {
	    println("FHashSet equality failed (phs0a) on iteration " + i);
	    exit();
	}
	FHashSet<MyInteger> nphs1 = new FHashSet<MyInteger>(phs1);
	nphs1 = nphs1.less(pick(rand, nphs1));
	FHashSet<MyInteger> phs1a = phs1.less(pick(rand, phs1));
	if (sgn(phs1a.compareTo(nphs1)) != compare(phs1a, nphs1)) {
	    println("FHashSet Compare failed (phs1) on iteration " + i);
	    exit();
	}
	if (phs1a.equals(nphs1) != equals(phs1a, nphs1)) {
	    println("FHashSet equality failed (phs1a) on iteration " + i);
	    exit();
	}
	while (!phs0.isEmpty()) {
	    MyInteger x = phs0.arb();
	    if (!phs0.contains(x) || !hs0.contains(x)) {
		println("FHashSet arb/contains failed on iteration " + i);
		exit();
	    }
	    phs0 = phs0.less(x);
	    hs0.remove(x);
	    if (hs0.isEmpty() != phs0.isEmpty()) {
		println("FHashSet less/isEmpty failed on iteration " + i);
		exit();
	    }
	}
	if (i % 50 == 0) {
	    try {
		// Check handling of null set
		FSet<MyInteger> phsser = (i == 0 ? phs0 : phs1);
		FileOutputStream fos = new FileOutputStream("phs.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(phsser);
		oos.close();
		FileInputStream fis = new FileInputStream("phs.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FSet<MyInteger> nphsser = (FSet<MyInteger>)ois.readObject();
		ois.close();
		if (!phsser.equals(nphsser)) {
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
	return phs1;
    }

/********
    static FSet testFCachedHashSet(Random rand, int i) {
	FSet phs0 = new FCachedHashSet();
	HashSet hs0 = new HashSet();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FSet tmp = phs0.with(R);
	    hs0.add(R);
	    if (!((FCachedHashSet)tmp).verify()) {
		println("FCachedHashSet Verification failure on iteration " + i);
		println(((FCachedHashSet)phs0).dump());
		println("Adding " + (R == null ? "null" : "" + R));
		println(((FCachedHashSet)tmp).dump());
		exit();
	    }
	    if (tmp.hashCode() != hs0.hashCode()) {
		println("FCachedHashSet hashCode failed on phs0 on iteration " + i);
		println(tmp);
		println(hs0);
		println("Adding " + R + "; " + tmp.hashCode() + ", " + hs0.hashCode());
		exit();
	    }
	    if (!phs0.isSubset(tmp) || !tmp.isSuperset(phs0) ||
		(!phs0.contains(R) && (tmp.isSubset(phs0) || phs0.isSuperset(tmp)))) {
		println("FCachedHashSet is{Sub,Super}set failed (phs0) on iteration " + i);
		println(phs0.isSubset(tmp) + ", " + tmp.isSuperset(phs0) + ", " +
			phs0.contains(R) + ", " + tmp.isSubset(phs0) + ", " +
			phs0.isSuperset(tmp) + "; " + R);
		println(phs0);
		println(tmp);
		//FCachedHashSet.debug = true;
		//phs0.isSubset(tmp);
		exit();
	    }
	    phs0 = tmp;
	}
	FSet phs1 = new FCachedHashSet();
	HashSet hs1 = new HashSet();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FSet tmp = phs1.with(R);
	    hs1.add(R);
	    if (!((FCachedHashSet)tmp).verify()) {
		println("FCachedHashSet Verification failure on iteration " + i);
		println(((FCachedHashSet)phs1).dump());
		println("Adding " + R);
		println(((FCachedHashSet)tmp).dump());
		exit();
	    }
	    if (tmp.hashCode() != hs1.hashCode()) {
		println("FCachedHashSet hashCode failed on phs1 on iteration " + i);
		println(tmp);
		println(hs1);
		println("Adding " + R + "; " + tmp.hashCode() + ", " + hs1.hashCode());
		exit();
	    }
	    if (!phs1.isSubset(tmp) || !tmp.isSuperset(phs1) ||
		(!phs1.contains(R) && (tmp.isSubset(phs1) || phs1.isSuperset(tmp)))) {
		println("FCachedHashSet is{Sub,Super}set failed (phs1) on iteration " + i);
		println(phs1.isSubset(tmp) + ", " + tmp.isSuperset(phs1) + ", " +
			phs1.contains(R) + ", " + tmp.isSubset(phs1) + ", " +
			phs1.isSuperset(tmp) + "; " + R);
		println(phs1);
		println(tmp);
		exit();
	    }
	    phs1 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FSet tmp = phs0.less(R);
	    hs0.remove(R);
	    if (!((FCachedHashSet)tmp).verify()) {
		println("FCachedHashSet Verification failure on iteration " + i);
		println(((FCachedHashSet)phs0).dump());
		println("Removing " + (R == null ? "null" : "" + R));
		println(((FCachedHashSet)tmp).dump());
		exit();
	    }
	    phs0 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FSet tmp = phs1.less(R);
	    hs1.remove(R);
	    if (!((FCachedHashSet)tmp).verify()) {
		println("FCachedHashSet Verification failure on iteration " + i);
		println(((FCachedHashSet)phs1).dump());
		println("Removing " + (R == null ? "null" : "" + R));
		println(((FCachedHashSet)tmp).dump());
		exit();
	    }
	    if (tmp.hashCode() != hs1.hashCode()) {
		println("FCachedHashSet hashCode failed on phs1 on iteration " + i);
		println(tmp);
		println(hs1);
		println("Removing " + R + "; " + tmp.hashCode() + ", " + hs1.hashCode());
		exit();
	    }
	    if (!tmp.equals(hs1)) {
		println("FCachedHashSet equality failed on phs1 on iteration " + i);
		println(tmp);
		println(hs1);
		println(new FCachedHashSet(hs1));
		println("Removing " + R + "; " + tmp.hashCode() + ", " + hs1.hashCode());
		exit();
	    }
	    phs1 = tmp;
	}
	if (phs0.hashCode() != hs0.hashCode()) {
	    println("FCachedHashSet hashCode failed on phs0 on iteration " + i);
	    println(phs0);
	    println(hs0);
	    exit();
	}
	if (phs1.hashCode() != hs1.hashCode()) {
	    println("FCachedHashSet hashCode failed on phs1 on iteration " + i);
	    exit();
	}
	if (!phs0.equals(hs0)) {
	    println("FCachedHashSet Equality failed (phs0, A) on iteration " + i);
	    println(phs0);
	    println(((FCachedHashSet)phs0).dump());
	    println(new TreeSet(hs0));
	    exit();
	}
	if (!phs0.equals(new FCachedHashSet(hs0))) {
	    println("FCachedHashSet Equality failed (phs0, B) on iteration " + i);
	    println(phs0);
	    println(((FCachedHashSet)phs0).dump());
	    FCachedHashSet nphs0 = new FCachedHashSet(hs0);
	    println(nphs0);
	    println(nphs0.dump());
	    exit();
	}
	if (!phs0.equals(new FCachedHashSet(new ArrayList(hs0)))) {
	    println("FCachedHashSet construction from ArrayList failed (phs0) on iteration "
		    + i);
	    exit();
	}
	if (!phs0.equals(new FCachedHashSet(hs0.toArray()))) {
	    println("FCachedHashSet construction from array failed (phs0) on iteration "
		    + i);
	    exit();
	}
	if (!phs1.equals(hs1)) {
	    println("FCachedHashSet Equality failed (phs1, A) on iteration " + i);
	    println(phs1);
	    println(hs1);
	    println(new FCachedHashSet(hs1));
	    exit();
	}
	// Next line also tests constructor from `Object[]'
	if (!phs1.equals(new FCachedHashSet(hs1.toArray()))) {
	    println("FCachedHashSet Equality failed (phs1, B) on iteration " + i);
	    exit();
	}
	FSet phsu = phs0.union(phs1);
	HashSet hsu = (HashSet)hs0.clone();
	hsu.addAll(hs1);
	if (!((FCachedHashSet)phsu).verify() || !phsu.equals(hsu)) {
	    println("FCachedHashSet Union failed on iteration " + i);
	    println(phs0);
	    println(phs1);
	    if (!((FCachedHashSet)phsu).verify())
		println(((FCachedHashSet)phsu).dump());
	    println(phsu.size() + ", " + hsu.size());
	    println(phsu);
	    println(hsu);
	    exit();
	}
	if (!phsu.equals(new FCachedHashSet(hsu))) {
	    println("FCachedHashSet Equality failed (phsu) on iteration " + i);
	}
	FSet phsi = phs0.intersection(phs1);
	HashSet hsi = (HashSet)hs0.clone();
	hsi.retainAll(hs1);
	if (!((FCachedHashSet)phsi).verify() || !phsi.equals(hsi)) {
	    println("FCachedHashSet Intersection failed on iteration " + i);
	    println(phs0);
	    println(((FCachedHashSet)phs0).dump());
	    println(phs1);
	    println(((FCachedHashSet)phs1).dump());
	    if (!((FCachedHashSet)phsi).verify())
		println(((FCachedHashSet)phsi).dump());
	    println(phsi.size() + ", " + hsi.size());
	    println(phsi);
	    println(new TreeSet(hsi));
	    exit();
	}
	if (!phsi.isSubset(phs0) || !phsi.isSubset(phs1)) {
	    println("FCachedHashSet isSubset failed on iteration " + i);
	    exit();
	}
	if (!phsi.equals(new FCachedHashSet(hsi))) {
	    println("FCachedHashSet Equality failed (phsi) on iteration " + i);
	}
	FSet phsd = phs0.difference(phs1);
	HashSet hsd = (HashSet)hs0.clone();
	hsd.removeAll(hs1);
	if (!((FCachedHashSet)phsd).verify() || !phsd.equals(hsd)) {
	    println("FCachedHashSet Difference failed on iteration " + i);
	    println(phs0);
	    println(((FCachedHashSet)phs0).dump());
	    println(phs1);
	    println(((FCachedHashSet)phs1).dump());
	    //if (!((FCachedHashSet)phsd).verify())
	    println(phsd.size() + ", " + hsd.size());
	    println(phsd);
	    println(((FCachedHashSet)phsd).dump());
	    println(hsd);
	    exit();
	}
	if (!phsd.equals(new FCachedHashSet(hsd))) {
	    println("FCachedHashSet Equality failed (phsd) on iteration " + i);
	}
	FSet nphs0 = new FCachedHashSet(phs0);
	nphs0 = nphs0.less(pick(rand, nphs0));
	FSet phs0a = phs0.less(pick(rand, phs0));
	if (sgn(((FCachedHashSet)phs0a).compareTo(nphs0)) !=
	      compare(phs0a, nphs0)) {
	    println("FCachedHashSet Compare failed (phs0) on iteration " + i);
	    println(((FCachedHashSet)phs0a).compareTo(nphs0) + ", " +
		    compare(phs0a, nphs0));
	    println(phs0a);
	    println(((FCachedHashSet)phs0a).dump());
	    println(nphs0);
	    println(((FCachedHashSet)nphs0).dump());
	    exit();
	}
	if (phs0a.equals(nphs0) != equals(phs0a, nphs0)) {
	    println("FCachedHashSet equality failed (phs0a) on iteration " + i);
	    exit();
	}
	FSet nphs1 = new FCachedHashSet(phs1);
	nphs1 = nphs1.less(pick(rand, nphs1));
	FSet phs1a = phs1.less(pick(rand, phs1));
	if (sgn(((FCachedHashSet)phs1a).compareTo(nphs1)) !=
	      compare(phs1a, nphs1)) {
	    println("FCachedHashSet Compare failed (phs1) on iteration " + i);
	    exit();
	}
	if (phs1a.equals(nphs1) != equals(phs1a, nphs1)) {
	    println("FCachedHashSet equality failed (phs1a) on iteration " + i);
	    exit();
	}
	while (!phs0.isEmpty()) {
	    MyInteger x = phs0.arb();
	    if (!phs0.contains(x) || !hs0.contains(x)) {
		println("FCachedHashSet arb/contains failed on iteration " + i);
		exit();
	    }
	    phs0 = phs0.less(x);
	    hs0.remove(x);
	    if (hs0.isEmpty() != phs0.isEmpty()) {
		println("FCachedHashSet less/isEmpty failed on iteration " + i);
		exit();
	    }
	}
	if (i % 50 == 0) {
	    try {
		// Check handling of null set
		FSet phsser = (i == 0 ? phs0 : phs1);
		FileOutputStream fos = new FileOutputStream("phs.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(phsser);
		oos.close();
		FileInputStream fis = new FileInputStream("phs.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FSet nphsser = (FSet)ois.readObject();
		ois.close();
		if (!phsser.equals(nphsser)) {
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
	return phs1;
    }
*/

    static void testFTreeMap(Random rand, int i, FTreeSet<MyInteger> set) {
	FTreeMap<MyInteger, MyInteger> ptm0 =
	    new FTreeMap<MyInteger, MyInteger>(TestComparator.Instance);
	TreeMap<MyInteger, MyInteger> tm0 = new TreeMap<MyInteger, MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200), v = rand.nextInt(3);
	    MyInteger R = new MyInteger(r), V = new MyInteger(v);
	    FTreeMap<MyInteger, MyInteger> tmp = ptm0.with(R, V);
	    tm0.put(R, V);
	    if (!tmp.verify()) {
		println("FTreeMap Verification failure on iteration " + i);
		println(ptm0.dump());
		println("Adding " + R + ", " + V);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != tm0.hashCode()) {
		println("FTreeMap hashCode failed on ptm0 on iteration " + i);
		println(ptm0);
		println(ptm0.dump());
		println("Adding " + r + " -> " + v);
		println(tmp.dump());
		println(tm0);
		exit();
	    }
	    ptm0 = tmp;
	}
	FTreeMap<MyInteger, MyInteger> ptm1 = new FTreeMap<MyInteger, MyInteger>(TestComparator.Instance);
	TreeMap<MyInteger, MyInteger> tm1 = new TreeMap<MyInteger, MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200), v = rand.nextInt(3);
	    MyInteger R = new MyInteger(r), V = new MyInteger(v);
	    FTreeMap<MyInteger, MyInteger> tmp = ptm1.with(R, V);
	    tm1.put(R, V);
	    if (!tmp.verify()) {
		println("FTreeMap Verification failure on iteration " + i);
		println(ptm1.dump());
		println("Adding " + R + ", " + V);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != tm1.hashCode()) {
		println("FTreeMap hashCode failed on ptm1 on iteration " + i);
		println(ptm1);
		println(ptm1.dump());
		println("Adding " + r + " -> " + v);
		println(tmp.dump());
		println(tm1);
		exit();
	    }
	    ptm1 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = new MyInteger(r);
	    if (!equals(ptm0.get(R), tm0.get(R))) {
		println("FTreeMap get (ptm0) failed on iteration " + i);
		exit();
	    }
	    FTreeMap<MyInteger, MyInteger> tmp = ptm0.less(R);
	    tm0.remove(R);
	    if (!tmp.verify()) {
		println("FTreeMap Verification failure on iteration " + i);
		println(ptm0.dump());
		println("Removing " + R);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != tm0.hashCode()) {
		println("FTreeMap hashCode failed on ptm0 on iteration " + i);
		println(ptm0);
		println(ptm0.dump());
		println("Removing " + r);
		println(tmp.dump());
		println(tm0);
		exit();
	    }
	    ptm0 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = new MyInteger(r);
	    if (!equals(ptm1.get(R), tm1.get(R))) {
		println("FTreeMap get failed (ptm1) on iteration " + i);
		exit();
	    }
	    FTreeMap<MyInteger, MyInteger> tmp = ptm1.less(R);
	    tm1.remove(R);
	    if (!tmp.verify()) {
		println("FTreeMap Verification failure on iteration " + i);
		println(ptm1.dump());
		println("Removing " + R);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != tm1.hashCode()) {
		println("FTreeMap hashCode failed on ptm1 on iteration " + i);
		println(ptm1);
		println(ptm1.dump());
		println("Removing " + r);
		println(tmp.dump());
		println(tm1);
		exit();
	    }
	    ptm1 = tmp;
	}
	if (i == 0) {
	    FTreeMap<MyInteger, MyInteger> tmp = ptm0.with(null, null);
	    if (!tmp.verify() || !tmp.containsKey(null) || tmp.firstKey() != null) {
		println("FTreeMap Verification failure on iteration " + i);
		println(ptm0.dump());
		println("Adding null");
		println(tmp.dump());
		exit();
	    }
	    tmp = tmp.less(null);
	    if (!tmp.verify() || tmp.containsKey(null)) {
		println("FTreeMap Verification failure on iteration " + i);
		println(ptm0.dump());
		println("Removing null");
		println(tmp.dump());
		exit();
	    }
	}		
	if (ptm0.hashCode() != tm0.hashCode()) {
	    println("FTreeMap hashCode failed on ptm0 on iteration " + i);
	    println(ptm0);
	    println(tm0);
	    exit();
	}
	if (ptm1.hashCode() != tm1.hashCode()) {
	    println("FTreeMap hashCode failed on ptm1 on iteration " + i);
	    exit();
	}
	if (!ptm0.equals(tm0)) {
	    println("FTreeMap Equality failed (ptm0, A) on iteration " + i);
	    println(ptm0.dump());
	    println(tm0);
	    exit();
	}
	if (!ptm0.equals(new FTreeMap<MyInteger, MyInteger>(tm0))) {
	    println("FTreeMap Equality failed (ptm0, B) on iteration " + i);
	    println(ptm0.dump());
	    println(tm0);
	    exit();
	}
	if (!ptm0.equals(new FTreeMap<MyInteger, MyInteger>(tm0, TestComparator.Instance))) {
	    println("FTreeMap Equality failed (ptm0, C) on iteration " + i);
	    println(ptm0.dump());
	    println(tm0);
	    exit();
	}
	if (!ptm1.equals(tm1)) {
	    println("FTreeMap Equality failed (ptm1, A) on iteration " + i);
	    println(ptm1.dump());
	    println(tm1);
	    exit();
	}
	if (!ptm1.equals(new FTreeMap<MyInteger, MyInteger>(tm1))) {
	    println("FTreeMap Equality failed (ptm1, B) on iteration " + i);
	    println(ptm1.dump());
	    exit();
	}
	if (!ptm1.equals(new FTreeMap<MyInteger, MyInteger>(tm1, TestComparator.Instance))) {
	    println("FTreeMap Equality failed (ptm1, C) on iteration " + i);
	    println(ptm1.dump());
	    exit();
	}
	if (ptm0.firstKey().intValue() / 2 != tm0.firstKey().intValue() / 2) {
	    println("FTreeMap `firstKey' failed (ptm0) on iteration " + i);
	    exit();
	}
	if (ptm1.firstKey().intValue() / 2 != tm1.firstKey().intValue() / 2) {
	    println("FTreeMap `firstKey' failed (ptm1) on iteration " + i);
	    exit();
	}
	if (ptm0.lastKey().intValue() / 2 != tm0.lastKey().intValue() / 2) {
	    println("FTreeMap `lastKey' failed (ptm0) on iteration " + i);
	    exit();
	}
	if (ptm1.lastKey().intValue() / 2 != tm1.lastKey().intValue() / 2) {
	    println("FTreeMap `lastKey' failed (ptm1) on iteration " + i);
	    exit();
	}
	FTreeMap<MyInteger, MyInteger> ptmm = ptm0.union(ptm1);
	TreeMap<MyInteger, MyInteger> tmm = (TreeMap<MyInteger, MyInteger>)tm0.clone();
	tmm.putAll(tm1);
	if (!ptmm.verify() || !ptmm.equals(tmm)) {
	    println("FTreeMap Union failed on iteration " + i);
	    println(ptm0);
	    println(ptm0.dump());
	    println(ptm1);
	    println(ptm1.dump());
	    //if (!((FTreeMap)ptmm).verify())
	    println(ptmm.size() + ", " + tmm.size());
	    println(ptmm);
	    println(ptmm.dump());
	    println(tmm);
	    exit();
	}
	if (!ptmm.equals(new FTreeMap<MyInteger, MyInteger>(tmm))) {
	    println("FTreeMap Equality failed (ptmm) on iteration " + i);
	    exit();
	}
	FTreeMap<MyInteger, MyInteger> ptmr = ptm0.restrictedTo(set);
	TreeMap<MyInteger, MyInteger> tmr = (TreeMap<MyInteger, MyInteger>)tm0.clone();
	for (Iterator it = tmr.keySet().iterator(); it.hasNext(); ) {
	    Object k = it.next();
	    if (!set.contains(k)) it.remove();
	}
	if (!ptmr.verify() || !ptmr.equals(tmr)) {
	    println("FTreeMap restrict failed on iteration " + i);
	    exit();
	}
	ptmr = ptm0.restrictedFrom(set);
	tmr = (TreeMap<MyInteger, MyInteger>)tm0.clone();
	for (Iterator it = tmr.keySet().iterator(); it.hasNext(); ) {
	    Object k = it.next();
	    if (set.contains(k)) it.remove();
	}
	if (!ptmr.verify() || !ptmr.equals(tmr)) {
	    println("FTreeMap restrictNot failed on iteration " + i);
	    exit();
	}
	ptm0 = ptm0.less(null);		// for benefit of `compare' below
	FSet<MyInteger> ptm0_dom = ptm0.domain();
	FTreeMap<MyInteger, MyInteger> ptm0a =
	    ptm0.less(pick(rand, ptm0_dom)).with(pick(rand, ptm0_dom),
						 new MyInteger(rand.nextInt(3)));
	FTreeMap<MyInteger, MyInteger> ptm0b =
	    ptm0.less(pick(rand, ptm0_dom)).with(pick(rand, ptm0_dom),
						 new MyInteger(rand.nextInt(3)));
	if (sgn(ptm0a.compareTo(ptm0b)) != compare(ptm0a, ptm0b)) {
	    println("FTreeMap Compare failed (ptm0) on iteration " + i);
	    println(ptm0a.dump());
	    println(ptm0b.dump());
	    println(ptm0a);
	    println(ptm0b);
	    println(ptm0a.compareTo(ptm0b));
	    println(compare(ptm0a, ptm0b));
	    exit();
	}
	ptm1 = ptm1.less(null);
	FSet<MyInteger> ptm1_dom = ptm1.domain();
	FTreeMap<MyInteger, MyInteger> ptm1a =
	    ptm1.less(pick(rand, ptm1_dom)).with(pick(rand, ptm1_dom),
						 new MyInteger(rand.nextInt(3)));
	FTreeMap<MyInteger, MyInteger> ptm1b =
	    ptm1.less(pick(rand, ptm1_dom)).with(pick(rand, ptm1_dom),
						 new MyInteger(rand.nextInt(3)));
	if (sgn(ptm1a.compareTo(ptm1b)) != compare(ptm1a, ptm1b)) {
	    println("FTreeMap Compare failed (ptm1) on iteration " + i);
	    println(ptm1a.dump());
	    println(ptm1b.dump());
	    println(ptm1a);
	    println(ptm1b);
	    println(ptm1a.compareTo(ptm1b));
	    println(compare(ptm1a, ptm1b));
	    exit();
	}
	int lo = rand.nextInt(150) - 25;
	int hi = rand.nextInt(125 - lo) + lo;
	lo *= 2;	// they have to be even because of the comparator behavior
	hi *= 2;
	MyInteger Lo = new MyInteger(lo);
	MyInteger Hi = new MyInteger(hi);
	SortedMap<MyInteger, MyInteger> ptsm = ptm0.subMap(Lo, Hi);
	SortedMap<MyInteger, MyInteger> tsm = tm0.subMap(Lo, Hi);
	if (!ptsm.equals(tsm)) {
	    println("FTreeMap subMap failed on iteration " + i);
	    println("[" + lo + ", " + hi + ")");
	    println(ptsm);
	    println(tsm);
	    exit();
	}
	if (!ptm0.headMap(Hi).equals(tm0.headMap(Hi))) {
	    println("FTreeMap headMap failed on iteration " + i);
	    exit();
	}
	if (!ptm0.tailMap(Lo).equals(tm0.tailMap(Lo))) {
	    println("FTreeMap tailMap failed on iteration " + i);
	    exit();
	}
	if (i % 50 == 0) {
	    try {
		// Check handling of null map
		FMap<MyInteger, MyInteger> ptmser =
		    (i == 0 ? new FTreeMap<MyInteger, MyInteger>(TestComparator.Instance)
		     : ptm0);
		FileOutputStream fos = new FileOutputStream("ptm.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(ptmser);
		oos.close();
		FileInputStream fis = new FileInputStream("ptm.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FMap<MyInteger, MyInteger> nptmser =
		    (FMap<MyInteger, MyInteger>)ois.readObject();
		ois.close();
		if (!ptmser.equals(nptmser)) {
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
	FHashMap<MyInteger, MyInteger> phm0 = new FHashMap<MyInteger, MyInteger>();
	HashMap<MyInteger, MyInteger> hm0 = new HashMap<MyInteger, MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200), v = rand.nextInt(3);
	    MyInteger R = r == 57 ? null : new MyInteger(r), V = new MyInteger(v);
	    FHashMap<MyInteger, MyInteger> tmp = phm0.with(R, V);
	    hm0.put(R, V);
	    if (!tmp.verify()) {
		println("FHashMap Verification failure on iteration " + i);
		println(phm0.dump());
		println("Adding " + R + ", " + V);
		println(tmp.dump());
		exit();
	    }
	    phm0 = tmp;
	}
	FHashMap<MyInteger, MyInteger> phm1 = new FHashMap<MyInteger, MyInteger>();
	HashMap<MyInteger, MyInteger> hm1 = new HashMap<MyInteger, MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200), v = rand.nextInt(3);
	    MyInteger R = r == 57 ? null : new MyInteger(r), V = new MyInteger(v);
	    FHashMap<MyInteger, MyInteger> tmp = phm1.with(R, V);
	    hm1.put(R, V);
	    if (!tmp.verify()) {
		println("FHashMap Verification failure on iteration " + i);
		println(phm1.dump());
		println("Adding " + R + ", " + V);
		println(tmp.dump());
		exit();
	    }
	    phm1 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FHashMap<MyInteger, MyInteger> tmp = phm0.less(R);
	    hm0.remove(R);
	    if (!tmp.verify()) {
		println("FHashMap Verification failure on iteration " + i);
		println(phm0.dump());
		println("Removing " + R);
		println(tmp.dump());
		exit();
	    }
	    phm0 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FHashMap<MyInteger, MyInteger> tmp = phm1.less(R);
	    hm1.remove(R);
	    if (!tmp.verify()) {
		println("FHashMap Verification failure on iteration " + i);
		println(phm1.dump());
		println("Removing " + R);
		println(tmp.dump());
		exit();
	    }
	    phm1 = tmp;
	}
	if (!phm0.equals(hm0)) {
	    println("FHashMap Equality failed (phm0, A) on iteration " + i);
	    println(phm0.dump());
	    println(phm0);
	    println(hm0);
	    exit();
	}
	if (!phm0.equals(new FHashMap<MyInteger, MyInteger>(hm0))) {
	    println("FHashMap Equality failed (phm0, B) on iteration " + i);
	    println(phm0.dump());
	    println(hm0);
	    exit();
	}
	if (!phm1.equals(hm1)) {
	    println("FHashMap Equality failed (phm1, A) on iteration " + i);
	    println(phm1.dump());
	    println(phm1);
	    println(hm1);
	    exit();
	}
	if (!phm1.equals(new FHashMap<MyInteger, MyInteger>(hm1))) {
	    println("FHashMap Equality failed (phm1, B) on iteration " + i);
	    println(phm1.dump());
	    exit();
	}
	FHashMap<MyInteger, MyInteger> phmm = phm0.union(phm1);
	HashMap<MyInteger, MyInteger> hmm = (HashMap<MyInteger, MyInteger>)hm0.clone();
	hmm.putAll(hm1);
	if (!phmm.verify() || !phmm.equals(hmm)) {
	    println("FHashMap Union failed on iteration " + i);
	    println(phm0);
	    println(phm0.dump());
	    println(phm1);
	    println(phm1.dump());
	    //if (!phmm.verify())
	    println(phmm.size() + ", " + hmm.size());
	    println(phmm);
	    println(phmm.dump());
	    println(hmm);
	    exit();
	}
	if (!phmm.equals(new FHashMap<MyInteger, MyInteger>(hmm))) {
	    println("FHashMap Equality failed (phmm) on iteration " + i);
	}
	FHashMap<MyInteger, MyInteger> phmr = phm0.restrictedTo(set);
	HashMap<MyInteger, MyInteger> hmr = (HashMap<MyInteger, MyInteger>)hm0.clone();
	for (Iterator it = hmr.keySet().iterator(); it.hasNext(); ) {
	    Object k = it.next();
	    if (!set.contains(k)) it.remove();
	}
	if (!phmr.verify() || !phmr.equals(hmr)) {
	    println("FHashMap restrictedTo failed on iteration " + i);
	    println(phmr);
	    println(hmr);
	    exit();
	}
	phmr = phm0.restrictedFrom(set);
	hmr = (HashMap<MyInteger, MyInteger>)hm0.clone();
	for (Iterator it = hmr.keySet().iterator(); it.hasNext(); ) {
	    Object k = it.next();
	    if (set.contains(k)) it.remove();
	}
	if (!phmr.verify() || !phmr.equals(hmr)) {
	    println("FHashMap restrictedFrom failed on iteration " + i);
	    println(phmr);
	    println(hmr);
	    exit();
	}
	phm0 = phm0.less(null);		// for benefit of `compare' below
	FSet<MyInteger> phm0_dom = phm0.domain();
	FHashMap<MyInteger, MyInteger> phm0a =
	    phm0.less(pick(rand, phm0_dom)).with(pick(rand, phm0_dom),
						 new MyInteger(rand.nextInt(3)));
	FHashMap<MyInteger, MyInteger> phm0b =
	    phm0.less(pick(rand, phm0_dom)).with(pick(rand, phm0_dom),
						 new MyInteger(rand.nextInt(3)));
	if (sgn(phm0a.compareTo(phm0b)) != compare(phm0a, phm0b)) {
	    println("FHashMap Compare failed (phm0) on iteration " + i);
	    println(phm0a.dump());
	    println(phm0b.dump());
	    println(phm0a);
	    println(phm0b);
	    println(phm0a.compareTo(phm0b));
	    println(compare(phm0a, phm0b));
	    exit();
	}
	phm1 = phm1.less(null);
	FSet<MyInteger> phm1_dom = phm1.domain();
	FHashMap<MyInteger, MyInteger> phm1a =
	    phm1.less(pick(rand, phm1_dom)).with(pick(rand, phm1_dom),
						 new MyInteger(rand.nextInt(3)));
	FHashMap<MyInteger, MyInteger> phm1b =
	    phm1.less(pick(rand, phm1_dom)).with(pick(rand, phm1_dom),
						 new MyInteger(rand.nextInt(3)));
	if (sgn(phm1a.compareTo(phm1b)) != compare(phm1a, phm1b)) {
	    println("FHashMap Compare failed (phm1) on iteration " + i);
	    println(phm1a.dump());
	    println(phm1b.dump());
	    println(phm1a);
	    println(phm1b);
	    println(phm1a.compareTo(phm1b));
	    println(compare(phm1a, phm1b));
	    exit();
	}
	if (i % 50 == 0) {
	    try {
		// Check handling of null map
		FMap<MyInteger, MyInteger> phmser =
		    (i == 0 ? new FHashMap<MyInteger, MyInteger>() : phm0);
		FileOutputStream fos = new FileOutputStream("phm.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(phmser);
		oos.close();
		FileInputStream fis = new FileInputStream("phm.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FMap<MyInteger, MyInteger> nphmser = (FMap<MyInteger, MyInteger>)ois.readObject();
		ois.close();
		if (!phmser.equals(nphmser)) {
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
	FMap phm0 = new FCachedHashMap();
	HashMap hm0 = new HashMap();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200), v = rand.nextInt(3);
	    MyInteger R = r == 57 ? null : new MyInteger(r), V = new MyInteger(v);
	    FMap tmp = phm0.with(R, V);
	    hm0.put(R, V);
	    if (!((FCachedHashMap)tmp).verify()) {
		println("FCachedHashMap Verification failure on iteration " + i);
		println(((FCachedHashMap)phm0).dump());
		println("Adding " + R + ", " + V);
		println(((FCachedHashMap)tmp).dump());
		exit();
	    }
	    phm0 = tmp;
	}
	FMap phm1 = new FCachedHashMap();
	HashMap hm1 = new HashMap();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200), v = rand.nextInt(3);
	    MyInteger R = r == 57 ? null : new MyInteger(r), V = new MyInteger(v);
	    FMap tmp = phm1.with(R, V);
	    hm1.put(R, V);
	    if (!((FCachedHashMap)tmp).verify()) {
		println("FCachedHashMap Verification failure on iteration " + i);
		println(((FCachedHashMap)phm1).dump());
		println("Adding " + R + ", " + V);
		println(((FCachedHashMap)tmp).dump());
		exit();
	    }
	    phm1 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FMap tmp = phm0.less(R);
	    hm0.remove(R);
	    if (!((FCachedHashMap)tmp).verify()) {
		println("FCachedHashMap Verification failure on iteration " + i);
		println(((FCachedHashMap)phm0).dump());
		println("Removing " + R);
		println(((FCachedHashMap)tmp).dump());
		exit();
	    }
	    phm0 = tmp;
	}
	for (int j = 0; j < 20; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    FMap tmp = phm1.less(R);
	    hm1.remove(R);
	    if (!((FCachedHashMap)tmp).verify()) {
		println("FCachedHashMap Verification failure on iteration " + i);
		println(((FCachedHashMap)phm1).dump());
		println("Removing " + R);
		println(((FCachedHashMap)tmp).dump());
		exit();
	    }
	    phm1 = tmp;
	}
	if (!phm0.equals(hm0)) {
	    println("FCachedHashMap Equality failed (phm0, A) on iteration " + i);
	    println(((FCachedHashMap)phm0).dump());
	    println(phm0);
	    println(hm0);
	    exit();
	}
	if (!phm0.equals(new FCachedHashMap(hm0))) {
	    println("FCachedHashMap Equality failed (phm0, B) on iteration " + i);
	    println(((FCachedHashMap)phm0).dump());
	    println(hm0);
	    exit();
	}
	if (!phm1.equals(hm1)) {
	    println("FCachedHashMap Equality failed (phm1, A) on iteration " + i);
	    println(((FCachedHashMap)phm1).dump());
	    println(hm1);
	    exit();
	}
	if (!phm1.equals(new FCachedHashMap(hm1))) {
	    println("FCachedHashMap Equality failed (phm1, B) on iteration " + i);
	    println(((FCachedHashMap)phm1).dump());
	    exit();
	}
	FMap phmm = phm0.union(phm1);
	HashMap hmm = (HashMap)hm0.clone();
	hmm.putAll(hm1);
	if (!((FCachedHashMap)phmm).verify() || !phmm.equals(hmm)) {
	    println("FCachedHashMap Union failed on iteration " + i);
	    println(phm0);
	    println(((FCachedHashMap)phm0).dump());
	    println(phm1);
	    println(((FCachedHashMap)phm1).dump());
	    //if (!((FCachedHashMap)phmm).verify())
	    println(phmm.size() + ", " + hmm.size());
	    println(phmm);
	    println(((FCachedHashMap)phmm).dump());
	    println(hmm);
	    exit();
	}
	if (!phmm.equals(new FCachedHashMap(hmm))) {
	    println("FCachedHashMap Equality failed (phmm) on iteration " + i);
	}
	FMap phmr = phm0.restrict(set);
	HashMap hmr = (HashMap)hm0.clone();
	for (Iterator it = hmr.keySet().iterator(); it.hasNext(); ) {
	    Object k = it.next();
	    if (!set.contains(k)) it.remove();
	}
	if (!((FCachedHashMap)phmr).verify() || !phmr.equals(hmr)) {
	    println("FCachedHashMap restrict failed on iteration " + i);
	    println(phmr);
	    println(hmr);
	    exit();
	}
	phmr = phm0.restrictNot(set);
	hmr = (HashMap)hm0.clone();
	for (Iterator it = hmr.keySet().iterator(); it.hasNext(); ) {
	    Object k = it.next();
	    if (set.contains(k)) it.remove();
	}
	if (!((FCachedHashMap)phmr).verify() || !phmr.equals(hmr)) {
	    println("FCachedHashMap restrictNot failed on iteration " + i);
	    println(phmr);
	    println(hmr);
	    exit();
	}
	phm0 = phm0.less(null);		// for benefit of `compare' below
	FSet phm0_dom = phm0.domain();
	FMap phm0a = phm0.less(pick(rand, phm0_dom))
			.with(pick(rand, phm0_dom), new MyInteger(rand.nextInt(3)));
	FMap phm0b = phm0.less(pick(rand, phm0_dom))
			.with(pick(rand, phm0_dom), new MyInteger(rand.nextInt(3)));
	if (sgn(((FCachedHashMap)phm0a).compareTo(phm0b)) !=
	      compare(phm0a, phm0b)) {
	    println("FHashMap Compare failed (phm0) on iteration " + i);
	    println(((FHashMap)phm0a).dump());
	    println(((FHashMap)phm0b).dump());
	    println(phm0a);
	    println(phm0b);
	    println(((FHashMap)phm0a).compareTo(phm0b));
	    println(compare(phm0a, phm0b));
	    exit();
	}
	phm1 = phm1.less(null);
	FSet phm1_dom = phm1.domain();
	FMap phm1a = phm1.less(pick(rand, phm1_dom))
			.with(pick(rand, phm1_dom), new MyInteger(rand.nextInt(3)));
	FMap phm1b = phm1.less(pick(rand, phm1_dom))
			.with(pick(rand, phm1_dom), new MyInteger(rand.nextInt(3)));
	if (sgn(((FCachedHashMap)phm1a).compareTo(phm1b)) !=
	      compare(phm1a, phm1b)) {
	    println("FHashMap Compare failed (phm1) on iteration " + i);
	    println(((FHashMap)phm1a).dump());
	    println(((FHashMap)phm1b).dump());
	    println(phm1a);
	    println(phm1b);
	    println(((FHashMap)phm1a).compareTo(phm1b));
	    println(compare(phm1a, phm1b));
	    exit();
	}
	if (i % 50 == 0) {
	    try {
		// Check handling of null map
		FMap phmser = (i == 0 ? new FCachedHashMap() : phm0);
		FileOutputStream fos = new FileOutputStream("phm.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(phmser);
		oos.close();
		FileInputStream fis = new FileInputStream("phm.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FMap nphmser = (FMap)ois.readObject();
		ois.close();
		if (!phmser.equals(nphmser)) {
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

    static void testFTreeList(Random rand, int i) {
	FTreeList<MyInteger> ptl0 = new FTreeList<MyInteger>();
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
	    if (ptl0.indexOf(R) != al0.indexOf(R)) {
		println("FTreeList indexOf failed (ptl0) on iteration " + i);
		exit();
	    }
	    if (which == 0 && !al0.isEmpty()) {
		if (!equals(ptl0.get(pos), al0.get(pos))) {
		    println("FTreeList get failed (ptl0) on iteration " + i);
		    exit();
		}
		tmp = ptl0.with(pos, R);
		al0.set(pos, R);
	    } else if (which == 1 && !al0.isEmpty()) {
		tmp = ptl0.less(pos);
		al0.remove(pos);
	    } else {
		tmp = ptl0.withInserted(pos, R);
		al0.add(pos, R);
	    }
	    if (!tmp.verify()) {
		println("FTreeList Verification failed on iteration " + i);
		println(ptl0.dump());
		if (which == 1)
		    println("Deleting at " + pos);
		else
		    println((which == 0 ? "Writing " : "Inserting ") + R + " at " + pos);
		println(tmp.dump());
		exit();
	    }
	    if (tmp.hashCode() != al0.hashCode()) {
		println("FTreeList hashCode failed on iteration " + i);
		println(ptl0);
		println(ptl0.dump());
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
	    ptl0 = tmp;
	}
	FTreeList<MyInteger> ptl1 = new FTreeList<MyInteger>();
	ArrayList<MyInteger> al1 = new ArrayList<MyInteger>();
	for (int j = 0; j < 100; ++j) {
	    int r = rand.nextInt(200);
	    MyInteger R = r == 57 ? null : new MyInteger(r);
	    int pos = al1.isEmpty() ? 0 : rand.nextInt(al1.size());
	    int which = rand.nextInt(5);
	    FTreeList<MyInteger> tmp;
	    if (ptl1.lastIndexOf(R) != al1.lastIndexOf(R)) {
		println("FTreeList lastIndexOf failed (ptl1) on iteration " + i);
		exit();
	    }
	    if (which == 0 && !al1.isEmpty()) {
		if (!equals(ptl1.get(pos), al1.get(pos))) {
		    println("FTreeList get failed (ptl1) on iteration " + i);
		    exit();
		}
		tmp = ptl1.with(pos, R);
		al1.set(pos, R);
	    } else if (which == 1 && !al1.isEmpty()) {
		tmp = ptl1.less(pos);
		al1.remove(pos);
	    } else {
		tmp = ptl1.withInserted(pos, R);
		al1.add(pos, R);
	    }
	    if (!tmp.verify()) {
		println("FTreeList Verification failed on iteration " + i);
		println(ptl1.dump());
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
	    ptl1 = tmp;
	}
	if (!ptl0.equals(al0)) {
	    println("FTreeList Equality failed (ptl0, A) on iteration " + i);
	    println(ptl0.dump());
	    println(ptl0);
	    println(al0);
	    exit();
	}
	if (!ptl0.equals(new FTreeList<MyInteger>(al0))) {
	    println("FTreeList Equality failed (ptl0, B) on iteration " + i);
	    println(ptl0.dump());
	    println(al0);
	    exit();
	}
	if (!ptl1.equals(al1)) {
	    println("FTreeList Equality failed (ptl1, A) on iteration " + i);
	    println(ptl1.dump());
	    println(al1);
	    exit();
	}
	if (!ptl1.equals(new FTreeList<MyInteger>(al1))) {
	    println("FTreeList Equality failed (ptl1, B) on iteration " + i);
	    println(ptl1.dump());
	    exit();
	}
	FTreeList<MyInteger> ptlc = ptl0.concat(ptl1);
	ArrayList<MyInteger> alc = (ArrayList<MyInteger>)al0.clone();
	alc.addAll(alc.size(), al1);
	if (!ptlc.equals(alc)) {
	    println("FTreeList concat failed on iteration " + i);
	    exit();
	}
	int lo = rand.nextInt(al0.size()), hi = rand.nextInt(al0.size() - lo) + lo;
	FTreeList<MyInteger> sptl0 = ptl0.subseq(lo, hi);
	List<MyInteger> sal0 = al0.subList(lo, hi);
	if (!sptl0.equals(sal0)) {
	    println("FTreeList subseq failed on iteration " + i);
	    println(ptl0);
	    println(ptl0.dump());
	    println(lo + ", " + hi);
	    println(sptl0);
	    println(sal0);
	    exit();
	}
	int delpos = rand.nextInt(ptl0.size());
	FTreeList<MyInteger> ptl0a = ptl0.less(delpos);
	List<MyInteger> al0a = (List<MyInteger>)al0.clone();
	al0a.remove(delpos);
	if (!ptl0a.equals(al0a)) {
	    println("FTreeList less failed on iteration " + i);
	    println(ptl0);
	    println(ptl0.dump());
	    println("Removing at " + delpos);
	    println(ptl0a);
	    println(ptl0a.dump());
	    exit();
	}
	FTreeList<MyInteger> ptl0b = ptl0.less(rand.nextInt(ptl0.size()));
	if (sgn(ptl0a.compareTo(ptl0b)) !=
	    compare(ptl0a, ptl0b)) {
	    println("FTreeList compareTo failed on iteration " + i);
	    println(ptl0a);
	    println(ptl0b);
	    println(ptl0a.compareTo(ptl0b));
	    println(compare(ptl0a, ptl0b));
	    exit();
	}
	FTreeList<MyInteger> ptl0s = ptl0.sort(TestComparator.Instance);
	ArrayList<MyInteger> al0s = (ArrayList<MyInteger>)al0.clone();
	Collections.sort(al0s, TestComparator.Instance);
	if (!ptl0s.equals(al0s)) {
	    println("FTreeList sort failed on iteration " + i);
	    println(ptl0s);
	    println(al0s);
	    exit();
	}
	ListIterator<MyInteger> ptli = ptl0.listIterator();
	ListIterator<MyInteger> ali = al0.listIterator();
	//FTreeList.debug = true;
	for (int j = 0; j < 400; ++j) {
	    int which = rand.nextInt(2);
	    if (ptli.nextIndex() != ali.nextIndex()) {
		println("FTreeList nextIndex failed on iteration "+ i + "." + j);
		exit();
	    }
	    if (which == 0 && ali.hasPrevious()) {
		//println("Rev");
		if (!ptli.hasPrevious()) {
		    println("FTreeList hasPrevious failed false on iteration " +
			    i + "." + j);
		    exit();
		}
		MyInteger x = ptli.previous();
		MyInteger y = ali.previous();
		if (x == null ? y != null : !x.equals(y)) {
		    println("FTreeList previous failed on iteration " + i + "." + j);
		    exit();
		}
	    } else if (ali.hasNext()) {
		//println("Fwd");
		if (!ali.hasPrevious() && ptli.hasPrevious()) {
		    println("FTreeList hasPrevious failed true on iteration " +
			    i + "." + j);
		    exit();
		}
		if (!ptli.hasNext()) {
		    println("FTreeList hasNext failed false on iteration " +
			    i + "." + j);
		    exit();
		}
		MyInteger x = ptli.next();
		MyInteger y = ali.next();
		if (x == null ? y != null : !x.equals(y)) {
		    println("FTreeList next failed on iteration " + i + "." + j);
		    exit();
		}
	    } else if (ptli.hasNext()) {
		println("FTreeList hasNext failed true on iteration " + i + "." + j);
		exit();
	    }
	}
	if (i % 50 == 0) {
	    try {
		// Check handling of null list
		FList<MyInteger> ptlser = (i == 0 ? new FTreeList<MyInteger>() : ptl0);
		FileOutputStream fos = new FileOutputStream("ptl.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(ptlser);
		oos.close();
		FileInputStream fis = new FileInputStream("ptl.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FList<MyInteger> nptlser = (FList<MyInteger>)ois.readObject();
		ois.close();
		if (!ptlser.equals(nptlser)) {
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
