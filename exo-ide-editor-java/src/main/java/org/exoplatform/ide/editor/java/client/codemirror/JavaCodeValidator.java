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
package org.exoplatform.ide.editor.java.client.codemirror;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.CodeLine.CodeType;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.CodeValidator;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.java.client.codeassistant.services.JavaCodeAssistantService;


/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class JavaCodeValidator extends CodeValidator
{  
  
   static int lastImportStatementLineNumber = 0;  

   List<Token> classesFromProject;
   
   JavaCodeAssistantService service;
   
   JavaCodeAssistantErrorHandler errorHandler;
   
   public JavaCodeValidator(JavaCodeAssistantService service, JavaCodeAssistantErrorHandler errorHandler)
   {
      this.service = service;
      this.errorHandler = errorHandler;
   }
   
   public JavaCodeValidator()
   {
   }

   /**
    * Find all classes in project by using rest service with url "find-by-project" for file with relPath
    */
   public void loadClassesFromProject(String fileRelPath)
   {
      if (service != null && errorHandler != null)
      {
         service.findClassesByProject(fileRelPath, new AsyncRequestCallback<List<Token>>()
         {
            @Override
            protected void onSuccess(List<Token> result)
            {
               classesFromProject = result;
            }
   
            @Override
            protected void onFailure(Throwable exception)
            {
               exception.printStackTrace();
               errorHandler.handleError(exception);
            }
         });
      }
   }

   /**
    * Short default java types like "int"...
   */
   private static List<String> shortJavaType = Arrays.asList("boolean", "char", "enum", "byte", "double", "float", "int", "long", "short", "void");
  
   public String getFqnFromDefaultPackages(String javaType)
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
      


   private List<CodeLine> verifyJavaTypes(List<TokenBeenImpl> tokenList)
   {
      List<CodeLine> javaTypeErrorList = new ArrayList<CodeLine>();
      
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
   private List<CodeLine> validateTokenJavaType(TokenBeenImpl currentToken, List<TokenBeenImpl> importStatementBlock)
   {
      List<CodeLine> javaTypeErrorList = new ArrayList<CodeLine>();

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
         javaType = getTypeWithoutParameter(javaType);
         
         String foundFqn;
         
         String fqn;
         
         // filter FQN type for full java types like "javax.ws.rs.GET", and "data.ProductItem", but parse type like "ResourceBundle.Control"
         if (javaType.contains(".")
                && (javaType.split("[.]").length > 2
                    || javaType.split("[.]").length == 2 && javaType.matches("^[a-z].*")  // to parse fqn like "data.ProductItem", not "ResourceBundle.Control"
                )
            )
         {
            currentToken.setFqn(javaType);
         }
         
         // verifying if this type is from import statements
         else if ((foundFqn = findImport(javaType, importStatementBlock)) != null)
         {
            currentToken.setFqn(foundFqn);
         }
         
         // verifying if this type is from import statements 
         else if (classesFromProject != null 
                && (foundFqn = findClassesFromProject(javaType, classesFromProject)) != null)
         {
            currentToken.setFqn(foundFqn);
         }

         // verifying if this short java type like "int" and stay "fqn = null" for such token
         else if (shortJavaType.contains(javaType))
         {
            currentToken.setFqn(null);
         }

         // verifying if this type is from one of the default packages like "String" from "java.lang.String"
         else if ((fqn = getFqnFromDefaultPackages(javaType)) != null)
         {
            currentToken.setFqn(fqn);
         }
         
         // add token into error list
         else
         {
            javaTypeErrorList.add(new CodeLine(CodeType.TYPE_ERROR, javaType, currentToken.getLineNumber()));
         }
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
    * Go through classesFromProject and looking for token with name = javaType
    * @param javaType
    * @param classesFromProject
    * @return FQN of token with name = javaType from classesFromProject  
    */
   private String findClassesFromProject(String javaType, List<Token> classesFromProject)
   {
      Iterator<Token> iterator = classesFromProject.iterator();
      while (iterator.hasNext())
      {
         Token token = iterator.next();
         
         if (javaType.equals(token.getName()))
         {
            return token.getProperty(TokenProperties.FQN).toString();
         }
      }
      
      return null;
   }

   /**
    * Return type where parameter part of parameterized type like "<Item>" in type "List<Item>" is removed
    * @param javaType
    * @return 
    */
   private String getTypeWithoutParameter(String javaType)
   {
      return javaType.replaceAll("<.*>", "");
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
    * Verify if there any such fqn among the default packages of import statements
    * @param fqn
    * @return <b>true</b> if there is no such fqn among the default packages of import statements
    */
   public boolean shouldImportStatementBeInsterted(List<TokenBeenImpl> tokenList, String fqn)
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
    * java.lang.*
    */
   protected LinkedHashMap<String, List<String>> defaultPackages = new LinkedHashMap<String, List<String>>() {{          
   
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
   
   /**
    * Get list of code errors and error marks. 
    * @param tokenList 
    */
   public List<CodeLine> getCodeErrorList(List<? extends Token> tokenList)
   {     
      List<CodeLine> newCodeErrorList = new ArrayList<CodeLine>();
      
      lastImportStatementLineNumber = 0;
      
      if (tokenList == null || tokenList.isEmpty())
      {
         return new ArrayList<CodeLine>();
      }
      
      newCodeErrorList.addAll(verifyJavaTypes((List<TokenBeenImpl>)tokenList));
      
      return newCodeErrorList;
   }
}
