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
package com.codenvy.ide.text.edits;

/**
 * Thrown to indicate that an edit got added to a parent edit but the child edit somehow conflicts with the parent or one of it
 * siblings.
 * <p>
 * This class is not intended to be serialized.
 * </p>
 * 
 * @see TextEdit#addChild(TextEdit)
 * @see TextEdit#addChildren(TextEdit[])
 * 
 * @since 3.0
 */
public class MalformedTreeException extends RuntimeException
{

   // Not intended to be serialized
   private static final long serialVersionUID = 1L;

   private TextEdit fParent;

   private TextEdit fChild;

   /**
    * Constructs a new malformed tree exception.
    * 
    * @param parent the parent edit
    * @param child the child edit
    * @param message the detail message
    */
   public MalformedTreeException(TextEdit parent, TextEdit child, String message)
   {
      super(message);
      fParent = parent;
      fChild = child;
   }

   /**
    * Returns the parent edit that caused the exception.
    * 
    * @return the parent edit
    */
   public TextEdit getParent()
   {
      return fParent;
   }

   /**
    * Returns the child edit that caused the exception.
    * 
    * @return the child edit
    */
   public TextEdit getChild()
   {
      return fChild;
   }

   void setParent(TextEdit parent)
   {
      fParent = parent;
   }
}
