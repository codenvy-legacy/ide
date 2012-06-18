Use antlr 3.3 to generate parser and lexer fro expressions.

java -cp ${PATH_TO_ANTLR}/antlr-3.3-complete.jar:$JAVA_HOME/lib/tools.jar org.antlr.Tool -o ../java org/exoplatform/ide/extension/java/jdi/server/expression/Java.g org/exoplatform/ide/extension/java/jdi/server/expression/JavaTreeParser.g