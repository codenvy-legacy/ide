/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.project.packaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.project.packaging.model.DependencyListItem;
import org.exoplatform.ide.client.project.packaging.model.PackageItem;
import org.exoplatform.ide.client.project.packaging.model.ProjectItem;
import org.exoplatform.ide.client.project.packaging.model.ResourceDirectoryItem;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class ProjectTreeParser
{

   public interface ParsingCompleteListener
   {

      void onParseComplete();

   }

   public interface FolderUpdateCompleteListener
   {

      void onUpdateComplete(Object item);

   }

   private ParsingCompleteListener parsingCompleteListener;

   private ProjectModel project;

   private ProjectItem projectItem;

   public ProjectTreeParser(ProjectModel project, ProjectItem projectItem)
   {
      this.project = project;
      this.projectItem = projectItem;
   }

   private List<String> getResourceDirectories()
   {
      ArrayList<String> sourceDirs = new ArrayList<String>();
      sourceDirs.add("src/main/java");
      sourceDirs.add("src/main/resources");
      sourceDirs.add("src/test/java");
      sourceDirs.add("src/test/resources");
      return sourceDirs;
   }

   public void parseProjectStructure(ParsingCompleteListener parsingCompleteListener)
   {
      this.parsingCompleteListener = parsingCompleteListener;

      for (String resourceDirectory : getResourceDirectories())
      {
         addResourceFolders(resourceDirectory);
      }

      loadPomXML();
   }

   /**
    * Load content of pom.xml file
    */
   private void loadPomXML()
   {
      FileModel pomXML = null;
      List<Item> items = project.getChildren().getItems();
      for (Item i : items)
      {
         if ("pom.xml".equals(i.getName()) && i instanceof FileModel)
         {
            pomXML = (FileModel)i;
         }
      }

      if (pomXML == null)
      {
         parseComplete();
      }

      // Load content of get pom.xml   
      try
      {
         VirtualFileSystem.getInstance().getContent(
            new AsyncRequestCallback<FileModel>(new FileContentUnmarshaller(pomXML))
            {
               @Override
               protected void onSuccess(FileModel result)
               {
                  parsePomXML(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception, "Service is not deployed.<br>Resource not found."));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e, "Service is not deployed.<br>Resource not found."));
         return;
      }

   }

   /**
    * Get list of maven properties
    * 
    * @param projectElement
    * @return
    */
   private Map<String, String> getPomProperties(Element projectElement)
   {
      Map<String, String> properties = new HashMap<String, String>();

      try
      {
         NodeList propertiesElements = projectElement.getElementsByTagName("properties");
         if (propertiesElements.getLength() == 0)
         {
            return properties;
         }

         Element propertiesElement = (Element)propertiesElements.item(0);
         NodeList propList = propertiesElement.getChildNodes();
         for (int i = 0; i < propList.getLength(); i++)
         {
            if (Node.ELEMENT_NODE != propList.item(i).getNodeType())
            {
               continue;
            }

            Element propertyElement = (Element)propList.item(i);
            String propertyName = propertyElement.getNodeName();
            String propertyValue = propertyElement.getChildNodes().item(0).getNodeValue();
            properties.put(propertyName, propertyValue);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      return properties;
   }

   /**
    * Parse pom.xml file
    * 
    * @param pomXML
    */
   private void parsePomXML(FileModel pomXML)
   {
      List<String> projectDependencies = new ArrayList<String>();

      Document dom = XMLParser.parse(pomXML.getContent());
      try
      {
         Element projectElement = (Element)dom.getElementsByTagName("project").item(0);

         Map<String, String> properties = getPomProperties(projectElement);

         Element dependenciesElement = (Element)projectElement.getElementsByTagName("dependencies").item(0);

         NodeList dependencies = dependenciesElement.getElementsByTagName("dependency");
         for (int i = 0; i < dependencies.getLength(); i++)
         {
            if (dependencies.item(i).getNodeType() != Node.ELEMENT_NODE)
            {
               continue;
            }

            Element dependencyElement = (Element)dependencies.item(i);
            Element artifactElement = (Element)dependencyElement.getElementsByTagName("artifactId").item(0);
            Element versionElement = (Element)dependencyElement.getElementsByTagName("version").item(0);
            if (Node.TEXT_NODE == artifactElement.getChildNodes().item(0).getNodeType()
               && Node.TEXT_NODE == versionElement.getChildNodes().item(0).getNodeType())
            {
               String artifact = artifactElement.getChildNodes().item(0).getNodeValue();
               String version = versionElement.getChildNodes().item(0).getNodeValue();

               if (version.startsWith("${") && version.endsWith("}"))
               {
                  version = version.substring(2, version.length() - 1);
                  version = properties.get(version);
               }

               String dependency = artifact + "-" + version + ".jar";
               projectDependencies.add(dependency);
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      addProjectDependencies(projectDependencies);
   }

   /**
    * Add project dependencies tree node
    * 
    * @param dependencies
    */
   private void addProjectDependencies(final List<String> dependencies)
   {
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            DependencyListItem referencedLibraries = new DependencyListItem("Referenced Libraries");
            projectItem.getDependencies().add(referencedLibraries);

            for (String dependency : dependencies)
            {
               referencedLibraries.getDependencies().add(dependency);
            }

            addFilesAndFolders();
         }
      });
   }

   private void addFilesAndFolders()
   {
      List<FolderModel> folders = new ArrayList<FolderModel>();
      List<FileModel> files = new ArrayList<FileModel>();

      for (Item item : project.getChildren().getItems())
      {
         if (item instanceof FolderModel)
         {
            folders.add((FolderModel)item);
         }
         else if (item instanceof FileModel)
         {
            files.add((FileModel)item);
         }
      }

      // sort files and folders here

      // add files and folders to ProjectItem
      projectItem.getFolders().addAll(folders);
      projectItem.getFiles().addAll(files);

      //PackageExplorerLogger.dump(project);
      parseComplete();
   }

   /**
    * On parsing complete
    */
   private void parseComplete()
   {
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            if (parsingCompleteListener != null)
            {
               parsingCompleteListener.onParseComplete();
            }
         }
      });
   }

   private void addResourceFolders(String resourceDirectory)
   {
      FolderModel resourceFolder = getFolderByPath(project.getChildren().getItems(), resourceDirectory, true);
      if (resourceFolder == null)
      {
         return;
      }

      //ResourceDirectoryItem resourceDirectoryItem = new ResourceDirectoryItem(resourceDirectory, new FolderModel(resourceFolder));
      ResourceDirectoryItem resourceDirectoryItem = new ResourceDirectoryItem(resourceDirectory, resourceFolder);
      projectItem.getResourceDirectories().add(resourceDirectoryItem);

      updatePackagesInResourceDirectory(resourceDirectory, resourceFolder, resourceDirectoryItem);

      //      List<FolderModel> resourceFolders = new ArrayList<FolderModel>();
      //      resourceFolders.addAll(searchFoldersRecursively(resourceFolder));
      //      for (FolderModel folder : resourceFolders)
      //      {
      //         if (folder.getChildren().getItems().size() == 0 && !resourceFolder.getPath().equals(folder.getPath()))
      //         {
      //            // add as empty package
      //            String packageName = getPackageName(resourceDirectory, folder);
      //            PackageItem packageItem = new PackageItem(packageName, resourceDirectory, folder);
      //            resourceDirectoryItem.getPackages().add(packageItem);
      //         }
      //         else if (hasFiles(folder.getChildren().getItems()))
      //         {
      //            // add files directly to Resource Folder
      //            if (resourceFolder.getPath().equals(folder.getPath()))
      //            {
      //               for (Item i : folder.getChildren().getItems())
      //               {
      //                  if (i instanceof FileModel)
      //                  {                     
      //                     resourceDirectoryItem.getFiles().add(new FileModel((FileModel)i));
      //                  }
      //               }
      //            }
      //            else
      //            {
      //               String packageName = getPackageName(resourceDirectory, folder);
      //               PackageItem packageItem = new PackageItem(packageName, resourceDirectory, folder);
      //               resourceDirectoryItem.getPackages().add(packageItem);
      //               
      //               for (Item i : folder.getChildren().getItems())
      //               {
      //                  if (i instanceof FileModel)
      //                  {
      //                     packageItem.getFiles().add((FileModel)i);
      //                  }
      //               }
      //            }
      //         }
      //         else if (hasOnlyFolders(folder.getChildren().getItems()))
      //         {
      //            // do not add
      //         }
      //      }
   }

   private void updatePackagesInResourceDirectory(String resourceDirectory, FolderModel resourceFolder,
      ResourceDirectoryItem resourceDirectoryItem)
   {
      List<FolderModel> resourceFolders = new ArrayList<FolderModel>();
      resourceFolders.addAll(searchFoldersRecursively(resourceFolder));
      for (FolderModel folder : resourceFolders)
      {
         if (folder.getChildren().getItems().size() == 0 && !resourceFolder.getPath().equals(folder.getPath()))
         {
            // add as empty package
            String packageName = getPackageName(resourceDirectory, folder);
            PackageItem packageItem = new PackageItem(packageName, resourceDirectory, folder);
            resourceDirectoryItem.getPackages().add(packageItem);
         }
         else if (hasFiles(folder.getChildren().getItems()))
         {
            // add files directly to Resource Folder
            if (resourceFolder.getPath().equals(folder.getPath()))
            {
               for (Item i : folder.getChildren().getItems())
               {
                  if (i instanceof FileModel)
                  {
                     resourceDirectoryItem.getFiles().add(new FileModel((FileModel)i));
                  }
               }
            }
            else
            {
               String packageName = getPackageName(resourceDirectory, folder);
               PackageItem packageItem = new PackageItem(packageName, resourceDirectory, folder);
               resourceDirectoryItem.getPackages().add(packageItem);

               for (Item i : folder.getChildren().getItems())
               {
                  if (i instanceof FileModel)
                  {
                     packageItem.getFiles().add((FileModel)i);
                  }
               }
            }
         }
         else if (hasOnlyFolders(folder.getChildren().getItems()))
         {
            // do not add
         }
      }
   }

   private boolean hasOnlyFolders(List<Item> items)
   {
      if (items.size() == 0)
      {
         return false;
      }

      for (Item i : items)
      {
         if (!(i instanceof FolderModel))
         {
            return false;
         }
      }

      return true;
   }

   private boolean hasFiles(List<Item> items)
   {
      if (items.size() == 0)
      {
         return false;
      }

      for (Item i : items)
      {
         if (i instanceof FileModel)
         {
            return true;
         }
      }

      return false;
   }

   private String getPackageName(String resourceDirectory, FolderModel folderAsAPackage)
   {
      String folderPathPrefix = project.getPath();
      folderPathPrefix += (folderPathPrefix.endsWith("/") ? "" : "/") + resourceDirectory;

      String path = folderAsAPackage.getPath();
      path = path.substring(folderPathPrefix.length());
      if (path.startsWith("/"))
      {
         path = path.substring(1);
      }

      path = path.replaceAll("/", ".");

      return path;
   }

   private List<FolderModel> searchFoldersRecursively(FolderModel folder)
   {
      List<FolderModel> folders = new ArrayList<FolderModel>();

      folders.add(folder);

      for (Item item : folder.getChildren().getItems())
      {
         if (item instanceof FolderModel)
         {
            folders.addAll(searchFoldersRecursively((FolderModel)item));
         }
      }

      return folders;
   }

   private FolderModel getFolderByPath(List<Item> items, String path, boolean removeFromParent)
   {
      String[] pathes = path.split("/");

      for (int i = 0; i < pathes.length; i++)
      {
         String p = pathes[i];

         for (Item item : items)
         {
            if (item.getName().equals(p) && item instanceof FolderModel)
            {
               if (i == pathes.length - 1)
               {
                  if (removeFromParent)
                  {
                     items.remove(item);
                  }

                  return (FolderModel)item;
               }

               items = ((FolderModel)item).getChildren().getItems();
            }
         }
      }

      return null;
   }

   private FolderUpdateCompleteListener folderUpdateCompleteListener;

   public void updateFolder(Folder folder, FolderUpdateCompleteListener listener)
   {
      folderUpdateCompleteListener = listener;

      if (refreshIfResourceDirectoryFolder(folder))
         return;

      if (refreshIfPackageFolder(folder))
         return;

      if (refreshIfFolder(folder))
         return;

      //      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      //      {
      //         @Override
      //         public void execute()
      //         {
      //            folderUpdateCompleteListener.onUpdateComplete(projectItem);
      //         }
      //      });
   }

   private boolean refreshIfResourceDirectoryFolder(Folder folder)
   {
      ResourceDirectoryItem resourceDirectoryItem = null; //asResourceDirectory(folder.getPath());

      for (ResourceDirectoryItem rdi : projectItem.getResourceDirectories())
      {
         if (rdi.getFolder().getPath().equals(folder.getPath()))
         {
            resourceDirectoryItem = rdi;
            break;
         }
      }

      if (resourceDirectoryItem == null)
      {
         System.out.println(">>> FOLDER is NOT A RESOURCE DIRECTORY!!!");
         return false;
      }

      System.out.println("FOLDER IS RESOURCE DIRECTORY");

      // read resource directory tree

      IDELoader.show("Loading project structure...");

      try
      {
         FolderModel folderToRefresh = new FolderModel(folder);
         FolderTreeUnmarshaller unmarshaller = new FolderTreeUnmarshaller(folderToRefresh, project);
         AsyncRequestCallback<FolderModel> callback = new AsyncRequestCallback<FolderModel>(unmarshaller)
         {
            @Override
            protected void onSuccess(FolderModel result)
            {
               IDELoader.hide();
               resourceDirectoryTreeReceived(result);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDELoader.hide();
               IDE.fireEvent(new ExceptionThrownEvent("Error loading project structure"));
               exception.printStackTrace();
            }
         };

         VirtualFileSystem.getInstance().getFolderTree(folderToRefresh, callback);
      }
      catch (Exception e)
      {
         IDELoader.hide();
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }

      return true;

      //      String resourceDirectoryPathPrefix = getResourceDirectoryPathPrefix(resourceDirectoryItem.getFolder().getPath());
      //      System.out.println("resource directory path prefix [" + resourceDirectoryPathPrefix + "]");
      //      if (resourceDirectoryPathPrefix == null)
      //      {
      //         System.out.println("ERROR FETCHING RESOURCE DIRECTORY PATH PREFIX !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      //         return null;
      //      }
      //      
      //      resourceDirectoryItem.getFolder().getChildren().getItems().clear();
      //      
      //      updatePackagesInResourceDirectory(resourceDirectoryPathPrefix, resourceDirectoryItem.getFolder(), resourceDirectoryItem);
      //
      //      return resourceDirectoryItem;
      //      return false;
   }

   private void resourceDirectoryTreeReceived(FolderModel folder)
   {
      System.out.println("RESOURCE DIRECTORY TREE RECEIVED");

   }

   private boolean refreshIfPackageFolder(Folder packageFolder)
   {
      return false;
   }

   private boolean refreshIfFolder(Folder folder)
   {
      return false;
   }

   /*****************************************************************************************
    * 
    * DEPRECATED FUNCTIONALITY
    * 
    *****************************************************************************************/

   public Object updateFolderStructure(Folder folder, List<Item> children)
   {
      // search folder's item
      // System.out.println("updating folder's children");
      // System.out.println("folder > " + folder.getPath());

      // if is resource directory -> reparse all it's packages
      ResourceDirectoryItem resourceDirectoryItem = refreshIfIsResourceDirectoryItem(folder, children);
      if (resourceDirectoryItem != null)
         return resourceDirectoryItem;

      // if is package -> reparse package children only
      PackageItem packageItem = refreshIfIsPackage(folder, children);
      if (packageItem != null)
         return packageItem;

      // if folder is project -> parse whole project structure

      // if one of the resource folder -> parse only resource folder

      return null;
   }

   /**
    * Refresh packages in resource directory if folder points to existed resource directory.
    * 
    * @param folder
    * @param children
    * @return
    */
   private ResourceDirectoryItem refreshIfIsResourceDirectoryItem(Folder folder, List<Item> children)
   {
      ResourceDirectoryItem resourceDirectoryItem = null; //asResourceDirectory(folder.getPath());

      for (ResourceDirectoryItem rdi : projectItem.getResourceDirectories())
      {
         if (rdi.getFolder().getPath().equals(folder.getPath()))
         {
            resourceDirectoryItem = rdi;
            break;
         }
      }

      if (resourceDirectoryItem == null)
      {
//         System.out.println(">>> FOLDER is NOT A RESOURCE DIRECTORY!!!");
         return null;
      }

//      System.out.println("FOLDER IS RESOURCE DIRECTORY");

      String resourceDirectoryPathPrefix = getResourceDirectoryPathPrefix(resourceDirectoryItem.getFolder().getPath());
//      System.out.println("resource directory path prefix [" + resourceDirectoryPathPrefix + "]");
      if (resourceDirectoryPathPrefix == null)
      {
//         System.out
//            .println("ERROR FETCHING RESOURCE DIRECTORY PATH PREFIX !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
         return null;
      }

      resourceDirectoryItem.getFolder().getChildren().getItems().clear();

      updatePackagesInResourceDirectory(resourceDirectoryPathPrefix, resourceDirectoryItem.getFolder(),
         resourceDirectoryItem);

      return resourceDirectoryItem;
   }

   private String getResourceDirectoryPathPrefix(String resourceDirectoryFullPath)
   {
      for (String resDir : getResourceDirectories())
      {
         if (resourceDirectoryFullPath.endsWith(resDir))
         {
            return resDir;
         }
      }

      return null;
   }

   /**
    * Refresh package items if <b>folder</b> points to a package.
    * 
    * @param folder
    * @param children
    * @return
    */
   private PackageItem refreshIfIsPackage(Folder folder, List<Item> children)
   {
      PackageItem packageItem = asPackage(folder.getPath());
      if (packageItem == null)
      {
         return null;
      }

//      System.out.println(">>> FOLDER is PACKAGE DIRECTORY!!!");

      packageItem.getFiles().clear();
      for (Item item : children)
      {
         if (item instanceof ItemContext)
         {
            ((ItemContext)item).setParent(packageItem.getPackageFolder());
            ((ItemContext)item).setProject(project);
         }

         if (item instanceof FolderModel)
         {
//            System.out.println("FOUNDED SUBFOLDER > " + item.getPath());
         }

         if (item instanceof FileModel)
         {
            packageItem.getFiles().add((FileModel)item);
         }
      }

      return packageItem;
   }

   /**
    * Returns {@link PackageItem} if path points to a package.
    * 
    * @param path
    * @return
    */
   private PackageItem asPackage(String path)
   {
      boolean belongToResources = false;
      for (String resourceDirectory : getResourceDirectories())
      {
         String resourceDirectoryPath = project.getPath() + "/" + resourceDirectory + "/";
//         System.out.println("resource directory path > " + resourceDirectoryPath);
         if (path.startsWith(resourceDirectoryPath))
         {
            belongToResources = true;
            break;
         }
      }

      if (!belongToResources)
      {
//         System.out.println("FOLDER not is package");
         return null;
      }

//      System.out.println("FOLDER is package");

      for (ResourceDirectoryItem resourceDirectoryItem : projectItem.getResourceDirectories())
      {
         for (PackageItem packageItem : resourceDirectoryItem.getPackages())
         {
            if (packageItem.getPackageFolder().getPath().equals(path))
            {
               return packageItem;
            }
         }
      }

      return null;
   }

}
