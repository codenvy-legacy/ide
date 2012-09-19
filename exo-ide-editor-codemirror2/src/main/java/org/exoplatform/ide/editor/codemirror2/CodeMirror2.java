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
package org.exoplatform.ide.editor.codemirror2;

import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorCapability;
import org.exoplatform.ide.editor.api.SelectionRange;
import org.exoplatform.ide.editor.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.event.EditorContentChangedHandler;
import org.exoplatform.ide.editor.api.event.EditorContextMenuEvent;
import org.exoplatform.ide.editor.api.event.EditorContextMenuHandler;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityHandler;
import org.exoplatform.ide.editor.api.event.EditorFocusReceivedEvent;
import org.exoplatform.ide.editor.api.event.EditorFocusReceivedHandler;
import org.exoplatform.ide.editor.api.event.EditorHotKeyPressedEvent;
import org.exoplatform.ide.editor.api.event.EditorHotKeyPressedHandler;
import org.exoplatform.ide.editor.api.event.EditorInitializedEvent;
import org.exoplatform.ide.editor.api.event.EditorInitializedHandler;
import org.exoplatform.ide.editor.api.event.SearchCompleteCallback;
import org.exoplatform.ide.editor.text.IDocument;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class CodeMirror2 extends AbsolutePanel implements Editor
{
   
   private final String id;
   
   private final String mimeType;
   
//   private Document iFrameDocument;
//   
//   private Element headElement;
//   
//   private Element bodyElement;
   
   private JavaScriptObject editor;
   
   public CodeMirror2(String mimeType)
   {
      id = "CodeMirror2-" + hashCode();
      this.mimeType = mimeType;
      
      System.out.println("generated id > " + id);
      
      getElement().getStyle().setBackgroundColor("grey");
      
//      final Frame iFrame = new Frame("_codemirror2.html");
//      add(iFrame);
//      iFrame.setSize("100%", "100%");
//      iFrame.getElement().setAttribute("frameborder", "0");
//      iFrame.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
//      
//      iFrame.addLoadHandler(new LoadHandler()
//      {
//         @Override
//         public void onLoad(LoadEvent event)
//         {
////            FrameElement frameElement = iFrame.getElement().cast();
////            iFrameDocument = frameElement.getContentDocument();
////            headElement = iFrameDocument.getElementsByTagName("head").getItem(0);
////            bodyElement = iFrameDocument.getElementsByTagName("body").getItem(0);
////            
////            System.out.println("body element > " + bodyElement);
//
////            injectStyleElement("codemirror2/lib/codemirror.css");
////            injectJSElement("codemirror2/lib/codemirror.js");
////            injectJSElement("codemirror2/mode/xml/xml.js");
////            injectStyleElement("codemirror2/codemirror-styles.css");
//            
//            //initCodeMirror();
//         }
//      });
      
      initCodeMirror();
      
   }
   
//   private void injectJSElement(String src)
//   {
//      ScriptElement sce = iFrameDocument.createScriptElement();
//      sce.setType("text/javascript");
//      sce.setSrc(src);
//      headElement.appendChild(sce);
//   }
//   
//   private void injectStyleElement(String href)
//   {
//      LinkElement le = iFrameDocument.createLinkElement();
//      le.setRel("stylesheet");
//      le.setHref(href);
//      headElement.appendChild(le);      
//   }
//   
   
   private void initCodeMirror()
   {
      //BodyPanel bodyPanel = new BodyPanel();
      //bodyElement.appendChild(bodyPanel.getElement());
      
//      bodyPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
//      bodyPanel.getElement().getStyle().setLeft(0, Unit.PX);
//      bodyPanel.getElement().getStyle().setTop(0, Unit.PX);
//      bodyPanel.getElement().getStyle().setRight(0, Unit.PX);
//      bodyPanel.getElement().getStyle().setBottom(0, Unit.PX);
//      bodyPanel.getElement().getStyle().setBackgroundColor("green");
      
       TextArea textArea = new TextArea();
       textArea.getElement().getStyle().setPosition(Position.ABSOLUTE);
       textArea.getElement().getStyle().setLeft(0, Unit.PX);
       textArea.getElement().getStyle().setTop(0, Unit.PX);
       textArea.getElement().getStyle().setRight(0, Unit.PX);
       textArea.getElement().getStyle().setBottom(0, Unit.PX);
       add(textArea);
       
       editor = init(textArea.getElement(), true, false);
//       
//        Scheduler.get().scheduleDeferred(new ScheduledCommand()
//        {
//           @Override
//           public void execute()
//           {
//              refreshEditor(editor);
//           }
//        });       
   }
   
   private native JavaScriptObject init(JavaScriptObject textArea, boolean showLineNumbers, boolean isReadOnly)
   /*-{
      var instance = this;
      
      alert('> ' + $wnd);
      alert('> ' + $doc);
      alert('codemirror > ' + $wnd.CodeMirror);
      
      var editor = $wnd.CodeMirror.fromTextArea(textArea, {
         mode: {
            name: "xml",
            alignCDATA: true
         },
   
         lineNumbers: showLineNumbers,
         readOnly: isReadOnly,
   
         //fixedGutter: true,
         onCursorActivity: function() {
            //editor.setLineClass(hlLine, null);
            //hlLine = editor.setLineClass(editor.getCursor().line, "activeline");
            //var cursor = editor.getCursor();            
            //instance.@org.exoplatform.ide.editor.codemirror2.CodeMirror2::cursorRow = cursor.line;
            //instance.@org.exoplatform.ide.editor.codemirror2.CodeMirror2::cursorColumn = cursor.ch;
            //instance.@org.exoplatform.ide.editor.codemirror2.CodeMirror2::onCursorActivity()();
         },
   
         onChange: function() {
            //instance.@org.exoplatform.ide.editor.codemirror2.CodeMirror2::onContentChanged()();
         }
      });
 
      //var hlLine = editor.setLineClass(0, "activeline");
      return editor;
   }-*/;

   private native void refreshEditor(JavaScriptObject editor)
   /*-{
      //editor.refresh();
   }-*/;   
   
   @Override
   public String getMimeType()
   {
      return mimeType;
   }

   @Override
   public String getName()
   {
      return "CodeMirror 2";
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
   public IDocument getDocument()
   {
      return null;
   }

   @Override
   public boolean isCapable(EditorCapability capability)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void formatSource()
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void showLineNumbers(boolean showLineNumbers)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void setFocus()
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void setCursorPosition(int row, int column)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void deleteCurrentLine()
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void search(String query, boolean caseSensitive, SearchCompleteCallback searchCompleteCallback)
   {
   }

   @Override
   public void replaceMatch(String replacement)
   {
   }

   @Override
   public boolean hasUndoChanges()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void undo()
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public boolean hasRedoChanges()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void redo()
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public boolean isReadOnly()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void setReadOnly(boolean readOnly)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public int getCursorRow()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public int getCursorColumn()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public void replaceTextAtCurrentLine(String line, int cursorPosition)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public String getLineText(int line)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setLineText(int line, String text)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public int getNumberOfLines()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public SelectionRange getSelectionRange()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void selectRange(int startLine, int startChar, int endLine, int endChar)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void selectAll()
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void cut()
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void copy()
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void paste()
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#delete()
    */
   @Override
   public void delete()
   {
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorOffsetLeft()
    */
   @Override
   public int getCursorOffsetLeft()
   {
      return 0;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorOffsetTop()
    */
   @Override
   public int getCursorOffsetTop()
   {
      return 0;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addContentChangedHandler(org.exoplatform.ide.editor.api.event.EditorContentChangedHandler)
    */
   @Override
   public HandlerRegistration addContentChangedHandler(EditorContentChangedHandler handler)
   {
      return addHandler(handler, EditorContentChangedEvent.TYPE);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addContextMenuHandler(org.exoplatform.ide.editor.api.event.EditorContextMenuHandler)
    */
   @Override
   public HandlerRegistration addContextMenuHandler(EditorContextMenuHandler handler)
   {
      return addHandler(handler, EditorContextMenuEvent.TYPE);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addCursorActivityHandler(org.exoplatform.ide.editor.api.event.EditorCursorActivityHandler)
    */
   @Override
   public HandlerRegistration addCursorActivityHandler(EditorCursorActivityHandler handler)
   {
      return addHandler(handler, EditorCursorActivityEvent.TYPE);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addFocusReceivedHandler(org.exoplatform.ide.editor.api.event.EditorFocusReceivedHandler)
    */
   @Override
   public HandlerRegistration addFocusReceivedHandler(EditorFocusReceivedHandler handler)
   {
      return addHandler(handler, EditorFocusReceivedEvent.TYPE);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addHotKeyPressedHandler(org.exoplatform.ide.editor.api.event.EditorHotKeyPressedHandler)
    */
   @Override
   public HandlerRegistration addHotKeyPressedHandler(EditorHotKeyPressedHandler handler)
   {
      return addHandler(handler, EditorHotKeyPressedEvent.TYPE);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addInitializedHandler(org.exoplatform.ide.editor.api.event.EditorInitializedHandler)
    */
   @Override
   public HandlerRegistration addInitializedHandler(EditorInitializedHandler handler)
   {
      return addHandler(handler, EditorInitializedEvent.TYPE);
   }

}
