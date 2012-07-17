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
package org.exoplatform.ide.client.debug;

import java.util.List;
import java.util.Map;

import org.exoplatform.ide.client.editor.EditorFactory;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.api.Editor;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class TraceAvailableEditorsPresenter implements ShowAvailableEditorsHandler
{

   public TraceAvailableEditorsPresenter() 
   {
      IDE.getInstance().addControl(new ShowAvailableEditorsControl());
      IDE.addHandler(ShowAvailableEditorsEvent.TYPE, this);
   }

   private String stretch(String s, int length) {
      String stretched = s;
      while (stretched.length() < length) {
         stretched += " ";
      }
      
      return stretched;
   }
   
   @Override
   public void onShowAvailableEditors(ShowAvailableEditorsEvent event)
   {
      System.out.println();
      
      Map<String, List<Editor>> editorMap = EditorFactory.getEditorMap();
      
      for (String key : editorMap.keySet()) {
         List<Editor> editors = editorMap.get(key);
         
         for (Editor editor : editors) {
            String info = "mime-type > " + editor.getMimeType();
            info = stretch(info, 50);
            info += "extension > " + editor.getFileExtension();
            info = stretch(info, 70);
            info += "description > " + editor.getDescription();
            System.out.println(info);
         }
      }
      
      
   }
   
}
