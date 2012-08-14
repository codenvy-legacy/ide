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
package org.exoplatform.ide.editor.codemirror2;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.module.EditorCreator;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.FileType;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.api.Editor;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class CodeMirror2EditorExtension extends Extension
{

   @Override
   public void initialize()
   {
      new Timer()
      {
         @Override
         public void run()
         {
            
            ImageResource xmlIcon = IDE.getInstance().getFileTypeRegistry().getFileType(MimeType.TEXT_XML).getIcon();
            
            IDE.getInstance().getFileTypeRegistry().addFileType(
               new FileType(MimeType.TEXT_XML, "xml", xmlIcon), 
               new EditorCreator()
               {
                  @Override
                  public Editor createEditor()
                  {
                     return new CodeMirror2(MimeType.TEXT_XML);
                  }
               });
            
         }
      }.schedule(1000);
   }
   
}
