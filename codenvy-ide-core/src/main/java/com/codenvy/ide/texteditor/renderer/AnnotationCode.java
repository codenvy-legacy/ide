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
package com.codenvy.ide.texteditor.renderer;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
class AnnotationCode
{

   private DocumentPosition start;

   private DocumentPosition end;

   private String decoration;

   /**
    * @param start
    * @param end
    * @param decoration
    */
   public AnnotationCode(DocumentPosition start, DocumentPosition end, String decoration)
   {
      super();
      this.start = start;
      this.end = end;
      this.decoration = decoration;
   }

   /**
    * @return file position where this Annotation starts
    */
   public DocumentPosition getStart()
   {
      return start;
   }

   /**
    * @return file position where this Annotation ends (inclusive)
    */
   public DocumentPosition getEnd()
   {
      return end;
   }

   /**
    * @return the decoration
    */
   public String getDecoration()
   {
      return decoration;
   }
}
