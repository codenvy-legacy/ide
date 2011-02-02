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

import com.google.gwt.junit.client.GWTTestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
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

   public void test1()
   {
      JSONSerializer<String> js = JSONSerializer.STRING_SERIALIZER;
      System.out.println(js.fromArray(new String[]{"a", "b", "c", "d", "e"}).toString());
      Map<String,String> m = new HashMap<String,String>();
      m.put("a","A");
      m.put("b","B");
      m.put("c","C");
      m.put("d","D");
      System.out.println(js.fromMap(m).toString());
   }

}
