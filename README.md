fset-java
=========

[Online API documentation](http://www.ergy.com/fset-java-doc/index.html)

This is the Java implementation of the FSet functional collections library.

_Functional_ collections are more than just immutable: they include "update" operations
which, instead of modifying a collection object in place, return a new one.

Functional collections have been available to Scala and Clojure users for some time, but
there does not seem to have been a high-quality functional collections package designed to
be used directly from Java.  FSet addresses this need.

It's likely that anyone who has found this page is aware of the benefits of functional
collections, but let me touch on them briefly.  Many of these are advantages of functional
programming in general.

* programs are easier to write, easier to understand, and easier to modify

* many concurrency-related problems evaporate (since the collections can't be modified,
they are automatically thread-safe, and there's no concern about one being modified in
one thread while another thread is iterating over it)

* there is no need for defensive cloning or wrapping of returned collections

FSet is implemented using heterogeneous weight-balanced binary trees.  Binary trees are an
excellent all-purpose data structure in the sense that all the important operations have
pretty good time complexity:

* set membership testing, map key lookup, and sequence indexing all take logarithmic time

* equality testing and set intersection, union, and difference all take linear time

* sorting is O(n log n), as usual

The fact that sequence indexing takes logarithmic time might be of some concern, since
we're used to it taking constant time (e.g. as it does on an `ArrayList`), but note that
this applies only to truly random access.  Iterating over any FSet collection takes
amortized constant time per element.

By "heterogeneous" I mean that the bottom two or three levels of tree nodes are replaced by
bounded-length vectors.  This makes for a considerable improvement in space efficiency
with little or no time penalty.  FSet is much more space-efficient than most of the JRE
classes.

For set and map types, FSet provides two implementations:

* "Tree" versions, which use a provided ordering (like the JRE's `TreeSet`)

* "Hash" versions, which are called that because they use hash codes to generate an
ordering; but internally they're still trees, not hash tables

Both of these handle ordering collisions correctly: if the ordering fails to distinguish
two elements, but `equals` on them returns false, they're both retained.  This would be
necessary anyway in the "Hash" versions, because hash code collisions are always possible,
but FSet also does it for the "Tree" versions, unlike `TreeSet`.

These also allow `null` to be used freely as an element or map key.  The "Tree" versions
treat it as coming first in any ordering, and the "Hash" versions treat it as having hash
code 0.

FSet maps return a user-specified default value when `get` is called on a key that is not
in the map.  The default default is `null`, but it can be changed using the `withDefault'
method.
