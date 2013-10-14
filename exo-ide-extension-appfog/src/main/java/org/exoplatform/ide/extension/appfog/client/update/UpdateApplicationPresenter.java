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
package org.exoplatform.ide.extension.appfog.client.update;

import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.appfog.client.AppfogAsyncRequestCallback;
import org.exoplatform.ide.extension.appfog.client.AppfogClientService;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for update application operation.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class UpdateApplicationPresenter extends GitPresenter implements UpdateApplicationHandler, ProjectBuiltHandler {
    /** Location of war file (Java only). */
    private String warUrl;

    public UpdateApplicationPresenter() {
        IDE.addHandler(UpdateApplicationEvent.TYPE, this);
    }

    LoggedInHandler loggedInHandler = new LoggedInHandler() {

        @Override
        public void onLoggedIn() {
            updateApplication();
        }
    };

    @Override
    public void onUpdateApplication(UpdateApplicationEvent event) {
        if (makeSelectionCheck()) {
            validateData();
        }
    }

    private void updateApplication() {
//      ProjectModel projectModel = ((ItemContext)selectedItems.get(0)).getProject();
        ProjectModel projectModel = getSelectedProject();

        final String projectId = projectModel.getId();

        try {
            AppfogClientService.getInstance().updateApplication(vfs.getId(), projectId, null, null, warUrl,
                                                                new AppfogAsyncRequestCallback<String>(null, loggedInHandler, null) {
                                                                    @Override
                                                                    protected void onSuccess(String result) {
                                                                        try {
                                                                            AutoBean<AppfogApplication> appfogApplication =
                                                                                    AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();

                                                                            AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
                                                                                    new AutoBeanUnmarshaller<AppfogApplication>(
                                                                                            appfogApplication);

                                                                            AppfogClientService.getInstance()
                                                                                               .getApplicationInfo(vfs.getId(), projectId,
                                                                                                                   null, null,
                                                                                                                   new
















































                                                                                                                           AppfogAsyncRequestCallback<AppfogApplication>(
                                                                                                                                   unmarshaller,
                                                                                                                                   null,
                                                                                                                                   null) {

                                                                                                                               @Override
                                                                                                                               protected void onSuccess(
                                                                                                                                       AppfogApplication result) {
                                                                                                                                   IDE.fireEvent(
                                                                                                                                           new OutputEvent(
                                                                                                                                                   AppfogExtension
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
        public void onLoggedIn() {
            validateData();
        }
    };

    private void validateData() {
//      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        final String projectId = getSelectedProject().getId();

        try {
            AppfogClientService.getInstance().validateAction("update", null, null, null, null, vfs.getId(),
                                                             projectId, 0, 0, false,
                                                             new AppfogAsyncRequestCallback<String>(null, validateHandler, null) {
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
        final ProjectModel project = getSelectedProject();

        if (project.getLinks().isEmpty()) {
            try {
                VirtualFileSystem.getInstance()
                                 .getItemById(project.getId(),
                                              new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(project))) {

                                                  @Override
                                                  protected void onSuccess(ItemWrapper result) {
                                                      project.setLinks(result.getItem().getLinks());
                                                      getProjectContent(project);
                                                  }

                                                  @Override
                                                  protected void onFailure(Throwable exception) {
                                                      IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                  }
                                              });
            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }
        } else {
            getProjectContent(project);
        }
    }

    private void getProjectContent(ProjectModel project) {
        try {
            VirtualFileSystem.getInstance()
                             .getChildren(project,
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
                                                               AppfogExtension.LOCALIZATION_CONSTANT
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
