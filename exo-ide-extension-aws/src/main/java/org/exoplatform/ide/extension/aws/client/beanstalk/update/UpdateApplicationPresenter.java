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
package org.exoplatform.ide.extension.aws.client.beanstalk.update;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.UpdateApplicationRequest;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 19, 2012 4:45:58 PM anya $
 */
public class UpdateApplicationPresenter implements UpdateApplicationHandler, ViewClosedHandler {
    interface Display extends IsView {
        HasClickHandlers getUpdateButton();

        HasClickHandlers getCancelButton();

        HasValue<String> getDescriptionField();

        void enableUpdateButton(boolean enabled);

        void focusInDescriptionField();
    }

    private Display display;

    private String vfsId;

    private String projectId;

    private ApplicationUpdatedHandler applicationUpdatedHandler;

    private ApplicationInfo applicationInfo;

    public UpdateApplicationPresenter() {
        IDE.addHandler(UpdateApplicationEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.update.UpdateApplicationHandler#onUpdateApplication(org.exoplatform.ide
     * .extension.aws.client.beanstalk.update.UpdateApplicationEvent) */
    @Override
    public void onUpdateApplication(UpdateApplicationEvent event) {
        this.vfsId = event.getVfsId();
        this.projectId = event.getProjectId();
        this.applicationInfo = event.getApplicationInfo();
        this.applicationUpdatedHandler = event.getApplicationUpdatedHandler();

        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }

        display.getDescriptionField().setValue(
                applicationInfo.getDescription() != null ? applicationInfo.getDescription() : "");
        display.focusInDescriptionField();
        display.enableUpdateButton(false);
    }

    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getDescriptionField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableUpdateButton(event.getValue() != null
                                           && !event.getValue().equals(applicationInfo.getDescription()));
            }
        });

        display.getUpdateButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doUpdate();
            }
        });
    }

    public void doUpdate() {
        UpdateApplicationRequest updateApplicationRequest =
                AWSExtension.AUTO_BEAN_FACTORY.updateApplicationRequest().as();
        updateApplicationRequest.setApplicationName(applicationInfo.getName());
        updateApplicationRequest.setDescription(display.getDescriptionField().getValue());

        AutoBean<ApplicationInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.applicationInfo();

        try {
            BeanstalkClientService.getInstance().updateApplication(
                    vfsId,
                    projectId,
                    updateApplicationRequest,
                    new AwsAsyncRequestCallback<ApplicationInfo>(new AutoBeanUnmarshaller<ApplicationInfo>(autoBean),
                                                                 new LoggedInHandler() {

                                                                     @Override
                                                                     public void onLoggedIn() {
                                                                         doUpdate();
                                                                     }
                                                                 }, null) {

                        @Override
                        protected void processFail(Throwable exception) {
                            String message =
                                    AWSExtension.LOCALIZATION_CONSTANT.updateApplicationFailed(applicationInfo.getName());
                            if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null) {
                                message += "<br>" + ((ServerException)exception).getMessage();
                            }
                            IDE.fireEvent(new OutputEvent(message, Type.ERROR));
                        }

                        @Override
                        protected void onSuccess(ApplicationInfo result) {
                            IDE.getInstance().closeView(display.asView().getId());
                            if (applicationUpdatedHandler != null) {
                                applicationUpdatedHandler.onApplicationUpdated(result);
                            }
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
