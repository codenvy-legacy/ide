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

package org.exoplatform.ide.editor.codemirror2;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.TextArea;

import org.exoplatform.ide.editor.api.Capability;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.EditorParameters;

import java.util.HashMap;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CodeMirror2 extends AbsolutePanel implements Editor
{
   
   private String content;
   
   private HashMap<String, Object> params;
   
   private String id;
   
   private JavaScriptObject editor;
   
   private int cursorRow;
   
   private int cursorColumn;
   
   public CodeMirror2(String content, HashMap<String, Object> params) {
      this.content = content;
      this.params = params;
      
      id = "CodeMirror2-" + String.valueOf(this.hashCode());
      setStyleName("Codemirror2Panel");      
      
      TextArea textArea = new TextArea();
      textArea.getElement().getStyle().setPosition(Position.ABSOLUTE);
      textArea.getElement().getStyle().setLeft(0, Unit.PX);
      textArea.getElement().getStyle().setTop(0, Unit.PX);
      textArea.getElement().getStyle().setRight(0, Unit.PX);
      textArea.getElement().getStyle().setBottom(0, Unit.PX);
      add(textArea);
      
      boolean showLineNumbers = params.get(EditorParameters.SHOW_LINE_NUMERS) != null ? 
         (Boolean)params.get(EditorParameters.SHOW_LINE_NUMERS) :
            false;
      boolean readOnly = params.get(EditorParameters.IS_READ_ONLY) != null ?
         (Boolean)params.get(EditorParameters.IS_READ_ONLY) :
            false;
      
      editor = init(textArea.getElement(), showLineNumbers, readOnly);
      
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            refreshEditor(editor);
         }
      });
   }
   
   private native JavaScriptObject init(JavaScriptObject textArea, boolean showLineNumbers, boolean isReadOnly) /*-{
      var instance = this;
   
      var editor = $wnd.CodeMirror.fromTextArea(textArea, {
         mode: {
            name: "xml",
            alignCDATA: true
            },
         lineNumbers: showLineNumbers,
         readOnly: isReadOnly,
         //fixedGutter: true,
         onCursorActivity: function() {
            editor.setLineClass(hlLine, null);
            hlLine = editor.setLineClass(editor.getCursor().line, "activeline");
            
            var cursor = editor.getCursor();            
            instance.@org.exoplatform.ide.editor.codemirror2.CodeMirror2::cursorRow = cursor.line;
            instance.@org.exoplatform.ide.editor.codemirror2.CodeMirror2::cursorColumn = cursor.ch;
            instance.@org.exoplatform.ide.editor.codemirror2.CodeMirror2::onCursorActivity()();
         },
         onChange: function() {
            instance.@org.exoplatform.ide.editor.codemirror2.CodeMirror2::onContentChanged()();
         }
      });
      
      var hlLine = editor.setLineClass(0, "activeline");
      return editor;
   }-*/;
   
   private native void refreshEditor(JavaScriptObject editor) /*-{
      editor.refresh();
   }-*/;
   
   private void onCursorActivity() {
      fireEvent(new EditorCursorActivityEvent(id, cursorRow, cursorColumn));
   }
   
   private void onContentChanged() {
      fireEvent(new EditorContentChangedEvent(id));
   }
   
   @Override
   public String getId()
   {
      return id;
   }

   @Override
   public String getText()
   {
      return null;
   }

   @Override
   public void setText(String text)
   {
   }

   @Override
   public native void focus() /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror2.CodeMirror2::editor;
      editor.focus();
   }-*/;

   @Override
   public void undo()
   {
   }

   @Override
   public void redo()
   {
   }

   @Override
   public boolean hasUndoChanges()
   {
//      undo()
//      Undo one edit (if any undo events are stored).
//      redo()
//      Redo one undone edit.
//      historySize() → object
//      Returns an object with {undo, redo} properties, both of which hold integers, indicating the amount of stored undo and redo operations.      
      
      return false;
   }

   @Override
   public boolean hasRedoChanges()
   {
      return false;
   }

   @Override
   public int getCursorRow()
   {
      return cursorRow;
   }

   @Override
   public int getCursorColumn()
   {
      return cursorColumn;
   }

   @Override
   public native boolean isReadOnly() /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror2.CodeMirror2::editor;
      
      var isReadOnly = editor.getOption("readOnly");
      return isReadOnly;

//      alert("Is Read Only > " + isReadOnly);
//      getOption(option) → value
//      readOnly (boolean)
      
   }-*/;

   @Override
   public boolean isCapable(Capability capability)
   {
      switch (capability)
      {
         case SHOW_LINE_NUMBERS : return true;
         case SET_CURSOR_POSITION: return true;
         case DELETE_LINE: return true;
         case INSERT_LINE: return true;
      }
      
      return false;
   }

   @Override
   public native void setCursorPosition(int row, int column) /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror2.CodeMirror2::editor;
      var cursor = {
        line: row,
        ch: column
      };
      editor.setCursor(cursor);
   }-*/;

   @Override
   public native void deleteLine(int lineNumber) /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror2.CodeMirror2::editor;
      editor.removeLine(lineNumber);
   }-*/;

   @Override
   public native void insetLine(int lineNumber, String text) /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror2.CodeMirror2::editor;
      
      var curLineText = editor.getLine(lineNumber);
      curLineText += "\r\n" + text; 
      editor.setLine(lineNumber, curLineText);
   }-*/;

   @Override
   public native void showLineNumbers(boolean lineNumbers) /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror2.CodeMirror2::editor;
      editor.setOption("lineNumbers", lineNumbers);
   }-*/;

   @Override
   public void format()
   {
   }

   @Override
   public boolean findText(String text, boolean caseSensitive)
   {
      return false;
   }

   @Override
   public void replaceSelection(String text)
   {
   }

   @Override
   public native String getLineText(int lineNumber) /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror2.CodeMirror2::editor;
      return editor.getLine(line);
   }-*/;

   @Override
   public native void setLineText(int lineNumber, String text) /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror2.CodeMirror2::editor;
      editor.setLine(lineNumber, text);
   }-*/;

}
