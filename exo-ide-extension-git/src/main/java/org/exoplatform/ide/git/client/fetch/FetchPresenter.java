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
package org.exoplatform.ide.git.client.fetch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.remote.HasBranchesPresenter;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.BranchListRequest;
import org.exoplatform.ide.git.shared.Remote;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Presenter of the view for fetching changes from remote repository.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 20, 2011 1:33:17 PM anya $
 */
public class FetchPresenter extends HasBranchesPresenter implements FetchHandler {

    interface Display extends IsView {
        /**
         * Get fetch button's click handler.
         * 
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getFetchButton();

        /**
         * Get cancel button's click handler.
         * 
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getCancelButton();

        /**
         * Get remote repository field.
         * 
         * @return {@link HasValue}
         */
        HasValue<String> getRemoteName();

        /**
         * Get remote branches field.
         * 
         * @return {@link HasValue}
         */
        HasValue<String> getRemoteBranches();

        /**
         * Get local branches field.
         * 
         * @return {@link HasValue}
         */
        HasValue<String> getLocalBranches();

        /**
         * Get remove deleted refs field.
         * 
         * @return {@link HasValue}
         */
        HasValue<Boolean> getRemoveDeletedRefs();

        /**
         * Set values of remote repository branches.
         * 
         * @param values values to set
         */
        void setRemoteBranches(String[] values);

        /**
         * Set values of local repository branches.
         * 
         * @param values values to set
         */
        void setLocalBranches(String[] values);

        /**
         * Change the enable state of the fecth button.
         * 
         * @param enable enable state
         */
        void enableFetchButton(boolean enable);

        /**
         * Set values of remote repositories.
         * 
         * @param values values to set
         */
        void setRemoteValues(LinkedHashMap<String, String> values);

        /**
         * Get remote repository display value.
         * 
         * @return String
         */
        String getRemoteDisplayValue();
    }

    /** Presenter's display. */
    private Display display;

    /**
     *
     */
    public FetchPresenter() {
        IDE.addHandler(FetchEvent.TYPE, this);
    }

    /**
     * Bind display with presenter.
     * 
     * @param d
     */
    public void bindDisplay(Display d) {
        this.display = d;

        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getFetchButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doFetch();
            }
        });

        display.getRemoteName().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.setRemoteBranches(getRemoteBranchesNamesToDisplay(display.getRemoteDisplayValue()));
            }
        });

        display.getRemoteBranches().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                boolean empty = (event.getValue() == null || event.getValue().length() <= 0);
                display.enableFetchButton(!empty);
            }
        });
    }

    /** @see org.exoplatform.ide.git.client.fetch.FetchHandler#onFetch(org.exoplatform.ide.git.client.fetch.FetchEvent) */
    @Override
    public void onFetch(FetchEvent event) {
        if (makeSelectionCheck()) {
            // String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
            // getRemotes(projectId);
            getRemotes(getSelectedProject().getId());
        }
    }

    /** Perform fetch from remote repository (sends request over WebSocket or HTTP). */
    private void doFetch() {
        final String remoteUrl = display.getRemoteName().getValue();
        String remoteName = display.getRemoteDisplayValue();
        boolean removeDeletedRefs = display.getRemoveDeletedRefs().getValue();

        // ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
        ProjectModel project = getSelectedProject();

        try {
            GitClientService.getInstance().fetchWS(vfs.getId(), project, remoteName, getRefs(), removeDeletedRefs,
                                                   new RequestCallback<String>() {
                                                       @Override
                                                       protected void onSuccess(String result) {
                                                           IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.fetchSuccess(remoteUrl),
                                                                                         Type.GIT));
                                                       }

                                                       @Override
                                                       protected void onFailure(Throwable exception) {
                                                           handleError(exception, remoteUrl);
                                                       }
                                                   });
            IDE.getInstance().closeView(display.asView().getId());
        } catch (WebSocketException e) {
            doFetchREST(project, remoteName, removeDeletedRefs, remoteUrl);
        }
    }

    /** Perform fetch from remote repository (sends request over HTTP). */
    private void doFetchREST(ProjectModel project, String remoteName, boolean removeDeletedRefs, final String remoteUrl) {
        try {
            GitClientService.getInstance().fetch(vfs.getId(), project, remoteName, getRefs(), removeDeletedRefs,
                                                 new AsyncRequestCallback<String>() {
                                                     @Override
                                                     protected void onSuccess(String result) {
                                                         IDE.fireEvent(
                                                            new OutputEvent(GitExtension.MESSAGES.fetchSuccess(remoteUrl), Type.GIT));
                                                     }

                                                     @Override
                                                     protected void onFailure(Throwable exception) {
                                                         handleError(exception, remoteUrl);
                                                     }
                                                 });
        } catch (RequestException e) {
            handleError(e, remoteUrl);
        }
        IDE.getInstance().closeView(display.asView().getId());
    }

    /**
     * Returns list of refs to fetch.
     * 
     * @return list of refs to fetch
     */
    private String[] getRefs() {
        String localBranch = display.getLocalBranches().getValue();
        String remoteBranch = display.getRemoteBranches().getValue();
        String remoteName = display.getRemoteDisplayValue();
        String refs =
                      (localBranch == null || localBranch.length() == 0) ? remoteBranch : "refs/heads/" + remoteBranch + ":"
                                                                                          + "refs/remotes/" + remoteName + "/"
                                                                                          + remoteBranch;
        return new String[]{refs};
    }

    private void handleError(Throwable t, String remoteUrl) {
        String errorMessage = (t.getMessage() != null) ? t.getMessage() : GitExtension.MESSAGES.fetchFail(remoteUrl);
        IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
    }

    /** @see org.exoplatform.ide.git.client.remote.HasBranchesPresenter#onRemotesReceived(java.util.List) */
    @Override
    public void onRemotesReceived(List<Remote> remotes) {
        Display d = GWT.create(Display.class);
        IDE.getInstance().openView(d.asView());
        bindDisplay(d);
        display.enableFetchButton(false);

        LinkedHashMap<String, String> remoteValues = new LinkedHashMap<String, String>();
        for (Remote remote : remotes) {
            remoteValues.put(remote.getUrl(), remote.getName());
        }

        display.setRemoteValues(remoteValues);

        // String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        String projectId = getSelectedProject().getId();

        getBranches(projectId, BranchListRequest.LIST_REMOTE);
        getBranches(projectId, BranchListRequest.LIST_LOCAL);
    }

    /** @see org.exoplatform.ide.git.client.remote.HasBranchesPresenter#setRemoteBranches(java.util.List) */
    @Override
    protected void setRemoteBranches(List<Branch> result) {
        display.setRemoteBranches(getRemoteBranchesNamesToDisplay(display.getRemoteDisplayValue()));
    }

    /** @see org.exoplatform.ide.git.client.remote.HasBranchesPresenter#setLocalBranches(java.lang.String[]) */
    @Override
    protected void setLocalBranches(List<Branch> branches) {
        String[] values = new String[branches.size()];
        for (int i = 0; i < branches.size(); i++) {
            values[i] = branches.get(i).getDisplayName();
        }
        display.setLocalBranches(values);
    }
}
