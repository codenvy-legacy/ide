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

import com.google.gwt.i18n.client.Constants;

/**
 * Interface to represent the constants contained in resource bundle: 'IdeUploadLocalizationConstant.properties'.
 * <p/>
 * Localization message for form from upload group.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdePreferencesLocalizationConstant.java Jun 3, 2011 12:58:29 PM vereshchaka $
 */
public interface IdeUploadLocalizationConstant extends Constants {
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

    /*
     * UploadFilePresenter
     */
    @Key("upload.file.exist.title")
    String uploadFileExistTitle();

    @Key("upload.file.exist.text")
    String uploadFileExistText();

    /*
     * DownloadForm
     */
    @Key("download.file.error")
    String downloadFileError();

    @Key("download.zip.folder.error")
    String downloadFolderError();

    @Key("upload.overwrite.title")
    String uploadOverwriteTitle();

    @Key("upload.overwrite.ask")
    String uploadOverwriteAsk();

    /*
     * UploadZipView
     */
    @Key("upload.overwriteAll.label")
    String uploadOverwriteAllLabel();

}
