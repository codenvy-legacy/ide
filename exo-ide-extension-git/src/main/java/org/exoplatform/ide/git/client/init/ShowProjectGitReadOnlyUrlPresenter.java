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
package org.exoplatform.ide.git.client.init;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;

/**
 * Presenter for Init Repository view.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 24, 2011 9:07:58 AM anya $
 */
public class ShowProjectGitReadOnlyUrlPresenter extends GitPresenter implements ShowProjectGitReadOnlyUrlHandler {

    public interface Display extends IsView {
        /**
         * Get's Git URl field field.
         * 
         * @return {@link HasValue}
         */
        HasValue<String> getGitUrl();

        /**
         * Gets cancel button.
         * 
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getCloseButton();
    }

    private Display display;

    /** @param eventBus */
    public ShowProjectGitReadOnlyUrlPresenter() {
        IDE.addHandler(ShowProjectGitReadOnlyUrlEvent.TYPE, this);
    }

    public void bindDisplay(Display d) {
        this.display = d;

        display.getCloseButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }

    @Override
    public void onShowGitUrl(ShowProjectGitReadOnlyUrlEvent event) {
        Display d = GWT.create(Display.class);
        IDE.getInstance().openView((View)d);
        bindDisplay(d);
        // String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        String projectId = getSelectedProject().getId();
        try {
            GitClientService.getInstance()
                            .getGitReadOnlyUrl(
                                               vfs.getId(),
                                               projectId,
                                               new AsyncRequestCallback<StringBuilder>(
                                                                                       new StringUnmarshaller(new StringBuilder())) {

                                                   @Override
                                                   protected void onSuccess(StringBuilder result) {
                                                       display.getGitUrl().setValue(result.toString());
                                                   }

                                                   @Override
                                                   protected void onFailure(Throwable exception) {
                                                       String errorMessage =
                                                                             (exception.getMessage() != null && exception.getMessage()
                                                                                                                         .length() > 0)
                                                                                 ? exception.getMessage()
                                                                                 : GitExtension.MESSAGES
                                                                                                        .initFailed();
                                                       IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                                                   }
                                               });
        } catch (RequestException e) {
            String errorMessage =
                                  (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage()
                                      : GitExtension.MESSAGES
                                                             .initFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
        }
    }

    private class StringUnmarshaller implements
                                    Unmarshallable<StringBuilder> {

        protected StringBuilder builder;

        /** @param callback */
        public StringUnmarshaller(StringBuilder builder) {
            this.builder = builder;
        }

        /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
        @Override
        public void unmarshal(Response response) {
            builder.append(response.getText());
        }

        @Override
        public StringBuilder getPayload() {
            return builder;
        }
    }
}
