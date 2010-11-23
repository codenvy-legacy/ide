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
import org.exoplatform.ide.client.framework.codeassistant.GroovyToken;
import org.exoplatform.ide.client.framework.codeassistant.GroovyTokenProperties;
import org.exoplatform.ide.client.module.groovy.ClientTest;
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
   private String json =
      "[{\"name\": \"Address\",\"qualifiedName\": \"org.exoplatform.ide.groovy.codeassistant.Address\",\"modifiers\": 0,\"type\": \"CLASS\"},"
         + "{\"name\": \"A\", \"qualifiedName\": \"org.exoplatform.ide.groovy.codeassistant.A\", \"modifiers\": 253,\"type\": \"CLASS\"}," +
      "{\"name\":\"List\", \"qualifiedName\":\"java.util.List\",\"modifiers\":0, \"type\":\"INTERFACE\"}," +
      "{\"name\":\"ArrayA\", \"qualifiedName\":\"java.util.ArrayList\",\"modifiers\":0, \"type\":\"ARRAY\"}]";

   public void testUnmarshall()
   {
      List<GroovyToken> tokens = new ArrayList<GroovyToken>();
      FindClassesUnmarshaller unmarshaller = new FindClassesUnmarshaller(tokens);

      MockResponse response = new MockResponse(json);
      
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
      
      GroovyToken tt = tokens.get(2);
      
      assertEquals("List", tt.getName());
      assertEquals("INTERFACE", tt.getType().toString());
      assertEquals("java.util.List", tt.getProperty(GroovyTokenProperties.FQN));
      
   }

}
