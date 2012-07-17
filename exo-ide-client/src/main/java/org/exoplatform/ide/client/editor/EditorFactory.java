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
package org.exoplatform.ide.client.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.codeassistant.CodeAssistantClientBundle;
import org.exoplatform.ide.editor.codemirror.CodeMirror;
import org.exoplatform.ide.editor.codemirror.CodeMirrorClientBundle;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditorFactory Feb 22, 2011 11:06:05 AM evgen $
 * 
 */
public class EditorFactory
{

   private static Map<String, List<Editor>> editorsMap = new HashMap<String, List<Editor>>();

   /*
    * Add default editors
    */
   static
   {
      addEditor(new CodeMirror(MimeType.TEXT_PLAIN, IDE.EDITOR_CONSTANT.codeMirrorTextEditor(), "txt", 
         new CodeMirrorConfiguration()));
      
      //      addEditor(new CodeMirrorProducer(MimeType.TEXT_PLAIN, IDE.EDITOR_CONSTANT.codeMirrorTextEditor(), "txt",
      //         IDEImageBundle.INSTANCE.textFile(), true, new CodeMirrorConfiguration()));
      //
      //      addEditor(new CodeMirrorProducer("application/x-ruby+html", IDE.EDITOR_CONSTANT.codeMirrorTextEditor(), "txt",
      //         IDEImageBundle.INSTANCE.textFile(), true, new CodeMirrorConfiguration()));

      // To initialize client bundle
      CodeAssistantClientBundle.INSTANCE.css().ensureInjected();
      CodeMirrorClientBundle.INSTANCE.css().ensureInjected();
   }

   public static Map<String, List<Editor>> getEditorMap()
   {
      return editorsMap;
   }
   
   public static void addEditor(Editor editor)
   {
      List<Editor> editorList = editorsMap.get(editor.getMimeType());
      if (editorList == null)
      {
         editorList = new ArrayList<Editor>();
         editorsMap.put(editor.getMimeType(), editorList);
      }
      editorList.add(editor);

      IDEMimeTypes.addExtension(editor.getFileExtension(), editor.getMimeType());

      //ImageUtil.putIcon(producer.getMimeType(), producer.getIcon());
   }

   //   public static EditorProducer getEditorProducer(String mimeType, String description) throws EditorNotFoundException
   //   {
   //      if (description == null)
   //      {
   //         return getDefaultEditor(mimeType);
   //      }
   //      if (editors.containsKey(mimeType))
   //      {
   //         for (EditorProducer p : editors.get(mimeType))
   //         {
   //            if (p.getDescription().equals(description))
   //               return p;
   //         }
   //      }
   //      throw new EditorNotFoundException();
   //   }

   public static Editor getEditor(String mimeType) throws EditorNotFoundException
   {
      return getEditors(mimeType)[0];
   }

   public static Editor getEditor(String mimeType, String description) throws EditorNotFoundException
   {
      Editor[] editors = getEditors(mimeType);

      if (description == null || description.isEmpty())
      {
         return editors[0].newInstance();
      }

      for (Editor editor : editors)
      {
         if (description.equals(editor.getDescription()))
         {
            return editor.newInstance();
         }
      }

      throw new EditorNotFoundException(mimeType, description);
   }

   public static Editor[] getEditors(String mimeType) throws EditorNotFoundException
   {
      if (!editorsMap.containsKey(mimeType))
      {
         throw new EditorNotFoundException(mimeType);
      }

      List<Editor> editorList = editorsMap.get(mimeType);
      Editor[] editors = new Editor[editorList.size()];

      for (int i = 0; i < editorList.size(); i++)
      {
         Editor editor = editorList.get(i);
         editors[i] = editor.newInstance();
      }

      return editors;
   }

   //   public static EditorP getDefaultEditor(String mimeType) throws EditorNotFoundException
   //   {
   //      // add editor for files with unknown mime-type and preset configuration of plain text
   //      if (!editors.containsKey(mimeType))
   //      {
   //         addEditor(new CodeMirrorProducer(mimeType, IDE.EDITOR_CONSTANT.codeMirrorTextEditor(), "",
   //            IDEImageBundle.INSTANCE.defaultFile(), true, new CodeMirrorConfiguration()));
   //      }
   //
   //      if (editors.containsKey(mimeType))
   //      {
   //         for (EditorProducer p : editors.get(mimeType))
   //         {
   //            if (p.isDefault())
   //               return p;
   //         }
   //      }
   //      throw new EditorNotFoundException();
   //   }

   //   /**
   //    * @param mimeType
   //    * @return
   //    */
   //   public static List<EditorProducer> getEditors(String mimeType) throws EditorNotFoundException
   //   {
   //      List<EditorProducer> editorProducers = editors.get(mimeType);
   //      if (editorProducers == null)
   //         throw new EditorNotFoundException();
   //      return editorProducers;
   //   }

}
