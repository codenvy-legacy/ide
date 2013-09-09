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
package org.exoplatform.ide.git.client.reset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.marshaller.LogResponse;
import org.exoplatform.ide.git.client.marshaller.LogResponseUnmarshaller;
import org.exoplatform.ide.git.shared.ResetRequest.ResetType;
import org.exoplatform.ide.git.shared.Revision;

/**
 * Presenter for view for reseting head to commit. The view must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 15, 2011 10:31:25 AM anya $
 */
public class ResetToCommitPresenter extends GitPresenter implements ResetToCommitHandler {

    interface Display extends IsView {
        /**
         * Get reset button click handler.
         * 
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getResetButton();

        /**
         * Get cancel button click handler.
         * 
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getCancelButton();

        /**
         * Get grid with revisions.
         * 
         * @return {@link ListGrid}
         */
        ListGridItem<Revision> getRevisionGrid();

        /**
         * Get selected revision in revision grid.
         * 
         * @return {@link Revision} selected revision
         */
        Revision getSelectedRevision();

        /**
         * Get the soft mode radio field.
         * 
         * @return {@link HasValue}
         */
        HasValue<Boolean> getSoftMode();

        /**
         * Get the mix mode radio field.
         * 
         * @return {@link HasValue}
         */
        HasValue<Boolean> getMixMode();

        /**
         * Get the hard mode radio field.
         * 
         * @return {@link HasValue}
         */
        HasValue<Boolean> getHardMode();

        /**
         * Set the enabled state of the reset button.
         * 
         * @param enabled enabled state
         */
        void enableResetButon(boolean enabled);

        /**
         * Set the enabled state of the reset button.
         * 
         * @param enabled enabled state
         */
        HasValue<Boolean> getKeepMode();

        /**
         * Set the enabled state of the reset button.
         * 
         * @param enabled enabled state
         */
        HasValue<Boolean> getMergeMode();
    }

    /** Presenter' display. */
    private Display display;

    /**
     * @param eventBus event handlers
     */
    public ResetToCommitPresenter() {
        IDE.addHandler(ResetToCommitEvent.TYPE, this);
    }

    public void bindDisplay(Display d) {
        this.display = d;

        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getResetButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doReset();
            }
        });

        display.getRevisionGrid().addSelectionHandler(new SelectionHandler<Revision>() {

            @Override
            public void onSelection(SelectionEvent<Revision> event) {
                boolean enable = event.getSelectedItem() != null;
                display.enableResetButon(enable);
            }
        });
    }

    /**
     * @see org.exoplatform.ide.git.client.reset.ResetToCommitHandler#onResetToCommit(org.exoplatform.ide.git.client.reset
     *      .ResetToCommitEvent)
     */
    @Override
    public void onResetToCommit(ResetToCommitEvent event) {
        if (makeSelectionCheck()) {
            getCommits();
        }
    }

    private void getCommits() {
        String projectId = getSelectedProject().getId();

        try {
            GitClientService.getInstance()
                            .log(vfs.getId(), projectId, false,
                                 new AsyncRequestCallback<LogResponse>(
                                                                       new LogResponseUnmarshaller(new LogResponse(), false)) {

                                     @Override
                                     protected void onSuccess(LogResponse result) {
                                         Display d = GWT.create(Display.class);
                                         IDE.getInstance().openView(d.asView());
                                         bindDisplay(d);

                                         display.getRevisionGrid().setValue(result.getCommits());
                                         display.getMixMode().setValue(true);
                                     }

                                     @Override
                                     protected void onFailure(Throwable exception) {
                                         String errorMessage =
                                                               (exception.getMessage() != null) ? exception.getMessage()
                                                                   : GitExtension.MESSAGES.logFailed();
                                         IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                                     }
                                 });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.logFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
        }
    }

    /** Reset the head to selected revision with chosen mode (hard, soft or mixed). */
    private void doReset() {
        Revision revision = display.getSelectedRevision();
        ResetType type = display.getMixMode().getValue() ? ResetType.MIXED : null;
        type = (type == null && display.getSoftMode().getValue()) ? ResetType.SOFT : type;
        type = (type == null && display.getHardMode().getValue()) ? ResetType.HARD : type;
        type = (type == null && display.getKeepMode().getValue()) ? ResetType.KEEP : type;
        type = (type == null && display.getMergeMode().getValue()) ? ResetType.MERGE : type;

        // Needed for onSuccess callback to detect which type of event to throw
        final ResetType resetType = type;

        String projectId = getSelectedProject().getId();

        try {
            GitClientService.getInstance().reset(vfs.getId(), projectId, revision.getId(), type,
                                                 new AsyncRequestCallback<String>() {

                                                     @Override
                                                     protected void onSuccess(String result) {
                                                         IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.resetSuccessfully(), Type.GIT));
                                                         if (ResetType.HARD.equals(resetType)) {
                                                             IDE.fireEvent(new RefreshBrowserEvent());
                                                         }
                                                         else {
                                                             IDE.fireEvent(new TreeRefreshedEvent(getSelectedProject()));
                                                         }
                                                         IDE.getInstance().closeView(display.asView().getId());
                                                     }

                                                     @Override
                                                     protected void onFailure(Throwable exception) {
                                                         String errorMessage = (exception.getMessage() != null) ? exception.getMessage()
                                                             : GitExtension.MESSAGES.resetFail();
                                                         IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                                                     }
                                                 });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.resetFail();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
        }
    }
}
