/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ide.client.editor;

import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.editor.ckeditor.CKEditor;
import org.exoplatform.ide.editor.codemirror.CodeMirror;

/**
 * @author <a href="mailto:dnochevnov@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public enum EditorType {

   SOURCE(org.exoplatform.ide.client.IDE.EDITOR_CONSTANT.editorControllerFileTabSourceView(),
      Images.Editor.SOURCE_BUTTON_ICON, 0), DESIGN(org.exoplatform.ide.client.IDE.EDITOR_CONSTANT
      .editorControllerFileTabDesignView(), Images.Editor.DESIGN_BUTTON_ICON, 1), DEFAULT(
      org.exoplatform.ide.client.IDE.EDITOR_CONSTANT.editorControllerFileTabSourceView(),
      Images.Editor.SOURCE_BUTTON_ICON, 2);

   private String label;

   private String iconUrl;

   /**
    * Editor position within editor area started from 0.
    */
   private int position;

   EditorType(String label, String icon, int position)
   {
      this.label = label;
      this.iconUrl = icon;
      this.position = position;
   }

   public String getLabel()
   {
      return label;
   }

   public String getIcon()
   {
      return iconUrl;
   }

   /**
    * Get editor position within editor area started from 0.
    * 
    * @return
    */
   public int getPosition()
   {
      return position;
   }

   public static EditorType getType(String editorClassName)
   {
      if (CodeMirror.class.getName().equals(editorClassName))
      {
         return SOURCE;
      }

      else if (CKEditor.class.getName().equals(editorClassName))
      {
         return DESIGN;
      }

      return DEFAULT;
   }
}
