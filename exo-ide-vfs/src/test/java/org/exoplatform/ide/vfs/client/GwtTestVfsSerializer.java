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
package org.exoplatform.ide.vfs.client;

import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.BooleanProperty;
import org.exoplatform.ide.vfs.shared.StringProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GwtTestVfsSerializer.java 65773 2011-02-02 13:46:50Z andrew00x
 *          $
 */
public class GwtTestVfsSerializer extends GWTTestCase
{
   @Override
   protected void gwtSetUp() throws Exception
   {
      super.gwtSetUp();
   }

   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ide.vfs.IDEVFS";
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   public void test1()
   {
      // TODO real test
      System.out.println(JSONSerializer.STRING_SERIALIZER.fromArray(new String[]{"a", "b", "c", "d", "e"}).toString());
      Map m = new HashMap();
      m.put("a", "A");
      m.put("b", "B");
      m.put("c", "C");
      m.put("d", "D");
      System.out.println(JSONSerializer.STRING_SERIALIZER.fromMap(m).toString());

      List listB = new ArrayList(2);
      listB.add(true);
      listB.add(false);
      JSONValue objectB = JSONSerializer.PROPERTY_SERIALIZER.fromObject(new BooleanProperty("testB", listB));
      System.out.println(objectB.toString());

      List listS = new ArrayList(6);
      listS.add("to");
      listS.add("be");
      listS.add("or");
      listS.add("not");
      listS.add("to");
      listS.add("be");
      JSONValue objectS = JSONSerializer.PROPERTY_SERIALIZER.fromObject(new StringProperty("testS", listS));
      System.out.println(objectS.toString());

      List l = new ArrayList();
      l.add(new StringProperty("testS", listS));
      l.add(new BooleanProperty("testB", listB));
      System.out.println(JSONSerializer.PROPERTY_SERIALIZER.fromCollection(l));

      AccessControlEntry acl1 = new AccessControlEntry("exo1", new HashSet(Arrays.asList("read", "write")));
      AccessControlEntry acl2 = new AccessControlEntry("exo2", new HashSet(Arrays.asList("write")));
      System.out.println(JSONSerializer.ACL_SERIALIZER.fromObject(acl1));
      System.out.println(JSONSerializer.ACL_SERIALIZER.fromCollection(new ArrayList(Arrays.asList(acl1, acl2))));
   }

   public void test2()
   {
      String json = "{\"a\":\"A\", \"b\":\"B\", \"c\":\"C\", \"d\":\"D\", \"e\":\"E\"}";
      Map map = JSONDeserializer.STRING_DESERIALIZER.toMap(JSONParser.parse(json));
      System.out.println("toMap: " + map);

      json = "{\"a\":true, \"b\":false, \"c\":true, \"d\":false, \"e\":false}";
      map = JSONDeserializer.BOOLEAN_DESERIALIZER.toMap(JSONParser.parse(json));
      System.out.println("toMap: " + map);

      json = "{\"a\":123, \"b\":456, \"c\":789, \"d\":987, \"e\":654}";
      map = JSONDeserializer.NUMBER_DESERIALIZER.toMap(JSONParser.parse(json));
      System.out.println("toMap: " + map);
   }
}
