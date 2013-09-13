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
package org.exoplatform.ide.extension.openshift.client.key;

import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownEvent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;

/**
 * Presenter for updating public key on OpenShift. First - get user's information to retrieve domain name, then update public SSH
 * key.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 21, 2011 12:36:44 PM anya $
 */
public class UpdatePublicKeyCommandHandler implements UpdatePublicKeyHandler, LoggedInHandler {

    private static UpdatePublicKeyCommandHandler instance;

    private UpdatePublicKeyCallback updatePublicKeyCallback;

    /**
     *
     */
    public UpdatePublicKeyCommandHandler() {
        instance = this;
        IDE.addHandler(UpdatePublicKeyEvent.TYPE, this);
    }

    public static UpdatePublicKeyCommandHandler getInstance() {
        return instance;
    }

    public void updatePublicKey(UpdatePublicKeyCallback updatePublicKeyCallback) {
        this.updatePublicKeyCallback = updatePublicKeyCallback;
        getUserInfo();
    }

    /** @see org.exoplatform.ide.extension.openshift.client.key.UpdatePublicKeyHandler#onUpdatePublicKey(org.exoplatform.ide.extension
     * .openshift.client.key.UpdatePublicKeyEvent) */
    @Override
    public void onUpdatePublicKey(UpdatePublicKeyEvent event) {
        getUserInfo();
    }

    /** Get user's information. */
    public void getUserInfo() {
        try {
            AutoBean<RHUserInfo> rhUserInfo = OpenShiftExtension.AUTO_BEAN_FACTORY.rhUserInfo();
            AutoBeanUnmarshaller<RHUserInfo> unmarhaller = new AutoBeanUnmarshaller<RHUserInfo>(rhUserInfo);
            OpenShiftClientService.getInstance().getUserInfo(false, new AsyncRequestCallback<RHUserInfo>(unmarhaller) {

                @Override
                protected void onSuccess(RHUserInfo result) {
                    doUpdatePublicKey(result.getNamespace());
                }

                /**
                 * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
                 */
                @Override
                protected void onFailure(Throwable exception) {
                    if (exception instanceof ServerException) {
                        ServerException serverException = (ServerException)exception;
                        if (HTTPStatus.OK == serverException.getHTTPStatus()
                            && "Authentication-required".equals(serverException.getHeader(HTTPHeader.JAXRS_BODY_PROVIDED))) {
                            addLoggedInHandler();
                            IDE.fireEvent(new LoginEvent());
                            return;
                        }
                    }

                    IDE.fireEvent(new OpenShiftExceptionThrownEvent(exception, OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                                 .getUserInfoFail()));
                    if (updatePublicKeyCallback != null) {
                        updatePublicKeyCallback.onPublicKeyUpdated(true);
                        updatePublicKeyCallback = null;
                    }
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new OpenShiftExceptionThrownEvent(e, OpenShiftExtension.LOCALIZATION_CONSTANT.getUserInfoFail()));
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
            getUserInfo();
        }
    }

    /**
     * Perform updating SSH public key on OpenShift.
     *
     * @param namespace
     *         domain name
     */
    public void doUpdatePublicKey(String namespace) {
        try {
            OpenShiftClientService.getInstance().createDomain(namespace, true, new AsyncRequestCallback<String>() {

                @Override
                protected void onSuccess(String result) {
                    IDE.fireEvent(new OutputEvent(OpenShiftExtension.LOCALIZATION_CONSTANT.updatePublicKeySuccess(),
                                                  Type.INFO));

                    if (updatePublicKeyCallback != null) {
                        updatePublicKeyCallback.onPublicKeyUpdated(true);
                        updatePublicKeyCallback = null;
                    }
                }

                /**
                 * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
                 */
                @Override
                protected void onFailure(Throwable exception) {
                    if (exception instanceof ServerException) {
                        ServerException serverException = (ServerException)exception;
                        if (HTTPStatus.OK == serverException.getHTTPStatus()
                            && "Authentication-required".equals(serverException.getHeader(HTTPHeader.JAXRS_BODY_PROVIDED))) {
                            addLoggedInHandler();
                            IDE.fireEvent(new LoginEvent());
                            return;
                        }
                    }

                    IDE.fireEvent(new OpenShiftExceptionThrownEvent(exception, OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                                 .updatePublicKeyFailed()));
                    if (updatePublicKeyCallback != null) {
                        updatePublicKeyCallback.onPublicKeyUpdated(false);
                        updatePublicKeyCallback = null;
                    }
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new OpenShiftExceptionThrownEvent(e, OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                 .updatePublicKeyFailed()));
        }
    }
}
