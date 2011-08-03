/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public interface IDEImageBundle extends ClientBundle
{

   public static final IDEImageBundle INSTANCE = GWT.create(IDEImageBundle.class);

   @Source("org/exoplatform/ide/public/images/logo/eXo-IDE-Logo.png")
   ImageResource ideLogo();
   
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

   /*
    * OUTLINE
    */

   @Source("../public/images/bundled/outline/outline.png")
   ImageResource outline();

   @Source("../public/images/bundled/outline/outline_Disabled.png")
   ImageResource outlineDisabled();

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

   @Source("../public/images/bundled/window/workspace.png")
   ImageResource workspace();

   @Source("../public/images/bundled/window/workspace_Disabled.png")
   ImageResource workspaceDisabled();

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
   ImageResource documentation_Disabled();

   /*
    * Maximize, Restore for Panels
    */

   @Source("../public/images/bundled/panel/maximize.png")
   ImageResource maximize();

   @Source("../public/images/bundled/panel/minimize.png")
   ImageResource restore();

}
