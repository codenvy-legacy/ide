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
package com.codenvy.ide.java.client.core;

import static org.junit.Assert.*;

import com.codenvy.ide.java.client.BaseTest;
import com.codenvy.ide.java.client.core.compiler.CharOperation;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  10:02:58 AM 34360 2009-07-22 23:58:59Z evgen $
 *
 */
@Ignore
public class CharOperationTest extends BaseTest
{
   @Test
   public void testJavaIdentifierPart()
   {
      String s = "for";
      for (char c : s.toCharArray())
      {
         if (!CharOperation.isJavaIdentifierPart(c))
            fail("Char '" + c + "' is valid Java identifier part");
      }
   }
   @Test
   public void testJavaIdentifierPartUnicode()
   {
      String s = "змінна";
      for (char c : s.toCharArray())
      {
         if (!CharOperation.isJavaIdentifierPart(c))
            fail("Char '" + c + "' is valid Java identifier part");
      }
   }
   @Test
   public void testNotJavaIdentifierPart()
   {
      String s = "@#%*";
      for (char c : s.toCharArray())
      {
         if (CharOperation.isJavaIdentifierPart(c))
            fail("Char '" + c + "' not valid Java identifier part");
      }
   }
   @Test
   public void testJavaIdentifierStart()
   {
      String s = "_$Ab";
      for (char c : s.toCharArray())
      {
         if (!CharOperation.isJavaIdentifierStart(c))
            fail("Char '" + c + "' is valid Java identifier part");
      }
   }
   @Test
   public void testNotJavaIdentifierStart()
   {
      String s = "123@#&";
      for (char c : s.toCharArray())
      {
         if (CharOperation.isJavaIdentifierStart(c))
            fail("Char '" + c + "' not valid Java identifier part");
      }
   }
}
