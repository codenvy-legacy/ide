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
package org.exoplatform.ide.client.application;

import java.util.HashMap;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.module.EditorCreator;
import org.exoplatform.ide.client.framework.module.EditorNotFoundException;
import org.exoplatform.ide.client.framework.module.FileType;
import org.exoplatform.ide.client.framework.module.FileTypeRegistry;
import org.exoplatform.ide.client.framework.util.ImageUtil;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.codemirror.CodeMirror;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class IDEFileTypeRegistry implements FileTypeRegistry
{
   
   /**
    * 
    */
   private HashMap<String, FileType> fileTypes = new HashMap<String, FileType>();
   
   /**
    * 
    */
   private HashMap<String, EditorCreator[]> editors = new HashMap<String, EditorCreator[]>();
   
   public IDEFileTypeRegistry()
   {
      addFileType(new FileType(MimeType.TEXT_PLAIN, "txt", IDEImageBundle.INSTANCE.textFile()), 
               new EditorCreator()
               {
                  @Override
                  public Editor createEditor()
                  {
                     return new CodeMirror(MimeType.TEXT_PLAIN);
                  }
               });
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.FileTypeRegistry#addFileType(org.exoplatform.ide.client.framework.module.FileType, org.exoplatform.ide.client.framework.module.EditorCreator[])
    */
   @Override
   public void addFileType(FileType fileType, EditorCreator... editors)
   {
      fileTypes.put(fileType.getMimeType(), fileType);
      this.editors.put(fileType.getMimeType(), editors);
      
      ImageUtil.putIcon(fileType.getMimeType(), fileType.getIcon());
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.FileTypeRegistry#getFileType(java.lang.String)
    */
   @Override
   public FileType getFileType(String mimeType)
   {
      return fileTypes.get(mimeType);
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.FileTypeRegistry#getEditors(java.lang.String)
    */
   @Override
   public Editor[] getEditors(String mimeType) throws EditorNotFoundException
   {
      EditorCreator[] creatorList = editors.containsKey(mimeType) ? editors.get(mimeType) :
         editors.get(MimeType.TEXT_PLAIN);
      
      Editor[] editors = new Editor[creatorList.length];
      for (int i = 0; i < creatorList.length; i++)
      {
         EditorCreator creator = creatorList[i];
         editors[i] = creator.createEditor();
      }
      
      return editors;
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.FileTypeRegistry#getEditor(java.lang.String)
    */
   @Override
   public Editor getEditor(String mimeType) throws EditorNotFoundException
   {
      if (!editors.containsKey(mimeType))
      {
         throw new EditorNotFoundException("Editor for " + mimeType + " not found");
      }
      
      EditorCreator[] creatorList = editors.get(mimeType);
      if (creatorList.length == 0)
      {
         throw new EditorNotFoundException("Editor for " + mimeType + " not found");
      }
      
      return creatorList[0].createEditor();
   }

   @Override
   public FileType[] getSupportedFileTypes()
   {
      return fileTypes.values().toArray(new FileType[fileTypes.size()]);
   }

}
