all:
	javac com/ergy/fset/*.java

test:
	java com/ergy/fset/TestSuite 10000 >& test.out

doc:
	javadoc -link http://java.sun.com/j2se/1.4.2/docs/api/ -overview ../Doc/Java-Overview.html -d ../Doc/JavaDoc com/ergy/fset/*Pure*.java

fset.jar: com/ergy/fset/*.class
	jar cf fset.jar com/ergy/fset/*.class
