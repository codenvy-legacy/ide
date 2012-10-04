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
package org.exoplatform.ide.texteditor;

import org.exoplatform.ide.text.BadLocationException;
import org.exoplatform.ide.text.Document;
import org.exoplatform.ide.text.edits.DeleteEdit;
import org.exoplatform.ide.text.edits.InsertEdit;
import org.exoplatform.ide.text.store.TextChange;
import org.exoplatform.ide.text.store.TextStoreMutator;
import org.exoplatform.ide.text.store.Line;
import org.exoplatform.ide.texteditor.api.BeforeTextListener;
import org.exoplatform.ide.texteditor.api.TextListener;
import org.exoplatform.ide.util.ListenerManager;
import org.exoplatform.ide.util.ListenerManager.Dispatcher;
import org.exoplatform.ide.util.ListenerRegistrar;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class EditorTextStoreMutator implements TextStoreMutator
{

   private Editor editor;

   private final ListenerManager<BeforeTextListener> beforeTextListenerManager = ListenerManager.create();

   private final ListenerManager<TextListener> textListenerManager = ListenerManager.create();

   /**
    * @param editor
    */
   public EditorTextStoreMutator(Editor editor)
   {
      this.editor = editor;
   }

   /**
    * @see org.exoplatform.ide.text.store.TextStoreMutator#deleteText(org.exoplatform.ide.text.store.Line, int, int)
    */
   @Override
   public TextChange deleteText(Line line, int column, int deleteCount)
   {
      return deleteText(line, line.getDocument().getLineFinder().findLine(line).number(), column, deleteCount);
   }

   /**
    * @see org.exoplatform.ide.text.store.TextStoreMutator#deleteText(org.exoplatform.ide.text.store.Line, int, int, int)
    */
   @Override
   public TextChange deleteText(Line line, int lineNumber, int column, int deleteCount)
   {
      if (editor.isReadOnly())
      {
         return null;
      }
      Document document = editor.getDocument();
      try
      {
         int lineOffset = document.getLineOffset(lineNumber);
         DeleteEdit delete = new DeleteEdit(lineOffset + column, deleteCount);
         delete.apply(document);

      }
      catch (BadLocationException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return null;
   }

   /**
    * @see org.exoplatform.ide.text.store.TextStoreMutator#insertText(org.exoplatform.ide.text.store.Line, int, java.lang.String)
    */
   @Override
   public TextChange insertText(Line line, int column, String text)
   {
      return insertText(line, line.getDocument().getLineFinder().findLine(line).number(), column, text);
   }

   /**
    * @see org.exoplatform.ide.text.store.TextStoreMutator#insertText(org.exoplatform.ide.text.store.Line, int, int, java.lang.String)
    */
   @Override
   public TextChange insertText(Line line, int lineNumber, int column, String text)
   {
      return insertText(line, lineNumber, column, text, true);
   }

   /**
    * @see org.exoplatform.ide.text.store.TextStoreMutator#insertText(org.exoplatform.ide.text.store.Line, int, int, java.lang.String, boolean)
    */
   @Override
   public TextChange insertText(Line line, int lineNumber, int column, String text, boolean canReplaceSelection)
   {
      if (editor.isReadOnly())
      {
         return null;
      }
      Document document = editor.getDocument();
      try
      {
         int lineOffset = document.getLineOffset(lineNumber);
         InsertEdit insert = new InsertEdit(lineOffset + column, text);
         insert.apply(document);
         TextChange textChange = TextChange.createInsertion(line, lineNumber, column, line, lineNumber, text);
         dispatchTextChange(textChange);

      }
      catch (BadLocationException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return null;
   }

   void dispatchTextChange(final TextChange textChange)
   {
      textListenerManager.dispatch(new Dispatcher<TextListener>()
      {
         @Override
         public void dispatch(TextListener listener)
         {
            listener.onTextChange(textChange);
         }
      });
   }

   /**
    * @return
    */
   public ListenerRegistrar<BeforeTextListener> getBeforeTextListenerRegistrar()
   {
      return beforeTextListenerManager;
   }

   /**
    * @return
    */
   public ListenerRegistrar<TextListener> getTextListenerRegistrar()
   {
      return textListenerManager;
   }

   /**
    * @see org.exoplatform.ide.text.store.TextStoreMutator#getUndoManager()
    */
   @Override
   public UndoManager getUndoManager()
   {
      return editor.getUndoManager();
   }

}
