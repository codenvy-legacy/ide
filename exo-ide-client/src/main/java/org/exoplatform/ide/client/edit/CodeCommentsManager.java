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
package org.exoplatform.ide.client.edit;


import org.exoplatform.gwtframework.commons.util.Log;
import org.exoplatform.ide.client.framework.editor.AddCommentsModifierEvent;
import org.exoplatform.ide.client.framework.editor.AddCommentsModifierHandler;
import org.exoplatform.ide.client.framework.editor.CommentsModifier;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorAddBlockCommentEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorAddBlockCommentHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorRemoveBlockCommentEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorRemoveBlockCommentHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.edits.MalformedTreeException;
import org.exoplatform.ide.editor.text.edits.TextEdit;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 6, 2012 4:59:20 PM anya $
 * 
 */
public class CodeCommentsManager implements AddCommentsModifierHandler, EditorAddBlockCommentHandler,
   EditorRemoveBlockCommentHandler, EditorActiveFileChangedHandler
{

   private Map<String, CommentsModifier> commentModifiers = new HashMap<String, CommentsModifier>();

   private Editor editor;

   private FileModel activeFile;

   public CodeCommentsManager()
   {
      IDE.addHandler(AddCommentsModifierEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);

      IDE.addHandler(EditorAddBlockCommentEvent.TYPE, this);
      IDE.addHandler(EditorRemoveBlockCommentEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorRemoveBlockCommentHandler#onEditorRemoveBlockComment(org.exoplatform.ide.client.framework.editor.event.EditorRemoveBlockCommentEvent)
    */
   @Override
   public void onEditorRemoveBlockComment(EditorRemoveBlockCommentEvent event)
   {
      if (commentModifiers.containsKey(activeFile.getMimeType()))
      {
         CommentsModifier commentsModifier = commentModifiers.get(activeFile.getMimeType());
         TextEdit textEdit = commentsModifier.removeBlockComment(editor.getSelectionRange(), editor.getDocument());
         try
         {
            textEdit.apply(editor.getDocument());
         }
         catch (MalformedTreeException e)
         {
            Log.info(e.getMessage());
         }
         catch (BadLocationException e)
         {
            Log.info(e.getMessage());
         }
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorAddBlockCommentHandler#onEditorAddBlockComment(org.exoplatform.ide.client.framework.editor.event.EditorAddBlockCommentEvent)
    */
   @Override
   public void onEditorAddBlockComment(EditorAddBlockCommentEvent event)
   {
      if (commentModifiers.containsKey(activeFile.getMimeType()))
      {
         CommentsModifier commentsModifier = commentModifiers.get(activeFile.getMimeType());
         TextEdit textEdit = commentsModifier.addBlockComment(editor.getSelectionRange(), editor.getDocument());
         try
         {
            textEdit.apply(editor.getDocument());
         }
         catch (MalformedTreeException e)
         {
            Log.info(e.getMessage());
         }
         catch (BadLocationException e)
         {
            Log.info(e.getMessage());
         }
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.AddCommentsModifierHandler#onAddCommentsModifier(org.exoplatform.ide.client.framework.editor.AddCommentsModifierEvent)
    */
   @Override
   public void onAddCommentsModifier(AddCommentsModifierEvent event)
   {
      commentModifiers.put(event.getMimeType(), event.getCommentsModifier());
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      this.activeFile = event.getFile();
      this.editor = event.getEditor();
   }
}
