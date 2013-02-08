/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.impl.fs;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.exoplatform.ide.vfs.impl.fs.LocalFileSystemTest.Pair;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class PathTest extends TestCase
{
   private Map<String, Pair<String, String[]>> legal;
   private String[] illegal;

   @SuppressWarnings("unchecked")
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      legal = new HashMap<String, Pair<String, String[]>>();
      legal.put("/a/b/c/d", new Pair("/a/b/c/d", new String[]{"a", "b", "c", "d"}));
      legal.put("/a/b/c/../d", new Pair("/a/b/d", new String[]{"a", "b", "d"}));
      legal.put("/a/b/c/./d", new Pair("/a/b/c/d", new String[]{"a", "b", "c", "d"}));
      illegal = new String[]{"..", "/a/../..", "/a/b/../../..", "/a/b/../../../c/././.."};
   }

   public void testPath()
   {
      for (Map.Entry<String, Pair<String, String[]>> e : legal.entrySet())
      {
         Path parsed = Path.fromString(e.getKey());
         assertEquals(e.getValue().a, parsed.toString());
         assertTrue(
            String.format("expected: %s but was: %s", Arrays.toString(e.getValue().b), Arrays.toString(parsed.elements())),
            Arrays.equals(e.getValue().b, parsed.elements()));
      }
   }

   public void testSubPath()
   {
      final String raw = "/a/b/c/d";
      Path parsed = Path.fromString(raw);
      assertEquals("/c/d", parsed.subPath(2).toString());
   }

   public void testSubPath2()
   {
      final String raw = "/a/b/c/d/";
      Path parsed = Path.fromString(raw);
      assertEquals("/a/b/c", parsed.subPath(0, parsed.length() - 1).toString());
   }

   public void testNewPath()
   {
      final String raw = "/a/b";
      Path parsed = Path.fromString(raw);
      assertEquals("/a/b/c/d", parsed.newPath("/c/d").toString());
   }

   public void testIllegalPath()
   {
      for (String s : illegal)
      {
         try
         {
            Path.fromString(s);
            fail(String.format("IllegalArgumentException expected for path '%s' ", s));
         }
         catch (IllegalArgumentException ok)
         {
            //System.err.println(ok.getMessage());
         }
      }
   }
}
