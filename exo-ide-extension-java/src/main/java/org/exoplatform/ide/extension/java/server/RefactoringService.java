/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.extension.java.server;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.search.indexing.IndexAllProject;
import org.eclipse.jdt.internal.core.search.indexing.IndexRequest;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.exoplatform.ide.eclipse.resources.ProjectResource;
import org.exoplatform.ide.eclipse.resources.WorkspaceResource;
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

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Path("/ide/refactoring/java")
public class RefactoringService
{
   @Inject
   private VirtualFileSystemRegistry vfsRegistry;

   @Inject
   private EventListenerList eventListenerList;

   /**
    * Logger.
    */
   private static final Log LOG = ExoLogger.getLogger(RefactoringService.class);

   private WorkspaceResource getWorkspace(String vfsid)
   {
      if (ResourcesPlugin.workspace == null)
      {
         try
         {
            VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null, eventListenerList);
            ResourcesPlugin.workspace = new WorkspaceResource(vfs);
         }
         catch (VirtualFileSystemException e)
         {
            LOG.error("Can't initialize Workspace.", e);
         }
      }
      return (WorkspaceResource)ResourcesPlugin.getWorkspace();
   }

   @Path("rename")
   @POST
   public void rename(@QueryParam("vfsid") String vfsid, @QueryParam("projectid") String projectid,
      @QueryParam("fqn") String fqn, @QueryParam("offset") int offset, @QueryParam("newName") String newname) throws CoreException
   {
      WorkspaceResource workspace = getWorkspace(vfsid);
      IJavaProject project = getOrCreateJavaProject(workspace, projectid);
      try
      {
         IType type = project.findType(fqn);
         if (type.exists())
         {
            ICompilationUnit cUnit = type.getCompilationUnit();
            IJavaElement elementAt = cUnit.getElementAt(offset);
            RenameSupport renameSupport;
            switch (elementAt.getElementType())
            {
               case IJavaElement.COMPILATION_UNIT:
                  renameSupport = RenameSupport.create((ICompilationUnit)elementAt, newname,
                     RenameSupport.UPDATE_REFERENCES);
                  break;
               case IJavaElement.METHOD:
                  renameSupport = RenameSupport.create((IMethod)elementAt, newname, RenameSupport.UPDATE_REFERENCES);
                  break;
               case IJavaElement.FIELD:
                  renameSupport = RenameSupport.create((IField)elementAt, newname, RenameSupport.UPDATE_REFERENCES);
                  break;
               case IJavaElement.TYPE:
                  renameSupport = RenameSupport.create((IType)elementAt, newname, RenameSupport.UPDATE_REFERENCES);
                  break;
               case IJavaElement.LOCAL_VARIABLE:
                  renameSupport = RenameSupport.create((ILocalVariable)elementAt, newname,
                     RenameSupport.UPDATE_REFERENCES);
                  break;
               case IJavaElement.TYPE_PARAMETER:
                  renameSupport = RenameSupport.create((ITypeParameter)elementAt, newname,
                     RenameSupport.UPDATE_REFERENCES);
                  break;
               default:
                  throw new IllegalArgumentException(
                     "Rename of element '" + elementAt.getElementName() + "' is not supported");
            }
            IStatus status = renameSupport.preCheck();
            if (status.isOK())
            {
               renameSupport.perform();
            }
            else
            {
               throw new CoreException(status);
            }
         }
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
      catch (InvocationTargetException e)
      {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
      finally
      {
         try
         {
            if (project != null)
            {
               project.close();
            }
         }
         catch (JavaModelException ignore)
         {
         }
      }
   }

   private IJavaProject getOrCreateJavaProject(WorkspaceResource workspace, String projectid)
   {
      VirtualFileSystem vfs = workspace.getVFS();
      try
      {
         Item item = vfs.getItem(projectid, PropertyFilter.ALL_FILTER);
         if (item instanceof Project)
         {
            if (!checkProjectInitialized(vfs, item))
            {
               initializeProject(item, workspace);
            }

            IProject iProject = workspace.getRoot().getProject(item.getName());
            IJavaProject project = JavaCore.create(iProject);
            project.open(null);
            JavaModelManager.getIndexManager().deleteIndexFiles();
            JavaModelManager.getIndexManager().indexAll(project.getProject());
            //            JavaModelManager.getIndexManager().reset();
            //            JavaModelManager.getIndexManager().enable();
            return project;
         }
         else
         {
            throw new IllegalArgumentException("Item id '" + projectid + "' is not a project");
         }
      }
      catch (VirtualFileSystemException e)
      {
         throw new WebApplicationException(e);
      }
      catch (JavaModelException e)
      {
         throw new WebApplicationException(e);
      }

   }

   private void initializeProject(Item vfsProject, WorkspaceResource workspace)
   {
      IProject project = new ProjectResource(new org.eclipse.core.runtime.Path(vfsProject.getPath()), workspace);
      try
      {
         project.create(null);
         project.open(null);
         IProjectDescription description = project.getDescription();
         description.setNatureIds(new String[]{JavaCore.NATURE_ID});
         project.setDescription(description, null);

         workspace.getVFS().createFolder(vfsProject.getId(), ".target");


         String sourcePath;
         if (vfsProject.hasProperty("sourceFolder"))
         {
            sourcePath = vfsProject.getProperty("sourceFolder").getValue().get(0);
         }
         else
         {
            sourcePath = JavaCodeAssistant.DEFAULT_SOURCE_FOLDER;
         }

         workspace.getVFS().createFile(vfsProject.getId(), ".classpath", MediaType.TEXT_PLAIN_TYPE,
            new ByteArrayInputStream(
               ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<classpath><classpathentry kind=\"output\" path=\".target\"/>" + "<classpathentry kind=\"src\" path=\"" + sourcePath + "\"/></classpath>").getBytes()));
      }
      catch (CoreException e)
      {
         throw new WebApplicationException(e);
      }
      catch (ItemNotFoundException e)
      {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
      catch (InvalidArgumentException e)
      {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
      catch (ItemAlreadyExistException e)
      {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
      catch (PermissionDeniedException e)
      {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
      catch (VirtualFileSystemException e)
      {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
   }

   private boolean checkProjectInitialized(VirtualFileSystem vfs, Item item) throws VirtualFileSystemException
   {
      ItemList<Item> children = vfs.getChildren(item.getId(), -1, 0, null, PropertyFilter.ALL_FILTER);
      for (Item i : children.getItems())
      {
         if (i.getName().equals(".classpath"))
         {
            return true;
         }
      }
      return false;
   }


}
