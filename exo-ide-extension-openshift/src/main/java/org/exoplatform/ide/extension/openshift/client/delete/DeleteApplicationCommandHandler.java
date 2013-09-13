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
package org.exoplatform.ide.extension.openshift.client.delete;

import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownEvent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.git.client.GitPresenter;

/**
 * Presenter for deleting application. Following steps are done:<br>
 * <ol>
 * <li>Get location of the Git working directory.</li>
 * <li>Get application information by Git working directory.</li>
 * <li>Ask user to delete application (pointed by name).</li>
 * <li>Delete application by it's name.</li>
 * </ol>
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 9, 2011 12:27:01 PM anya $
 */
public class DeleteApplicationCommandHandler extends GitPresenter implements DeleteApplicationHandler, LoggedInHandler {

    /**
     *
     */
    public DeleteApplicationCommandHandler() {
        IDE.addHandler(DeleteApplicationEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.extension.openshift.client.delete.DeleteApplicationHandler#onDeleteApplication(org.exoplatform.ide
     * .extension.openshift.client.delete.DeleteApplicationEvent) */
    @Override
    public void onDeleteApplication(DeleteApplicationEvent event) {
        if (makeSelectionCheck()) {
            getApplicationsInfo();
        }
    }

    /** Get applications information. */
    protected void getApplicationsInfo() {
//      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        String projectId = getSelectedProject().getId();

        try {
            AutoBean<AppInfo> appInfo = OpenShiftExtension.AUTO_BEAN_FACTORY.appInfo();
            AutoBeanUnmarshaller<AppInfo> unmarshaller = new AutoBeanUnmarshaller<AppInfo>(appInfo);
            OpenShiftClientService.getInstance().getApplicationInfo(null, vfs.getId(), projectId,
                                                                    new AsyncRequestCallback<AppInfo>(unmarshaller) {

                                                                        @Override
                                                                        protected void onSuccess(AppInfo result) {
                                                                            askDeleteApplication(result.getName());
                                                                        }

                                                                        @Override
                                                                        protected void onFailure(Throwable exception) {
                                                                            if (exception instanceof ServerException) {
                                                                                ServerException serverException =
                                                                                        (ServerException)exception;
                                                                                if (HTTPStatus.OK == serverException.getHTTPStatus()
                                                                                    && "Authentication-required".equals(serverException
                                                                                                                                .getHeader(




















                                                                                                                                        HTTPHeader.JAXRS_BODY_PROVIDED))) {
                                                                                    addLoggedInHandler();
                                                                                    IDE.fireEvent(new LoginEvent());
                                                                                    return;
                                                                                }
                                                                            }
                                                                            IDE.fireEvent(new OpenShiftExceptionThrownEvent(exception,
                                                                                                                            OpenShiftExtension
                                                                                                                                    .LOCALIZATION_CONSTANT
                                                                                                                                    .getApplicationInfoFail()));
                                                                        }
                                                                    });
        } catch (RequestException e) {
            IDE.fireEvent(new OpenShiftExceptionThrownEvent(e, OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                 .getApplicationInfoFail()));
        }

    }

    /** Register {@link LoggedInHandler} handler. */
    protected void addLoggedInHandler() {
        IDE.addHandler(LoggedInEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.openshift
     * .client.login.LoggedInEvent) */
    @Override
    public void onLoggedIn(LoggedInEvent event) {
        IDE.removeHandler(LoggedInEvent.TYPE, this);
        if (!event.isFailed()) {
            getApplicationsInfo();
        }
    }

    /**
     * Confirm the deleting of the application on OpenShift.
     *
     * @param name
     *         application's name
     */
    protected void askDeleteApplication(final String name) {
        Dialogs.getInstance().ask(OpenShiftExtension.LOCALIZATION_CONSTANT.deleteApplicationTitle(),
                                  OpenShiftExtension.LOCALIZATION_CONSTANT.deleteApplication(name), new BooleanValueReceivedHandler() {

            @Override
            public void booleanValueReceived(Boolean value) {
                if (value != null && value) {
                    doDeleteApplication(name);
                }
            }
        });
    }

    /**
     * Perform deleting application on OpenShift.
     *
     * @param name
     *         application's name
     */
    protected void doDeleteApplication(final String name) {
//      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        final String projectId = getSelectedProject().getId();

        try {
            OpenShiftClientService.getInstance().destroyApplication(name, vfs.getId(), projectId,
                                                                    new AsyncRequestCallback<String>() {

                                                                        @Override
                                                                        protected void onSuccess(String result) {
                                                                            IDE.fireEvent(new OutputEvent(
                                                                                    OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                                      .deleteApplicationSuccess(name),
                                                                                    Type.INFO));
                                                                            IDE.fireEvent(
                                                                                    new ApplicationDeletedEvent(vfs.getId(), projectId));
//                  IDE.fireEvent(new RefreshBrowserEvent(((ItemContext)selectedItems.get(0)).getProject()));
                                                                            IDE.fireEvent(new RefreshBrowserEvent(getSelectedProject()));
                                                                        }

                                                                        /**
                                                                         * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
                                                                         */
                                                                        @Override
                                                                        protected void onFailure(Throwable exception) {
                                                                            IDE.fireEvent(new OpenShiftExceptionThrownEvent(exception,
                                                                                                                            OpenShiftExtension
                                                                                                                                    .LOCALIZATION_CONSTANT
                                                                                                                                    .deleteApplicationFail(
                                                                                                                                            name)));
                                                                        }
                                                                    });
        } catch (RequestException e) {
            IDE.fireEvent(new OpenShiftExceptionThrownEvent(e, OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                 .deleteApplicationFail(name)));
        }
    }
}
