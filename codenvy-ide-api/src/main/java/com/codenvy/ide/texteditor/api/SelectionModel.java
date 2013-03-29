/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.texteditor.api;

import com.codenvy.ide.text.Position;

/**
 * A interface that models the user's selection.
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface SelectionModel
{
   /**
    * Clear selection
    */
   void deselect();

   /**
    * @return true if editor has selection
    */
   boolean hasSelection();

   /**
    * Select all test in editor.
    */
   void selectAll();

   /**
    * Get selected range
    * @return the selected range
    */
   Position getSelectedRange();

   /**
    * Move cursor to offset.
    * @param offset the offset
    */
   void setCursorPosition(int offset);

   /**
    * Select and reveal text in editor
    * @param offset the offset, start selection
    * @param length the length of the selection
    */
   void selectAndReveal(int offset, int length);

   /**
    * Get cursor position
    * @return the position of cursor.
    */
   Position getCursorPosition();
}
