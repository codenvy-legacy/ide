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
package org.exoplatform.ide.extension.cloudbees.client.update;

import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialog.StringValueReceivedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesLocalizationConstant;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudbees.shared.ApplicationInfo;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent;
import org.exoplatform.ide.git.client.GitPresenter;

/**
 * Presenter for updating application on CloudBees.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: UpdateApplicationPresenter.java Oct 10, 2011 5:07:40 PM vereshchaka $
 */
public class UpdateApplicationPresenter extends GitPresenter implements UpdateApplicationHandler,
                                                                        ApplicationBuiltHandler {
    private CloudBeesLocalizationConstant lb = CloudBeesExtension.LOCALIZATION_CONSTANT;

    private String appId;

    private String appTitle;

    /** Message for git commit. */
    private String updateMessage;

    /** Location of war file (Java only). */
    private String warUrl;

    /** @param eventBus */
    public UpdateApplicationPresenter() {
        IDE.addHandler(UpdateApplicationEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.update.UpdateApplicationHandler#onUpdateApplication(org.exoplatform.ide
     * .extension.cloudbees.client.update.UpdateApplicationEvent) */
    @Override
    public void onUpdateApplication(UpdateApplicationEvent event) {

        if (event.getAppId() != null && event.getAppTitle() != null) {
            appId = event.getAppId();
            appTitle = event.getAppTitle();
            askForMessage();
        } else if (makeSelectionCheck()) {
            getApplicationInfo();
        }
    }

    private void askForMessage() {
        Dialogs.getInstance().askForValue(lb.updateAppAskForMsgTitle(), lb.updateAppAskForMsgText(), "",
                                          new StringValueReceivedHandler() {
                                              @Override
                                              public void stringValueReceived(String value) {
                                                  if (value == null) {
                                                      updateMessage = null;
                                                      return;
                                                  } else if (value.isEmpty()) {
                                                      updateMessage = null;
                                                  } else {
                                                      updateMessage = value;
                                                  }
                                                  buildApplication();
                                              }
                                          });
    }

    /** Get information about application. */
    protected void getApplicationInfo() {
//      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        String projectId = getSelectedProject().getId();

        try {
            AutoBean<ApplicationInfo> autoBean = CloudBeesExtension.AUTO_BEAN_FACTORY.applicationInfo();
            CloudBeesClientService.getInstance().getApplicationInfo(
                    null,
                    vfs.getId(),
                    projectId,
                    new CloudBeesAsyncRequestCallback<ApplicationInfo>(new AutoBeanUnmarshaller<ApplicationInfo>(autoBean),
                                                                       new LoggedInHandler() {
                                                                           @Override
                                                                           public void onLoggedIn() {
                                                                               getApplicationInfo();
                                                                           }
                                                                       }, null) {

                        @Override
                        protected void onSuccess(ApplicationInfo appInfo) {
                            appId = appInfo.getId();
                            appTitle = appInfo.getTitle();
                            askForMessage();
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void doUpdate() {
        String projectId = null;

//      if (((ItemContext)selectedItems.get(0)).getProject() != null)
//      {
//         projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
//      }
        if (getSelectedProject() != null) {
            projectId = getSelectedProject().getId();
        }

        try {
            AutoBean<ApplicationInfo> autoBean = CloudBeesExtension.AUTO_BEAN_FACTORY.applicationInfo();
            CloudBeesClientService.getInstance().updateApplication(
                    appId,
                    vfs.getId(),
                    projectId,
                    warUrl,
                    updateMessage,
                    new CloudBeesAsyncRequestCallback<ApplicationInfo>(new AutoBeanUnmarshaller<ApplicationInfo>(autoBean),
                                                                       new LoggedInHandler() {

                                                                           @Override
                                                                           public void onLoggedIn() {
                                                                               doUpdate();
                                                                           }
                                                                       }, null) {

                        @Override
                        protected void onSuccess(ApplicationInfo appInfo) {
                            IDE.fireEvent(new OutputEvent(CloudBeesExtension.LOCALIZATION_CONSTANT
                                                                            .applicationUpdatedMsg(appTitle), Type.INFO));
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler#onApplicationBuilt(org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent) */
    @Override
    public void onApplicationBuilt(ApplicationBuiltEvent event) {
        IDE.removeHandler(event.getAssociatedType(), this);
        if (event.getJobStatus().getArtifactUrl() != null) {
            warUrl = event.getJobStatus().getArtifactUrl();
            doUpdate();
        }
    }

    private void buildApplication() {
        IDE.addHandler(ApplicationBuiltEvent.TYPE, this);
        IDE.fireEvent(new BuildApplicationEvent());
    }

}
