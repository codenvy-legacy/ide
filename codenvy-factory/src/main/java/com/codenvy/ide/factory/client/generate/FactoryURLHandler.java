/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.factory.client.generate;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.shared.Status;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import static com.codenvy.ide.factory.client.FactoryExtension.LOCALIZATION_CONSTANTS;

/**
 * Handler to process action when user tries to share opened project with Factory URL.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FactoryURLHandler.java Jun 11, 2013 5:50:55 PM azatsarynnyy $
 */
public class FactoryURLHandler implements ShareWithFactoryUrlHandler, VfsChangedHandler, ProjectOpenedHandler, ProjectClosedHandler {

    /** Current virtual file system. */
    private VirtualFileSystemInfo vfs;

    /** Current project. */
    private ProjectModel          openedProject;

    public FactoryURLHandler() {
        IDE.addHandler(ShareWithFactoryUrlEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.ShareWithFactoryUrlHandler#onShare(com.codenvy.ide.factory.client.generate.ShareWithFactoryUrlEvent)
     */
    @Override
    public void onShare(ShareWithFactoryUrlEvent event) {
        checkGitRepoStatus(openedProject);
    }

    private void checkGitRepoStatus(final ProjectModel project) {
        try {
            GitClientService.getInstance()
                            .status(vfs.getId(),
                                    project.getId(),
                                    new AsyncRequestCallback<Status>(
                                                                     new AutoBeanUnmarshaller<Status>(
                                                                                                      GitExtension.AUTO_BEAN_FACTORY.status())) {
                                        @Override
                                        protected void onSuccess(Status result) {
                                            checkUncommittedChanges(result);
                                        }

                                        @Override
                                        protected void onFailure(Throwable exception) {
                                            initializeRepository(project);
                                        }
                                    });
        } catch (RequestException e) {
            IDE.fireEvent(new OutputEvent(LOCALIZATION_CONSTANTS.checkRepoStatusFailed(), Type.ERROR));
        }
    }

    private void checkUncommittedChanges(Status status) {
        if (status.isClean()) {
            IDE.fireEvent(new GetCodeNowButtonEvent());
        } else {
            IDE.fireEvent(new CommitChangesEvent());
        }
    }

    /** Initialize of the Git-repository by sending request over WebSocket or HTTP. */
    private void initializeRepository(final ProjectModel project) {
        try {
            GitClientService.getInstance().initWS(vfs.getId(), project.getId(), project.getName(), false,
                                                  new RequestCallback<String>() {
                                                      @Override
                                                      protected void onSuccess(String result) {
                                                          onInitializingSuccess(project);
                                                      }

                                                      @Override
                                                      protected void onFailure(Throwable exception) {
                                                          handleGitInitializingError(exception);
                                                      }
                                                  });
        } catch (WebSocketException e) {
            initializeRepositoryREST(project);
        }
    }

    /** Initialize Git repository (sends request over HTTP). */
    private void initializeRepositoryREST(final ProjectModel project) {
        try {
            GitClientService.getInstance().init(vfs.getId(), project.getId(), project.getName(), false,
                                                new AsyncRequestCallback<String>() {
                                                    @Override
                                                    protected void onSuccess(String result) {
                                                        onInitializingSuccess(project);
                                                    }

                                                    @Override
                                                    protected void onFailure(Throwable exception) {
                                                        handleGitInitializingError(exception);
                                                    }
                                                });
        } catch (RequestException e) {
            handleGitInitializingError(e);
        }
    }

    private void onInitializingSuccess(ProjectModel project) {
        IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.initSuccess(), Type.INFO));
        IDE.fireEvent(new RefreshBrowserEvent(project));
        IDE.fireEvent(new GetCodeNowButtonEvent());
    }

    private void handleGitInitializingError(Throwable e) {
        String errorMessage = (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES.initFailed();
        IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
     */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfs = event.getVfsInfo();
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
     */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        openedProject = event.getProject();
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
     */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        openedProject = null;
    }

}
