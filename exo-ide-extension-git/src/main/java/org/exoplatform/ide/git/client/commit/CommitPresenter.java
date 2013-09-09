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
package org.exoplatform.ide.git.client.commit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.marshaller.RevisionUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.RevisionUnmarshallerWS;
import org.exoplatform.ide.git.shared.Revision;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.Date;

/**
 * Presenter for commit view. The view must implement {@link CommitPresenter.Display} interface and pointed in Views.gwt.xml file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 31, 2011 10:02:25 AM anya $
 */
public class CommitPresenter extends GitPresenter implements CommitHandler {

    public interface Display extends IsView {
        /**
         * Get commit button handler.
         * 
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getCommitButton();

        /**
         * Get cancel button handler.
         * 
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getCancelButton();

        /**
         * Get message field value.
         * 
         * @return {@link HasValue}
         */
        HasValue<String> getMessage();

        /**
         * Change the enable state of the commit button.
         * 
         * @param enable enabled or not
         */
        void enableCommitButton(boolean enable);

        /** Give focus to message field. */
        void focusInMessageField();

        /**
         * Get all field.
         * 
         * @return {@link HasValue}
         */
        HasValue<Boolean> getAllField();

        /**
         * Get amend field.
         * 
         * @return {@link HasValue}
         */
        HasValue<Boolean> getAmendField();
    }

    /** Display. */
    private Display display;

    /**
     *
     */
    public CommitPresenter() {
        IDE.addHandler(CommitEvent.TYPE, this);
    }

    /**
     * Bind display(view) with presenter.
     * 
     * @param d display
     */
    public void bindDisplay(Display d) {
        this.display = d;

        display.getCommitButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doCommit();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getMessage().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                boolean notEmpty = (event.getValue() != null && event.getValue().length() > 0);
                display.enableCommitButton(notEmpty);
            }
        });
    }

    /** @see org.exoplatform.ide.git.client.commit.CommitHandler#onCommit(org.exoplatform.ide.git.client.commit.CommitEvent) */
    @Override
    public void onCommit(CommitEvent event) {
        if (makeSelectionCheck()) {
            Display d = GWT.create(Display.class);
            IDE.getInstance().openView(d.asView());
            bindDisplay(d);
            // Commit button is disabled, because message is empty:
            display.enableCommitButton(false);
            display.focusInMessageField();
        }
    }

    /** Perform the commit to repository and process the response (sends request over WebSocket or HTTP). */
    private void doCommit() {
        ProjectModel project = getSelectedProject();
        String message = display.getMessage().getValue();
        boolean all = display.getAllField().getValue();
        boolean amend = display.getAmendField().getValue();

        try {
            GitClientService.getInstance().commitWS(vfs.getId(),
                                                    project,
                                                    message,
                                                    all,
                                                    amend,
                                                    new RequestCallback<Revision>(
                                                                                  new RevisionUnmarshallerWS(new Revision(null, message, 0,
                                                                                                                          null))) {
                                                        @Override
                                                        protected void onSuccess(Revision result) {
                                                            if (!result.isFake()) {
                                                                onCommitSuccess(result);
                                                            } else {
                                                                IDE.fireEvent(new OutputEvent(result.getMessage(), Type.GIT));
                                                            }
                                                        }

                                                        @Override
                                                        protected void onFailure(Throwable exception) {
                                                            handleError(exception);
                                                        }
                                                    });
            IDE.getInstance().closeView(display.asView().getId());
        } catch (WebSocketException e) {
            doCommitREST(project, message, all, amend);
        }
    }

    /** Perform the commit to repository and process the response (sends request over HTTP). */
    private void doCommitREST(ProjectModel project, String message, boolean all, boolean amend) {
        try {
            GitClientService.getInstance().commit(vfs.getId(),
                                                  project,
                                                  message,
                                                  all,
                                                  amend,
                                                  new AsyncRequestCallback<Revision>(
                                                                                     new RevisionUnmarshaller(new Revision(null, message,
                                                                                                                           0, null))) {
                                                      @Override
                                                      protected void onSuccess(Revision result) {
                                                          if (!result.isFake()) {
                                                              onCommitSuccess(result);
                                                          } else {
                                                              IDE.fireEvent(new OutputEvent(result.getMessage(), Type.GIT));
                                                          }
                                                      }

                                                      @Override
                                                      protected void onFailure(Throwable exception) {
                                                          handleError(exception);
                                                      }
                                                  });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
        IDE.getInstance().closeView(display.asView().getId());
    }

    /**
     * Performs action when commit is successfully completed.
     * 
     * @param revision a {@link Revision}
     */
    private void onCommitSuccess(Revision revision) {
        DateTimeFormat formatter = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
        String date = formatter.format(new Date(revision.getCommitTime()));
        String message = GitExtension.MESSAGES.commitMessage(revision.getId(), date);
        message +=
                   (revision.getCommitter() != null && revision.getCommitter().getName() != null && revision.getCommitter()
                                                                                                            .getName().length() > 0)
                       ? " " +

                         GitExtension
                         .MESSAGES
                                  .commitUser(
                                  revision.getCommitter()
                                          .getName())
                       : "";
        IDE.fireEvent(new OutputEvent(message, Type.GIT));
        IDE.fireEvent(new TreeRefreshedEvent(getSelectedProject()));
    }

    private void handleError(Throwable exception) {
        String errorMessage =
                              (exception.getMessage() != null && exception.getMessage().length() > 0) ? exception.getMessage()
                                  : GitExtension.MESSAGES.commitFailed();
        IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
    }

}
