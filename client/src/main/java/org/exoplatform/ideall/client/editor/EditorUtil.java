/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.editor;

import java.util.List;

import org.exoplatform.gwtframework.editor.api.Editor;
import org.exoplatform.gwtframework.editor.api.EditorFactory;
import org.exoplatform.gwtframework.editor.api.EditorNotFoundException;
import org.exoplatform.ideall.client.model.ApplicationContext;



/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class EditorUtil
{
   
   public static Editor getEditor(String mimeType, String defaultEditorDescription) throws EditorNotFoundException
   {
      Editor editor = null;

      if (defaultEditorDescription == null)
      {
         editor = EditorFactory.getDefaultEditor(mimeType);
      }
      else
      {
         List<Editor> editors = EditorFactory.getEditors(mimeType);
         for (Editor e : editors)
         {
            if (e.getDescription().equals(defaultEditorDescription))
            {
               editor = e;
               break;
            }
         }
      }

      if (editor == null)
      {
         throw new EditorNotFoundException();
      }

      return editor;
   }
   public static Editor getEditor(String mimeType, ApplicationContext context) throws EditorNotFoundException
   {
      
      String defaultEditorDescription;
      if (context.getSelectedEditorDescription() != null)
      {
         defaultEditorDescription = context.getSelectedEditorDescription();
         context.setSelectedEditorDescriptor(null);
      }
      else
      {
         defaultEditorDescription = context.getDefaultEditors().get(mimeType);
      }

      return getEditor(mimeType, defaultEditorDescription);
      
   }
   
}

