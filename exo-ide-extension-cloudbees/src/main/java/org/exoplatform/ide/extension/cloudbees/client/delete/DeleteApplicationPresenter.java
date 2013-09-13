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
package org.exoplatform.ide.extension.cloudbees.client.delete;

import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudbees.shared.ApplicationInfo;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Presenter for deleting application from CloudBees. Performs following actions on delete: 1. Gets application id (application
 * info) by work dir (location on file system). 2. Asks user to confirm the deleting of the application. 3. When user confirms -
 * performs deleting the application.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeleteApplicationPresenter.java Jul 1, 2011 12:59:52 PM vereshchaka $
 */
public class DeleteApplicationPresenter extends GitPresenter implements DeleteApplicationHandler {

    public DeleteApplicationPresenter() {
        IDE.addHandler(DeleteApplicationEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.extension.heroku.client.delete.DeleteApplicationHandler#onDeleteApplication(org.exoplatform.ide.extension
     * .heroku.client.delete.DeleteApplicationEvent) */
    @Override
    public void onDeleteApplication(DeleteApplicationEvent event) {
        // application id and application title can be received from event
        // e.g.when, delete from application manager form
        if (event.getAppId() != null && event.getAppTitle() != null) {
            String appId = event.getAppId();
            String appTitle = event.getAppTitle() != null ? event.getAppTitle() : appId;
            askForDelete(appId, appTitle);
        } else if (makeSelectionCheck()) {
            getApplicationInfo();
        }
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
                            askForDelete(appInfo.getId(), appInfo.getTitle());
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Show confirmation message before delete.
     *
     * @param gitWorkDir
     */
    protected void askForDelete(final String appId, final String appTitle) {
        Dialogs.getInstance().ask(CloudBeesExtension.LOCALIZATION_CONSTANT.deleteApplicationTitle(),
                                  CloudBeesExtension.LOCALIZATION_CONSTANT.deleteApplicationQuestion(appTitle),
                                  new BooleanValueReceivedHandler() {

                                      @Override
                                      public void booleanValueReceived(Boolean value) {
                                          if (value != null && value) {
                                              doDelete(appId, appTitle);
                                          }
                                      }
                                  });
    }

    protected void doDelete(final String appId, final String appTitle) {
        String projectId = null;

//      if (selectedItems.size() > 0 && selectedItems.get(0) instanceof ItemContext)
//      {
//         ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
//         if (project != null && project.getPropertyValue("cloudbees-application") != null
//            && appId.equals((String)project.getPropertyValue("cloudbees-application")))
//         {
//            projectId = project.getId();
//         }
//      }

        if (selectedItem != null) {
            ProjectModel project = getSelectedProject();
            if (project != null && project.getPropertyValue("cloudbees-application") != null
                && appId.equals((String)project.getPropertyValue("cloudbees-application"))) {
                projectId = project.getId();
            }
        }

        try {
            CloudBeesClientService.getInstance().deleteApplication(appId, vfs.getId(), projectId,
                                                                   new CloudBeesAsyncRequestCallback<String>(new LoggedInHandler() {
                                                                       @Override
                                                                       public void onLoggedIn() {
                                                                           doDelete(appId, appTitle);
                                                                       }
                                                                   }, null) {
                                                                       @Override
                                                                       protected void onSuccess(String result) {
                                                                           IDE.fireEvent(
                                                                                   new OutputEvent(CloudBeesExtension.LOCALIZATION_CONSTANT
                                                                                                                     .applicationDeletedMsg(
                                                                                                                             appTitle),
                                                                                                   Type.INFO));
                                                                           IDE.fireEvent(new ApplicationDeletedEvent(appId));
                                                                       }
                                                                   });
        } catch (RequestException e) {
            IDE.fireEvent(new OutputEvent(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationDeletedMsg(appTitle),
                                          Type.INFO));
            IDE.fireEvent(new ApplicationDeletedEvent(appId));
        }
    }
}
