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
package org.exoplatform.ide.editor.xml.client;

import org.exoplatform.ide.client.framework.editor.AbstractCommentsModifier;
import org.exoplatform.ide.editor.api.SelectionRange;
import org.exoplatform.ide.editor.text.IDocument;
import org.exoplatform.ide.editor.text.edits.TextEdit;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 11, 2012 9:35:21 AM anya $
 * 
 */
public class XmlCommentsModifier extends AbstractCommentsModifier
{

   /**
    * @see org.exoplatform.ide.client.framework.editor.AbstractCommentsModifier#getOpenBlockComment()
    */
   @Override
   public String getOpenBlockComment()
   {
      return "<!-- ";
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.AbstractCommentsModifier#getCloseBlockComment()
    */
   @Override
   public String getCloseBlockComment()
   {
      return " -->";
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.AbstractCommentsModifier#getSingleLineComment()
    */
   @Override
   public String getSingleLineComment()
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.AbstractCommentsModifier#toggleSingleLineComment(org.exoplatform.ide.editor.api.SelectionRange,
    *      org.exoplatform.ide.editor.text.IDocument)
    */
   @Override
   public TextEdit toggleSingleLineComment(SelectionRange selectionRange, IDocument document)
   {
      return null;
   }
}
