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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;
import org.exoplatform.ide.client.framework.util.ImageUtil;
import org.exoplatform.ide.editor.api.EditorProducer;
import org.exoplatform.ide.editor.codeassistant.CodeAssistantClientBundle;
import org.exoplatform.ide.editor.codemirror.CodeMirrorClientBundle;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorProducer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditorFactory Feb 22, 2011 11:06:05 AM evgen $
 *
 */
public class EditorFactory
{
   private static Map<String, List<EditorProducer>> editors = new HashMap<String, List<EditorProducer>>();

   static
   {

      addEditor(new CodeMirrorProducer(MimeType.TEXT_PLAIN, IDE.EDITOR_CONSTANT.codeMirrorTextEditor(), "txt",
         IDEImageBundle.INSTANCE.textFile(), true, new CodeMirrorConfiguration()));
      addEditor(new CodeMirrorProducer("text/yaml", IDE.EDITOR_CONSTANT.codeMirrorTextEditor(), "txt",
         IDEImageBundle.INSTANCE.textFile(), true, new CodeMirrorConfiguration()));

      addEditor(new CodeMirrorProducer("application/x-ruby+html", IDE.EDITOR_CONSTANT.codeMirrorTextEditor(), "txt",
         IDEImageBundle.INSTANCE.textFile(), true, new CodeMirrorConfiguration()));

      //To initialize client bundle 
      CodeAssistantClientBundle.INSTANCE.css().ensureInjected();
      CodeMirrorClientBundle.INSTANCE.css().ensureInjected();
   }

   public static void addEditor(EditorProducer producer)
   {
      if (editors.containsKey(producer.getMimeType()))
         editors.get(producer.getMimeType()).add(producer);
      else
      {
         ArrayList<EditorProducer> producers = new ArrayList<EditorProducer>();
         producers.add(producer);
         editors.put(producer.getMimeType(), producers);
      }
      IDEMimeTypes.addExtension(producer.getDefaultFileExtension(), producer.getMimeType());
      ImageUtil.putIcon(producer.getMimeType(), producer.getIcon());
   }

   public static EditorProducer getEditorProducer(String mimeType, String description) throws EditorNotFoundException
   {
      if (description == null)
      {
         return getDefaultEditor(mimeType);
      }
      if (editors.containsKey(mimeType))
      {
         for (EditorProducer p : editors.get(mimeType))
         {
            if (p.getDescription().equals(description))
               return p;
         }
      }
      throw new EditorNotFoundException();
   }

   public static EditorProducer getDefaultEditor(String mimeType) throws EditorNotFoundException
   {
      if (editors.containsKey(mimeType))
      {
         for (EditorProducer p : editors.get(mimeType))
         {
            if (p.isDefault())
               return p;
         }
      }
      throw new EditorNotFoundException();
   }

   /**
    * @param mimeType
    * @return
    */
   public static List<EditorProducer> getEditors(String mimeType) throws EditorNotFoundException
   {
      List<EditorProducer> editorProducers = editors.get(mimeType);
      if (editorProducers == null)
         throw new EditorNotFoundException();
      return editorProducers;
   }
}
