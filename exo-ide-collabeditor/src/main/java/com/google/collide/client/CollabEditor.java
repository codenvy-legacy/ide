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

import com.google.collide.client.editor.gutter.LeftGutterNotificationManager;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import com.google.gwt.core.client.Scheduler;

import com.google.gwt.event.shared.HandlerRegistration;

import com.google.collide.client.editor.Editor.TextListener;

import com.google.collide.shared.document.TextChange;

import com.google.collide.client.code.EditableContentArea;
import com.google.collide.client.code.EditorBundle;
import com.google.collide.client.code.errorrenderer.EditorErrorListener;
import com.google.collide.client.util.PathUtil;
import com.google.collide.shared.document.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorCapability;
import org.exoplatform.ide.editor.api.SelectionRange;
import org.exoplatform.ide.editor.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.event.EditorInitializedEvent;
import org.exoplatform.ide.editor.marking.EditorLineNumberContextMenuHandler;
import org.exoplatform.ide.editor.marking.EditorLineNumberDoubleClickHandler;
import org.exoplatform.ide.editor.marking.Markable;
import org.exoplatform.ide.editor.marking.Marker;
import org.exoplatform.ide.editor.marking.ProblemClickHandler;
import org.exoplatform.ide.editor.text.DocumentEvent;
import org.exoplatform.ide.editor.text.IDocument;
import org.exoplatform.ide.editor.text.IDocumentListener;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CollabEditor extends Widget implements Editor, IDocumentListener, Markable
{

   /**
    * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
    * @version $Id:
    *
    */
   private final class TextListenerImpl implements TextListener
   {
      @Override
      public void onTextChange(TextChange textChange)
      {
         fireEvent(new EditorContentChangedEvent(getId()));
      }
   }

   private final EditorBundle editorBundle;

   private String mimeType;

   private String id;

   private IDocument document;
   
   private LeftGutterNotificationManager notificationManager;

   public CollabEditor(String mimeType)
   {
      this.mimeType = mimeType;

      id = "CollabEditor - " + hashCode();
      editorBundle =
         EditorBundle.create(CollabEditorExtension.get().getContext(), CollabEditorExtension.get().getManager(),
            EditorErrorListener.NOOP_ERROR_RECEIVER);
      editorBundle.getEditor().getTextListenerRegistrar().add(new TextListenerImpl());
      EditableContentArea.View v =
         new EditableContentArea.View(CollabEditorExtension.get().getContext().getResources());
      EditableContentArea contentArea =
         EditableContentArea.create(v, CollabEditorExtension.get().getContext(), editorBundle);
      contentArea.setContent(editorBundle);
      notificationManager = editorBundle.getEditor().getLeftGutterNotificationManager();
      setElement((Element)v.getElement());
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
      return editorBundle.getEditor().getDocument().asText();
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setText(java.lang.String)
    */
   @Override
   public void setText(final String text)
   {
      document = new org.exoplatform.ide.editor.text.Document(text);
      document.addDocumentListener(this);
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         
         @Override
         public void execute()
         {
            editorBundle.setDocument(Document.createFromString(text), new PathUtil("test.java"), "");
         }
      });
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getDocument()
    */
   @Override
   public IDocument getDocument()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#isCapable(org.exoplatform.ide.editor.api.EditorCapability)
    */
   @Override
   public boolean isCapable(EditorCapability capability)
   {
      // TODO Auto-generated method stub
      return false;
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
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setCursorPosition(int, int)
    */
   @Override
   public void setCursorPosition(int row, int column)
   {
      // TODO Auto-generated method stub

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
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#undo()
    */
   @Override
   public void undo()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#hasRedoChanges()
    */
   @Override
   public boolean hasRedoChanges()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#redo()
    */
   @Override
   public void redo()
   {
      // TODO Auto-generated method stub

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
      editorBundle.getEditor().setReadOnly(readOnly);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorRow()
    */
   @Override
   public int getCursorRow()
   {
      return 0;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorColumn()
    */
   @Override
   public int getCursorColumn()
   {
      // TODO Auto-generated method stub
      return 0;
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
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getSelectionRange()
    */
   @Override
   public SelectionRange getSelectionRange()
   {
      // TODO Auto-generated method stub
      return null;
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
    * @see org.exoplatform.ide.editor.text.IDocumentListener#documentChanged(org.exoplatform.ide.editor.text.DocumentEvent)
    */
   @Override
   public void documentChanged(DocumentEvent event)
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
      // TODO Auto-generated method stub
      return null;
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

}
