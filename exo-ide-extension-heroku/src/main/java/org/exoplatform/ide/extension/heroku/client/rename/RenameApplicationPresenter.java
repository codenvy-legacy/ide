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
package org.exoplatform.ide.extension.heroku.client.rename;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Presenter for rename application on Heroku. The view must be pointed in Views.gwt.xml.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 2, 2011 11:54:59 AM anya $
 */
public class RenameApplicationPresenter extends GitPresenter implements RenameApplicationHandler, ViewClosedHandler,
                                                                        LoggedInHandler {

    interface Display extends IsView {
        /**
         * Get rename text field.
         *
         * @return {@link TextFieldItem}
         */
        TextFieldItem getRenameField();

        /**
         * Get rename button's click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getRenameButton();

        /**
         * Get cancel button's click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getCancelButton();

        /** Select value in rename field. */
        void selectValueInRenameField();

        /**
         * Change the enable state of the rename button.
         *
         * @param isEnabled
         */
        void enableRenameButton(boolean isEnabled);
    }

    private Display display;

    /** Heroku application's name. */
    private String applicationName;

    private static final String NAME_PROPERTY = "name";

    /**
     * @param eventBus
     *         events handler
     */
    public RenameApplicationPresenter() {
        IDE.addHandler(RenameApplicationEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** Bind display with presenter. */
    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getRenameButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doRenameApplication();
            }
        });

        display.getRenameField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                boolean enable =
                        (applicationName != null && !event.getValue().equals(applicationName) && event.getValue() != null && !event
                                .getValue().isEmpty());
                display.enableRenameButton(enable);
            }
        });

        display.getRenameField().addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == 13) {
                    doRenameApplication();
                }
            }
        });
    }

    /** @see org.exoplatform.ide.extension.heroku.client.rename.RenameApplicationHandler#onRenameApplication(org.exoplatform.ide
     * .extension.heroku.client.rename.RenameApplicationEvent) */
    @Override
    public void onRenameApplication(RenameApplicationEvent event) {
        applicationName = event.getApplication();
        if (applicationName != null && !applicationName.isEmpty()) {
            if (display == null) {
                display = GWT.create(Display.class);
                bindDisplay();
                IDE.getInstance().openView(display.asView());
            }
            display.getRenameField().setValue(applicationName);
            display.selectValueInRenameField();
            display.enableRenameButton(false);
            return;
        }

        if (makeSelectionCheck()) {
            getApplicationInfo();
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

    /** Get information about application. */
    protected void getApplicationInfo() {
//      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        String projectId = getSelectedProject().getId();

        try {
            HerokuClientService.getInstance().getApplicationInfo(null, vfs.getId(), projectId, false,
                                                                 new HerokuAsyncRequestCallback(this) {
                                                                     @Override
                                                                     protected void onSuccess(List<Property> properties) {
                                                                         for (Property property : properties) {
                                                                             if (NAME_PROPERTY.equals(property.getName())) {
                                                                                 applicationName = property.getValue();
                                                                                 break;
                                                                             }
                                                                         }
                                                                         if (display == null) {
                                                                             display = GWT.create(Display.class);
                                                                             bindDisplay();
                                                                             IDE.getInstance().openView(display.asView());
                                                                             display.getRenameField().setValue(applicationName);
                                                                             display.selectValueInRenameField();
                                                                             display.enableRenameButton(false);
                                                                         }
                                                                     }
                                                                 });
        } catch (RequestException e) {
        }
    }

    /** @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client
     * .login.LoggedInEvent) */
    @Override
    public void onLoggedIn(LoggedInEvent event) {
        IDE.removeHandler(LoggedInEvent.TYPE, this);
        if (!event.isFailed()) {
            getApplicationInfo();
        }
    }

    /** Perform renaming the application. */
    public void doRenameApplication() {
        final String newName = display.getRenameField().getValue();
        final String projectId = detectProjectId();

        try {
            HerokuClientService.getInstance().renameApplication(applicationName, vfs.getId(), projectId, newName,
                                                                new HerokuAsyncRequestCallback(this) {
                                                                    @Override
                                                                    protected void onSuccess(List<Property> properties) {
                                                                        IDE.fireEvent(new OutputEvent(HerokuExtension.LOCALIZATION_CONSTANT






                                                                                                                     .renameApplicationSuccess(


                                                                                                                             applicationName,
                                                                                                                             newName),
                                                                                                      Type.INFO));
                                                                        IDE.getInstance().closeView(display.asView().getId());
                                                                        IDE.fireEvent(
                                                                                new ApplicationRenamedEvent(properties, applicationName));
                                                                    }
                                                                });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Detects project's id by Heroku application name, if opened project is the following Heroku application.
     *
     * @return {@link String} project's id or <code>null</code> if not found
     */
    private String detectProjectId() {
        String projectId = null;

//      if (selectedItems.size() > 0 && selectedItems.get(0) instanceof ItemContext)
//      {
//         ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
//         if (project != null && project.getPropertyValue("heroku-application") != null
//            && applicationName.equals((String)project.getPropertyValue("heroku-application")))
//         {
//            projectId = project.getId();
//         }
//      }

        if (selectedItem != null) {
            ProjectModel project = getSelectedProject();
            if (project != null && project.getPropertyValue("heroku-application") != null
                && applicationName.equals((String)project.getPropertyValue("heroku-application"))) {
                projectId = project.getId();
            }
        }

        return projectId;
    }
}
