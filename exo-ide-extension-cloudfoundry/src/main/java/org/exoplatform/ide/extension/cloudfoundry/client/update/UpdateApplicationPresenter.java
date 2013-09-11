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
package org.exoplatform.ide.extension.cloudfoundry.client.update;

import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for update application operation.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: OperationsApplicationPresenter.java Jul 14, 2011 11:51:13 AM vereshchaka $
 */
public class UpdateApplicationPresenter extends GitPresenter implements UpdateApplicationHandler, ProjectBuiltHandler {
    /** Location of war file (Java only). */
    private String warUrl;

    private PAAS_PROVIDER paasProvider;

    public UpdateApplicationPresenter() {
        IDE.addHandler(UpdateApplicationEvent.TYPE, this);
    }

    LoggedInHandler loggedInHandler = new LoggedInHandler() {

        @Override
        public void onLoggedIn(String server) {
            updateApplication();
        }
    };

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.update.UpdateApplicationHandler#onUpdateApplication(org.exoplatform.ide
     * .extension.cloudfoundry.client.update.UpdateApplicationEvent) */
    @Override
    public void onUpdateApplication(UpdateApplicationEvent event) {
        paasProvider = event.getPaasProvider();
        if (makeSelectionCheck()) {
            validateData();
        }
    }

    private void updateApplication() {
//      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        final String projectId = getSelectedProject().getId();

        try {
            CloudFoundryClientService.getInstance().updateApplication(vfs.getId(), projectId, null, null, warUrl,
                                                                      new CloudFoundryAsyncRequestCallback<String>(null, loggedInHandler,
                                                                                                                   null, paasProvider) {
                                                                          @Override
                                                                          protected void onSuccess(String result) {
                                                                              try {
                                                                                  AutoBean<CloudFoundryApplication>
                                                                                          cloudFoundryApplication =
                                                                                          CloudFoundryExtension.AUTO_BEAN_FACTORY
                                                                                                               .cloudFoundryApplication();

                                                                                  AutoBeanUnmarshaller<CloudFoundryApplication>
                                                                                          unmarshaller =
                                                                                          new AutoBeanUnmarshaller<CloudFoundryApplication>(
                                                                                                  cloudFoundryApplication);

                                                                                  CloudFoundryClientService.getInstance()
                                                                                                           .getApplicationInfo(vfs.getId(),
                                                                                                                               projectId,
                                                                                                                               null, null,
                                                                                                                               new
                                                                                                                                       CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(
                                                                                                                                       unmarshaller,
                                                                                                                                       null,
                                                                                                                                       null,
                                                                                                                                       paasProvider) {

                                                                                                                                   @Override

                                                                                                                                   protected void onSuccess(
                                                                                                                                           CloudFoundryApplication result) {
                                                                                                                                       IDE.fireEvent(
                                                                                                                                               new OutputEvent(
                                                                                                                                                       CloudFoundryExtension
                                                                                                                                                               .LOCALIZATION_CONSTANT
                                                                                                                                                               .updateApplicationSuccess(
                                                                                                                                                                       result.getName()),
                                                                                                                                                       Type.INFO));
                                                                                                                                   }
                                                                                                                               });
                                                                              } catch (RequestException e) {
                                                                                  IDE.fireEvent(new ExceptionThrownEvent(e));
                                                                              }
                                                                          }
                                                                      });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler#onProjectBuilt(org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent) */
    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        IDE.removeHandler(event.getAssociatedType(), this);
        if (event.getBuildStatus().getDownloadUrl() != null) {
            warUrl = event.getBuildStatus().getDownloadUrl();
            updateApplication();
        }
    }

    private LoggedInHandler validateHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn(String server) {
            validateData();
        }
    };

    private void validateData() {
//      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        final String projectId = getSelectedProject().getId();

        try {
            CloudFoundryClientService.getInstance().validateAction("update", null, null, null, null, vfs.getId(),
                                                                   projectId, paasProvider, 0, 0, false,
                                                                   new CloudFoundryAsyncRequestCallback<String>(null, validateHandler,
                                                                                                                null, paasProvider) {
                                                                       @Override
                                                                       protected void onSuccess(String result) {
                                                                           isBuildApplication();
                                                                       }
                                                                   });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Check, is work directory contains <code>pom.xml</code> file. */
    private void isBuildApplication() {
//      final ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
        final ProjectModel project = getSelectedProject();

        try {
            VirtualFileSystem.getInstance().getChildren(project,
                                                        new AsyncRequestCallback<List<Item>>(
                                                                new ChildrenUnmarshaller(new ArrayList<Item>())) {

                                                            @Override
                                                            protected void onSuccess(List<Item> result) {
                                                                for (Item item : result) {
                                                                    if ("pom.xml".equals(item.getName())) {
                                                                        buildApplication();
                                                                        return;
                                                                    }
                                                                }
                                                                warUrl = null;
                                                                updateApplication();
                                                            }

                                                            @Override
                                                            protected void onFailure(Throwable exception) {
                                                                String msg =
                                                                        CloudFoundryExtension.LOCALIZATION_CONSTANT
                                                                                             .updateApplicationForbidden(project.getName());
                                                                IDE.fireEvent(new ExceptionThrownEvent(msg));
                                                            }
                                                        });
        } catch (RequestException ignored) {
        }
    }

    private void buildApplication() {
        IDE.addHandler(ProjectBuiltEvent.TYPE, this);
        IDE.fireEvent(new BuildProjectEvent());
    }
}
