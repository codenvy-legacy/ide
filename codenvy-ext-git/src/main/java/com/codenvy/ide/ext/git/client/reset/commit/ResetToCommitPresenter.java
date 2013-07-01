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
package com.codenvy.ide.ext.git.client.reset.commit;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.marshaller.LogResponseUnmarshaller;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.LogResponse;
import com.codenvy.ide.ext.git.shared.ResetRequest;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Presenter for reseting head to commit.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 15, 2011 10:31:25 AM anya $
 */
@Singleton
public class ResetToCommitPresenter implements ResetToCommitView.ActionDelegate {
    private ResetToCommitView       view;
    private GitClientService        service;
    private Revision                selectedRevison;
    private ResourceProvider        resourceProvider;
    private GitLocalizationConstant constant;
    private ConsolePart             console;
    private String                  projectId;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param constant
     * @param console
     */
    @Inject
    public ResetToCommitPresenter(ResetToCommitView view, GitClientService service, ResourceProvider resourceProvider,
                                  GitLocalizationConstant constant, ConsolePart console) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.console = console;
    }

    /** Show dialog. */
    public void showDialog() {
        projectId = resourceProvider.getActiveProject().getId();
        DtoClientImpls.LogResponseImpl logResponse = DtoClientImpls.LogResponseImpl.make();
        LogResponseUnmarshaller unmarshaller = new LogResponseUnmarshaller(logResponse, false);

        try {
            service.log(resourceProvider.getVfsId(), projectId, false, new AsyncRequestCallback<LogResponse>(unmarshaller) {
                @Override
                protected void onSuccess(LogResponse result) {
                    selectedRevison = null;
                    view.setRevisions(result.getCommits());
                    view.setMixMode(true);
                    view.setEnableResetButton(false);
                    view.showDialog();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : constant.logFailed();
                    console.print(errorMessage);
                }
            });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : constant.logFailed();
            console.print(errorMessage);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onResetClicked() {
        ResetRequest.ResetType type = view.isMixMode() ? ResetRequest.ResetType.MIXED : null;
        type = (type == null && view.isSoftMode()) ? ResetRequest.ResetType.SOFT : type;
        type = (type == null && view.isHardMode()) ? ResetRequest.ResetType.HARD : type;
        type = (type == null && view.isKeepMode()) ? ResetRequest.ResetType.KEEP : type;
        type = (type == null && view.isMergeMode()) ? ResetRequest.ResetType.MERGE : type;

        try {
            service.reset(resourceProvider.getVfsId(), projectId, selectedRevison.getId(), type, new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    resourceProvider.getActiveProject().refreshTree(new AsyncCallback<Project>() {
                        @Override
                        public void onSuccess(Project result) {
                            console.print(constant.resetSuccessfully());
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(ResetToCommitPresenter.class, caught);
                        }
                    });
                    view.close();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : constant.resetFail();
                    console.print(errorMessage);
                }
            });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : constant.resetFail();
            console.print(errorMessage);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onRevisionSelected(@NotNull Revision revision) {
        selectedRevison = revision;
        view.setEnableResetButton(true);
    }
}