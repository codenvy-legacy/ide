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
package org.exoplatform.ide.extension.java.server;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.exoplatform.ide.extension.java.shared.Action;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IProjectDescription;
import com.codenvy.eclipse.core.resources.ResourcesPlugin;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IStatus;
import com.codenvy.eclipse.core.runtime.Status;
import com.codenvy.eclipse.jdt.core.ICompilationUnit;
import com.codenvy.eclipse.jdt.core.IField;
import com.codenvy.eclipse.jdt.core.IInitializer;
import com.codenvy.eclipse.jdt.core.IJavaElement;
import com.codenvy.eclipse.jdt.core.IJavaProject;
import com.codenvy.eclipse.jdt.core.ILocalVariable;
import com.codenvy.eclipse.jdt.core.IMethod;
import com.codenvy.eclipse.jdt.core.IType;
import com.codenvy.eclipse.jdt.core.ITypeParameter;
import com.codenvy.eclipse.jdt.core.JavaCore;
import com.codenvy.eclipse.jdt.core.JavaModelException;
import com.codenvy.eclipse.jdt.internal.core.JavaModelManager;
import com.codenvy.eclipse.jdt.ui.refactoring.RenameSupport;
import com.codenvy.eclipse.resources.ProjectResource;
import com.codenvy.eclipse.resources.WorkspaceResource;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Path("{ws-name}/refactoring/java")
public class RefactoringService {
    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    @Inject
    private EventListenerList         eventListenerList;

    /** Logger. */
    private static final Log          LOG = ExoLogger.getLogger(RefactoringService.class);

    private WorkspaceResource getWorkspace(String vfsid) {
        Object tenantName = EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_ID);

        if (tenantName == null) {
            if (ResourcesPlugin.getDefaultWorkspace() == null) {
                try {
                    VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null, eventListenerList);
                    // ResourcesPlugin.setDefaultWorkspace(new WorkspaceResource(vfs));
                    ResourcesPlugin.setDefaultWorkspace(new ObservableWorkspace(vfs));
                } catch (VirtualFileSystemException e) {
                    LOG.error("Can't initialize Workspace.", e);
                }
            }
        } else {
            try {
                VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null, eventListenerList);
                // ResourcesPlugin.addWorkspace(new WorkspaceResource(vfs));
                ResourcesPlugin.addWorkspace(new ObservableWorkspace(vfs));
            } catch (VirtualFileSystemException e) {
                LOG.error("Can't initialize Workspace.", e);
            }
        }
        return (WorkspaceResource)ResourcesPlugin.getWorkspace();
    }

    @Path("rename")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public List<Action> rename(@QueryParam("vfsid") String vfsid, @QueryParam("projectid") String projectid,
                               @QueryParam("fqn") String fqn, @QueryParam("offset") int offset,
                               @QueryParam("newName") String newname) throws CoreException,
                                                                     ItemNotFoundException,
                                                                     PermissionDeniedException,
                                                                     VirtualFileSystemException {
        WorkspaceResource workspace = getWorkspace(vfsid);
        IJavaProject project = getOrCreateJavaProject(workspace, projectid);
        if (project == null) {
            throw new CoreException(new Status(IStatus.ERROR, "IDE", "The project not initialized"));
        }
        try {
            IType type = project.findType(fqn);
            if (type == null) {
                throw new CoreException(new Status(IStatus.ERROR, "IDE", "The type '" + fqn + "' not found"));
            }
            if (type.exists()) {
                ICompilationUnit cUnit = type.getCompilationUnit();
                RenameSupport renameSupport;
                if (offset != -1) {
                    IJavaElement[] iJavaElements = cUnit.codeSelect(offset, 0);
                    if (iJavaElements != null && iJavaElements.length > 0) {
                        renameSupport = getRenameSupport(newname, iJavaElements[0]);
                    } else {
                        throw new CoreException(new Status(IStatus.ERROR, "IDE", "Cannot perform rename at current selection"));
                    }

                } else {
                    renameSupport = RenameSupport.create(cUnit, newname, RenameSupport.UPDATE_REFERENCES);
                }
                IStatus status = renameSupport.preCheck();
                if (status.isOK()) {
                    renameSupport.perform();
                    Project proj = (Project)workspace.getVFS().getItem(projectid, false, PropertyFilter.ALL_FILTER);
                    String workspaceName = EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME).toString();
                    String user = ConversationState.getCurrent().getIdentity().getUserId();
                    LOG.info("EVENT#user-code-refactor# WS#" + workspaceName + "# USER#" + user + "# PROJECT#" +
                             proj.getName() + "# TYPE#" + proj.getProjectType() + "# FEATURE#rename#");
                    if (workspace instanceof ObservableWorkspace) {
                        return ((ObservableWorkspace)workspace).getActions();
                    } else {
                        return new ArrayList<Action>();
                    }
                } else {
                    throw new CoreException(status);
                }
            } else {
                throw new CoreException(new Status(IStatus.ERROR, "IDE", "The type '" + fqn + "' not found"));
            }
        } catch (InterruptedException e) {
            throw new WebApplicationException(e);
        } catch (InvocationTargetException e) {
            throw new WebApplicationException(e);
        } finally {
            try {
                if (project != null) {
                    project.close();
                }
            } catch (JavaModelException ignore) {
            }
        }
    }

    private RenameSupport getRenameSupport(String newname, IJavaElement element) throws CoreException {
        RenameSupport renameSupport;

        switch (element.getElementType()) {
            case IJavaElement.COMPILATION_UNIT:
                renameSupport = RenameSupport.create((ICompilationUnit)element, newname, RenameSupport.UPDATE_REFERENCES);
                break;
            case IJavaElement.METHOD:
                if (((IMethod)element).isConstructor()) {
                    renameSupport = RenameSupport.create(((IMethod)element).getDeclaringType(), newname, RenameSupport.UPDATE_REFERENCES);
                } else {
                    // renameSupport = RenameSupport.create((IMethod)element, newname, RenameSupport.UPDATE_REFERENCES);
                    throw new UnsupportedOperationException("Currently we do not support renaming methods");
                }
                break;
            case IJavaElement.FIELD:
                renameSupport = RenameSupport.create((IField)element, newname, RenameSupport.UPDATE_REFERENCES);
                break;
            case IJavaElement.TYPE:
                renameSupport = RenameSupport.create((IType)element, newname, RenameSupport.UPDATE_REFERENCES);
                break;
            case IJavaElement.LOCAL_VARIABLE:
                renameSupport = RenameSupport.create((ILocalVariable)element, newname, RenameSupport.UPDATE_REFERENCES);
                break;
            case IJavaElement.TYPE_PARAMETER:
                renameSupport = RenameSupport.create((ITypeParameter)element, newname, RenameSupport.UPDATE_REFERENCES);
                break;
            case IJavaElement.INITIALIZER:
                renameSupport = RenameSupport.create(((IInitializer)element).getDeclaringType(), newname,
                                                     RenameSupport.UPDATE_REFERENCES);
                break;
            case IJavaElement.PACKAGE_FRAGMENT:
                // renameSupport = RenameSupport.create( (IPackageFragment)element , newname,
                // RenameSupport.UPDATE_REFERENCES);
                throw new UnsupportedOperationException("Currently we do not support renaming packages");
            default:
                throw new IllegalArgumentException("Rename of element '" + element.getElementName() + "' is not supported");
        }
        return renameSupport;
    }

    private IJavaProject getOrCreateJavaProject(WorkspaceResource workspace, String projectid) {
        VirtualFileSystem vfs = workspace.getVFS();
        try {
            Item item = vfs.getItem(projectid, false, PropertyFilter.ALL_FILTER);
            if (item instanceof Project) {
                if (!checkProjectInitialized(vfs, item)) {
                    initializeProject(item, workspace);
                }

                IProject iProject = workspace.getRoot().getProject(item.getName());
                IJavaProject project = JavaCore.create(iProject);
                project.open(null);
                JavaModelManager.getIndexManager().removeIndex(project.getPath());
                CountDownLatch latch = new CountDownLatch(2);
                JavaModelManager.getIndexManager().indexAll(project.getProject(), latch);
                if (latch.await(2, TimeUnit.MINUTES)) {
                    return project;
                }

                return null;

            } else {
                throw new IllegalArgumentException("Item id '" + projectid + "' is not a project");
            }
        } catch (VirtualFileSystemException e) {
            throw new WebApplicationException(e);
        } catch (JavaModelException e) {
            throw new WebApplicationException(e);
        } catch (InterruptedException e) {
            throw new WebApplicationException(e);
        }

    }

    private void initializeProject(Item vfsProject, WorkspaceResource workspace) {
        IProject project = new ProjectResource(new com.codenvy.eclipse.core.runtime.Path(vfsProject.getPath()), workspace);
        try {
            project.create(null);
            project.open(null);
            IProjectDescription description = project.getDescription();
            description.setNatureIds(new String[]{JavaCore.NATURE_ID});
            project.setDescription(description, null);

            workspace.getVFS().createFolder(vfsProject.getId(), ".target");


            String sourcePath;
            StringBuilder b = new StringBuilder();
            if (vfsProject.hasProperty("sourceFolder")) {
                for (String s : vfsProject.getProperty("sourceFolder").getValue()) {
                    b.append("<classpathentry kind=\"src\" path=\"").append(s).append("\"/>");

                }
            } else {

                b.append("<classpathentry kind=\"src\" path=\"").append(JavaCodeAssistant.DEFAULT_SOURCE_FOLDER).append(
                                                                                                                        "\"/>");
                // if (workspace.getRoot().getFolder(
                // new org.eclipse.core.runtime.Path(vfsProject.getPath() + "/src/test/java")).exists())
                // {
                b.append("<classpathentry kind=\"src\" path=\"").append("src/test/java").append("\"/>");
                // }
            }
            sourcePath = b.toString();

            workspace.getVFS()
                     .createFile(vfsProject.getId(), ".classpath", MediaType.TEXT_PLAIN_TYPE,
                                 new ByteArrayInputStream(
                                                          ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<classpath><classpathentry " +
                                                           "kind=\"output\" path=\".target\"/>" +
                                                           sourcePath + "</classpath>").getBytes()));
        } catch (CoreException e) {
            throw new WebApplicationException(e);
        } catch (ItemNotFoundException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidArgumentException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        } catch (ItemAlreadyExistException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        } catch (PermissionDeniedException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        } catch (VirtualFileSystemException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        }
    }

    private boolean checkProjectInitialized(VirtualFileSystem vfs, Item item) throws VirtualFileSystemException {
        ItemList<Item> children = vfs.getChildren(item.getId(), -1, 0, null, false, PropertyFilter.ALL_FILTER);
        for (Item i : children.getItems()) {
            if (i.getName().equals(".classpath")) {
                return true;
            }
        }
        return false;
    }


}
