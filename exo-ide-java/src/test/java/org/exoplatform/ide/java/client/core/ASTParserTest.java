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
package org.exoplatform.ide.java.client.core;

import org.exoplatform.ide.java.client.core.dom.TypeDeclaration;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 34360 2009-07-22 23:58:59Z evgen $
 * 
 */
public class ASTParserTest extends ParserBaseTest
{

   @Test
   public void testParseUnit() throws Exception
   {
      assertFalse(unit.types().size() == 0);
      assertEquals(1, unit.types().size());
   }

   @Test
   public void testPareseClass() throws Exception
   {
      TypeDeclaration td = (TypeDeclaration)unit.types().get(0);
      assertEquals("CreateJavaClassPresenter", td.getName().getFullyQualifiedName());
   }

   @Test
   public void testParseInnerType() throws Exception
   {
      TypeDeclaration td = (TypeDeclaration)unit.types().get(0);
      assertEquals(1, td.getTypes().length);
      TypeDeclaration innerType = td.getTypes()[0];
      assertEquals("Display", innerType.getName().getFullyQualifiedName());
   }

   @Test
   public void testInnerTypeMethods() throws Exception
   {
      TypeDeclaration td = (TypeDeclaration)unit.types().get(0);
      TypeDeclaration innerType = td.getTypes()[0];
      assertEquals(19, innerType.getMethods().length);
   }

   @Test
   public void testInnerTypeFields() throws Exception
   {
      TypeDeclaration td = (TypeDeclaration)unit.types().get(0);
      TypeDeclaration innerType = td.getTypes()[0];
      assertEquals(1, innerType.getFields().length);
   }

}