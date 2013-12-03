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
package org.exoplatform.ide.git.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.git.client.add.AddToIndexPresenter;
import org.exoplatform.ide.git.client.branch.BranchPresenter;
import org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter;
import org.exoplatform.ide.git.client.commit.CommitPresenter;
import org.exoplatform.ide.git.client.control.AddFilesControl;
import org.exoplatform.ide.git.client.control.BranchesControl;
import org.exoplatform.ide.git.client.control.CloneRepositoryControl;
import org.exoplatform.ide.git.client.control.CommitControl;
import org.exoplatform.ide.git.client.control.DeleteRepositoryControl;
import org.exoplatform.ide.git.client.control.FetchControl;
import org.exoplatform.ide.git.client.control.InitRepositoryControl;
import org.exoplatform.ide.git.client.control.MergeControl;
import org.exoplatform.ide.git.client.control.PullControl;
import org.exoplatform.ide.git.client.control.PushToRemoteControl;
import org.exoplatform.ide.git.client.control.RemoteControl;
import org.exoplatform.ide.git.client.control.RemotesControl;
import org.exoplatform.ide.git.client.control.RemoveFilesControl;
import org.exoplatform.ide.git.client.control.ResetFilesControl;
import org.exoplatform.ide.git.client.control.ResetToCommitControl;
import org.exoplatform.ide.git.client.control.ShowHistoryControl;
import org.exoplatform.ide.git.client.control.ShowProjectGitReadOnlyUrl;
import org.exoplatform.ide.git.client.control.ShowStatusControl;
import org.exoplatform.ide.git.client.delete.DeleteRepositoryCommandHandler;
import org.exoplatform.ide.git.client.editor.UpdateOpenedFilesPresenter;
import org.exoplatform.ide.git.client.fetch.FetchPresenter;
import org.exoplatform.ide.git.client.github.gitimport.ImportFromGithubControl;
import org.exoplatform.ide.git.client.github.gitimport.ImportFromGithubPresenter;
import org.exoplatform.ide.git.client.history.HistoryPresenter;
import org.exoplatform.ide.git.client.init.InitRepositoryPresenter;
import org.exoplatform.ide.git.client.init.ShowProjectGitReadOnlyUrlPresenter;
import org.exoplatform.ide.git.client.merge.MergePresenter;
import org.exoplatform.ide.git.client.pull.PullPresenter;
import org.exoplatform.ide.git.client.push.PushToRemotePresenter;
import org.exoplatform.ide.git.client.remote.RemotePresenter;
import org.exoplatform.ide.git.client.remove.RemoveFromIndexPresenter;
import org.exoplatform.ide.git.client.reset.ResetFilesPresenter;
import org.exoplatform.ide.git.client.reset.ResetToCommitPresenter;
import org.exoplatform.ide.git.client.ssh.SSHKeyProcessor;
import org.exoplatform.ide.git.client.status.StatusCommandHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;

/**
 * Git extension to be added to IDE application.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 22, 2011 12:53:29 PM anya $
 */
public class GitExtension extends Extension implements InitializeServicesHandler {

    public static final GitLocalizationConstant MESSAGES = GWT.create(GitLocalizationConstant.class);

    public static final String GIT_REPOSITORY_PROP = "isGitRepository";

    public static final GitAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(GitAutoBeanFactory.class);

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        // Add controls:
        IDE.getInstance().addControl(new InitRepositoryControl());
        IDE.getInstance().addControl(new CloneRepositoryControl());
        IDE.getInstance().addControl(new DeleteRepositoryControl());
        IDE.getInstance().addControl(new AddFilesControl());
        IDE.getInstance().addControl(new ResetFilesControl());
        IDE.getInstance().addControl(new ResetToCommitControl());
        IDE.getInstance().addControl(new RemoveFilesControl());
        IDE.getInstance().addControl(new CommitControl());
        IDE.getInstance().addControl(new BranchesControl());
        IDE.getInstance().addControl(new MergeControl());
        IDE.getInstance().addControl(new PushToRemoteControl());
        IDE.getInstance().addControl(new FetchControl());
        IDE.getInstance().addControl(new PullControl());
        IDE.getInstance().addControl(new RemoteControl());
        IDE.getInstance().addControl(new RemotesControl());

        IDE.getInstance().addControl(new ShowHistoryControl());
        IDE.getInstance().addControl(new ShowStatusControl());
        IDE.getInstance().addControl(new ShowProjectGitReadOnlyUrl());
        IDE.getInstance().addControl(new ImportFromGithubControl());
        IDE.getInstance().addControlsFormatter(new GitControlsFormatter());


        // Create presenters:
        new CloneRepositoryPresenter();
        new InitRepositoryPresenter();
        new StatusCommandHandler();
        new AddToIndexPresenter();
        new RemoveFromIndexPresenter();
        new ResetFilesPresenter();
        new ResetToCommitPresenter();
        new RemotePresenter();

        new CommitPresenter();
        new PushToRemotePresenter();
        new BranchPresenter();
        new FetchPresenter();
        new PullPresenter();
        new HistoryPresenter();
        new DeleteRepositoryCommandHandler();
        new MergePresenter();

        new ShowProjectGitReadOnlyUrlPresenter();

        new ImportFromGithubPresenter();
        new SSHKeyProcessor();
        new UpdateOpenedFilesPresenter();

    }

    /** {@inheritDoc} */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new GitClientServiceImpl(Utils.getRestContext(), Utils.getWorkspaceName(), event.getLoader(), IDE.messageBus());
    }

    /**
     * View links for folder which we want to delete and try to delete folder.
     *
     * @param folder
     *         folder to delete
     */
    public static void deleteFolder(FolderModel folder) {
        if (folder.getLinks().isEmpty()) {
            try {
                VirtualFileSystem.getInstance()
                                 .getItemById(folder.getId(),
                                              new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(folder))) {

                                                  @Override
                                                  protected void onSuccess(ItemWrapper result) {
                                                      deleteDirSrv((FolderModel)result.getItem());
                                                  }

                                                  @Override
                                                  protected void onFailure(Throwable exception) {
                                                      IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                  }
                                              });
            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }
        } else {
            deleteDirSrv(folder);
        }
    }

    /**
     * Private service method for deleting directory.
     *
     * @param folder
     *         folder to delete
     */
    private static void deleteDirSrv(FolderModel folder) {
        try {
            VirtualFileSystem.getInstance().delete(folder,
                                                   new AsyncRequestCallback<String>() {
                                                       @Override
                                                       protected void onSuccess(String result) {
                                                           // Do nothing
                                                       }

                                                       @Override
                                                       protected void onFailure(Throwable e) {
                                                           // Do nothing
                                                       }
                                                   });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e, "Exception during removing of directory project"));
        }
    }

    /**
     * Handle user errors and showing them to output.
     *
     * @param exception
     *         exception which happened
     * @param remoteUri
     *         url for remote repository
     */
    public static void handleError(Throwable exception, String remoteUri) {
        String errorMessage = (exception.getMessage() != null && !exception.getMessage().isEmpty()) ?
                              exception.getMessage() : GitExtension.MESSAGES.cloneFailed(remoteUri);
        IDE.fireEvent(new OutputEvent(errorMessage, OutputMessage.Type.GIT));
    }
}
