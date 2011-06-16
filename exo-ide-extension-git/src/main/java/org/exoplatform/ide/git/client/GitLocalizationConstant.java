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
package org.exoplatform.ide.git.client;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public interface GitLocalizationConstant extends com.google.gwt.i18n.client.Messages
{
   
   //BUTTONS
   
   @Key("button.add")
   String buttonAdd();
   
   @Key("button.cancel")
   String buttonCancel();
   
   @Key("button.create")
   String buttonCreate();
   
   @Key("button.checkout")
   String buttonCheckout();
   
   @Key("button.delete")
   String buttonDelete();
   
   @Key("button.close")
   String buttonClose();
   
   //MESSAGES
   @Key("messages.not_git_repository")
   String notGitRepository();

   @Key("messages.selected_items_fail")
   String selectedItemsFail();

   @Key("messages.add_success")
   String addSuccess();

   @Key("messages.add_failed")
   String addFailed();

   @Key("messages.branches_list_failed")
   String branchesListFailed();

   @Key("messages.branch_create_failed")
   String branchCreateFailed();

   @Key("messages.branch_checkout_failed")
   String branchCheckoutFailed();

   @Key("messages.branch_delete_failed")
   String branchDeleteFailed();

   @Key("messages.clone_success")
   String cloneSuccess();

   @Key("messages.clone_failed")
   String cloneFailed();

   @Key("messages.commit_success")
   String commitSuccess();

   @Key("messages.commit_failed")
   String commitFailed();

   @Key("messages.diff.failed")
   String diffFailed();

   @Key("messages.nothing_to_commit")
   String nothingToCommit();

   @Key("messages.log_failed")
   String logFailed();

   @Key("messages.init_success")
   String initSuccess();

   @Key("messages.init_failed")
   String initFailed();

   @Key("messages.push_success")
   String pushSuccess(String remote);

   @Key("messages.push_fail")
   String pushFail();

   @Key("messages.pull_success")
   String pullSuccess(String remoteUrl);

   @Key("messages.pull_fail")
   String pullFail(String remoteUrl);

   @Key("messages.fetch_success")
   String fetchSuccess(String remoteUrl);

   @Key("messages.fetch_fail")
   String fetchFail(String remoteUrl);

   @Key("messages.remote_list_failed")
   String remoteListFailed();

   @Key("messages.remove_files_failed")
   String removeFilesFailed();

   @Key("messages.remote_add_failed")
   String remoteAddFailed();

   @Key("messages.remote_delete_failed")
   String remoteDeleteFailed();

   @Key("messages.reset_files_failed")
   String resetFilesFailed();

   @Key("messages.reset_files_successfully")
   String resetFilesSuccessfully();

   @Key("messages.reset_successfully")
   String resetSuccessfully();

   @Key("messages.reset_fail")
   String resetFail();

   @Key("messages.repository_already_exists")
   String repositoryAlreadyExists();

   @Key("messages.status_failed")
   String statusFailed();

   //----VIEWS------------------------------------------------------------------

   //Add
   @Key("view.add_to_index.all_changes")
   String addToIndexAllChanges();

   @Key("view.add_to_index.folder")
   String addToIndexFolder(String folder);

   @Key("view.add_to_index.file")
   String addToIndexFile(String file);
   
   @Key("view.add_to_index.update_field_title")
   String addToIndexUpdateFieldTitle();
   
   @Key("view.add_to_index.title")
   String addToIndexTitle();
   
   //Branch
   
   @Key("view.branch.grid.name_column")
   String branchGridNameColumn();
   
   @Key("view.branch.ceate_new")
   String branchCreateNew();
   
   @Key("view.branch.type_new")
   String branchTypeNew();
   
   @Key("view.branch.delete")
   String branchDelete();
   
   @Key("view.branch.delete_ask")
   String branchDeleteAsk(String name);
   
   @Key("view.branch.title")
   String branchTitle();
   
}
