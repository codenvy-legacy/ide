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

import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.editor.api.Editor;

import com.google.gwt.user.client.ui.Image;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditorView Mar 21, 2011 4:33:38 PM evgen $
 *
 */
public class EditorView extends ViewImpl
{

   private static int i = 0;
   
   private Editor editor;

   private File file;
   
   /**
    * @param id
    * @param type
    * @param title
    */
   public EditorView(Editor editor, File file, String title)
   {
      super("editor-" + i++, "editor", title, new Image(file.getIcon()));
      this.editor = editor;
      this.file = file;
      add(editor);
   }

   /**
    * @return the editor
    */
   public Editor getEditor()
   {
      return editor;
   }
   
   public void setContent(File file)
   {
      this.file = file;
      try
      {
      editor.setText(file.getContent());
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   public String getFileHref()
   {
      return file.getHref();
   }

   /**
    * @return
    */
   public File getFile()
   {
      return file;
   }

}
