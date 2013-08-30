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
package org.exoplatform.ide.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public interface IDEImageBundle extends ClientBundle {

    public static final IDEImageBundle INSTANCE = GWT.create(IDEImageBundle.class);

    @Source("org/exoplatform/ide/public/images/logo/codenvy-welcome.png")
    ImageResource ideLogo();

    @Source("org/exoplatform/ide/public/images/blank.png")
    ImageResource blankImage();

   /*
    * BUTTONS
    */

    @Source("bundled-images/ok.png")
    ImageResource ok();

    @Source("bundled-images/ok-disabled.png")
    ImageResource okDisabled();

    @Source("bundled-images/cancel.png")
    ImageResource cancel();

    @Source("bundled-images/cancel-disabled.png")
    ImageResource cancelDisabled();

    @Source("bundled-images/yes.png")
    ImageResource yes();

    @Source("bundled-images/yes-disabled.png")
    ImageResource yesDisabled();

    @Source("bundled-images/hide.png")
    ImageResource hide();

    @Source("bundled-images/hide-disabled.png")
    ImageResource hideDisabled();

    @Source("bundled-images/link-with-editor.png")
    ImageResource linkWithEditor();

    @Source("bundled-images/link-with-editor-disabled.png")
    ImageResource linkWithEditorDisabled();

    @Source("bundled-images/resource-open.png")
    ImageResource openResource();

    @Source("bundled-images/resource-open-disabled.png")
    ImageResource openResourceDisabled();

    /*
     * Navigation buttons
     */
    @Source("bundled-images/welcome/next.png")
    ImageResource next();

    @Source("bundled-images/welcome/next_Disabled.png")
    ImageResource nextDisabled();

    @Source("bundled-images/welcome/back.png")
    ImageResource back();

    @Source("bundled-images/welcome/back_Disabled.png")
    ImageResource backDisabled();

    @Source("bundled-images/welcome/welcome.png")
    ImageResource welcome();

    @Source("bundled-images/welcome/welcome-disabled.png")
    ImageResource welcomeDisabled();

   /*
    * Actions
    */

    @Source("../public/images/bundled/actions/add.png")
    ImageResource add();

    @Source("../public/images/bundled/actions/add_Disabled.png")
    ImageResource addDisabled();

    @Source("../public/images/bundled/actions/remove.png")
    ImageResource remove();

    @Source("../public/images/bundled/actions/remove_Disabled.png")
    ImageResource removeDisabled();

    @Source("../public/images/bundled/actions/up.png")
    ImageResource up();

    @Source("../public/images/bundled/actions/up_Disabled.png")
    ImageResource upDisabled();

    @Source("../public/images/bundled/actions/down.png")
    ImageResource down();

    @Source("../public/images/bundled/actions/down_Disabled.png")
    ImageResource downDisabled();

    @Source("../public/images/bundled/actions/defaults.png")
    ImageResource defaults();

    @Source("../public/images/bundled/actions/defaults_Disabled.png")
    ImageResource defaultsDisabled();

    @Source("org/exoplatform/ide/public/images/filetype/folder_closed.png")
    ImageResource folder();

    @Source("org/exoplatform/ide/public/images/filetype/default.png")
    ImageResource defaultFile();

    @Source("bundled-images/edit.png")
    ImageResource edit();

    @Source("bundled-images/edit-disabled.png")
    ImageResource editDisabled();

   /*
    * BROWSER
    */

    @Source("../public/images/bundled/output/output.png")
    ImageResource browser();

    @Source("../public/images/bundled/browser/browser_Disabled.png")
    ImageResource browserDisabled();

   /*
    * EDIT
    */

    @Source("../public/images/bundled/edit/cut.png")
    ImageResource cut();

    @Source("../public/images/bundled/edit/cut_Disabled.png")
    ImageResource cutDisabled();

    @Source("../public/images/bundled/edit/copy.png")
    ImageResource copy();

    @Source("../public/images/bundled/edit/copy_Disabled.png")
    ImageResource copyDisabled();

    @Source("../public/images/bundled/edit/paste.png")
    ImageResource paste();

    @Source("../public/images/bundled/edit/paste_Disabled.png")
    ImageResource pasteDisabled();

    @Source("../public/images/bundled/edit/undo.png")
    ImageResource undo();

    @Source("../public/images/bundled/edit/undo_Disabled.png")
    ImageResource undoDisabled();

    @Source("../public/images/bundled/edit/redo.png")
    ImageResource redo();

    @Source("../public/images/bundled/edit/redo_Disabled.png")
    ImageResource redoDisabled();

    @Source("../public/images/bundled/edit/select_all.png")
    ImageResource selectAll();

    @Source("../public/images/bundled/edit/select_all_Disabled.png")
    ImageResource selectAllDisabled();

    @Source("../public/images/bundled/edit/format.png")
    ImageResource format();

    @Source("../public/images/bundled/edit/format_Disabled.png")
    ImageResource formatDisabled();

    @Source("../public/images/bundled/edit/find_text.png")
    ImageResource findText();

    @Source("../public/images/bundled/edit/find_text_Disabled.png")
    ImageResource findTextDisabled();

    @Source("../public/images/bundled/edit/show_line_numbers.png")
    ImageResource showLineNumbers();

    @Source("../public/images/bundled/edit/show_line_numbers_Disabled.png")
    ImageResource showLineNumbersDisabled();

    @Source("../public/images/bundled/edit/hide_line_numbers.png")
    ImageResource hideLineNumbers();

    @Source("../public/images/bundled/edit/hide_line_numbers_Disabled.png")
    ImageResource hideLineNumbersDisabled();

    @Source("../public/images/bundled/edit/delete_current_line.png")
    ImageResource deleteCurrentLine();

    @Source("../public/images/bundled/edit/delete_current_line_Disabled.png")
    ImageResource deleteCurrentLineDisabled();

    @Source("../public/images/bundled/edit/lock_file.png")
    ImageResource lockUnlockFile();

    @Source("../public/images/bundled/edit/lock_file_Disabled.png")
    ImageResource lockUnlockFileDisabled();

    @Source("../public/images/bundled/edit/autocomplete.png")
    ImageResource autocomplete();

    @Source("../public/images/bundled/edit/autocomplete_Disabled.png")
    ImageResource autocompleteDisabled();

    @Source("../public/images/bundled/edit/lineup.png")
    ImageResource lineUp();

    @Source("../public/images/bundled/edit/lineup_Disabled.png")
    ImageResource lineUpDisabled();

    @Source("../public/images/bundled/edit/linedown.png")
    ImageResource lineDown();

    @Source("../public/images/bundled/edit/linedown_Disabled.png")
    ImageResource lineDownDisabled();

    @Source("../public/images/bundled/edit/toggle_comment.png")
    ImageResource toggleComment();

    @Source("../public/images/bundled/edit/toggle_comment_Disabled.png")
    ImageResource toggleCommentDisabled();

    @Source("../public/images/bundled/edit/add_block_comment.png")
    ImageResource addBlockComment();

    @Source("../public/images/bundled/edit/add_block_comment_Disabled.png")
    ImageResource addBlockCommentDisabled();

    @Source("../public/images/bundled/edit/remove_block_comment.png")
    ImageResource removeBlockComment();

    @Source("../public/images/bundled/edit/remove_block_comment_Disabled.png")
    ImageResource removeBlockCommentDisabled();

    /*
     * FILE
     */
    @Source("../public/images/bundled/file/upload.png")
    ImageResource upload();

    @Source("../public/images/bundled/file/upload_Disabled.png")
    ImageResource uploadDisabled();

    @Source("../public/images/bundled/file/open_local_file.png")
    ImageResource openLocalFile();

    @Source("../public/images/bundled/file/open_local_file_Disabled.png")
    ImageResource openLocalFileDisabled();

    @Source("../public/images/bundled/file/open_file_by_path.png")
    ImageResource openFileByPath();

    @Source("../public/images/bundled/file/open_file_by_path_Disabled.png")
    ImageResource openFileByPathDisabled();

    @Source("../public/images/bundled/file/download_file.png")
    ImageResource downloadFile();

    @Source("../public/images/bundled/file/download_file_Disabled.png")
    ImageResource downloadFileDisabled();

    @Source("../public/images/bundled/file/download_folder.png")
    ImageResource downloadFolder();

    @Source("../public/images/bundled/file/download_folder_Disabled.png")
    ImageResource downloadFolderDisabled();

    @Source("../public/images/bundled/file/new_file.png")
    ImageResource newFile();

    @Source("../public/images/bundled/file/new_file_Disabled.png")
    ImageResource newFileDisabled();

    @Source("../public/images/bundled/file/save.png")
    ImageResource save();

    @Source("../public/images/bundled/file/save_Disabled.png")
    ImageResource saveDisabled();

    @Source("../public/images/bundled/file/save_as.png")
    ImageResource saveAs();

    @Source("../public/images/bundled/file/save_as_Disabled.png")
    ImageResource saveAsDisabled();

    @Source("../public/images/bundled/file/save_all.png")
    ImageResource saveAll();

    @Source("../public/images/bundled/file/save_all_Disabled.png")
    ImageResource saveAllDisabled();

    @Source("../public/images/bundled/file/save_file_as_template.png")
    ImageResource saveFileAsTemplate();

    @Source("../public/images/bundled/file/save_file_as_template_Disabled.png")
    ImageResource saveFileAsTemplateDisabled();

    @Source("../public/images/bundled/file/new_folder.png")
    ImageResource newFolder();

    @Source("../public/images/bundled/file/new_folder_Disabled.png")
    ImageResource newFolderDisabled();

    @Source("../public/images/bundled/file/rename.png")
    ImageResource rename();

    @Source("../public/images/bundled/file/rename_Disabled.png")
    ImageResource renameDisabled();

    @Source("../public/images/bundled/file/delete.png")
    ImageResource delete();

    @Source("../public/images/bundled/file/delete_Disabled.png")
    ImageResource deleteDisabled();

    @Source("../public/images/bundled/file/search.png")
    ImageResource search();

    @Source("../public/images/bundled/file/search_Disabled.png")
    ImageResource searchDisabled();

    @Source("../public/images/bundled/file/refresh.png")
    ImageResource refresh();

    @Source("../public/images/bundled/file/refresh_Disabled.png")
    ImageResource refreshDisabled();

    @Source("../public/images/bundled/file/create_from_template.png")
    ImageResource createFromTemplate();

    @Source("../public/images/bundled/file/create_from_template_Disabled.png")
    ImageResource createFromTemplateDisabled();

    @Source("../public/images/bundled/file/create_project_template.png")
    ImageResource createProjectTemplate();

    @Source("../public/images/bundled/file/create_project_template_Disabled.png")
    ImageResource createProjectTemplateDisabled();

    /*
     * Project Explorer
     */
    @Source("bundled-images/project-explorer.png")
    ImageResource projectExplorer();

    @Source("bundled-images/project-explorer-disabled.png")
    ImageResource projectExplorerDisabled();

    /*
     * Open Project
     */
    @Source("bundled-images/projects/open-project.png")
    ImageResource openProject();

    @Source("bundled-images/projects/open-project-disabled.png")
    ImageResource openProjectDisabled();

    /*
     * Project Opened
     */
    @Source("bundled-images/projects/project-opened.png")
    ImageResource projectOpened();

    @Source("bundled-images/projects/project-opened-disabled.png")
    ImageResource projectOpenedDisabled();

    /*
     * PAAS
     */
    @Source("bundled-images/projects/paas.png")
    ImageResource paas();

    @Source("bundled-images/projects/paas_Disabled.png")
    ImageResource paasDisabled();

    @Source("bundled-images/projects/none-target.png")
    ImageResource noneTarget();

    @Source("bundled-images/projects/project-closed.png")
    ImageResource projectClosed();

    @Source("bundled-images/projects/project-closed-disabled.png")
    ImageResource projectClosedDisabled();

    @Source("bundled-images/projects/new-project.png")
    ImageResource newProject();

    @Source("bundled-images/projects/new-project-disabled.png")
    ImageResource newProjectDisabled();

    @Source("bundled-images/projects/project-properties.png")
    ImageResource projectProperties();

    @Source("bundled-images/projects/project-properties-disabled.png")
    ImageResource projectPropertiesDisabled();

   /*
    * Navigator
    */

    @Source("bundled-images/navigator.png")
    ImageResource navigator();

    @Source("bundled-images/navigator-disabled.png")
    ImageResource navigatorDisabled();

   /*
    * HELP
    */

    @Source("../public/images/bundled/help/about.png")
    ImageResource about();

    @Source("../public/images/bundled/help/about_Disabled.png")
    ImageResource aboutDisabled();

    @Source("../public/images/bundled/help/restServicesDiscovery.png")
    ImageResource restServicesDiscovery();

    @Source("../public/images/bundled/help/restServicesDiscovery_Disabled.png")
    ImageResource restServicesDiscoveryDisabled();

    @Source("../public/images/bundled/help/google.png")
    ImageResource google();

    @Source("../public/images/bundled/help/google_Disabled.png")
    ImageResource googleDisabled();

   /*
    * OUTLINE
    */

    @Source("../public/images/bundled/outline/outline.png")
    ImageResource outline();

    @Source("../public/images/bundled/outline/outline_Disabled.png")
    ImageResource outlineDisabled();

    @Source("bundled-images/loader.gif")
    ImageResource loader();

   /*
    * OUTPUT
    */

    @Source("../public/images/bundled/output/output.png")
    ImageResource output();

    @Source("../public/images/bundled/output/output_Disabled.png")
    ImageResource outputDisabled();

   /*
    * RUN
    */

    @Source("../public/images/bundled/run/preview.png")
    ImageResource preview();

    @Source("../public/images/bundled/run/preview_Disabled.png")
    ImageResource previewDisabled();

   /*
    * VIEW
    */

    @Source("../public/images/bundled/view/go_to_line.png")
    ImageResource goToLine();

    @Source("../public/images/bundled/view/go_to_line_Disabled.png")
    ImageResource goToLineDisabled();

    @Source("../public/images/bundled/view/properties.png")
    ImageResource properties();

    @Source("../public/images/bundled/view/properties_Disabled.png")
    ImageResource propertiesDisabled();

    @Source("../public/images/bundled/view/url.png")
    ImageResource url();

    @Source("../public/images/bundled/view/url_Disabled.png")
    ImageResource urlDisabled();

    @Source("../public/images/bundled/view/go_to_folder.png")
    ImageResource goToFolder();

    @Source("../public/images/bundled/view/go_to_folder_Disabled.png")
    ImageResource goToFolderDisabled();

    @Source("../public/images/bundled/view/show_hidden_files.png")
    ImageResource showHiddenFiles();

    /*
     * VERSIONING
     */
    @Source("../public/images/bundled/versioning/view_versions.png")
    ImageResource viewVersions();

    @Source("../public/images/bundled/versioning/view_versions_Disabled.png")
    ImageResource viewVersionsDisabled();

    @Source("../public/images/bundled/versioning/view_version_content.png")
    ImageResource viewVersionContent();

    @Source("../public/images/bundled/versioning/view_version_content_Disabled.png")
    ImageResource viewVersionContentDisabled();

    @Source("../public/images/bundled/versioning/restore_version.png")
    ImageResource restoreVersion();

    @Source("../public/images/bundled/versioning/restore_version_Disabled.png")
    ImageResource restoreVersionDisabled();

    @Source("../public/images/bundled/versioning/older_version.png")
    ImageResource viewOlderVersion();

    @Source("../public/images/bundled/versioning/older_version_Disabled.png")
    ImageResource viewOlderVersionDisabled();

    @Source("../public/images/bundled/versioning/newer_version.png")
    ImageResource viewNewerVersion();

    @Source("../public/images/bundled/versioning/newer_version_Disabled.png")
    ImageResource viewNewerVersionDisabled();

   /*
    * WINDOW
    */

    @Source("../public/images/bundled/window/customize_toolbar.png")
    ImageResource customizeToolbar();

    @Source("../public/images/bundled/window/customize_toolbar_Disabled.png")
    ImageResource customizeToolbarDisabled();

    @Source("../public/images/bundled/window/hotkeys.png")
    ImageResource customizeHotKeys();

    @Source("../public/images/bundled/window/hotkeys_Disabled.png")
    ImageResource customizeHotKeysDisabled();

    @Source("../public/images/bundled/window/showHotkeys.png")
    ImageResource showHotKeys();

    @Source("../public/images/bundled/window/showHotkeys_Disabled.png")
    ImageResource showHotKeysDisabled();

    @Source("../public/images/bundled/window/workspace.png")
    ImageResource workspace();

    @Source("../public/images/bundled/window/workspace_Disabled.png")
    ImageResource workspaceDisabled();

    @Source("../public/images/bundled/window/preferences.png")
    ImageResource preferences();

    @Source("../public/images/bundled/window/preferences_Disabled.png")
    ImageResource preferencesDisabled();

    /*
     * AUTO COMPLETE
     */
    @Source("../public/images/bundled/outline/function-item.png")
    ImageResource functionItem();

    @Source("../public/images/bundled/outline/method-item.png")
    ImageResource methodItem();

    @Source("../public/images/bundled/outline/var-item.png")
    ImageResource varItem();

    @Source("../public/images/bundled/outline/property-item.png")
    ImageResource propertyItem();

    @Source("../public/images/bundled/outline/template.png")
    ImageResource templateItem();

    @Source("../public/images/bundled/outline/keyword.png")
    ImageResource keywordItem();

    @Source("../public/images/bundled/outline/tag.png")
    ImageResource tagItem();

    @Source("../public/images/bundled/outline/attribute.png")
    ImageResource attributeItem();

    @Source("../public/images/bundled/documentation/documentation.png")
    ImageResource documentation();

    @Source("../public/images/bundled/documentation/documentation_Disabled.png")
    ImageResource documentationDisabled();

   /*
    * Maximize, Restore for Panels
    */

    @Source("../public/images/bundled/panel/maximize.png")
    ImageResource maximize();

    @Source("../public/images/bundled/panel/minimize.png")
    ImageResource restore();

    /*
     * Welcome images
     */
    @Source("bundled-images/welcome/tutorial.png")
    ImageResource welcomeTutorial();

    @Source("bundled-images/welcome/samples.png")
    ImageResource welcomeSamples();

    @Source("org/exoplatform/ide/public/images/filetype/txt.png")
    ImageResource textFile();

    @Source("bundled-images/welcome/noPhoto.gif")
    ImageResource noPhoto();

    /*
     * Progress images
     */
    @Source("org/exoplatform/ide/client/bundled-images/async_request.gif")
    ImageResource asyncRequest();

    @Source("org/exoplatform/ide/client/bundled-images/progress.gif")
    ImageResource progresImage();

    @Source("org/exoplatform/ide/client/bundled-images/progress_remall.gif")
    ImageResource progresRemall();

    /*
     *  Third party icons
     */
    @Source("org/exoplatform/ide/public/images/github-icon.png")
    ImageResource gitHubIconSmall();

    @Source("org/exoplatform/ide/public/images/browsers/chrome.png")
    ImageResource chrome();

    @Source("org/exoplatform/ide/public/images/browsers/firefox.png")
    ImageResource firefox();

    @Source("org/exoplatform/ide/public/images/browsers/safari.png")
    ImageResource safari();

    @Source("org/exoplatform/ide/public/images/jrebel.png")
    ImageResource jrebel();

    @Source("org/exoplatform/ide/public/images/question.png")
    ImageResource question();

    @Source("org/exoplatform/ide/public/images/readonly.png")
    ImageResource readOnly();

    @Source("org/exoplatform/ide/public/images/readonly-hover.png")
    ImageResource readOnlyHover();

}
