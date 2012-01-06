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

package org.exoplatform.ide.editor.api;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public interface Editor extends IsWidget
{
   
   /**
    * Returns unique identifier of editor.
    * 
    * @return id of editor's element
    */
   String getId();
   
   /**
    * Returns editor's text.
    * 
    * @return editor's text
    */
   String getText();

   /**
    * Sets new text in editor.
    * 
    * @param text - new text
    */
   void setText(String text);
   
   /**
    * Sets focus into the editor.
    */
   void focus();

   /**
    * Undo latest change of content.
    */
   void undo();

   /**
    * Redo latest change of content.
    */
   void redo();

   /**
    * Determines whether changes in the editor to undo.
    * 
    * @return <b>true</b> if changes to undo are presents, <b>false</b> otherwise
    */
   boolean hasUndoChanges();

   /**
    * Determined whether changes in the editor to redo.
    * 
    * @return <b>true</b> changes to redo are presents, <b>false</b> otherwise
    */
   boolean hasRedoChanges();

   /**
    * Returns row number of cursor position.
    * 
    * @return row number of cursor position
    */
   int getCursorRow();

   /**
    * Returns column number of cursor position.
    * 
    * @return column number of cursor position
    */
   int getCursorColumn();
   
   /**
    * Determines whether the editor is opened in read-only mode.
    *  
    * @return <b>true</b> if editor is opened in read-only mode, <b>false</b> otherwise
    */
   boolean isReadOnly();
   
   /**
    * Check that editor supports feature.
    * 
    * @param capability feature
    * @return true if editor capable do.
    */
   boolean isCapable(Capability capability);
   
   /**
    * Moves cursor to new position.
    * If there are now such row or column, then position of cursor will not be changed.
    * 
    * @param row row number, starts at 1
    * @param column column number, starts at 1
    */
   void setCursorPosition(int row, int column);
   
   /**
    * Delete line at cursor.
    */
   void deleteCurrentLine();
   
   /**
    * Shows or hides line numbers.
    * 
    * @param showLineNumbers
    */
   void showLineNumbers(boolean isShowLineNumbers);
   
   /**
    * Format text according to type of the content.
    */
   void format();
   
   /**
    * Find and select text.
    * 
    * @param text text to find
    * @param caseSensitive is case sensitive
    * @return <b>true</b> if text is found, <b>false</b> otherwise
    */
   boolean findText(String text, boolean caseSensitive);
   
   /**
    * Replace selected text with new value.
    * 
    * @param text new text
    */
   void replaceSelection(String text);
   
   /**
    * Get content of the line.
    * 
    * @param line number of line. <b>Must be larger 0 and less the file line count</b>
    * @return String content of line
    */
   String getLineText(int line);
   
   /**
    * @param line
    * @param text
    */
   void setLineText(int line, String text);
   
}
