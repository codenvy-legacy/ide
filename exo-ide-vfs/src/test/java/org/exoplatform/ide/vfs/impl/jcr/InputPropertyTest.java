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
package org.exoplatform.ide.vfs.impl.jcr;

import org.exoplatform.ide.vfs.server.InputProperty;
import org.exoplatform.ws.frameworks.json.JsonHandler;
import org.exoplatform.ws.frameworks.json.JsonParser;
import org.exoplatform.ws.frameworks.json.impl.JsonDefaultHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonParserImpl;
import org.exoplatform.ws.frameworks.json.impl.ObjectBuilder;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

import java.io.StringReader;

import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class InputPropertyTest extends JcrFileSystemTest
{
   private JsonParser parser;

   private JsonHandler handler;

   protected void setUp() throws Exception
   {
      super.setUp();
      parser = new JsonParserImpl();
      handler = new JsonDefaultHandler();
   }

   public void testRestoreFromJson() throws Exception
   {
      String json = "{\"name\":\"mediaType\", \"value\":[\"text/plain;charset=utf8\"]}";
      parser.parse(new StringReader(json), handler);
      JsonValue jsonValue = handler.getJsonObject();
      InputProperty inputProperty = ObjectBuilder.createObject(InputProperty.class, jsonValue);
      MediaType[] mediaTypes = inputProperty.valueAs(MediaType[].class);
      assertNotNull(mediaTypes);
      assertEquals(1, mediaTypes.length);
      MediaType mt = mediaTypes[0];
      assertEquals("text", mt.getType());
      assertEquals("plain", mt.getSubtype());
      assertEquals("utf8", mt.getParameters().get("charset"));
      System.out.println(inputProperty);
   }
}
