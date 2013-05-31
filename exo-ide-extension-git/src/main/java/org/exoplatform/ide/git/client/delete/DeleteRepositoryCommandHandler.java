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
