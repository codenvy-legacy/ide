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
package org.eclipse.jdt.client.outline;

import org.eclipse.jdt.client.core.dom.ImportDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Node is used to group all import declarations.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 8, 2012 5:19:10 PM anya $
 * 
 */
public class ImportGroupNode
{
   /**
    * Name of the node in Outline.
    */
   private String name;

   /**
    * The list of imports, contains {@link ImportDeclaration} nodes.
    */
   private List<Object> imports;

   /**
    * @param name display name of the node in Outline
    * @param imports imports
    */
   public ImportGroupNode(String name, List<Object> imports)
   {
      this.name = name;
      this.imports = imports;
   }

   /**
    * @return the display node name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return {@link List} the list of imports, which contains {@link ImportDeclaration} nodes.
    */
   public List<Object> getImports()
   {
      if (imports == null)
      {
         imports = new ArrayList<Object>();
      }
      return imports;
   }

}
