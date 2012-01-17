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
package org.eclipse.jdt.client.core;

import org.eclipse.jdt.client.core.dom.TypeDeclaration;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 34360 2009-07-22 23:58:59Z evgen $
 * 
 */
public class ASTParserTestGwt extends ParserBaseTestGwt
{

   public void testParseUnit() throws Exception
   {
      assertFalse(unit.types().size() == 0);
      assertEquals(1, unit.types().size());
   }

   public void testPareseClass() throws Exception
   {
      TypeDeclaration td = (TypeDeclaration)unit.types().get(0);
      assertEquals("CreateJavaClassPresenter", td.getName().getFullyQualifiedName());
   }

   public void testParseInnerType() throws Exception
   {
      TypeDeclaration td = (TypeDeclaration)unit.types().get(0);
      assertEquals(1, td.getTypes().length);
      TypeDeclaration innerType = td.getTypes()[0];
      assertEquals("Display", innerType.getName().getFullyQualifiedName());
   }

   public void testInnerTypeMethods() throws Exception
   {
      TypeDeclaration td = (TypeDeclaration)unit.types().get(0);
      TypeDeclaration innerType = td.getTypes()[0];
      assertEquals(19, innerType.getMethods().length);
   }

   public void testInnerTypeFields() throws Exception
   {
      TypeDeclaration td = (TypeDeclaration)unit.types().get(0);
      TypeDeclaration innerType = td.getTypes()[0];
      assertEquals(1, innerType.getFields().length);
   }

}