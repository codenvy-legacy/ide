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
package org.exoplatform.ide.extension.openshift.client.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownEvent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.git.client.GitPresenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Presenter for getting and displaying application's information. The view must be pointed in Views.gwt.xml.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 1, 2011 11:32:37 AM anya $
 */
public class ApplicationInfoPresenter extends GitPresenter implements ShowApplicationInfoHandler, ViewClosedHandler,
                                                                      LoggedInHandler {

    interface Display extends IsView {
        HasClickHandlers getOkButton();

        ListGridItem<Property> getApplicationInfoGrid();
    }

    private Display display;

    /**
     *
     */
    public ApplicationInfoPresenter() {
        IDE.addHandler(ShowApplicationInfoEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** Bind presenter with display. */
    public void bindDisplay() {
        display.getOkButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }

    /** @see org.exoplatform.ide.extension.openshift.client.info.ShowApplicationInfoHandler#onShowApplicationInfo(org.exoplatform.ide
     * .extension.openshift.client.info.ShowApplicationInfoEvent) */
    @Override
    public void onShowApplicationInfo(ShowApplicationInfoEvent event) {
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

    /** Get application's information. */
    public void getApplicationInfo() {
//      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        String projectId = getSelectedProject().getId();

        try {
            AutoBean<AppInfo> appInfo = OpenShiftExtension.AUTO_BEAN_FACTORY.appInfo();
            AutoBeanUnmarshaller<AppInfo> unmarshaller = new AutoBeanUnmarshaller<AppInfo>(appInfo);
            OpenShiftClientService.getInstance().getApplicationInfo(null, vfs.getId(), projectId,
                                                                    new AsyncRequestCallback<AppInfo>(unmarshaller) {
                                                                        @Override
                                                                        protected void onSuccess(AppInfo result) {
                                                                            if (display == null) {
                                                                                display = GWT.create(Display.class);
                                                                                bindDisplay();
                                                                                IDE.getInstance().openView(display.asView());
                                                                            }

                                                                            List<Property> properties = new ArrayList<Property>();
                                                                            properties.add(new Property(
                                                                                    OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                                      .applicationName(), result
                                                                                            .getName()));
                                                                            properties.add(new Property(
                                                                                    OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                                      .applicationType(), result
                                                                                            .getType()));
                                                                            properties.add(new Property(
                                                                                    OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                                      .applicationPublicUrl(),
                                                                                    "<a href =\"" + result.getPublicUrl() +
                                                                                    "\" target=\"_blank\">" + result.getPublicUrl() +
                                                                                    "</a>"));
                                                                            properties.add(new Property(
                                                                                    OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                                      .applicationGitUrl(), result
                                                                                            .getGitUrl()));
                                                                            String time =
                                                                                    DateTimeFormat
                                                                                            .getFormat(PredefinedFormat.DATE_TIME_MEDIUM)
                                                                                            .format(
                                                                                                    new Date((long)result
                                                                                                            .getCreationTime()));
                                                                            properties.add(new Property(
                                                                                    OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                                      .applicationCreationTime(), time));
                                                                            display.getApplicationInfoGrid().setValue(properties);
                                                                        }

                                                                        /**
                                                                         * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
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
            getApplicationInfo();
        }
    }
}
