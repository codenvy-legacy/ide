Use antlr 3.3 to generate parser and lexer for expressions.

java -cp ${PATH_TO_ANTLR}/antlr-3.3-complete.jar:$JAVA_HOME/lib/tools.jar org.antlr.Tool -o ../java com/codenvy/ide/ext/java/jdi/server/expression/Java.g com/codenvy/ide/ext/java/jdi/server/expression/JavaTreeParser.g