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
package com.codenvy.ide.extension.css.editor;

import com.codenvy.ide.texteditor.api.TextEditorPartView;

/**
 * Invocation context for Css code assistant, it's hold prefix, offset and resources of currant code assistant session.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class InvocationContext
{
   private final String prefix;

   private final int offset;

   private final CssResources resources;

   private final TextEditorPartView editor;

   /**
    * @param prefix
    * @param offset
    */
   public InvocationContext(String prefix, int offset, CssResources resources, TextEditorPartView editor)
   {
      super();
      this.prefix = prefix;
      this.offset = offset;
      this.resources = resources;
      this.editor = editor;
   }

   /**
    * @return the prefix
    */
   public String getPrefix()
   {
      return prefix;
   }

   /**
    * @return the offset
    */
   public int getOffset()
   {
      return offset;
   }

   /**
    * @return the resourcess
    */
   public CssResources getResources()
   {
      return resources;
   }

   /**
    * @return the editor
    */
   public TextEditorPartView getEditor()
   {
      return editor;
   }
}
