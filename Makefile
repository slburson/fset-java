all:
	javac -source 1.6 -target 1.6 com/ergy/fset/*.java

test:
	java com/ergy/fset/TestSuite 10000 >& test.out

doc:
	javadoc -link http://docs.oracle.com/javase/7/docs/api/ -d Doc/JavaDoc com/ergy/fset/{F,AbstractF,BinaryOp}*.java

fset.jar: all com/ergy/fset/*.class
	jar cf fset.jar com/ergy/fset/*.class
