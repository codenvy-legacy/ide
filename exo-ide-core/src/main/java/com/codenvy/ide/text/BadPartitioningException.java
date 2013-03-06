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
package com.codenvy.ide.text;

/**
 * Represents the attempt to refer to a non-existing document partitioning.
 * <p>
 * This class is not intended to be serialized.
 * </p>
 * 
 * @see org.eclipse.Document.text.IDocument
 * @see org.eclipse.jface.text.IDocumentExtension3
 * @since 3.0
 */
public class BadPartitioningException extends Exception
{

   /**
    * Serial version UID for this class.
    * <p>
    * Note: This class is not intended to be serialized.
    * </p>
    * 
    * @since 3.1
    */
   private static final long serialVersionUID = 3256439205327876408L;

   /** Creates a new bad partitioning exception. */
   public BadPartitioningException()
   {
   }

   /**
    * Creates a new bad partitioning exception.
    * 
    * @param message message describing the exception
    */
   public BadPartitioningException(String message)
   {
      super(message);
   }
}
