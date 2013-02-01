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
package org.exoplatform.ide.client.framework.editor;

import org.exoplatform.ide.editor.client.api.SelectionRange;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.edits.TextEdit;

/**
 * Code comments modifier.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 6, 2012 2:59:01 PM anya $
 * 
 */
public interface CommentsModifier
{
   /**
    * @param document
    * @return {@link TextEdit}
    */
   TextEdit addBlockComment(SelectionRange selectionRange, IDocument document);

   /**
    * @param document
    * @return {@link TextEdit}
    */
   TextEdit removeBlockComment(SelectionRange selectionRange, IDocument document);

   /**
    * @param selectionRange
    * @param document
    * @return {@link TextEdit}
    */
   TextEdit toggleSingleLineComment(SelectionRange selectionRange, IDocument document);
}
