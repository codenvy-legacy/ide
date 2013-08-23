/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.client.messages;

import com.google.gwt.i18n.client.Messages;

/**
 * Interface to represent the constants contained in resource bundle: 'IdeNavigationLocalizationConstant.properties'.
 * <p/>
 * Localization message for form from navigation group.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdePreferencesLocalizationConstant.java Jun 3, 2011 12:58:29 PM vereshchaka $
 */
public interface IdeNavigationLocalizationConstant extends Messages {

    @Key("folder.has.opened.file")
    String cutFolderHasOpenFile(String folderName, String fileName);

    @Key("cut.opened.file")
    String cutOpenFile(String fileName);

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
    @Key("getItemUrl.webdavItemsPrivateUrl")
    String getItemUrlWebdavItemsPrivateUrl();

    @Key("getItemUrl.webdavItemsPublicUrl")
    String getItemUrlWebdavItemsPublicUrl();

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
