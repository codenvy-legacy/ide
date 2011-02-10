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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.editor.api.codeassitant.Token;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Editor Feb 9, 2011 4:24:07 PM evgen $
 *
 */
public abstract class Editor extends AbsolutePanel
{

   protected HandlerManager eventBus;

   protected File file;

   protected Map<String, Object> params;

   public Editor(File file, HashMap<String, Object> params, HandlerManager eventBus)
   {
      this.file = file;
      this.eventBus = eventBus;
      this.params = params;
   }

   /**
    * @return unique identifier which can be used to found out editor instance in the DOM
    */
   public abstract String getEditorId();

   /**
    * @return content of editor
    */
   public abstract String getText();

   /**
    * replace current content of editor by text parameter
    * @param text - new editor content
    */
   public abstract void setText(String text);

   /**
    * Check that editor support feature
    * @param capability
    * @return true if editor capable do.
    */
   public abstract boolean isCapable(EditorCapability capability);

   /**
    * indents code according to content type
    */
   public abstract void formatSource();

   /**
    * Displays line numbers if showLineNumbers = true, or hides otherwise
    * @param showLineNumbers
    */
   public abstract void setShowLineNumbers(boolean showLineNumbers);

   /**
    * sets text cursor into the editor area
    */
   public abstract void setFocus();

   /**
    * Set cursor at the position (row, column). If there are now such row or column in the specified row in the text, then cursor will be stayed as it.
    * @param column
    * @param row
    */
   public abstract  void goToPosition(int row, int column);

   public abstract void deleteCurrentLine();

   public abstract boolean findAndSelect(String find, boolean caseSensitive);

   public abstract void replaceFoundedText(String find, String replace, boolean caseSensitive);

   /**
    * @return <b>true</b> if there are any changes which can be undo in editor
    */
   public abstract boolean hasUndoChanges();

   /**
    * undo latest change of content
    */
   public abstract void undo();

   /**
    * @return <b>true</b> if there are any changes which can be redo in editor.
    */
   public abstract boolean hasRedoChanges();

   /**
    * redo latest change of content
    */
   public abstract void redo();

   /**
    * @return <b>true</b> if content is read-only
    */
   public abstract boolean isReadOnly();

   public abstract int getCursorRow();

   public abstract int getCursorCol();

   public abstract void setHotKeyList(List<String> hotKeyList);

   public abstract List<Token> getTokenList();

   /**
    * Replaces current line content and set, in this line, cursor position
    */
   public abstract void replaceTextAtCurrentLine(String line, int cursorPosition);
}

