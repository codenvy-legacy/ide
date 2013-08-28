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
package org.exoplatform.ide.extension.heroku.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.HerokuRESTfulRequestCallback;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Presenter for created application view. The view must be pointed in Views.gwt.xml.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 26, 2011 2:37:21 PM anya $
 */
public class CreateApplicationPresenter extends GitPresenter implements ViewClosedHandler, CreateApplicationHandler,
                                                                        LoggedInHandler {
    interface Display extends IsView {
        /**
         * Get create button's click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getCreateButton();

        /**
         * Get cancel button's click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getCancelButton();

        /**
         * Get application name field.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getApplicationNameField();

        /**
         * Get remote repository name field.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getRemoteNameField();

        /**
         * Get Git work directory location field.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getWorkDirLocationField();

        /**
         * Change the enable state of the create button.
         *
         * @param enable
         */
        void enableCreateButton(boolean enable);

        /** Give focus to application name field. */
        void focusInApplicationNameField();
    }

    private Display display;

//   private ProjectModel project;

    private String applicationName;

    private String remoteName;

    public CreateApplicationPresenter() {
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(CreateApplicationEvent.TYPE, this);
    }

    /** Bind display with presenter. */
    public void bindDisplay() {
        display.getCreateButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                applicationName = display.getApplicationNameField().getValue();
                remoteName = display.getRemoteNameField().getValue();
//            project = ((ItemContext)selectedItems.get(0)).getProject();
                IDE.getInstance().closeView(display.asView().getId());
                doCreateApplication();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }

    /** @see org.exoplatform.ide.extension.heroku.client.create.CreateApplicationHandler#onCreateApplication(org.exoplatform.ide.extension
     * .heroku.client.create.CreateApplicationEvent) */
    @Override
    public void onCreateApplication(CreateApplicationEvent event) {
        if (makeSelectionCheck()) {
//         String workdir = ((ItemContext)selectedItems.get(0)).getProject().getPath();
            ProjectModel project = getSelectedProject();
            String workdir = project.getPath();
            if (display == null) {
                display = GWT.create(Display.class);
                bindDisplay();
                IDE.getInstance().openView(display.asView());
                applicationName = null;
                remoteName = null;
                project = null;
                display.focusInApplicationNameField();
                display.getWorkDirLocationField().setValue(workdir);
            }
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** Perform creation of application on Heroku by sending request over WebSocket or HTTP. */
    protected void doCreateApplication() {
        try {
            final ProjectModel project = getSelectedProject();

            HerokuClientService.getInstance().createApplicationWS(applicationName, vfs.getId(), project.getId(),
                                                                  remoteName, new HerokuRESTfulRequestCallback(this) {
                @Override
                protected void onSuccess(List<Property> properties) {
                    IDE.fireEvent(new OutputEvent(formApplicationCreatedMessage(properties), Type.INFO));
                    IDE.fireEvent(new RefreshBrowserEvent(project));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    super.onFailure(exception);
                }
            });
        } catch (WebSocketException e) {
            doCreateApplicationREST();
        }
    }

    /** Perform creation of application on Heroku (sends request over HTTP). */
    protected void doCreateApplicationREST() {
        try {
            final ProjectModel project = getSelectedProject();

            HerokuClientService.getInstance().createApplication(applicationName, vfs.getId(), project.getId(), remoteName,
                                                                new HerokuAsyncRequestCallback(this) {
                                                                    @Override
                                                                    protected void onSuccess(List<Property> properties) {
                                                                        IDE.fireEvent(
                                                                                new OutputEvent(formApplicationCreatedMessage(properties),
                                                                                                Type.INFO));
                                                                        IDE.fireEvent(new RefreshBrowserEvent(project));
                                                                    }

                                                                    @Override
                                                                    protected void onFailure(Throwable exception) {
                                                                        super.onFailure(exception);
                                                                    }
                                                                });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Form the message about application creation to display in output.
     *
     * @param properties
     *         application's properties
     * @return {@link String}
     */
    public String formApplicationCreatedMessage(List<Property> properties) {
        if (properties == null) {
            return HerokuExtension.LOCALIZATION_CONSTANT.createApplicationSuccess("");
        }
        StringBuilder message = new StringBuilder("<br> [");
        for (Property property : properties) {
            if ("webUrl".equals(property.getName())) {
                message.append("<b>").append(property.getName()).append("</b>").append(" : ").append("<a href='")
                       .append(property.getValue()).append("' target='_blank'>").append(property.getValue()).append("</a>")
                       .append("<br>");
            } else {
                message.append("<b>").append(property.getName()).append("</b>").append(" : ").append(property.getValue())
                       .append("<br>");
            }
        }
        message.append("] ");
        return HerokuExtension.LOCALIZATION_CONSTANT.createApplicationSuccess(message.toString());
    }

    /** @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client
     * .login.LoggedInEvent) */
    @Override
    public void onLoggedIn(LoggedInEvent event) {
        IDE.removeHandler(LoggedInEvent.TYPE, this);
        if (!event.isFailed()) {
            doCreateApplication();
        }
    }
}
