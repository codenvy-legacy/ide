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
package org.exoplatform.ide.java.client.editor.outline;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.outline.CodeBlock;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaCodeBlock implements CodeBlock
{

   private JsonArray<CodeBlock> children = JsonCollections.createArray();

   private CodeBlock parent;

   private String type;

   private int offset;

   private int length;

   private int modifiers;

   private String name;

   private String javaType;

   /**
    * 
    */
   public JavaCodeBlock()
   {

   }

   /**
    * @param children
    * @param parent
    * @param type
    * @param offset
    * @param length
    */
   public JavaCodeBlock(CodeBlock parent, String type, int offset, int length)
   {
      super();
      this.parent = parent;
      this.type = type;
      this.offset = offset;
      this.length = length;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getType()
   {
      return type;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getOffset()
   {
      return offset;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getLength()
   {
      return length;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JsonArray<CodeBlock> getChildren()
   {
      return children;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CodeBlock getParent()
   {
      return parent;
   }

   /**
    * @param children the children to set
    */
   public void setChildren(JsonArray<CodeBlock> children)
   {
      this.children = children;
   }

   /**
    * @param parent the parent to set
    */
   public void setParent(CodeBlock parent)
   {
      this.parent = parent;
   }

   /**
    * @param type the type to set
    */
   public void setType(String type)
   {
      this.type = type;
   }

   /**
    * @param offset the offset to set
    */
   public void setOffset(int offset)
   {
      this.offset = offset;
   }

   /**
    * @param length the length to set
    */
   public void setLength(int length)
   {
      this.length = length;
   }

   /**
    * @return the modifiers
    */
   public int getModifiers()
   {
      return modifiers;
   }

   /**
    * @param modifiers the modifiers to set
    */
   public void setModifiers(int modifiers)
   {
      this.modifiers = modifiers;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the javaType
    */
   public String getJavaType()
   {
      return javaType;
   }

   /**
    * @param javaType the javaType to set
    */
   public void setJavaType(String javaType)
   {
      this.javaType = javaType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId()
   {
      return type + name + offset + length;
   }

}
