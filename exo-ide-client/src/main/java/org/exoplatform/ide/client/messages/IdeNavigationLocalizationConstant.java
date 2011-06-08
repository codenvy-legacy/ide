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
 *      'IdeNavigationLocalizationConstant.properties'.
 * <p/>
 * Localization message for form from navigation group.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdePreferencesLocalizationConstant.java Jun 3, 2011 12:58:29 PM vereshchaka $
 *
 */
public interface IdeNavigationLocalizationConstant extends Constants
{
   /*
    * WorkspaceViewExtended
    */
   @Key("workspace.title")
   String workspaceTitle();
   
   /*
    * SearchResultsViewExtended
    */
   @Key("searchResult.title")
   String searchResultTitle();
   
   /*
    * SearchFilesView
    */
   @Key("searchFiles.title")
   String searchFilesTitle();
   
   @Key("searchFiles.path")
   String searchFilesPath();
   
   @Key("searchFiles.containingText")
   String searchFilesContainingText();
   
   @Key("searchFiles.mimeType")
   String searchFilesMimeType();
   
   /*
    * RenameItemForm
    */
   @Key("renameItem.title")
   String renameItemTitle();
   
   @Key("renameItem.renameItemTo")
   String renameItemTo();
   
   @Key("renameItem.selectMimeType")
   String renameItemSelectMimeType();
   
   @Key("renameItem.renameButton")
   String renameItemRenameBtn();
   
   /*
    * GetItemURLView
    */
   @Key("getItemUrl.webdavItemsUrl")
   String getItemUrlWebdavItemsUrl();
   
   @Key("getItemUrl.title")
   String getItemUrlTitle();
   
   /*
    * DeleteItemView
    */
   @Key("deleteItem.title")
   String deleteItemTitle();
   
   /*
    * DeleteItemsPresenter
    */
   @Key("deleteFile.dialog.title")
   String deleteFileDialogTitle();
   
   /*
    * AbstractCreateFolderForm
    */
   @Key("createFolder.nameOfNewFolder")
   String createFolderNameOfNewFolder();
   
   /*
    * CreateFileCommandHandler
    */
   @Key("createFile.untitledFile.name")
   String createFileUntitledFileName();
   
   /*
    * SaveFileAsCommandHandler
    */
   @Key("saveFileAs.newFileName.prefix")
   String saveFileAsNewFileNamePrefix();
   
   @Key("saveFileAs.dialog.title")
   String saveFileAsDialogTitle();
   
   @Key("saveFileAs.dialog.enterNewName")
   String saveFileAsDialogEnterNewName();
   
   @Key("saveFileAs.dialog.doYouWantToSave")
   String saveFileAsDialogDoYouWantToSave();
   
   /*
    * 
    */
   @Key("paste.saveFileBeforeCutAskDialog.title")
   String pasteSaveFileBeforeCutAskDialogTitle();
   
   
   
   

   
}
