///*
// * Copyright (C) 2012 eXo Platform SAS.
// *
// * This is free software; you can redistribute it and/or modify it
// * under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation; either version 2.1 of
// * the License, or (at your option) any later version.
// *
// * This software is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this software; if not, write to the Free
// * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
// * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
// */
//package org.eclipse.jdt.client.packaging;
//
//import com.google.gwt.core.client.Scheduler;
//import com.google.gwt.core.client.Scheduler.ScheduledCommand;
//import com.google.gwt.http.client.RequestException;
//import com.google.gwt.xml.client.Document;
//import com.google.gwt.xml.client.Element;
//import com.google.gwt.xml.client.Node;
//import com.google.gwt.xml.client.NodeList;
//import com.google.gwt.xml.client.XMLParser;
//
//import org.eclipse.jdt.client.packaging.model.Project;
//import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
//import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
//import org.exoplatform.ide.client.framework.module.IDE;
//import org.exoplatform.ide.client.framework.output.event.OutputEvent;
//import org.exoplatform.ide.client.framework.output.event.OutputMessage;
//import org.exoplatform.ide.vfs.client.VirtualFileSystem;
//import org.exoplatform.ide.vfs.client.marshal.FileContentUnmarshaller;
//import org.exoplatform.ide.vfs.client.model.FileModel;
//import org.exoplatform.ide.vfs.client.model.FolderModel;
//import org.exoplatform.ide.vfs.client.model.ProjectModel;
//import org.exoplatform.ide.vfs.shared.Item;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
// * @version $
// *
// */
//public class ProjectTreeParser
//{
//
//   public interface ParsingCompleteListener
//   {
//      void onParseComplete(Project resultItem);
//   }
//
//   public interface FolderUpdateCompleteListener
//   {
//      void onUpdateComplete(Object item);
//   }
//
//   public interface ProjectDependenciesUpdateCompleteListener
//   {
//      void onUpdateComplete(Object item);
//   }
//
//   private ParsingCompleteListener parsingCompleteListener;
//
//   private ProjectModel project;
//
//   private Project projectItem;
//
//   private List<String> projectDependencies;
//
//   public ProjectTreeParser(ProjectModel project, Project projectItem)
//   {
//      this.project = project;
//      this.projectItem = projectItem;
//   }
//
//   private List<String> getResourceDirectories()
//   {
//      ArrayList<String> sourceDirs = new ArrayList<String>();
//      sourceDirs.add("src/main/java");
//      sourceDirs.add("src/main/resources");
//      sourceDirs.add("src/test/java");
//      sourceDirs.add("src/test/resources");
//      return sourceDirs;
//   }
//
//   public void parseProjectStructure(ParsingCompleteListener parsingCompleteListener)
//   {
//      this.parsingCompleteListener = parsingCompleteListener;
//
//      projectItem.getResourceDirectories().clear();
//      projectItem.getDependencies().clear();
//      projectItem.getFolders().clear();
//      projectItem.getFiles().clear();
//
//      for (String resourceDirectory : getResourceDirectories())
//      {
//         addResourceFolders(resourceDirectory);
//      }
//
//      if (projectDependencies == null)
//      {
//         loadPomXML();
//      }
//      else
//      {
//         addFilesAndFoldersToProjectItem();
//      }
//   }
//
//   public DependencyListItem updateProjectDependencies(String pomXmlFileContent)
//   {
//      List<DependencyItem> dependencyList = getDependenciesFromPomXml(pomXmlFileContent);
//      if (dependencyList == null)
//      {
//         return null;
//      }
//
//      DependencyListItem dependencies = setProjectDependencies(dependencyList);
//      return dependencies;
//   }
//
//   /**
//    * Load content of pom.xml file
//    */
//   private void loadPomXML()
//   {
//      FileModel pomXML = null;
//      List<Item> items = project.getChildren().getItems();
//      for (Item i : items)
//      {
//         if ("pom.xml".equals(i.getName()) && i instanceof FileModel)
//         {
//            pomXML = (FileModel)i;
//         }
//      }
//
//      if (pomXML == null)
//      {
//         parseComplete();
//      }
//
//      // Load content of get pom.xml   
//      try
//      {
//         VirtualFileSystem.getInstance().getContent(
//            new AsyncRequestCallback<FileModel>(new FileContentUnmarshaller(pomXML))
//            {
//               @Override
//               protected void onSuccess(FileModel result)
//               {
//                  List<DependencyItem> dependencies = getDependenciesFromPomXml(result.getContent());
//                  setProjectDependencies(dependencies);
//                  addFilesAndFoldersToProjectItem();
//               }
//
//               @Override
//               protected void onFailure(Throwable exception)
//               {
//                  //IDE.fireEvent(new ExceptionThrownEvent(exception, "Service is not deployed.<br>Resource not found."));
//                  addFilesAndFoldersToProjectItem();
//               }
//            });
//      }
//      catch (RequestException e)
//      {
//         IDE.fireEvent(new ExceptionThrownEvent(e, "Service is not deployed.<br>Resource not found."));
//         return;
//      }
//
//   }
//
//   /**
//    * Get list of maven properties
//    *
//    * @param projectElement
//    * @return
//    */
//   private Map<String, String> getPomProperties(Element projectElement)
//   {
//      Map<String, String> properties = new HashMap<String, String>();
//
//      try
//      {
//         NodeList propertiesElements = projectElement.getElementsByTagName("properties");
//         if (propertiesElements.getLength() == 0)
//         {
//            return properties;
//         }
//
//         Element propertiesElement = (Element)propertiesElements.item(0);
//         NodeList propList = propertiesElement.getChildNodes();
//         for (int i = 0; i < propList.getLength(); i++)
//         {
//            if (Node.ELEMENT_NODE != propList.item(i).getNodeType())
//            {
//               continue;
//            }
//
//            Element propertyElement = (Element)propList.item(i);
//            String propertyName = propertyElement.getNodeName();
//            String propertyValue = propertyElement.getChildNodes().item(0).getNodeValue();
//            properties.put(propertyName, propertyValue);
//         }
//      }
//      catch (Exception e)
//      {
//         e.printStackTrace();
//      }
//
//      return properties;
//   }
//
//   private List<DependencyItem> getDependenciesFromPomXml(String pomXmlFileContent)
//   {
//      List<DependencyItem> projectDependencies = new ArrayList<DependencyItem>();
//
//      try
//      {
//         Document dom = XMLParser.parse(pomXmlFileContent);
//
//         Element projectElement = (Element)dom.getElementsByTagName("project").item(0);
//
//         Map<String, String> properties = getPomProperties(projectElement);
//
//         Element dependenciesElement = (Element)projectElement.getElementsByTagName("dependencies").item(0);
//
//         if (dependenciesElement != null)
//         {
//            NodeList dependencies = dependenciesElement.getElementsByTagName("dependency");
//            for (int i = 0; i < dependencies.getLength(); i++)
//            {
//               if (dependencies.item(i).getNodeType() != Node.ELEMENT_NODE)
//               {
//                  continue;
//               }
//
//               Element dependencyElement = (Element)dependencies.item(i);
//               Element artifactElement = (Element)dependencyElement.getElementsByTagName("artifactId").item(0);
//               Element versionElement = (Element)dependencyElement.getElementsByTagName("version").item(0);
//               if (Node.TEXT_NODE == artifactElement.getChildNodes().item(0).getNodeType()
//                  && Node.TEXT_NODE == versionElement.getChildNodes().item(0).getNodeType())
//               {
//                  String artifact = artifactElement.getChildNodes().item(0).getNodeValue();
//                  String version = versionElement.getChildNodes().item(0).getNodeValue();
//
//                  if (version.startsWith("${") && version.endsWith("}"))
//                  {
//                     version = version.substring(2, version.length() - 1);
//                     version = properties.get(version);
//                  }
//
//                  String dependency = artifact + "-" + version + ".jar";
//                  projectDependencies.add(new DependencyItem(dependency));
//               }
//            }
//         }
//      }
//      catch (Exception e)
//      {
//         e.printStackTrace();
//         IDE.fireEvent(new OutputEvent("Error parsing pom.xml.", OutputMessage.Type.ERROR));
//      }
//      return projectDependencies;
//   }
//
//   private DependencyListItem setProjectDependencies(List<DependencyItem> dependencies)
//   {
//      DependencyListItem referencedLibraries = null;
//      for (DependencyListItem dl : projectItem.getDependencies())
//      {
//         if ("Referenced Libraries".equals(dl.getName()))
//         {
//            referencedLibraries = dl;
//         }
//      }
//
//      if (referencedLibraries == null)
//      {
//         referencedLibraries = new DependencyListItem("Referenced Libraries");
//         projectItem.getDependencies().add(referencedLibraries);
//      }
//      else
//      {
//         referencedLibraries.getDependencies().clear();
//      }
//
//      if (dependencies != null)
//      {
//         referencedLibraries.getDependencies().addAll(dependencies);
//      }
//
//      return referencedLibraries;
//   }
//
//   //   /**
//   //    * Add project dependencies tree node
//   //    */
//   //   private void addProjectDependencies()
//   //   {
//   //      Scheduler.get().scheduleDeferred(new ScheduledCommand()
//   //      {
//   //         @Override
//   //         public void execute()
//   //         {
//   //            DependencyListItem referencedLibraries = new DependencyListItem("Referenced Libraries");
//   //            projectItem.getDependencies().add(referencedLibraries);
//   //
//   //            for (String dependency : projectDependencies)
//   //            {
//   //               referencedLibraries.getDependencies().add(new DependencyItem(dependency));
//   //            }
//   //
//   //            addFilesAndFoldersToProjectItem();
//   //         }
//   //      });
//   //   }
//
//   private void addFilesAndFoldersToProjectItem()
//   {
//      List<FolderModel> folders = new ArrayList<FolderModel>();
//      List<FileModel> files = new ArrayList<FileModel>();
//
//      for (Item item : project.getChildren().getItems())
//      {
//         if (item instanceof FolderModel)
//         {
//            folders.add((FolderModel)item);
//         }
//         else if (item instanceof FileModel)
//         {
//            files.add((FileModel)item);
//         }
//      }
//
//      // sort files and folders here
//
//      // add files and folders to ProjectItem
//      projectItem.getFolders().addAll(folders);
//      projectItem.getFiles().addAll(files);
//      parseComplete();
//   }
//
//   /**
//    * On parsing complete
//    */
//   private void parseComplete()
//   {
//      Scheduler.get().scheduleDeferred(new ScheduledCommand()
//      {
//         @Override
//         public void execute()
//         {
//            if (parsingCompleteListener != null)
//            {
//               parsingCompleteListener.onParseComplete(projectItem);
//            }
//         }
//      });
//   }
//
//   private void addResourceFolders(String resourceDirectory)
//   {
//      FolderModel resourceFolder = getFolderByPath(project.getChildren().getItems(), resourceDirectory, true);
//      if (resourceFolder == null)
//      {
//         return;
//      }
//
//      //ResourceDirectoryItem resourceDirectoryItem = new ResourceDirectoryItem(resourceDirectory, new FolderModel(resourceFolder));
//      ResourceDirectoryItem resourceDirectoryItem = new ResourceDirectoryItem(resourceDirectory, resourceFolder);
//      projectItem.getResourceDirectories().add(resourceDirectoryItem);
//
//      updatePackagesInResourceDirectory(resourceDirectory, resourceFolder, resourceDirectoryItem);
//   }
//
//   private void updatePackagesInResourceDirectory(String resourceDirectory, FolderModel resourceFolder,
//      ResourceDirectoryItem resourceDirectoryItem)
//   {
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
//   }
//
//   private boolean hasOnlyFolders(List<Item> items)
//   {
//      if (items.size() == 0)
//      {
//         return false;
//      }
//
//      for (Item i : items)
//      {
//         if (!(i instanceof FolderModel))
//         {
//            return false;
//         }
//      }
//
//      return true;
//   }
//
//   private boolean hasFiles(List<Item> items)
//   {
//      if (items.size() == 0)
//      {
//         return false;
//      }
//
//      for (Item i : items)
//      {
//         if (i instanceof FileModel)
//         {
//            return true;
//         }
//      }
//
//      return false;
//   }
//
//   private String getPackageName(String resourceDirectory, FolderModel folderAsAPackage)
//   {
//      String folderPathPrefix = project.getPath();
//      folderPathPrefix += (folderPathPrefix.endsWith("/") ? "" : "/") + resourceDirectory;
//
//      String path = folderAsAPackage.getPath();
//      path = path.substring(folderPathPrefix.length());
//      if (path.startsWith("/"))
//      {
//         path = path.substring(1);
//      }
//
//      path = path.replaceAll("/", ".");
//
//      return path;
//   }
//
//   private List<FolderModel> searchFoldersRecursively(FolderModel folder)
//   {
//      List<FolderModel> folders = new ArrayList<FolderModel>();
//
//      folders.add(folder);
//
//      for (Item item : folder.getChildren().getItems())
//      {
//         if (item instanceof FolderModel)
//         {
//            folders.addAll(searchFoldersRecursively((FolderModel)item));
//         }
//      }
//
//      return folders;
//   }
//
//   private FolderModel getFolderByPath(List<Item> items, String path, boolean removeFromParent)
//   {
//      String[] pathes = path.split("/");
//
//      for (int i = 0; i < pathes.length; i++)
//      {
//         String p = pathes[i];
//
//         for (Item item : items)
//         {
//            if (item.getName().equals(p) && item instanceof FolderModel)
//            {
//               if (i == pathes.length - 1)
//               {
//                  if (removeFromParent)
//                  {
//                     items.remove(item);
//                  }
//
//                  return (FolderModel)item;
//               }
//
//               items = ((FolderModel)item).getChildren().getItems();
//            }
//         }
//      }
//
//      return null;
//   }
//
//   private void searchItemInPackages(List<Object> navigateItems, ResourceDirectoryItem resourceDirectoryItem,
//      Item itemToNavigate)
//   {
//      // search for item in packages
//      for (PackageItem packageItem : resourceDirectoryItem.getPackages())
//      {
//         if (itemToNavigate.getPath().equals(packageItem.getPackageFolder().getPath()))
//         {
//            navigateItems.add(packageItem);
//            return;
//         }
//
//         if (itemToNavigate instanceof FileModel)
//         {
//            String itemFolderPath = itemToNavigate.getPath();
//            itemFolderPath = itemFolderPath.substring(0, itemFolderPath.lastIndexOf("/") + 1);
//
//            if (itemFolderPath.equals(packageItem.getPackageFolder().getPath() + "/"))
//            {
//               navigateItems.add(packageItem);
//               for (FileModel file : packageItem.getFiles())
//               {
//                  if (itemToNavigate.getPath().equals(file.getPath()))
//                  {
//                     navigateItems.add(file);
//                     return;
//                  }
//               }
//
//            }
//
//         }
//      }
//
//      // search for item in root of resource directory
//      for (FileModel file : resourceDirectoryItem.getFiles())
//      {
//         if (itemToNavigate.getPath().equals(file.getPath()))
//         {
//            navigateItems.add(file);
//            return;
//         }
//      }
//   }
//
//   private boolean searchItemInResourceFolders(List<Object> navigateItems, Item itemToNavigate)
//   {
//      for (ResourceDirectoryItem resourceDirectoryItem : projectItem.getResourceDirectories())
//      {
//         if (itemToNavigate.getPath().equals(resourceDirectoryItem.getFolder().getPath()))
//         {
//            navigateItems.add(resourceDirectoryItem);
//            return true;
//         }
//
//         if (itemToNavigate.getPath().startsWith(resourceDirectoryItem.getFolder().getPath() + "/"))
//         {
//            navigateItems.add(resourceDirectoryItem);
//            searchItemInPackages(navigateItems, resourceDirectoryItem, itemToNavigate);
//            return true;
//         }
//      }
//
//      return false;
//   }
//
//   private boolean searchItemInTreeOfItems(List<Object> navigateItems, List<Item> items, Item itemToNavigate)
//   {
//      for (Item item : items)
//      {
//         if (item instanceof FolderModel)
//         {
//            FolderModel folder = (FolderModel)item;
//
//            if (itemToNavigate.getPath().equals(folder.getPath()))
//            {
//               navigateItems.add(folder);
//               return true;
//            }
//
//            if (itemToNavigate.getPath().startsWith(folder.getPath() + "/"))
//            {
//               navigateItems.add(folder);
//               searchItemInTreeOfItems(navigateItems, folder.getChildren().getItems(), itemToNavigate);
//               return true;
//            }
//
//            continue;
//         }
//
//         if (item instanceof FileModel)
//         {
//            FileModel file = (FileModel)item;
//            if (itemToNavigate.getPath().equals(file.getPath()))
//            {
//               navigateItems.add(file);
//               return true;
//            }
//         }
//      }
//
//      return false;
//   }
//
//   public List<Object> getItemList(Item itemToNavigate)
//   {
//      List<Object> navigateItems = new ArrayList<Object>();
//      if (itemToNavigate == null)
//      {
//         return navigateItems;
//      }
//
//      if (itemToNavigate.getPath().equals(project.getPath()))
//      {
//         navigateItems.add(projectItem);
//         return navigateItems;
//      }
//
//      navigateItems.add(projectItem);
//
//      if (searchItemInResourceFolders(navigateItems, itemToNavigate))
//      {
//         return navigateItems;
//      }
//
//      List<Item> items = new ArrayList<Item>();
//      items.addAll(projectItem.getFolders());
//      items.addAll(projectItem.getFiles());
//      searchItemInTreeOfItems(navigateItems, items, itemToNavigate);
//      return navigateItems;
//   }
//
//}
