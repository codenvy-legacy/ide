/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.codeassistant.storage.extractors;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Class which extract java docs from sources.
 */
public class QDoxJavaDocExtractor
{

   /**
    * <p>
    * Method gets InputStream of <code>zip</code> file with <code>.java</code>
    * sources, and return Map of javaDocs for classes from all sources of zip.
    * </p>
    * 
    * For more details of result format see {@link #extractSource(InputStream)}
    * 
    * @param sourceZipStream
    *           stream of jar file with sources
    * @return map with key - member's fqn, and value - javaDoc comment
    * @throws IOException
    */
   public Map<String, String> extractZip(InputStream sourceZipStream) throws IOException
   {
      HashMap<String, String> result = new HashMap<String, String>();
      ZipInputStream zip = new ZipInputStream(sourceZipStream);
      ZipEntry entry = zip.getNextEntry();
      while (entry != null)
      {
         if (entry.getName().endsWith(".java"))
         {
            // extract sources without closing stream
            result.putAll(extractSource(zip, false));
         }
         entry = zip.getNextEntry();
      }
      return result;
   }

   /**
    * <p>
    * Method gets InputStream of <code>.java</code> file, and return Map of
    * javaDocs for classes from this source.
    * </p>
    * <ul>
    * <li>Key of a result map is fqn of member (class, field, constructor,
    * method, etc).</li>
    * <li>Value of a result map is javaDoc comment for member with this fqn.</li>
    * </ul>
    * <p>
    * Member's fqn has next format:
    * </p>
    * <ul>
    * <li>If this member is <b>class</b>, then fqn equals full qualified name of
    * class. For Example: java.util.HashMap, java.io.InputStream, ....</li>
    * <li>If this member is <b>inner class</b>, then fqn contains full qualified
    * name of declared class and name of inner class, separated by $. For
    * example: java.util.HashMap$KeySet</li>
    * <li>If this member is <b>field</b>, then fqn contains full qualified name
    * of declared class and field name, separated by #. For example:
    * java.lang.Integer#MAX_VALUE, etc.</li>
    * <li>If this member is <b>method</b>, then fqn contains full qualified name
    * of declared class and method description, separated by #. Method
    * description contains from method name, and list of parameter types in
    * brackets. For example:
    * java.util.HashMap#put(java.lang.Object,java.lang.Object),
    * java.util.HashMap#clear(), etc.</li>
    * <li>If this member is <b>constructor</b>, then it's contains like
    * <b>method</b> but without method name. For example:
    * java.lang.HashMap(int,float), java.lang.HashMap(int)
    * </ul>
    * 
    * <p>
    * <ul>
    * <li><b>SimpleName</b> ::= [a-zA-Z][a-zA-Z0-9_]*</li>
    * <li><b>Package</b> ::= SimpleName (.SimpleName)*</li>
    * <li><b>ClassName</b> ::= SimpleName | SimpleName$SimpleName</li>
    * <li><b>ClassFqn</b> ::= (Package.)* ClassName</li>
    * <li><b>FieldFqn</b> ::= (Package.)* ClassName#SimpleName</li>
    * <li><b>MethodFqn</b> ::= (Package.)* ClassName#SimpleName(Params)</li>
    * <li><b>ConstructorFqn</b> ::= (Package.)* ClassName(Params)</li>
    * <li><b>Params</b> ::= < empty string > | Param(,Param)*</li>
    * <li><b>Param</b> ::= SimpleParam | ObjectParam</li>
    * <li><b>SimpleParam</b> ::= void | byte | char | boolean | short | int |
    * long | float | double</li>
    * <li><b>ObjectParam</b> ::= ClassFqn | ClassFqn < Generic > | Generic</li>
    * <li><b>Generic</b> ::= GenericBase | GenericBase extends ClassFqn |
    * GenericBase super ClassFqn</li>
    * <li><b>GenericBase</b> = SimpleName | ?</li>
    * </ul>
    * </p>
    * 
    * <p>
    * 
    * </p>
    * Examples:
    * <ul>
    * <li>java.lang.Integer</li>
    * <li>java.util.HashMap$KeySet</li>
    * <li>java.lang.Integer#MAX_VALUE</li>
    * <li>java.lang.Integer(int)</li>
    * <li>java.lang.Integer(java.lang.Integer)</li>
    * <li>java.lang.Integer#valueOf(java.lang.String,int)</li>
    * <li>java.util.HashMap#put(K,V)</li>
    * <li>java.util.Collections#sort(java.util.List<T>,java.util.Comparator<?
    * super T>)</li>
    * </ul>
    * <p>
    * If member not contains java doc, then result will not contains it's member
    * fqn.
    * </p>
    * 
    * WARNING: This method closes input stream!!! If you need no close stream,
    * use method {@link #extractSource(InputStream, boolean)}
    * 
    * @param sourceZipStream
    *           stream of <code>.java</code> file
    * @return map with key - member's fqn, and value - javaDoc comment
    */
   public Map<String, String> extractSource(InputStream sourceStream)
   {
      Map<String, String> javaDocs = new HashMap<String, String>();

      JavaDocBuilder javaDocBuilder = new JavaDocBuilder();
      // here will be closed sourceStream
      javaDocBuilder.addSource(new InputStreamReader(sourceStream));

      for (JavaClass currentClass : javaDocBuilder.getClasses())
      {
         DocletTag[] classTags = currentClass.getTags();
         String classComment = getCommentWithTags(currentClass.getComment(), classTags);
         if (classComment != null)
         {
            javaDocs.put(getFqnForClass(currentClass), classComment);
         }

         for (JavaField currentField : currentClass.getFields())
         {
            DocletTag[] fieldTags = currentField.getTags();
            String fieldComment = getCommentWithTags(currentField.getComment(), fieldTags);
            if (fieldComment != null)
            {
               javaDocs.put(getFqnForField(currentField), fieldComment);
            }
         }
         for (JavaMethod currentMethod : currentClass.getMethods())
         {
            DocletTag[] methodTags = currentMethod.getTags();
            String methodComment = getCommentWithTags(currentMethod.getComment(), methodTags);
            if (methodComment != null)
            {
               javaDocs.put(getFqnForMethod(currentMethod), methodComment);
            }
         }
      }

      return javaDocs;
   }

   /**
    * <p>
    * This method useful if you didn't want to close stream after parsing,
    * because original {@link #extractSource(InputStream)} method closes stream.
    * </p>
    * 
    * @param stream
    * @param isCloseStream
    *           to close or not to close stream
    * @return
    * @throws IOException
    */
   public Map<String, String> extractSource(InputStream stream, boolean isCloseStream) throws IOException
   {
      InputStream sourceStream;
      if (isCloseStream)
      {
         sourceStream = stream;
      }
      else
      {
         // copy stream
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         int length = 0;
         byte[] buf = new byte[1024];
         while (length >= 0)
         {
            bout.write(buf, 0, length);
            length = stream.read(buf);
         }
         sourceStream = new ByteArrayInputStream(bout.toByteArray());
      }
      return extractSource(sourceStream);
   }

   /**
    * Method returns fqn of some class in specified format
    * 
    * @param javaClass
    * @return
    * @see #extractSource(InputStream)
    */
   private String getFqnForClass(JavaClass javaClass)
   {
      return javaClass.getFullyQualifiedName();
   }

   /**
    * Method returns fqn of some field in specified format
    * 
    * @param javaField
    * @return
    * @see #extractSource(InputStream)
    */
   private String getFqnForField(JavaField javaField)
   {
      StringBuilder fqnBuilder = new StringBuilder();
      String prefix = javaField.getParentClass().getFullyQualifiedName();
      fqnBuilder.append(prefix);
      fqnBuilder.append("#");

      fqnBuilder.append(javaField.getName());
      return fqnBuilder.toString();
   }

   /**
    * Method returns fqn of some method in specified format
    * 
    * @param javaMethod
    * @return
    * @see #extractSource(InputStream)
    */
   private String getFqnForMethod(JavaMethod javaMethod)
   {
      StringBuilder fqnBuilder = new StringBuilder();
      String prefix = javaMethod.getParentClass().getFullyQualifiedName();
      fqnBuilder.append(prefix);

      if (!javaMethod.isConstructor())
      {
         fqnBuilder.append("#");
         fqnBuilder.append(javaMethod.getName());
      }

      fqnBuilder.append("(");
      boolean isFirst = true;
      for (JavaParameter parameter : javaMethod.getParameters())
      {
         if (!isFirst)
         {
            fqnBuilder.append(",");
         }
         fqnBuilder.append(parameter.getType().getGenericValue());
         isFirst = false;
      }
      fqnBuilder.append(")");
      return fqnBuilder.toString();
   }

   /**
    * This method add to comment string all doclet tags.
    * 
    * @param comment
    * @param tags
    * @return javadoc comment with unresolved tags
    */
   private String getCommentWithTags(String comment, DocletTag[] tags)
   {
      if (comment == null && (tags == null || tags.length == 0))
      {
         return null;
      }
      StringBuilder commentBuilder = new StringBuilder();
      commentBuilder.append(comment);
      for (DocletTag tag : tags)
      {
         commentBuilder.append("\n");
         commentBuilder.append("@");
         commentBuilder.append(tag.getName());
         commentBuilder.append(" ");
         commentBuilder.append(tag.getValue());
      }
      return commentBuilder.toString().trim();
   }

}
