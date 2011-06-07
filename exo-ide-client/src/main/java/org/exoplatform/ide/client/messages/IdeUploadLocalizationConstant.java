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
package org.exoplatform.ide.client.messages;

import com.google.gwt.i18n.client.Constants;

/**
 * Interface to represent the constants contained in resource bundle:
 *      'IdeUploadLocalizationConstant.properties'.
 * <p/>
 * Localization message for form from upload group.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdePreferencesLocalizationConstant.java Jun 3, 2011 12:58:29 PM vereshchaka $
 *
 */
public interface IdeUploadLocalizationConstant extends Constants
{
   /*
    * UploadForm
    */
   @Key("upload.button.browse")
   String uploadBrowseBtn();
   
   @Key("upload.folder.title")
   String uploadFolderTitle();
   
   @Key("upload.button")
   String uploadButton();
   
   @Key("upload.folderToUpload")
   String folderToUpload();
   
   /*
    * UploadFileForm
    */
   @Key("uploadFile.title")
   String uploadFileTitle();
   
   @Key("uploadFile.fileToUpload")
   String fileToUpload();
   
   @Key("uploadFile.mimeType")
   String uploadFileMimeType();
   
   /*
    * OpenLocalFileForm
    */
   @Key("openLocalFile.title")
   String openLocalFileTitle();
   
   @Key("openButton")
   String openButton();
   
   @Key("fileToOpen")
   String fileToOpen();
   
   /*
    * OpenFileByPathForm
    */
   @Key("openFileByPath.title")
   String openFileByPathTitle();
   
   @Key("openFileByPath.fileUrl")
   String openFileByPathFileUrl();

}
