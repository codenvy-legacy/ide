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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.CodeLine.CodeType;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;


/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class JavaCodeValidator extends GroovyCodeValidator
{
   
   @Override
   public CodeLine getImportStatement(List<? extends Token> tokenList, String fqn)
   {
      if (shouldImportStatementBeInsterted((List<TokenBeenImpl>) tokenList, fqn))
      {
         int lineNumber = getAppropriateLineNumberToInsertImportStatement((List<TokenBeenImpl>)tokenList);         
         return new CodeLine(CodeType.IMPORT_STATEMENT, "import " + fqn + ";\n", lineNumber);
      }
      
      return null;
   }   
   
   /**
    * Map of default packages which could be omitted within the import statements, like "String" from package "java.lang.String"
    * java.lang.*
    */
   private static LinkedHashMap<String, List<String>> defaultPackages = new LinkedHashMap<String, List<String>>() {{          
   
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
   }};
}
