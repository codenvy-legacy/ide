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
package org.exoplatform.ide.extension.aws.client.beanstalk.versions.delete;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationVersionInfo;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 20, 2012 5:01:04 PM anya $
 */
public class DeleteVersionPresenter implements DeleteVersionHandler, ViewClosedHandler {
    interface Display extends IsView {
        HasClickHandlers getDeleteButton();

        HasClickHandlers getCancelButton();

        HasValue<String> getDeleteQuestion();

        HasValue<Boolean> getDeleteS3Bundle();
    }

    private Display display;

    private String vfsId;

    private String projectId;

    private ApplicationVersionInfo version;

    private VersionDeletedHandler versionDeletedHandler;

    public DeleteVersionPresenter() {
        IDE.addHandler(DeleteVersionEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getDeleteButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                deleteVersion();
            }
        });
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.delete.DeleteVersionHandler#onDeleteVersion(org.exoplatform.ide
     * .extension.aws.client.beanstalk.versions.delete.DeleteVersionEvent) */
    @Override
    public void onDeleteVersion(DeleteVersionEvent event) {
        this.vfsId = event.getVfsId();
        this.projectId = event.getProjectId();
        this.versionDeletedHandler = event.getVersionDeletedHandler();
        this.version = event.getVersion();

        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }
        display.getDeleteS3Bundle().setValue(true);
        display.getDeleteQuestion().setValue(
                AWSExtension.LOCALIZATION_CONSTANT.deleteVersionQuestion(version.getVersionLabel()));
    }

    private void deleteVersion() {
        try {
            BeanstalkClientService.getInstance().deleteVersion(vfsId, projectId, version.getApplicationName(),
               version.getVersionLabel(), display.getDeleteS3Bundle().getValue(),
               new AwsAsyncRequestCallback<Object>(new LoggedInHandler() {

                   @Override
                   public void onLoggedIn() {
                       deleteVersion();
                   }
               }, null) {

                   @Override
                   protected void processFail(Throwable exception) {
                       String message = AWSExtension.LOCALIZATION_CONSTANT
                                                    .deleteVersionFailed(
                                                            version.getVersionLabel());
                       if (exception instanceof ServerException &&
                           ((ServerException)exception).getMessage() != null) {
                           message += "<br>" + ((ServerException)exception).getMessage();
                       }
                       Dialogs.getInstance().showError(message);
                   }

                   @Override
                   protected void onSuccess(Object result) {
                       IDE.getInstance().closeView(display.asView().getId());
                       if (versionDeletedHandler != null) {
                           versionDeletedHandler.onVersionDeleted(version);
                       }
                   }
               });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
