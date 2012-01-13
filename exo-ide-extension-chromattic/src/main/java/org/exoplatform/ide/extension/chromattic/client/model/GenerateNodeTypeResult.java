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
package org.exoplatform.ide.extension.chromattic.client.model;

/**
 * The result bean of node type generation.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 6, 2010 $
 * 
 */
public class GenerateNodeTypeResult
{
   /**
    * Generated node type definition.
    */
   private String nodeTypeDefinition;

   /**
    * @return the nodeTypeDefinition
    */
   public String getNodeTypeDefinition()
   {
      return nodeTypeDefinition;
   }

   /**
    * @param nodeTypeDefinition the nodeTypeDefinition to set
    */
   public void setNodeTypeDefinition(String nodeTypeDefinition)
   {
      this.nodeTypeDefinition = nodeTypeDefinition;
   }
}
