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
package com.codenvy.ide.ext.git.client.commit;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.marshaller.RevisionUnmarshaller;
import com.codenvy.ide.ext.git.client.marshaller.RevisionUnmarshallerWS;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.i18n.client.DateTimeFormat;
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
        RevisionUnmarshallerWS unmarshaller = new RevisionUnmarshallerWS(new Revision(null, message, 0, null));

        try {
            service.commitWS(resourceProvider.getVfsId(), project, message, all, amend, new RequestCallback<Revision>(unmarshaller) {
                @Override
                protected void onSuccess(Revision result) {
                    if (!result.isFake()) {
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
    private void doCommitREST(Project project, String message, boolean all, boolean amend) {
        RevisionUnmarshaller unmarshaller = new RevisionUnmarshaller(new Revision(null, message, 0, null));

        try {
            service.commit(resourceProvider.getVfsId(), project, message, all, amend,
                           new AsyncRequestCallback<Revision>(unmarshaller) {
                               @Override
                               protected void onSuccess(Revision result) {
                                   if (!result.isFake()) {
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
    private void onCommitSuccess(Revision revision) {
        DateTimeFormat formatter = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
        String date = formatter.format(new Date(revision.getCommitTime()));
        String message = constant.commitMessage(revision.getId(), date);
        message += (revision.getCommitter() != null && revision.getCommitter().getName() != null &&
                    !revision.getCommitter().getName().isEmpty())
                   ? " " + constant.commitUser(revision.getCommitter().getName()) : "";
        console.print(message);
        // TODO refresh tree
        // IDE.fireEvent(new TreeRefreshedEvent(getSelectedProject()));
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param e
     *         exception what happened
     */
    private void handleError(Throwable e) {
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