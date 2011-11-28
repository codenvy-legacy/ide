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

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.event.EditorHotKeyCalledEvent;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * This is abstract Editor for eXo IDE<br>
 * Editor - a visual component designed to display and edit content file.<br> 
 * Furthermore the editor may support additional features (capabilities), such as:
 *  
 * <li>Syntax coloring ;
 * <li>Validation Code (according to the syntax file to be edited); 
 * <li>CodeAssistant (autocomlation, viewing documentation to the code, etc.);
 * <li>Deliver a set of content dependent tokens for alternative interviews (for example CodeOutline);
 *  
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Editor Feb 9, 2011 4:24:07 PM evgen $
 *
 */
public abstract class Editor extends AbsolutePanel
{

   protected HandlerManager eventBus;

   protected String content;

   protected Map<String, Object> params;

   public Editor(String content, Map<String, Object> params, HandlerManager eventBus)
   {
      this.content = content;
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
   public abstract void showLineNumbers(boolean showLineNumbers);

   /**
    * sets text cursor into the editor area
    */
   public abstract void setFocus();

   /**
    * Set cursor at the position (row, column). If there are now such row or column in the specified row in the text, then cursor will be stayed as it.
    * @param column
    * @param row
    */
   public abstract void goToPosition(int row, int column);

   /**
    * Delete line content at cursor 
    */
   public abstract void deleteCurrentLine();

   /**
    * Find and select text
    * @param find pattern
    * @param caseSensitive is pattern case sensitive
    * @return <code>true</code> if editor text contains par matched to pattern
    */
   public abstract boolean findAndSelect(String find, boolean caseSensitive);

   /**
    * Replace founded text block
    * @param find pattern
    * @param replace text to replace
    * @param caseSensitive is pattern case sensetive
    */
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

   /**
    * Get cursor row
    * @return number of row with cursor
    */
   public abstract int getCursorRow();

   /**
    * Get cursor column
    * @return number of column with cursor
    */
   public abstract int getCursorCol();

   /**
    * Set hot keys that editor must ignore, every hot keys must transport to main IDE frame via {@link EditorHotKeyCalledEvent}  
    * @param hotKeyList
    */
   public abstract void setHotKeyList(List<String> hotKeyList);

   /**
    * If editor support autocompletion and outline feature,
    * this method return {@link List} of {@link Token} parsed from current file
    * @return {@link List} of {@link Token}
    */
   public abstract List<? extends Token> getTokenList();

   /**
    * If editor support autocompletion and outline feature,
    * this method return {@link List} of {@link Token} parsed from current file in background without freezing of browser
    * @return {@link List} of {@link Token}
    */
   public abstract void getTokenListInBackground();
   
   /**
    * Replaces current line content and set, in this line, cursor position
    */
   public abstract void replaceTextAtCurrentLine(String line, int cursorPosition);
   
   /**
    * Verify if there any such fqn among the default packages of import statements  and insert import statement like "import <fqn>;" in the appropriate place of file 
    * @param fqn
    */
   public abstract void insertImportStatement(String fqn);
   
   /**
    * Get content of the line.
    * @param line number of line. <b>Must be larger 0 and less the file line count</b>
    * @return String content of line
    */
   public abstract String getLineContent(int line); 
}
