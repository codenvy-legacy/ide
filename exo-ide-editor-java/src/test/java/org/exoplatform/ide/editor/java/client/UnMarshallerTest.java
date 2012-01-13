/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.editor.java.client;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.java.client.codeassistant.services.marshal.ClassDescriptionUnmarshaller;
import org.exoplatform.ide.editor.java.client.codeassistant.services.marshal.FindClassesUnmarshaller;
import org.exoplatform.ide.editor.java.client.codeassistant.services.marshal.JavaClass;
import org.exoplatform.ide.editor.java.client.codeassistant.services.marshal.TypesUnmarshaller;
import org.exoplatform.ide.editor.java.client.model.ShortTypeInfo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Random;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class UnMarshallerTest extends GWTTestCase
{
   private String classes =
      "[{\"name\":\"java.lang.String$1\",\"modifiers\":4104,\"type\":\"CLASS\"},{\"name\":\"java.lang.StringCoding$1\",\"modifiers\":4104,\"type\":\"CLASS\"},{\"name\":\"java.lang.StringValue\",\"modifiers\":0,\"type\":\"CLASS\"},{\"name\":\"java.lang.StringIndexOutOfBoundsException\",\"modifiers\":1,\"type\":\"CLASS\"},{\"name\":\"java.lang.StackTraceElement\",\"modifiers\":17,\"type\":\"CLASS\"},{\"name\":\"java.lang.StringBuffer\",\"modifiers\":17,\"type\":\"CLASS\"},{\"name\":\"java.lang.StackOverflowError\",\"modifiers\":1,\"type\":\"CLASS\"},{\"name\":\"java.lang.StrictMath\",\"modifiers\":17,\"type\":\"CLASS\"},{\"name\":\"java.lang.StringCoding$StringEncoder\",\"modifiers\":10,\"type\":\"CLASS\"},{\"name\":\"java.lang.StringBuilder\",\"modifiers\":17,\"type\":\"CLASS\"},{\"name\":\"java.lang.StringCoding$StringDecoder\",\"modifiers\":10,\"type\":\"CLASS\"},{\"name\":\"java.lang.StringCoding\",\"modifiers\":0,\"type\":\"CLASS\"},{\"name\":\"java.lang.String\",\"modifiers\":17,\"type\":\"CLASS\"},{\"name\":\"java.lang.String$CaseInsensitiveComparator\",\"modifiers\":10,\"type\":\"CLASS\"},{\"name\":\"java.util.logging.StreamHandler\",\"modifiers\":1,\"type\":\"CLASS\"},{\"name\":\"java.util.StringTokenizer\",\"modifiers\":1,\"type\":\"CLASS\"},{\"name\":\"java.util.Stack\",\"modifiers\":1,\"type\":\"CLASS\"}]";

   private final String[] expFqn = {"java.lang.String$1", "java.lang.StringCoding$1", "java.lang.StringValue",
      "java.lang.StringIndexOutOfBoundsException", "java.lang.StackTraceElement", "java.lang.StringBuffer",
      "java.lang.StackOverflowError", "java.lang.StrictMath", "java.lang.StringCoding$StringEncoder",
      "java.lang.StringBuilder", "java.lang.StringCoding$StringDecoder", "java.lang.StringCoding", "java.lang.String",
      "java.lang.String$CaseInsensitiveComparator", "java.util.logging.StreamHandler", "java.util.StringTokenizer",
      "java.util.Stack"};

   private final String[] expName = {"String$1", "StringCoding$1", "StringValue", "StringIndexOutOfBoundsException",
      "StackTraceElement", "StringBuffer", "StackOverflowError", "StrictMath", "StringCoding$StringEncoder",
      "StringBuilder", "StringCoding$StringDecoder", "StringCoding", "String", "String$CaseInsensitiveComparator",
      "StreamHandler", "StringTokenizer", "Stack"};

   private final String classdesc =
      "{\"methods\":[{\"genericExceptionTypes\":[],\"declaringClass\":\"java.lang.String\","
         + "\"name\":\"equals\",\"modifiers\":1,\"genericReturnType\":\"boolean\",\"constructor\":false,"
         + "\"parameterTypes\":[\"java.lang.Object\"]},{\"genericExceptionTypes\":[],\"declaringClass\":\"java.lang.String\","
         + "\"name\":\"toString\",\"modifiers\":1024,\"genericReturnType\":\"java.lang.String\","
         + "\"constructor\":false,\"parameterTypes\":[]},],\"superClass\":\"java.lang.Object\","
         + "\"name\":\"java.lang.String\",\"interfaces\":[\"java.io.Serializable\","
         + "\"java.lang.Comparable\",\"java.lang.CharSequence\"],\"modifiers\":17,"
         + "\"type\":\"CLASS\",\"fields\":[{\"declaringClass\":\"java.lang.String\","
         + "\"name\":\"CASE_INSENSITIVE_ORDER\",\"modifiers\":25,\"type\":\"java.util.Comparator\"}]}";

   @Test
   public void testTypesUnmarshaller() throws Exception
   {
      List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();
      TypesUnmarshaller unmarshaller = new TypesUnmarshaller(types);
      MockResponse response = new MockResponse(classes);
      unmarshaller.unmarshal(response);
      assertEquals(17, types.size());
      int i = Random.nextInt(16);
      assertEquals(expFqn[i], types.get(i).getQualifiedName());
      assertEquals(expName[i], types.get(i).getName());
      assertEquals("StringBuffer", types.get(5).getName());
   }

   @Test
   public void testFindClassesUnmarshaller() throws Exception
   {
      List<Token> tokens = new ArrayList<Token>();
      FindClassesUnmarshaller unmarshaller = new FindClassesUnmarshaller(tokens);
      MockResponse response = new MockResponse(classes);
      unmarshaller.unmarshal(response);
      assertEquals(17, tokens.size());

      for (int i = 0; i < expName.length; i++)
      {
         assertEquals(expName[i], tokens.get(i).getName());
      }
   }

   @Test
   public void testClassDescriptionUnmarshaller() throws Exception
   {
      JavaClass javaClass = new JavaClass();
      ClassDescriptionUnmarshaller unmarshaller = new ClassDescriptionUnmarshaller(javaClass);
      MockResponse response = new MockResponse(classdesc);
      unmarshaller.unmarshal(response);
      assertEquals(1, javaClass.getPublicMethods().size());
      assertEquals(1, javaClass.getPublicFields().size());
      assertEquals(1, javaClass.getAbstractMethods().size());

   }

   class MockResponse extends Response
   {
      private final String text;

      public MockResponse(String text)
      {
         this.text = text;
      }

      @Override
      public String getHeader(String header)
      {
         return null;
      }

      @Override
      public Header[] getHeaders()
      {
         return null;
      }

      @Override
      public String getHeadersAsString()
      {
         return null;
      }

      @Override
      public int getStatusCode()
      {
         return 200;
      }

      @Override
      public String getStatusText()
      {
         return null;
      }

      @Override
      public String getText()
      {
         return text;
      }

   }

   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ide.editor.java.JavaEditorExtension";
   }

}
