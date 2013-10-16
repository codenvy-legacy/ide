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
package org.exoplatform.ide.client.project.explorer;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectProperties;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.PropertyImpl;

import java.util.ArrayList;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class ProjectUpdater {

    /**
     * TODO Temporary method.
     * <p/>
     * Detected whether project type is deprecated or not.
     *
     * @param project
     * @return <code>true</code> if deprecated
     */
    public static boolean isNeedUpdateProject(ProjectModel project) {
        return ProjectResolver.deprecatedTypes.contains(project.getProjectType())
               && project.getPropertyValues(ProjectProperties.TARGET.value()) == null;
    }

    public interface ProjectUpdatedHandler {
        void onProjectUpdated();
    }

    /**
     * TODO Temporary method.
     * <p/>
     * Is used to detect and set targets to deprecated project types (to support them).
     *
     * @param project
     */
    public static void updateProject(final ProjectModel project, final ProjectUpdatedHandler itemUpdatedHandler) {
        ArrayList<String> targets = ProjectResolver.resolveProjectTarget(project.getProjectType());
        project.getProperties().add(new PropertyImpl(ProjectProperties.TARGET.value(), targets));

        if (project.getLinks().isEmpty()) {
            try {
                VirtualFileSystem.getInstance()
                                 .getItemById(project.getId(),
                                              new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(project))) {

                                                  @Override
                                                  protected void onSuccess(ItemWrapper result) {
                                                      project.setLinks(result.getItem().getLinks());
                                                      updateProjectProperties(project, itemUpdatedHandler);
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
            updateProjectProperties(project, itemUpdatedHandler);
        }
    }
    
    private static void updateProjectProperties(ProjectModel project, final ProjectUpdatedHandler itemUpdatedHandler) {
        try {
            VirtualFileSystem.getInstance().updateItem(project, null, new AsyncRequestCallback<ItemWrapper>() {

                @Override
                protected void onSuccess(ItemWrapper result) {
                    // loadProject();
                    // openProject();
                    if (itemUpdatedHandler != null) {
                        itemUpdatedHandler.onProjectUpdated();
                    }
                }

                @Override
                protected void onFailure(Throwable e) {
                    IDE.fireEvent(new ExceptionThrownEvent(e));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

}
