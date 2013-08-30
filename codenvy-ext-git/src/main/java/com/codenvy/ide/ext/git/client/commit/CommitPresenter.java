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
package com.codenvy.ide.ext.git.client.commit;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.marshaller.RevisionUnmarshaller;
import com.codenvy.ide.ext.git.client.marshaller.RevisionUnmarshallerWS;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.Date;

/**
 * Presenter for commit changes on git.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 31, 2011 10:02:25 AM anya $
 */
@Singleton
public class CommitPresenter implements CommitView.ActionDelegate {
    private CommitView              view;
    private GitClientService        service;
    private ResourceProvider        resourceProvider;
    private GitLocalizationConstant constant;
    private ConsolePart             console;
    private Project                 project;
    private EventBus                eventBus;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param console
     * @param constant
     * @param eventBus
     */
    @Inject
    public CommitPresenter(CommitView view, GitClientService service, ResourceProvider resourceProvider, ConsolePart console,
                           GitLocalizationConstant constant, EventBus eventBus) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
        this.eventBus = eventBus;
    }

    /** Show dialog. */
    public void showDialog() {
        project = resourceProvider.getActiveProject();
        view.setAmend(false);
        view.setAllFilesInclude(false);
        view.setMessage("");
        view.focusInMessageField();
        view.setEnableCommitButton(false);
        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onCommitClicked() {
        String message = view.getMessage();
        boolean all = view.isAllFilesInclued();
        boolean amend = view.isAmend();
        DtoClientImpls.RevisionImpl revision = DtoClientImpls.RevisionImpl.make();
        revision.setMessage(message);
        revision.setCommitTime(0);
        RevisionUnmarshallerWS unmarshaller = new RevisionUnmarshallerWS();

        try {
            service.commitWS(resourceProvider.getVfsId(), project, message, all, amend, new RequestCallback<Revision>(unmarshaller) {
                @Override
                protected void onSuccess(Revision result) {
                    if (!result.fake()) {
                        onCommitSuccess(result);
                    } else {
                        console.print(result.getMessage());
                    }
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception);
                }
            });
        } catch (WebSocketException e) {
            doCommitREST(project, message, all, amend);
        }
        view.close();
    }

    /** Perform the commit to repository and process the response (sends request over HTTP). */
    private void doCommitREST(@NotNull Project project, @NotNull String message, boolean all, boolean amend) {
        DtoClientImpls.RevisionImpl revision = DtoClientImpls.RevisionImpl.make();
        revision.setMessage(message);
        revision.setCommitTime(0);
        RevisionUnmarshaller unmarshaller = new RevisionUnmarshaller();

        try {
            service.commit(resourceProvider.getVfsId(), project, message, all, amend,
                           new AsyncRequestCallback<Revision>(unmarshaller) {
                               @Override
                               protected void onSuccess(Revision result) {
                                   if (!result.fake()) {
                                       onCommitSuccess(result);
                                   } else {
                                       console.print(result.getMessage());
                                   }
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   handleError(exception);
                               }
                           });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Performs action when commit is successfully completed.
     *
     * @param revision
     *         a {@link Revision}
     */
    private void onCommitSuccess(@NotNull final Revision revision) {
        resourceProvider.getProject(project.getName(), new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                DateTimeFormat formatter = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
                String date = formatter.format(new Date((long)revision.getCommitTime()));
                String message = constant.commitMessage(revision.getId(), date);
                message += (revision.getCommitter() != null && revision.getCommitter().getName() != null &&
                            !revision.getCommitter().getName().isEmpty())
                           ? " " + constant.commitUser(revision.getCommitter().getName()) : "";
                console.print(message);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(CommitPresenter.class, "can not get project " + project.getName());
            }
        });
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param e
     *         exception what happened
     */
    private void handleError(@NotNull Throwable e) {
        String errorMessage = (e.getMessage() != null && !e.getMessage().isEmpty()) ? e.getMessage() : constant.commitFailed();
        console.print(errorMessage);
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        String message = view.getMessage();
        view.setEnableCommitButton(!message.isEmpty());
    }
}