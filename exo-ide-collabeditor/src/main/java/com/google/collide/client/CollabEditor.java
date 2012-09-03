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
package com.google.collide.client;

import com.google.collide.client.code.EditableContentArea;
import com.google.collide.client.code.EditorBundle;
import com.google.collide.client.code.errorrenderer.EditorErrorListener;
import com.google.collide.client.editor.gutter.NotificationManager;
import com.google.collide.client.editor.selection.SelectionModel;
import com.google.collide.client.hover.HoverPresenter;
import com.google.collide.client.util.PathUtil;
import com.google.collide.json.shared.JsonArray;
import com.google.collide.shared.document.Document;
import com.google.collide.shared.document.Document.TextListener;
import com.google.collide.shared.document.LineInfo;
import com.google.collide.shared.document.Position;
import com.google.collide.shared.document.TextChange;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorCapability;
import org.exoplatform.ide.editor.api.SelectionRange;
import org.exoplatform.ide.editor.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.event.EditorContentChangedHandler;
import org.exoplatform.ide.editor.api.event.EditorContextMenuHandler;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityHandler;
import org.exoplatform.ide.editor.api.event.EditorFocusReceivedHandler;
import org.exoplatform.ide.editor.api.event.EditorHotKeyPressedHandler;
import org.exoplatform.ide.editor.api.event.EditorInitializedEvent;
import org.exoplatform.ide.editor.api.event.EditorInitializedHandler;
import org.exoplatform.ide.editor.marking.EditorLineNumberContextMenuHandler;
import org.exoplatform.ide.editor.marking.EditorLineNumberDoubleClickHandler;
import org.exoplatform.ide.editor.marking.Markable;
import org.exoplatform.ide.editor.marking.Marker;
import org.exoplatform.ide.editor.marking.ProblemClickHandler;
import org.exoplatform.ide.editor.text.IDocument;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CollabEditor extends Widget implements Editor, Markable
{

   protected final EditorBundle editorBundle;

   protected final com.google.collide.client.editor.Editor editor;

   private String mimeType;

   private String id;

   protected IDocument document;

   protected NotificationManager notificationManager;

   protected DocumentAdaptor documentAdaptor;
   
   private HoverPresenter hoverPresenter;

   private boolean initialized;

   private final class TextListenerImpl implements TextListener
   {

      /**
       * @see com.google.collide.shared.document.Document.TextListener#onTextChange(com.google.collide.shared.document.Document, com.google.collide.json.shared.JsonArray)
       */
      @Override
      public void onTextChange(Document document, JsonArray<TextChange> textChanges)
      {
         fireEvent(new EditorContentChangedEvent(getId()));
         udateDocument();
      }

   }

   public CollabEditor(String mimeType)
   {
      this.mimeType = mimeType;

      id = "CollabEditor - " + hashCode();
      editorBundle =
         EditorBundle.create(CollabEditorExtension.get().getContext(), CollabEditorExtension.get().getManager(),
            EditorErrorListener.NOOP_ERROR_RECEIVER);
      editor = editorBundle.getEditor();
      //editor.getTextListenerRegistrar().add(new TextListenerImpl());
      EditableContentArea.View v =
         new EditableContentArea.View(CollabEditorExtension.get().getContext().getResources());
      EditableContentArea contentArea =
         EditableContentArea.create(v, CollabEditorExtension.get().getContext(), editorBundle);
      contentArea.setContent(editorBundle);
      notificationManager = editor.getLeftGutterNotificationManager();
      notificationManager.setErrorListener(editorBundle.getErrorListener());
      setElement((Element)v.getElement());
      documentAdaptor = new DocumentAdaptor();
//      editor.getMouseHoverManager().addMouseHoverListener(new MouseHoverListener()
//      {
//
//         @Override
//         public void onMouseHover(int x, int y, LineInfo lineInfo, int column)
//         {
//            if(notificationManager.getMarkers().hasKey(lineInfo.number()))
//            {
//            
//               int startColumn = TextUtils.skipNonwhitespaceSimilar(lineInfo.line().getText(), column, false) +1;
//               int endColumn = TextUtils.skipNonwhitespaceSimilar(lineInfo.line().getText(), column, true);
//               int lineOffset = 0;
//               try
//               {
//                  lineOffset = document.getLineOffset(lineInfo.number());
//               }
//               catch (BadLocationException e)
//               {
//                  e.printStackTrace();
//               }
//               int pointOffset = lineOffset + column;
//               JsoArray<Marker> jsoArray = notificationManager.getMarkers().get(lineInfo.number());
//               
//               Marker mark = null;
//               for(Marker m : jsoArray.asIterable())
//               {
//                  if(m.getStart() <= pointOffset && pointOffset<= m.getEnd())
//                  {
//                     mark = m;
//                     break;
//                  }
//               }
//               if(mark != null)
//               {
//               final String wordAtColumn = mark.getMessage();
//               editorBundle.getEditorPopupController().showPopup(lineInfo,
//                  startColumn,
//                  endColumn, null,
//                  new PopupRenderer()
//                  {
//
//                     @Override
//                     public elemental.html.Element renderDom()
//                     {
//                        DivElement el = Elements.createDivElement("");
//                        el.setTextContent(wordAtColumn);
//                        return el;
//                     }
//                  }, null, VerticalAlign.BOTTOM, true, 400);
//               }
//            }
//            else
//               editorBundle.getEditorPopupController().hide();
//         }
//      });

   }

   /**
    * 
    */
   private void udateDocument()
   {
      //TODO change document, not all content
      document.removeDocumentListener(documentAdaptor);
      document.set(getText());
      document.addDocumentListener(documentAdaptor);
   }

   /**
    * @see com.google.gwt.user.client.ui.Widget#onLoad()
    */
   @Override
   protected void onLoad()
   {
      fireEvent(new EditorInitializedEvent(id));
      super.onLoad();
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getMimeType()
    */
   @Override
   public String getMimeType()
   {
      return mimeType;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getId()
    */
   @Override
   public String getId()
   {
      return id;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getText()
    */
   @Override
   public String getText()
   {
      return editor.getDocument().asText();
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setText(java.lang.String)
    */
   @Override
   public void setText(final String text)
   {
      document = new org.exoplatform.ide.editor.text.Document(text);
      document.addDocumentListener(documentAdaptor);
      hoverPresenter = new HoverPresenter(this,editor, document);
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {

         @Override
         public void execute()
         {
            initialized = true;
            Document editorDocument = Document.createFromString(text);
            editorDocument.putTag("IDocument", document);
            editorDocument.getTextListenerRegistrar().add(new TextListenerImpl());
            editorBundle.setDocument(editorDocument, new PathUtil("test.java"), "");
            documentAdaptor.setDocument(editorDocument, editor.getEditorDocumentMutator());
         }
      });
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getDocument()
    */
   @Override
   public IDocument getDocument()
   {
      return document;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#isCapable(org.exoplatform.ide.editor.api.EditorCapability)
    */
   @Override
   public boolean isCapable(EditorCapability capability)
   {
      switch (capability)
      {
         case AUTOCOMPLETION:
         case OUTLINE:
         case VALIDATION:
         case FIND_AND_REPLACE:
         case DELETE_LINES:
         case FORMAT_SOURCE:
         case SET_CURSOR_POSITION:
            return true;

         default :
            return false;
      }
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#formatSource()
    */
   @Override
   public void formatSource()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#showLineNumbers(boolean)
    */
   @Override
   public void showLineNumbers(boolean showLineNumbers)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setFocus()
    */
   @Override
   public void setFocus()
   {

      editor.getFocusManager().focus();

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setCursorPosition(int, int)
    */
   @Override
   public void setCursorPosition(final int row, final int column)
   {
      if (initialized)
      {
         LineInfo lineInfo = editor.getDocument().getLineFinder().findLine(row - 1);
         editor.getSelection().setCursorPosition(lineInfo, column - 1);
      }
      else
         Scheduler.get().scheduleDeferred(new ScheduledCommand()
         {

            @Override
            public void execute()
            {
               setCursorPosition(row, column);
            }
         });
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#deleteCurrentLine()
    */
   @Override
   public void deleteCurrentLine()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#findAndSelect(java.lang.String, boolean)
    */
   @Override
   public boolean findAndSelect(String find, boolean caseSensitive)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#replaceFoundedText(java.lang.String, java.lang.String, boolean)
    */
   @Override
   public void replaceFoundedText(String find, String replace, boolean caseSensitive)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#hasUndoChanges()
    */
   @Override
   public boolean hasUndoChanges()
   {
      return editor.isMutatingDocumentFromUndoOrRedo();
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#undo()
    */
   @Override
   public void undo()
   {
      editor.undo();
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#hasRedoChanges()
    */
   @Override
   public boolean hasRedoChanges()
   {
      return editor.isMutatingDocumentFromUndoOrRedo();
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#redo()
    */
   @Override
   public void redo()
   {
      editor.redo();
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#isReadOnly()
    */
   @Override
   public boolean isReadOnly()
   {
      return editorBundle.isReadOnly();
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setReadOnly(boolean)
    */
   @Override
   public void setReadOnly(boolean readOnly)
   {
      editor.setReadOnly(readOnly);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorRow()
    */
   @Override
   public int getCursorRow()
   {
      return editor.getSelection().getCursorLineNumber() + 1;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorColumn()
    */
   @Override
   public int getCursorColumn()
   {
      return editor.getSelection().getCursorColumn() + 1;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#replaceTextAtCurrentLine(java.lang.String, int)
    */
   @Override
   public void replaceTextAtCurrentLine(String line, int cursorPosition)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getLineText(int)
    */
   @Override
   public String getLineText(int line)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setLineText(int, java.lang.String)
    */
   @Override
   public void setLineText(int line, String text)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getNumberOfLines()
    */
   @Override
   public int getNumberOfLines()
   {
      return editor.getDocument().getLastLineNumber();
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getSelectionRange()
    */
   @Override
   public SelectionRange getSelectionRange()
   {
      SelectionModel selection = editor.getSelection();
      return new SelectionRange(selection.getBaseLineNumber() + 1, selection.getBaseColumn(),
         selection.getCursorLineNumber() + 1, selection.getCursorColumn());
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#selectRange(int, int, int, int)
    */
   @Override
   public void selectRange(int startLine, int startChar, int endLine, int endChar)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#selectAll()
    */
   @Override
   public void selectAll()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#cut()
    */
   @Override
   public void cut()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#copy()
    */
   @Override
   public void copy()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#paste()
    */
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
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getName()
    */
   @Override
   public String getName()
   {
      return "Source";
   }

   /**
    * @see org.exoplatform.ide.editor.marking.Markable#markProblem(org.exoplatform.ide.editor.marking.Marker)
    */
   @Override
   public void markProblem(Marker problem)
   {
      notificationManager.addProblem(problem);
   }

   /**
    * @see org.exoplatform.ide.editor.marking.Markable#unmarkProblem(org.exoplatform.ide.editor.marking.Marker)
    */
   @Override
   public void unmarkProblem(Marker problem)
   {
      notificationManager.unmarkProblem(problem);
   }

   /**
    * @see org.exoplatform.ide.editor.marking.Markable#unmarkAllProblems()
    */
   @Override
   public void unmarkAllProblems()
   {
      notificationManager.clear();
   }

   /**
    * @see org.exoplatform.ide.editor.marking.Markable#addProblemClickHandler(org.exoplatform.ide.editor.marking.ProblemClickHandler)
    */
   @Override
   public HandlerRegistration addProblemClickHandler(ProblemClickHandler handler)
   {
      return editor.getLeftGutterNotificationManager().addProblemClickHandler(handler);
   }

   /**
    * @see org.exoplatform.ide.editor.marking.Markable#addLineNumberDoubleClickHandler(org.exoplatform.ide.editor.marking.EditorLineNumberDoubleClickHandler)
    */
   @Override
   public HandlerRegistration addLineNumberDoubleClickHandler(EditorLineNumberDoubleClickHandler handler)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.marking.Markable#addLineNumberContextMenuHandler(org.exoplatform.ide.editor.marking.EditorLineNumberContextMenuHandler)
    */
   @Override
   public HandlerRegistration addLineNumberContextMenuHandler(EditorLineNumberContextMenuHandler handler)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorOffsetLeft()
    */
   @Override
   public int getCursorOffsetLeft()
   {
      int scrollLeft = editor.getBuffer().getScrollLeft();
      Position position = editor.getSelection().getCursorPosition();
      int offsetLeft =
         getElement().getAbsoluteLeft() + editor.getLeftGutter().getWidth()
            + editor.getLeftGutterNotificationManager().getLeftGutter().getWidth()
            + editor.getBuffer().convertColumnToX(position.getLine(), position.getColumn());

      return offsetLeft - scrollLeft + 2;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorOffsetTop()
    */
   @Override
   public int getCursorOffsetTop()
   {
      int scrollTop = editor.getBuffer().getScrollTop();
      Position position = editor.getSelection().getCursorPosition();
      int offsetTop = getElement().getAbsoluteTop() + editor.getBuffer().convertLineNumberToY(position.getLineNumber());
      return offsetTop - scrollTop + 1;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addContentChangedHandler(org.exoplatform.ide.editor.api.event.EditorContentChangedHandler)
    */
   @Override
   public HandlerRegistration addContentChangedHandler(EditorContentChangedHandler handler)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addContextMenuHandler(org.exoplatform.ide.editor.api.event.EditorContextMenuHandler)
    */
   @Override
   public HandlerRegistration addContextMenuHandler(EditorContextMenuHandler handler)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addCursorActivityHandler(org.exoplatform.ide.editor.api.event.EditorCursorActivityHandler)
    */
   @Override
   public HandlerRegistration addCursorActivityHandler(EditorCursorActivityHandler handler)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addFocusReceivedHandler(org.exoplatform.ide.editor.api.event.EditorFocusReceivedHandler)
    */
   @Override
   public HandlerRegistration addFocusReceivedHandler(EditorFocusReceivedHandler handler)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addHotKeyPressedHandler(org.exoplatform.ide.editor.api.event.EditorHotKeyPressedHandler)
    */
   @Override
   public HandlerRegistration addHotKeyPressedHandler(EditorHotKeyPressedHandler handler)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addInitializedHandler(org.exoplatform.ide.editor.api.event.EditorInitializedHandler)
    */
   @Override
   public HandlerRegistration addInitializedHandler(EditorInitializedHandler handler)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.marking.Markable#addProblems(org.exoplatform.ide.editor.marking.Marker[])
    */
   @Override
   public void addProblems(Marker[] problems)
   {
      notificationManager.addProblems(problems);
   }

   /**
    * @return the hoverPresenter
    */
   public HoverPresenter getHoverPresenter()
   {
      return hoverPresenter;
   }
   
   /**
    * @return the editorBundle
    */
   public EditorBundle getEditorBundle()
   {
      return editorBundle;
   }
}
