/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.appfog.client.rename;

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
import org.exoplatform.ide.extension.appfog.client.AppfogAsyncRequestCallback;
import org.exoplatform.ide.extension.appfog.client.AppfogClientService;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;
import org.exoplatform.ide.git.client.GitPresenter;

/**
 * Presenter for rename operation with application.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
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
        public void onLoggedIn() {
            getApplicationInfo();
        }
    };

    private void getApplicationInfo() {
//      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        String projectId = getSelectedProject().getId();

        try {
            AutoBean<AppfogApplication> appfogApplication =
                    AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();

            AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
                    new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);

            AppfogClientService.getInstance().getApplicationInfo(vfs.getId(), projectId, null, null,
                                                                 new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller,
                                                                                                                   appInfoLoggedInHandler,
                                                                                                                   null) {
                                                                     @Override
                                                                     protected void onSuccess(AppfogApplication result) {
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
        public void onLoggedIn() {
            renameApplication();
        }
    };

    private void renameApplication() {
        final String newName = display.getRenameField().getValue();

//      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        String projectId = getSelectedProject().getId();

        try {
            AppfogClientService.getInstance().renameApplication(vfs.getId(), projectId, applicationName, null,
                                                                newName,
                                                                new AppfogAsyncRequestCallback<String>(null, renameAppLoggedInHandler,
                                                                                                       null) {
                                                                    @Override
                                                                    protected void onSuccess(String result) {
                                                                        closeView();
                                                                        IDE.fireEvent(new OutputEvent(AppfogExtension.LOCALIZATION_CONSTANT




                                                                                                                     .renameApplicationSuccess(


                                                                                                                             applicationName,
                                                                                                                             newName)));
                                                                    }
                                                                });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.rename.RenameApplicationHandler#onRenameApplication(org.exoplatform.ide.extension.cloudfoundry.client.rename.RenameApplicationEvent) */
    @Override
    public void onRenameApplication(RenameApplicationEvent event) {
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
