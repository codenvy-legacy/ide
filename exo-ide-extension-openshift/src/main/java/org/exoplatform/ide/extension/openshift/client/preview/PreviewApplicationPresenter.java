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
package org.exoplatform.ide.extension.openshift.client.preview;

import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Image;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.PreviewForm;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientBundle;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownEvent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.git.client.GitPresenter;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PreviewApplicationPresenter extends GitPresenter implements PreviewApplicationHandler, ViewClosedHandler,
                                                                         EditorActiveFileChangedHandler, LoggedInHandler {

    private PreviewForm previewForm;

    private boolean previewOpened = false;

    public PreviewApplicationPresenter() {
        IDE.addHandler(PreviewApplicationEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    @Override
    public void onPreviewApplication(PreviewApplicationEvent event) {
        if (makeSelectionCheck()) {
            getApplicationInfo();
        }
    }

    private void getApplicationInfo() {
//      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        String projectId = getSelectedProject().getId();

        try {
            AutoBean<AppInfo> appInfo = OpenShiftExtension.AUTO_BEAN_FACTORY.appInfo();
            AutoBeanUnmarshaller<AppInfo> unmarshaller = new AutoBeanUnmarshaller<AppInfo>(appInfo);
            OpenShiftClientService.getInstance().getApplicationInfo(null, vfs.getId(), projectId,
                                                                    new AsyncRequestCallback<AppInfo>(unmarshaller) {

                                                                        @Override
                                                                        protected void onSuccess(AppInfo result) {
                                                                            applicationInfoReceived(result);
                                                                        }

                                                                        /**
                                                                         * @see org.exoplatform.gwtframework.commons.rest
                                                                         * .AsyncRequestCallback#onFailure(java.lang.Throwable)
                                                                         */
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

    private void addLoggedInHandler() {
        IDE.addHandler(LoggedInEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.openshift
     * .client.login.LoggedInEvent) */
    @Override
    public void onLoggedIn(LoggedInEvent event) {
        IDE.removeHandler(LoggedInEvent.TYPE, this);
        if (!event.isFailed()) {
            getApplicationInfo();
        }
    }

    private void applicationInfoReceived(AppInfo result) {
        String href = result.getPublicUrl();

        if (previewForm == null) {
            previewForm = new PreviewForm();
            previewForm.setIcon(new Image(OpenShiftClientBundle.INSTANCE.previewControl()));
        }
        previewForm.showPreview(href);

        if (previewOpened) {
            previewForm.setViewVisible();
        } else {
            IDE.getInstance().openView(previewForm);
        }
        previewOpened = true;
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (previewForm == null)
            return;
        if (event.getView().getId().equals(previewForm.getId())) {
            previewOpened = false;
            previewForm = null;
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        if (previewOpened) {
            IDE.getInstance().closeView(PreviewForm.ID);
            previewOpened = false;
        }
    }

}
