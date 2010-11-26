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
package org.exoplatform.ide.client.module.groovy.codeassistant;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtProperties;
import org.exoplatform.ide.client.module.groovy.ClientTest;
import org.exoplatform.ide.client.module.groovy.codeassistant.autocompletion.GroovyClass;
import org.exoplatform.ide.client.module.groovy.service.codeassistant.marshal.ClassDescriptionUnmarshaller;
import org.exoplatform.ide.client.module.groovy.service.codeassistant.marshal.FindClassesUnmarshaller;
import org.exoplatform.ide.testframework.http.MockResponse;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 3:15:57 PM evgen $
 *
 */
public class GwtTestUnmarshallFindClasses extends ClientTest
{
   private String jsonFindClass =
      "[{\"name\": \"Address\",\"qualifiedName\": \"org.exoplatform.ide.groovy.codeassistant.Address\",\"modifiers\": 0,\"type\": \"CLASS\"},"
         + "{\"name\": \"A\", \"qualifiedName\": \"org.exoplatform.ide.groovy.codeassistant.A\", \"modifiers\": 253,\"type\": \"CLASS\"},"
         + "{\"name\":\"List\", \"qualifiedName\":\"java.util.List\",\"modifiers\":0, \"type\":\"INTERFACE\"},"
         + "{\"name\":\"ArrayA\", \"qualifiedName\":\"java.util.ArrayList\",\"modifiers\":0, \"type\":\"ARRAY\"}]";

   private String jsonGetClass =
      "{\"methods\":[{\"generic\":\"public java.lang.String org.exoplatform.ide.groovy.codeassistant.Address.getCity()\",\"genericExceptionTypes\":[],\"declaringClass\":\"org.exoplatform.ide.groovy.codeassistant.Address\",\"name\":\"getCity\",\"genericParameterTypes\":\"()\",\"modifiers\":1,\"returnType\":\"String\",\"parameterTypes\":\"()\",\"genericReturnType\":\"java.lang.String\"},{\"generic\":\"public final native void java.lang.Object.wait(long) throws java.lang.InterruptedException\",\"genericExceptionTypes\":[\"java.lang.InterruptedException\"],\"declaringClass\":\"java.lang.Object\",\"name\":\"wait\",\"genericParameterTypes\":\"(long)\",\"modifiers\":273,\"returnType\":\"void\",\"parameterTypes\":\"(long)\",\"genericReturnType\":\"void\"},{\"generic\":\"public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException\",\"genericExceptionTypes\":[\"java.lang.InterruptedException\"],\"declaringClass\":\"java.lang.Object\",\"name\":\"wait\",\"genericParameterTypes\":\"(long, int)\",\"modifiers\":17,\"returnType\":\"void\",\"parameterTypes\":\"(long, int)\",\"genericReturnType\":\"void\"},{\"generic\":\"public final void java.lang.Object.wait() throws java.lang.InterruptedException\",\"genericExceptionTypes\":[\"java.lang.InterruptedException\"],\"declaringClass\":\"java.lang.Object\",\"name\":\"wait\",\"genericParameterTypes\":\"()\",\"modifiers\":17,\"returnType\":\"void\",\"parameterTypes\":\"()\",\"genericReturnType\":\"void\"},{\"generic\":\"public boolean java.lang.Object.equals(java.lang.Object)\",\"genericExceptionTypes\":[],\"declaringClass\":\"java.lang.Object\",\"name\":\"equals\",\"genericParameterTypes\":\"(java.lang.Object)\",\"modifiers\":1,\"returnType\":\"boolean\",\"parameterTypes\":\"(Object)\",\"genericReturnType\":\"boolean\"},{\"generic\":\"public java.lang.String java.lang.Object.toString()\",\"genericExceptionTypes\":[],\"declaringClass\":\"java.lang.Object\",\"name\":\"toString\",\"genericParameterTypes\":\"()\",\"modifiers\":1,\"returnType\":\"String\",\"parameterTypes\":\"()\",\"genericReturnType\":\"java.lang.String\"},{\"generic\":\"public native int java.lang.Object.hashCode()\",\"genericExceptionTypes\":[],\"declaringClass\":\"java.lang.Object\",\"name\":\"hashCode\",\"genericParameterTypes\":\"()\",\"modifiers\":257,\"returnType\":\"int\",\"parameterTypes\":\"()\",\"genericReturnType\":\"int\"},{\"generic\":\"public final native java.lang.Class<?> java.lang.Object.getClass()\",\"genericExceptionTypes\":[],\"declaringClass\":\"java.lang.Object\",\"name\":\"getClass\",\"genericParameterTypes\":\"()\",\"modifiers\":273,\"returnType\":\"Class\",\"parameterTypes\":\"()\",\"genericReturnType\":\"java.lang.Class<?>\"},{\"generic\":\"public final native void java.lang.Object.notify()\",\"genericExceptionTypes\":[],\"declaringClass\":\"java.lang.Object\",\"name\":\"notify\",\"genericParameterTypes\":\"()\",\"modifiers\":273,\"returnType\":\"void\",\"parameterTypes\":\"()\",\"genericReturnType\":\"void\"},{\"generic\":\"public final native void java.lang.Object.notifyAll()\",\"genericExceptionTypes\":[],\"declaringClass\":\"java.lang.Object\",\"name\":\"notifyAll\",\"genericParameterTypes\":\"()\",\"modifiers\":273,\"returnType\":\"void\",\"parameterTypes\":\"()\",\"genericReturnType\":\"void\"}],\"superClass\":\"java.lang.Object\",\"declaredFields\":[{\"declaringClass\":\"org.exoplatform.ide.groovy.codeassistant.Address\",\"name\":\"address\",\"modifiers\":1,\"type\":\"String\"},{\"declaringClass\":\"org.exoplatform.ide.groovy.codeassistant.Address\",\"name\":\"city\",\"modifiers\":2,\"type\":\"String\"},{\"declaringClass\":\"org.exoplatform.ide.groovy.codeassistant.Address\",\"name\":\"state\",\"modifiers\":1,\"type\":\"String\"}],\"name\":\"Address\",\"qualifiedName\":\"org.exoplatform.ide.groovy.codeassistant.Address\",\"declaredMethods\":[{\"generic\":\"public java.lang.String org.exoplatform.ide.groovy.codeassistant.Address.getCity()\",\"genericExceptionTypes\":[],\"declaringClass\":\"org.exoplatform.ide.groovy.codeassistant.Address\",\"name\":\"getCity\",\"genericParameterTypes\":\"()\",\"modifiers\":1,\"returnType\":\"String\",\"parameterTypes\":\"()\",\"genericReturnType\":\"java.lang.String\"}],\"interfaces\":[],\"modifiers\":1,\"type\":\"CLASS\",\"declaredConstructors\":[{\"generic\":\"public org.exoplatform.ide.groovy.codeassistant.Address()\",\"genericExceptionTypes\":[],\"declaringClass\":\"org.exoplatform.ide.groovy.codeassistant.Address\",\"name\":\"org.exoplatform.ide.groovy.codeassistant.Address\",\"genericParameterTypes\":\"()\",\"modifiers\":1,\"parameterTypes\":\"()\"}],\"constructors\":[{\"generic\":\"public org.exoplatform.ide.groovy.codeassistant.Address()\",\"genericExceptionTypes\":[],\"declaringClass\":\"org.exoplatform.ide.groovy.codeassistant.Address\",\"name\":\"org.exoplatform.ide.groovy.codeassistant.Address\",\"genericParameterTypes\":\"()\",\"modifiers\":1,\"parameterTypes\":\"()\"}],\"fields\":[{\"declaringClass\":\"org.exoplatform.ide.groovy.codeassistant.Address\",\"name\":\"address\",\"modifiers\":1,\"type\":\"String\"},{\"declaringClass\":\"org.exoplatform.ide.groovy.codeassistant.Address\",\"name\":\"state\",\"modifiers\":1,\"type\":\"String\"}]}";

   public void testUnmarshallFindClass()
   {
      List<TokenExt> tokens = new ArrayList<TokenExt>();
      FindClassesUnmarshaller unmarshaller = new FindClassesUnmarshaller(tokens);

      MockResponse response = new MockResponse(jsonFindClass);

      try
      {
         unmarshaller.unmarshal(response);
      }
      catch (UnmarshallerException e)
      {
         System.out.println(e.getMessage());
         e.printStackTrace();
         fail();
      }

      assertEquals(4, tokens.size());

      TokenExt tt = tokens.get(2);

      assertEquals("List", tt.getName());
      assertEquals("INTERFACE", tt.getType().toString());
      assertEquals("java.util.List", tt.getProperty(TokenExtProperties.FQN));

   }

   public void testUnmarhallClassInfo()
   {
      GroovyClass classInfo = new GroovyClass();
      ClassDescriptionUnmarshaller unmarshaller = new ClassDescriptionUnmarshaller(classInfo);
      
      MockResponse response = new MockResponse(jsonGetClass);
      
      try
      {
         unmarshaller.unmarshal(response);
      }
      catch (UnmarshallerException e)
      {
         System.out.println(e.getMessage());
         e.printStackTrace();
         fail();
      }
      
      for (TokenExt t : classInfo.getPublicConstructors())
      {
         System.out.println(t.toString());
      }
      for (TokenExt t : classInfo.getPublicFields())
      {
         System.out.println(t.toString());
      }
      for (TokenExt t : classInfo.getPublicMethods())
      {
         System.out.println(t.toString());
      }
   }
}
