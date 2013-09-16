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
package org.exoplatform.ide.git.client.delete;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;

/**
 * Delete repository command handler, performs deleting Git repository.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 21, 2011 5:57:30 PM anya $
 */
public class DeleteRepositoryCommandHandler extends GitPresenter implements DeleteRepositoryHandler {
    /**
     *
     */
    public DeleteRepositoryCommandHandler() {
        IDE.addHandler(DeleteRepositoryEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.git.client.delete.DeleteRepositoryHandler#onDeleteRepository(org.exoplatform.ide.git.client.delete
     *      .DeleteRepositoryEvent)
     */
    @Override
    public void onDeleteRepository(DeleteRepositoryEvent event) {
        if (makeSelectionCheck()) {
            String workDir = getSelectedProject().getPath();
            askBeforeDelete(workDir);
        }
    }

    /**
     * Confirm, that user wants to delete Git repository.
     * 
     * @param repository
     */
    protected void askBeforeDelete(String repository) {
        Dialogs.getInstance().ask(GitExtension.MESSAGES.deleteGitRepositoryTitle(),
                                  GitExtension.MESSAGES.deleteGitRepositoryQuestion(repository), new BooleanValueReceivedHandler() {
                                      @Override
                                      public void booleanValueReceived(Boolean value) {
                                          if (value != null && value) {
                                              doDeleteRepository();
                                          }
                                      }
                                  });
    }

    /** Perform deleting Git repository. */
    public void doDeleteRepository() {

        try {
            GitClientService.getInstance().deleteRepository(vfs.getId(), getSelectedProject().getId(),
                                                            new AsyncRequestCallback<Void>() {
                                                                @Override
                                                                protected void onSuccess(Void result) {
                                                                    IDE.fireEvent(new OutputEvent(
                                                                                                  GitExtension.MESSAGES.deleteGitRepositorySuccess(),
                                                                                                  Type.INFO));
                                                                    IDE.fireEvent(new RefreshBrowserEvent(getSelectedProject()));
                                                                }

                                                                @Override
                                                                protected void onFailure(Throwable exception) {
                                                                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                                }
                                                            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
