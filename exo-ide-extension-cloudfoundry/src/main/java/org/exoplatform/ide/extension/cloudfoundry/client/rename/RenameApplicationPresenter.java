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
package org.exoplatform.ide.extension.cloudfoundry.client.rename;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.git.client.GitPresenter;

/**
 * Presenter for rename operation with application.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: RenameApplicationPresenter.java Jul 15, 2011 11:32:02 AM vereshchaka $
 */
public class RenameApplicationPresenter extends GitPresenter implements RenameApplicationHandler, ViewClosedHandler {

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

    /** The name of application. */
    private String applicationName;

    private PAAS_PROVIDER paasProvider;

    /** The new name of application. */
    public RenameApplicationPresenter() {
        IDE.addHandler(RenameApplicationEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                closeView();
            }
        });

        display.getRenameButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                renameApplication();
            }
        });

        display.getRenameField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                String newName = event.getValue();
                boolean enable = !applicationName.equals(newName) && newName != null && !newName.isEmpty();
                display.enableRenameButton(enable);
            }
        });

        display.getRenameField().addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == 13) {
                    renameApplication();
                }
            }
        });
    }

    private LoggedInHandler appInfoLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn(String server) {
            getApplicationInfo();
        }
    };

    private void getApplicationInfo() {
//      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        String projectId = getSelectedProject().getId();

        try {
            AutoBean<CloudFoundryApplication> cloudFoundryApplication =
                    CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();

            AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
                    new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

            CloudFoundryClientService.getInstance().getApplicationInfo(vfs.getId(), projectId, null, null, new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(
                                                                               unmarshaller, appInfoLoggedInHandler, null, paasProvider) {
                                                                           @Override
                                                                           protected void onSuccess(CloudFoundryApplication result) {
                                                                               applicationName = result.getName();
                                                                               showRenameDialog();
                                                                           }
                                                                       });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void showRenameDialog() {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
            display.getRenameField().setValue(applicationName);
            display.selectValueInRenameField();
            display.enableRenameButton(false);
        }
    }

    private LoggedInHandler renameAppLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn(String server) {
            renameApplication();
        }
    };

    private void renameApplication() {
        final String newName = display.getRenameField().getValue();
//      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        String projectId = getSelectedProject().getId();

        try {
            CloudFoundryClientService.getInstance().renameApplication(vfs.getId(), projectId, applicationName, null,
                                                                      newName, new CloudFoundryAsyncRequestCallback<String>(null,
                                                                                                                            renameAppLoggedInHandler,
                                                                                                                            null, paasProvider) {
                @Override
                protected void onSuccess(String result) {
                    closeView();
                    IDE.fireEvent(new OutputEvent(CloudFoundryExtension.LOCALIZATION_CONSTANT.renameApplicationSuccess(
                            applicationName, newName)));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.rename.RenameApplicationHandler#onRenameApplication(org.exoplatform.ide.extension.cloudfoundry.client.rename.RenameApplicationEvent) */
    @Override
    public void onRenameApplication(RenameApplicationEvent event) {
        paasProvider = event.getPaasProvider();
        if (makeSelectionCheck()) {
            getApplicationInfo();
        }
    }

    private void closeView() {
        IDE.getInstance().closeView(display.asView().getId());
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

}
