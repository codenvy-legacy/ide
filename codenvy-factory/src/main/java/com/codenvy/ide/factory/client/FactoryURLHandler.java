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
package com.codenvy.ide.factory.client;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Handle action when user tries to share opened project with Factory URL.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FactoryURLHandler.java Jun 11, 2013 5:50:55 PM azatsarynnyy $
 */
public class FactoryURLHandler implements ShareWithFactoryUrlHandler {

    /**
     * @see com.codenvy.ide.factory.client.ShareWithFactoryUrlHandler#onShare(com.codenvy.ide.factory.client.ShareWithFactoryUrlEvent)
     */
    @Override
    public void onShare(ShareWithFactoryUrlEvent event) {
        if (isGitRepoInitialized()) {
            initializeRepository();
        } else if (isAllChangesAreCommitted()) {
            commitChanges();
        } else {
            IDE.fireEvent(new GetCodeNowButtonEvent());
        }
    }

    private boolean isAllChangesAreCommitted() {
        // TODO
        return false;
    }

    private boolean isGitRepoInitialized() {
        // TODO
        return false;
    }


    /** Initialize of the Git-repository by sending request over WebSocket or HTTP. */
    private void initializeRepository(final ProjectModel project) {
        try {
            GitClientService.getInstance().initWS(vfs.getId(), project.getId(), project.getName(), false,
                                                  new RequestCallback<String>() {
                                                      @Override
                                                      protected void onSuccess(String result) {
                                                          onInitSuccess();
                                                      }

                                                      @Override
                                                      protected void onFailure(Throwable exception) {
                                                          handleError(exception);
                                                      }
                                                  });
        } catch (WebSocketException e) {
            initRepositoryREST(project);
        }
    }

    /** Initialize Git repository (sends request over HTTP). */
    private void initializeRepositoryREST(final ProjectModel project) {
        try {
            GitClientService.getInstance().init(vfs.getId(), project.getId(), project.getName(), false,
                                                new AsyncRequestCallback<String>() {
                                                    @Override
                                                    protected void onSuccess(String result) {
                                                        onInitSuccess();
                                                    }

                                                    @Override
                                                    protected void onFailure(Throwable exception) {
                                                        handleError(exception);
                                                    }
                                                });
        } catch (RequestException e) {
            handleError(e);
        }
    }


    private void commitChanges() {
        IDE.fireEvent(new CommitChangesEvent());
    }


    private void handleError(Throwable e) {
        String errorMessage =
                (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES.initFailed();
        IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
    }

}
