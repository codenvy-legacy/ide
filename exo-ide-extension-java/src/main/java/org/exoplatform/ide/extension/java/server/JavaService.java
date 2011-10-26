/*
 * Copyright (C) 2011 eXo Platform SAS.
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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.ide.extension.java.shared.ast.AstItem;
import org.exoplatform.ide.extension.java.shared.ast.CompilationUnit;
import org.exoplatform.ide.extension.java.shared.ast.JavaProject;
import org.exoplatform.ide.extension.java.shared.ast.Package;
import org.exoplatform.ide.extension.java.shared.ast.RootPackage;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.Project;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/application/java")
public class JavaService
{

   private final VirtualFileSystemRegistry vfsRegistry;

   public JavaService(VirtualFileSystemRegistry vfsRegistry)
   {
      this.vfsRegistry = vfsRegistry;
   }

   /**
    * Search for projects in Workspace.
    * 
    * @param vfsId
    * @param uriInfo
    * @return
    * @throws VirtualFileSystemException
    */
   @GET
   @Path("projects")
   @Produces(MediaType.APPLICATION_JSON)
   public List<JavaProject> getProjects(@QueryParam("vfsId") String vfsId, @Context UriInfo uriInfo)
      throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
      {
         throw new VirtualFileSystemException("Virtual file system not initialized");
      }

      String statement = "select * from vfs:project";
      ItemList<Item> items = vfs.search(statement, 1000, 0);

      List<JavaProject> projects = new ArrayList<JavaProject>();
      for (Item item : items.getItems())
      {
         if (item instanceof Project)
         {
            Project p = (Project)item;

            JavaProject project = new JavaProject();
            project.setId(p.getId());
            project.setName(p.getName());
            projects.add(project);
         }
      }

      return projects;
   }

   /**
    * Get root packages of Project.
    * 
    * @param vfsId
    * @param projectId
    * @param uriInfo
    * @return
    * @throws VirtualFileSystemException
    */
   @GET
   @Path("project/packages/root")
   @Produces(MediaType.APPLICATION_JSON)
   public List<RootPackage> getRootPackages(@QueryParam("vfsId") String vfsId,
      @QueryParam("projectId") String projectId, @Context UriInfo uriInfo) throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
      {
         throw new VirtualFileSystemException("Virtual file system not initialized");
      }

      Item projectItem = vfs.getItem(projectId, PropertyFilter.valueOf("*"));
      if (!(projectItem instanceof Project))
      {
         throw new VirtualFileSystemException("Not a project.");
      }

      Project project = (Project)projectItem;
      List<RootPackage> rootPackages = new ArrayList<RootPackage>();

      /*
       * Look for the root packages
       */
      RootPackage srcMainJava = getRootPackage(vfs, project, "src/main/java");
      if (srcMainJava != null)
      {
         rootPackages.add(srcMainJava);
      }

      RootPackage srcMainResources = getRootPackage(vfs, project, "src/main/resources");
      if (srcMainResources != null)
      {
         rootPackages.add(srcMainResources);
      }

      RootPackage srcTestJava = getRootPackage(vfs, project, "src/test/java");
      if (srcTestJava != null)
      {
         rootPackages.add(srcTestJava);
      }

      RootPackage srcTestResources = getRootPackage(vfs, project, "src/test/resources");
      if (srcTestResources != null)
      {
         rootPackages.add(srcTestResources);
      }

      return rootPackages;
   }

   private RootPackage getRootPackage(VirtualFileSystem vfs, Item project, String source)
      throws VirtualFileSystemException
   {
      String projectId = project.getId();
      try
      {
         String[] names = source.split("/");
         Item item = project;
         for (String name : names)
         {
            item = getItem(vfs, item.getId(), name);
         }

         RootPackage rootPackage = new RootPackage();
         rootPackage.setSource(source);
         rootPackage.setId(item.getId());
         rootPackage.setProjectId(projectId);
         return rootPackage;
      }
      catch (ItemNotFoundException e)
      {
         return null;
      }
   }

   private Item getItem(VirtualFileSystem vfs, String folderId, String itemName) throws VirtualFileSystemException
   {
      ItemList<Item> items = vfs.getChildren(folderId, -1, 0, PropertyFilter.ALL_FILTER);
      for (Item item : items.getItems())
      {
         if (itemName.equals(item.getName()))
         {
            return item;
         }
      }

      throw new ItemNotFoundException(itemName + " not found");
   }

   private Item getItemByRelPath(VirtualFileSystem vfs, Item parent, String relPath) throws VirtualFileSystemException
   {
      String[] names = relPath.split("/");
      for (String name : names)
      {
         parent = getItem(vfs, parent.getId(), name);
      }

      return parent;
   }

   /**
    * Get packages in RootPackage.
    * 
    * @param vfsId
    * @param projectId
    * @param rootPackage
    * @param uriInfo
    * @return
    * @throws VirtualFileSystemException
    */
   @GET
   @Path("project/packages/list")
   @Produces(MediaType.APPLICATION_JSON)
   public List<Package> getPackages(@QueryParam("vfsId") String vfsId, @QueryParam("projectId") String projectId,
      @QueryParam("source") String source, @Context UriInfo uriInfo) throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
      {
         throw new VirtualFileSystemException("Virtual file system not initialized");
      }

      Item projectItem = vfs.getItem(projectId, PropertyFilter.valueOf("*"));
      if (!(projectItem instanceof Project))
      {
         throw new VirtualFileSystemException("Not a project.");
      }

      Project project = (Project)projectItem;
      Item packageRoot = getItemByRelPath(vfs, project, source);
      if (!(packageRoot instanceof Folder))
      {
         throw new VirtualFileSystemException(source + " is not a source folder.");
      }

      List<String> folders = new ArrayList<String>();
      scanFolder(vfs, (Folder)packageRoot, false, folders);

      List<Package> packages = new ArrayList<Package>();

      String pathPrefix = projectItem.getPath() + "/" + source;
      for (String folder : folders)
      {
         Package p = new Package();
         p.setProjectId(projectId);
         p.setSource(source);
         String name = folder.substring(pathPrefix.length() + 1);
         p.setName(name);
         packages.add(p);
      }

      return packages;
   }

   private void scanFolder(VirtualFileSystem vfs, Folder folder, boolean canBePackage, List<String> folders)
      throws VirtualFileSystemException
   {
      System.out.println("scanning folder [" + folder.getPath() + "]");
      
      ItemList<Item> children = vfs.getChildren(folder.getId(), -1, 0, PropertyFilter.ALL_FILTER);
      //System.out.println("childer count > " + children.getItems().size());
      
      if (canBePackage && showInPackageList(children)) {
         folders.add(folder.getPath());
      }
      
      for (Item item : children.getItems())
      {
         //System.out.println("ITEM > " + item.getPath());
         if (item instanceof Folder)
         {
            Folder f = (Folder)item;
//
//            if (showInPackageList(children)) {
//               folders.add(f.getPath());
//            }
//            
            scanFolder(vfs, f, true, folders);
         }
      }
   }
   
   private boolean showInPackageList(ItemList<Item> children) {
      boolean hasFiles = false;
      int foldersCount = 0;
      
      for (Item item : children.getItems())  {
         if (item instanceof Folder) {
            foldersCount++;
         } else {
            hasFiles = true;
            break;
         }
      }
      
      if (!hasFiles && foldersCount == 1) {
         return false;
      }

      return true;
   }

   @GET
   @Path("project/package")
   @Produces(MediaType.APPLICATION_JSON)
   public List<AstItem> getPackageEntries(@QueryParam("vfsId") String vfsId, @QueryParam("projectId") String projectId,
      @QueryParam("packageName") String packageName, @QueryParam("packageSource") String packageSource,
      @Context UriInfo uriInfo) throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
      {
         throw new VirtualFileSystemException("Virtual file system not initialized");
      }

      Item projectItem = vfs.getItem(projectId, PropertyFilter.valueOf("*"));
      if (!(projectItem instanceof Project))
      {
         throw new VirtualFileSystemException("Not a project.");
      }

      String path = packageSource + "/" + packageName;
      Item sourceDirectory = getItemByRelPath(vfs, projectItem, path);

      List<AstItem> entries = new ArrayList<AstItem>();
      
      ItemList<Item> items = vfs.getChildren(sourceDirectory.getId(), -1, 0, PropertyFilter.ALL_FILTER);
      for (Item item : items.getItems())
      {
         if (item instanceof File)
         {
            File file = (File)item;
            
            String mimeType = file.getMimeType();
            if (mimeType.indexOf(";") > 0) {
               mimeType = mimeType.substring(0, mimeType.indexOf(";"));
            }
            
            if ("application/java".equals(mimeType))
            {
               CompilationUnit compilationUnit = new CompilationUnit();
               compilationUnit.setName(file.getName());
               entries.add(compilationUnit);
            }
         }
      }

      return entries;
   }

}
