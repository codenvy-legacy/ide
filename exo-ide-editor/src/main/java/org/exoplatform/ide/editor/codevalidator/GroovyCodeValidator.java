/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.editor.codevalidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.CodeError;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.api.codeassitant.CodeError.CodeErrorType;


/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class GroovyCodeValidator extends CodeValidatorImpl
{
   static int lastImportStatementLineNumber = 0;
   
   /**
    * Short default java types like "int"...
   */
   private static List<String> shortJavaType = Arrays.asList("boolean", "char", "enum", "byte", "double", "float", "int", "long", "short", "void");
  
   public static String getFqnFromDefaultPackages(String javaType)
   {      
      Iterator<String> iterator = defaultPackages.keySet().iterator();
      while (iterator.hasNext())
      {
         String defaultPackagePrefix = iterator.next(); 
         List<String> defaultClasses = defaultPackages.get(defaultPackagePrefix);
                 
         if (defaultClasses.contains(javaType))
         {
            return defaultPackagePrefix + "." + javaType;
         }
         
         // test if javaType is the fqn of default class, e.g. java.lang.String
         else if (javaType.startsWith(defaultPackagePrefix + ".")
                  && defaultClasses.contains(javaType.replace(defaultPackagePrefix + ".", "")))
         {
            return javaType;
         }
      }
      
      return null;
   }
      
   /**
    * Updates list of code errors and error marks. Also updates the fqn of tokens within the tokenList 
    * @param tokenList 
    * @param editor Code Editor
    */
   public void validateCode(List<? extends Token> tokenList, Editor editor)
   {     
      lastImportStatementLineNumber = 0;
      
      if (tokenList == null || tokenList.isEmpty())
      {
         // clear code error marks
         for (CodeError lastCodeError : codeErrorList)
         {
            editor.clearErrorMark(lastCodeError.getLineNumber());
         }
         return;
      }

      List<CodeError> newCodeErrorList = new ArrayList<CodeError>();
      
      newCodeErrorList.addAll(verifyJavaTypes((List<TokenBeenImpl>)tokenList));
      
      udpateErrorMarks(newCodeErrorList, editor);
   }

   private List<CodeError> verifyJavaTypes(List<TokenBeenImpl> tokenList)
   {
      List<CodeError> javaTypeErrorList = new ArrayList<CodeError>();
      
      List<TokenBeenImpl> importStatementBlock = getImportStatementBlock(tokenList);
      
      // verify java types
      for (TokenBeenImpl token : tokenList)
      {
         // filter IMPORT statements
         if (!TokenType.PACKAGE.equals(token.getType()) && !TokenType.IMPORT.equals(token.getType()))
         {
            javaTypeErrorList.addAll(validateTokenJavaType(token, importStatementBlock));
         }
      }
      
      return javaTypeErrorList; 
   }

   /**
    * Collect import statements end update lastImportStatementLineNumber 
    * @param tokenList
    * @return token list of import statements
    */
   public static List<TokenBeenImpl> getImportStatementBlock(List<TokenBeenImpl> tokenList)
   {
      List<TokenBeenImpl> importStatementBlock = new ArrayList<TokenBeenImpl>();

      // collect importStatments
      for (TokenBeenImpl token : tokenList)
      {
         if (TokenType.IMPORT.equals(token.getType()))
         {           
            importStatementBlock.add(token);
            lastImportStatementLineNumber = token.getLineNumber(); // it is needed for inserting the new import statement just after the last import
         }
      }
      
      return importStatementBlock;
   }

   /**
    * Validate token's java types and set token's FQN
    * @param currentToken
    * @param importStatementBlock
    * @return
    */
   private List<CodeError> validateTokenJavaType(TokenBeenImpl currentToken, List<TokenBeenImpl> importStatementBlock)
   {
      List<CodeError> javaTypeErrorList = new ArrayList<CodeError>();

      // validate annotations
      List<TokenBeenImpl> annotations = currentToken.getAnnotations();
      if (annotations != null)
      {
         for (TokenBeenImpl annotation : annotations)
         {
            javaTypeErrorList.addAll(validateTokenJavaType(annotation, importStatementBlock));
         }
      }
      
      // validate token
      String javaType = currentToken.getElementType();
      if (javaType != null && !javaType.isEmpty())
      {
         // filter FQN type for full java types like "javax.ws.rs.GET", but parse type like "ResourceBundle.Control"
//         if (javaType.contains(".")
//                && javaType.split("[.]").length > 2)
//         {
//            currentToken.setFqn(javaType);
//         }
//         
//         else 
//         {  
            // verifying if this type is from import statements
            String foundImport = findImport(javaType, importStatementBlock);
            if (foundImport != null)
            {
               currentToken.setFqn(foundImport);
               
            }
            else
            {
               // verifying if this short java type like "int" and stay "fqn = null" for such token
               if (shortJavaType.contains(javaType))
               {
                  currentToken.setFqn(null);
               }
               else
               {
                  // verifying if this type is from one of the default packages like "String" from "java.lang.String"
                  String fqn = getFqnFromDefaultPackages(javaType);
                  if (fqn != null)
                  {
                     currentToken.setFqn(fqn);
                  }
                  else
                  {
                     javaTypeErrorList.add(new CodeError(CodeErrorType.TYPE_ERROR, javaType, currentToken.getLineNumber()));
                  }
               }
            }
//         }
      }

      // validate parameters
      List<TokenBeenImpl> parameters = currentToken.getParameters();
      if (parameters != null)
      {
         for (TokenBeenImpl parameter : parameters)
         {
            javaTypeErrorList.addAll(validateTokenJavaType(parameter, importStatementBlock));
         }
      }
      
      // validate sub-tokens
      List<TokenBeenImpl> subTokenList = currentToken.getSubTokenList();
      if (subTokenList != null)
      {
         for (TokenBeenImpl token : subTokenList)
         {
            javaTypeErrorList.addAll(validateTokenJavaType(token, importStatementBlock));
         }
      }

      return javaTypeErrorList;
   }

   /**
    * 
    * @param javaType
    * @param importStatementBlock
    * @return appropriate FQN for java type from import statements in the importStatementBlock
    */
   public String findImport(String javaType, List<TokenBeenImpl> importStatementBlock)
   {     
      for (TokenBeenImpl importToken : importStatementBlock)
      {
         if (importToken.getElementType().endsWith(javaType))
         {
            return importToken.getElementType();
         }
      }
      
      return null;
   }

   /**
    * 
    * @param tokenList
    * @return lastImportStatementLineNumber defined in the "verifyJavaTypes", or search "package" token before class or interface, or return 1; 
    */
   public static int getAppropriateLineNumberToInsertImportStatement(List<TokenBeenImpl> tokenList)
   {
      if (lastImportStatementLineNumber > 0)
      {
         return lastImportStatementLineNumber + 1;
      }
      else
      {
         // search package token
         for (TokenBeenImpl token : tokenList)
         {
            switch (token.getType()) {
               case CLASS:
               case INTERFACE:
                  return 1;  // insert import token before class token
                  
               case PACKAGE:
                  return token.getLineNumber() + 1;  // insert import token at the second line after package statement
                  
               default:
            }
         }         
      }
      
      return 1;
   }
   
   @Override
   public void insertImportStatement(List<TokenBeenImpl> tokenList, String fqn, Editor editor)
   {
      if (shouldImportStatementBeInsterted(tokenList, fqn))
      {
         int lineNumber = getAppropriateLineNumberToInsertImportStatement(tokenList);         
         editor.insertIntoLine("import " + fqn + "\n", lineNumber);
      }
   }   

   /**
    * Verify if there any such fqn among the default packages of import statements
    * @param fqn
    * @return <b>true</b> if there is no such fqn among the default packages of import statements
    */
   static boolean shouldImportStatementBeInsterted(List<TokenBeenImpl> tokenList, String fqn)
   {
//      // test if this is correct FQN with more the two point delimiters like "java.lang.String", not "HelloWorld" fqn or even ""java.lang."
//      if (fqn.split("[.]").length <= 2)
//      {
//         return false;
//      }
      
      // search similar fqn among the default packages 
      Iterator<String> iterator = defaultPackages.keySet().iterator();
      String fqnClassName = fqn.substring(fqn.lastIndexOf(".") + 1);  // get class name as string after the last "." 
      while (iterator.hasNext())
      {
         String defaultPackagePrefix = iterator.next(); 
         
         // test if this is the same package as in fqn
         if (! fqn.equals(defaultPackagePrefix + "." + fqnClassName))
         {
            continue;
         }

         // test if there any class name in the default package which is equal with class name from fqn
         List<String> defaultClasses = defaultPackages.get(defaultPackagePrefix);
         if (defaultClasses.contains(fqnClassName))
         {
            return false;
         }
      }
      
      // search similar fqn among the import block 
      List<TokenBeenImpl> importStatementBlock = getImportStatementBlock(tokenList);
      for (TokenBeenImpl importStatement : importStatementBlock)
      {
         if (importStatement.getElementType().equals(fqn))
         {
            return false;
         }
      }

      // search similar fqn among the inner classes or interfaces 
      for (TokenBeenImpl token : tokenList)
      {
         if ((TokenType.CLASS.equals(token.getType()) || TokenType.INTERFACE.equals(token.getType()))
                  && token.getName().equals(fqn))
         {
            return false;
         }
      }      
      
      return true;
   }
   
   /**
    * Map of default packages which could be omitted within the import statements, like "String" from package "java.lang.String"
    * java.io.*
    * java.lang.*
    * java.math.BigDecimal
    * java.math.BigInteger
    * java.net.*
    * java.util.*
    * groovy.lang.*
    * groovy.util.*
    */
   private static LinkedHashMap<String, List<String>> defaultPackages = new LinkedHashMap<String, List<String>>() {{          

      // http://download.oracle.com/javase/6/docs/api/java/io/package-tree.html
      put("java.io", new LinkedList<String>(){{
         // classes
         add("Console");
         add("File");
         add("FileDescriptor");
         add("InputStream");
         add("ByteArrayInputStream");
         add("FileInputStream");
         add("FilterInputStream");
         add("BufferedInputStream");
         add("DataInputStream");
         add("LineNumberInputStream");
         add("PushbackInputStream");
         add("ObjectInputStream");
         add("PipedInputStream");
         add("SequenceInputStream");
         add("StringBufferInputStream");
         add("ObjectInputStream.GetField");
         add("ObjectOutputStream.PutField");
         add("ObjectStreamClass");
         add("ObjectStreamField");
         add("OutputStream");
         add("ByteArrayOutputStream");
         add("FileOutputStream");
         add("FilterOutputStream");
         add("BufferedOutputStream");
         add("DataOutputStream");
         add("PrintStream");
         add("ObjectOutputStream");
         add("PipedOutputStream");
         add("SerializablePermission");
         add("FilePermission");
         add("RandomAccessFile");
         add("Reader");
         add("BufferedReader");
         add("LineNumberReader");
         add("CharArrayReader");
         add("FilterReader");
         add("PushbackReader");
         add("InputStreamReader");
         add("FileReader");
         add("PipedReader");
         add("StringReader");
         add("StreamTokenizer");
         add("IOError");
         add("IOException");
         add("CharConversionException");
         add("EOFException");
         add("FileNotFoundException");
         add("InterruptedIOException");
         add("ObjectStreamException");
         add("InvalidClassException");
         add("InvalidObjectException");
         add("NotActiveException");
         add("NotSerializableException");
         add("OptionalDataException");
         add("StreamCorruptedException");
         add("WriteAbortedException");
         add("SyncFailedException");
         add("UnsupportedEncodingException");
         add("UTFDataFormatException");
         add("Writer");
         add("BufferedWriter");
         add("CharArrayWriter");
         add("FilterWriter");
         add("OutputStreamWriter");
         add("FileWriter");
         add("PipedWriter");
         add("PrintWriter");
         add("StringWriter");
         
         // interfaces
         add("Closeable");
         add("DataInput");
         add("ObjectInput");
         add("DataOutput");
         add("ObjectOutput");
         add("FileFilter");
         add("FilenameFilter");
         add("Flushable");
         add("ObjectInputValidation");
         add("ObjectStreamConstants");
         add("Serializable");
         add("Externalizable");
      }});
      
      // types from java.lang package http://download.oracle.com/javase/6/docs/api/java/lang/package-tree.html
      // LinkedList has better performance as ArrayList [http://download.oracle.com/javase/tutorial/collections/interfaces/list.html]      
      put("java.lang", new LinkedList<String>(){{
         // classes
         add("Object");
         add("Boolean");
         add("Character");
         add("Class");
         add("ClassLoader");
         add("Compiler");
         add("Enum");      
         add("Math");      
         add("Number");
         add("Byte");
         add("Double");
         add("Float");
         add("Integer");
         add("Long");
         add("Short");
         add("Package");
         add("RuntimePermission");      
         add("Process");
         add("ProcessBuilder");      
         add("Runtime");
         add("SecurityManager");
         add("StackTraceElement");
         add("StrictMath");
         add("String");
         add("StringBuffer");
         add("StringBuilder");      
         add("System");
         add("Thread");
         add("ThreadGroup");
         add("ThreadLocal");
         add("InheritableThreadLocal");      
         add("Throwable");
         add("Error");
         add("AssertionError");
         add("LinkageError");
         add("ClassCircularityError");
         add("ClassFormatError");    
         add("UnsupportedClassVersionError");
         add("ExceptionInInitializerError");
         add("IncompatibleClassChangeError");
         add("AbstractMethodError");
         add("IllegalAccessError");
         add("InstantiationError");
         add("NoSuchFieldError");
         add("NoSuchMethodError");
         add("NoClassDefFoundError");
         add("UnsatisfiedLinkError");
         add("VerifyError");
         add("ThreadDeath");
         add("VirtualMachineError");
         add("InternalError");
         add("OutOfMemoryError");
         add("StackOverflowError");
         add("UnknownError");
         add("Exception");
         add("ClassNotFoundException");
         add("CloneNotSupportedException");
         add("IllegalAccessException");
         add("InstantiationException");
         add("InterruptedException");
         add("NoSuchFieldException");
         add("NoSuchMethodException");
         add("RuntimeException");
         add("ArithmeticException");
         add("ArrayStoreException");
         add("ClassCastException");
         add("EnumConstantNotPresentException");
         add("IllegalArgumentException");
         add("IllegalThreadStateException");
         add("NumberFormatException");
         add("IllegalMonitorStateException");
         add("IllegalStateException");
         add("IndexOutOfBoundsException");
         add("ArrayIndexOutOfBoundsException");
         add("StringIndexOutOfBoundsException");
         add("NegativeArraySizeException");
         add("NullPointerException");
         add("SecurityException");
         add("TypeNotPresentException");
         add("UnsupportedOperationException");
         add("Void");
         add("Appendable");
         add("CharSequence");
         add("Cloneable");
         add("Comparable");
         add("Iterable");
         add("Readable");
         add("Runnable");
         add("Deprecated");
         add("Override");
         add("SuppressWarnings");
         
         // interfaces
         add("Appendable");
         add("CharSequence");
         add("Cloneable");
         add("Comparable");
         add("Iterable");
         add("Readable");
         add("Runnable");
         add("Thread.UncaughtExceptionHandler");         
      }});
 
      put("java.math", new LinkedList<String>(){{
         add("BigDecimal");
         add("BigInteger");         
      }});
      
      // http://download.oracle.com/javase/6/docs/api/java/net/package-tree.html
      put("java.net", new LinkedList<String>(){{
         // classes
         add("Authenticator");
         add("CacheRequest");
         add("CacheResponse");
         add("SecureCacheResponse");
         add("URLClassLoader");
         add("ContentHandler");
         add("CookieHandler");
         add("CookieManager");
         add("DatagramPacket");
         add("DatagramSocket");
         add("MulticastSocket");
         add("DatagramSocketImpl");
         add("HttpCookie");
         add("IDN");
         add("InetAddress");
         add("Inet4Address");
         add("Inet6Address");
         add("InterfaceAddress");
         add("NetworkInterface");
         add("PasswordAuthentication");
         add("NetPermission");
         add("SocketPermission");
         add("Proxy");
         add("ProxySelector");
         add("ResponseCache");
         add("ServerSocket");
         add("Socket");
         add("SocketAddress");
         add("InetSocketAddress");
         add("SocketImpl");
         add("HttpRetryException");
         add("SocketTimeoutException");
         add("MalformedURLException");
         add("ProtocolException");
         add("SocketException");
         add("BindException");
         add("ConnectException");
         add("NoRouteToHostException");
         add("PortUnreachableException");
         add("UnknownHostException");
         add("UnknownServiceException");
         add("URISyntaxException");
         add("URI");
         add("URL");
         add("URLConnection");
         add("HttpURLConnection");
         add("JarURLConnection");
         add("URLDecoder");
         add("URLEncoder");
         add("URLStreamHandler");

         // interfaces
         add("ContentHandlerFactory");
         add("CookiePolicy");
         add("CookieStore");
         add("DatagramSocketImplFactory");
         add("FileNameMap");
         add("SocketImplFactory");
         add("SocketOptions");
         add("URLStreamHandlerFactory");
       }});
      
      // http://download.oracle.com/javase/6/docs/api/java/util/package-tree.html
      put("java.util", new LinkedList<String>(){{
         // classes
         add("AbstractCollection");
         add("AbstractList");
         add("AbstractSequentialList");
         add("LinkedList");
         add("ArrayList");
         add("Vector");
         add("Stack");
         add("AbstractQueue");
         add("PriorityQueue");
         add("AbstractSet");
         add("EnumSet");
         add("HashSet");
         add("LinkedHashSet");
         add("TreeSet");
         add("ArrayDeque");
         add("AbstractMap");
         add("EnumMap");
         add("HashMap");
         add("LinkedHashMap");
         add("IdentityHashMap");
         add("TreeMap");
         add("WeakHashMap");
         add("AbstractMap");
         add("AbstractMap.SimpleEntry");
         add("AbstractMap.SimpleImmutableEntry");
         add("Arrays");
         add("BitSet");
         add("Calendar");
         add("GregorianCalendar");
         add("Collections");
         add("Currency");
         add("Date");
         add("Dictionary");
         add("Hashtable");
         add("Properties");
         add("EventListenerProxy");
         add("EventObject");
         add("FormattableFlags");
         add("Formatter");
         add("Locale");
         add("Observable");
         add("PropertyPermission");
         add("Random");
         add("ResourceBundle");
         add("ListResourceBundle");
         add("PropertyResourceBundle");
         add("ResourceBundle.Control");
         add("Scanner");
         add("ServiceLoader");
         add("StringTokenizer");
         add("ServiceConfigurationError");
         add("InvalidPropertiesFormatException");
         add("ConcurrentModificationException");
         add("EmptyStackException");
         add("IllegalFormatException");
         add("DuplicateFormatFlagsException");
         add("FormatFlagsConversionMismatchException");
         add("IllegalFormatCodePointException");
         add("IllegalFormatConversionException");
         add("IllegalFormatFlagsException");
         add("IllegalFormatPrecisionException");
         add("IllegalFormatWidthException");
         add("MissingFormatArgumentException");
         add("MissingFormatWidthException");
         add("UnknownFormatConversionException");
         add("UnknownFormatFlagsException");
         add("FormatterClosedException");
         add("MissingResourceException");
         add("NoSuchElementException");
         add("InputMismatchException");
         add("TooManyListenersException");
         add("Timer");
         add("TimerTask");
         add("TimeZone");
         add("SimpleTimeZone");
         add("UUID");
         
         // interfaces
         add("Comparator");
         add("Enumeration");
         add("EventListener");
         add("Formattable");
         add("Iterable");
         add("Collection");
         add("List");
         add("Queue");
         add("Deque");
         add("Set");
         add("SortedSet");
         add("NavigableSet");
         add("Iterator");
         add("ListIterator");
         add("Map");
         add("SortedMap");
         add("NavigableMap");
         add("Map.Entry");
         add("Observer");
         add("RandomAccess");         
      }});

      // http://groovy.codehaus.org/api/groovy/lang/package-tree.html
      put("groovy.lang", new LinkedList<String>(){{
         // classes
         add("Sequence");
         add("NonEmptySequence");
         add("EmptyRange");
         add("IntRange");
         add("ObjectRange");
         add("Tuple");
         add("SpreadMap");
         add("BenchmarkInterceptor");
         add("GroovyClassLoader");
         add("GroovyClassLoader.InnerLoader");
         add("GroovyClassLoader.ClassCollector");
         add("MetaClassImpl.Index");
         add("DelegatingMetaClass");
         add("MetaClassRegistryChangeEvent");
         add("GroovyCodeSource");
         add("GroovyObjectSupport");
         add("Binding");
         add("Closure");
         add("ExpandoMetaClass");
         add("ExpandoMetaClass.ExpandoMetaConstructor");
         add("ExpandoMetaClass.ExpandoMetaProperty");
         add("GroovyShell");
         add("GString");
         add("Reference");
         add("Script");
         add("GroovySystem");
         add("MapWithDefault");
         add("MetaClassImpl");
         add("ExpandoMetaClass");
         add("ProxyMetaClass");
         add("MetaClassRegistry");
         add("MetaClassRegistry.MetaClassCreationHandle");
         add("ExpandoMetaClassCreationHandle");
         add("MetaProperty");
         add("MetaArrayLengthProperty");
         add("MetaBeanProperty");
         add("MetaExpandoProperty");
         add("ParameterArray");
         add("MetaMethod");
         add("PropertyValue");
         add("ClosureException");
         add("DeprecationException");
         add("GroovyRuntimeException");
         add("IncorrectClosureArgumentsException");
         add("MissingClassException");
         add("MissingFieldException");
         add("MissingMethodException");
         add("MissingPropertyException");
         add("IllegalPropertyAccessException");
         add("ReadOnlyPropertyException");
         add("SpreadListEvaluatingException");
         add("SpreadMapEvaluatingException");
         add("StringWriterIOException");
         add("TracingInterceptor");
         
         // interfaces
         add("Buildable");
         add("ClosureInvokingMethod");
         add("MetaClassRegistryChangeEventListener");
         add("GroovyObject");
         add("GroovyInterceptable");
         add("GroovyResourceLoader");
         add("Interceptor");
         add("PropertyAccessInterceptor");
         add("Range");
         add("MetaClassRegistry");
         add("MetaObjectProtocol");
         add("MetaClass");
         add("AdaptingMetaClass");
         add("MutableMetaClass");
         add("Writable");
      }});
      
      // http://groovy.codehaus.org/api/groovy/util/package-tree.html
      put("groovy.util", new LinkedList<String>(){{
         // classes
         add("NodeList");
         add("AbstractFactory");
         add("GroovyTestCase");
         add("CharsetToolkit");
         add("ClosureComparator");
         add("XmlSlurper");
         add("Eval");
         add("ObservableList.ElementEvent");
         add("ObservableList.ElementAddedEvent");
         add("ObservableList.ElementClearedEvent");
         add("ObservableList.ElementRemovedEvent");
         add("ObservableList.ElementUpdatedEvent");
         add("ObservableList.MultiElementAddedEvent");
         add("ObservableList.MultiElementRemovedEvent");
         add("ObservableMap.PropertyEvent");
         add("ObservableMap.MultiPropertyEvent");
         add("ObservableMap.PropertyAddedEvent");
         add("ObservableMap.PropertyClearedEvent");
         add("ObservableMap.PropertyRemovedEvent");
         add("ObservableMap.PropertyUpdatedEvent");
         add("GroovyCollections");
         add("FactoryBuilderSupport");
         add("ObjectGraphBuilder");
         add("BuilderSupport");
         add("AntBuilder");
         add("NodeBuilder");
         add("Expando");
         add("GroovyLog");
         add("GroovyMBean");
         add("Proxy");
         add("GroovyScriptEngine");
         add("IndentPrinter");
         add("MapEntry");
         add("Node");
         add("NodePrinter");         
         add("ObjectGraphBuilder.DefaultChildPropertySetter");
         add("ObjectGraphBuilder.DefaultClassNameResolver");
         add("ObjectGraphBuilder.DefaultIdentifierResolver");
         add("ObjectGraphBuilder.DefaultNewInstanceResolver");
         add("ObjectGraphBuilder.DefaultReferenceResolver");
         add("ObjectGraphBuilder.DefaultRelationNameResolver");
         add("ObjectGraphBuilder.ReflectionClassNameResolver"); 
         add("ObservableList");
         add("ObservableMap");
         add("OrderBy");
         add("PermutationGenerator");
         add("ProxyGenerator");
         add("AllTestSuite");
         add("GroovyTestSuite");
         add("ResourceException");
         add("ScriptException");
         add("XmlNodePrinter");
         add("XmlNodePrinter.NamespaceContext");
         add("XmlParser");
         
         // interfaces
         add("Factory");
         add("IFileNameFinder");
         add("ObjectGraphBuilder.ChildPropertySetter");
         add("ObjectGraphBuilder.ClassNameResolver");
         add("ObjectGraphBuilder.IdentifierResolver");
         add("ObjectGraphBuilder.NewInstanceResolver");
         add("ObjectGraphBuilder.ReferenceResolver");
         add("ObjectGraphBuilder.RelationNameResolver");
         add("ResourceConnector");
      }});
   }};
}
