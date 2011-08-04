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
package org.exoplatform.ide.editor.groovy.client.codemirror;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.CodeLine.CodeType;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.java.client.codemirror.JavaCodeValidator;


/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class GroovyCodeValidator extends JavaCodeValidator
{   
   public GroovyCodeValidator()
   {
      super.defaultPackages = this.defaultPackages;
   }
  
   @Override
   public CodeLine getImportStatement(List<? extends Token> tokenList, String fqn)
   {
      if (shouldImportStatementBeInsterted((List<TokenBeenImpl>) tokenList, fqn))
      {
         int lineNumber = getAppropriateLineNumberToInsertImportStatement((List<TokenBeenImpl>)tokenList);         
         return new CodeLine(CodeType.IMPORT_STATEMENT, "import " + fqn + "\n", lineNumber);
      }
      
      return null;
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
   protected LinkedHashMap<String, List<String>> defaultPackages = new LinkedHashMap<String, List<String>>() {{          

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
   
   public List<CodeLine> getCodeErrorList(List<? extends Token> javaCode)
   {
      return super.getCodeErrorList(javaCode);
   }

   public boolean shouldImportStatementBeInsterted(List<TokenBeenImpl> javaCode, String fqn)
   {
      return super.shouldImportStatementBeInsterted(javaCode, fqn);
   }
}
