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
package org.eclipse.jdt.client.create;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.client.event.CreateJavaClassEvent;
import org.eclipse.jdt.client.event.CreateJavaClassHandler;
import org.eclipse.jdt.client.packaging.PackageExplorerPresenter;
import org.eclipse.jdt.client.packaging.model.PackageItem;
import org.eclipse.jdt.client.packaging.model.ProjectItem;
import org.eclipse.jdt.client.packaging.model.ResourceDirectoryItem;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.ActiveProjectChangedEvent;
import org.exoplatform.ide.client.framework.project.ActiveProjectChangedHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CreateJavaClassPresenter implements CreateJavaClassHandler, ViewClosedHandler, ItemsSelectedHandler,
   ProjectOpenedHandler, ProjectClosedHandler, EditorActiveFileChangedHandler, ActiveProjectChangedHandler
{

   /**
    * 
    */
   private static final String TYPE_CONTENT = "\n{\n}";
   
   private static final String DEFAULT_PACKAGE = "(default package)";

   public interface Display extends IsView
   {
      HasValue<String> sourceFolderField();
      
      void setSourceFolders(Collection<String> sourceFolders);

      HasValue<String> packageField();
      
      void setPackages(Collection<String> packages);

      HasValue<String> classNameField();
      
      void focusInClassNameField();
      
      HasValue<String> classTypeField();
      
      void setClassTypes(Collection<String> types);
      
      HasClickHandlers createButton();

      void enableCreateButton(boolean enabled);

      HasClickHandlers cancelButton();

   }

   private enum JavaTypes {
      CLASS("Class"), INTERFACE("Interface"), ENUM("Enum"), ANNOTATION("Annotation");

      private String value;

      private JavaTypes(String value)
      {
         this.value = value;
      }

      /**
       * @see java.lang.Enum#toString()
       */
      @Override
      public String toString()
      {
         return value;
      }
   }

   /**
    * Default Maven 'sourceDirectory' value
    */
   public static final String DEFAULT_SOURCE_FOLDER = "src/main/java";

   private HandlerManager eventBus;

   private Display display;

   private ProjectModel project;

   private FolderModel parentFolder;
   
   private Item selectedItem;
   
   FolderModel classParentFolder;

   private final VirtualFileSystem vfs;

   private HandlerRegistration fileOpenedHandler;

   /**
    * @param eventBus
    */
   public CreateJavaClassPresenter(HandlerManager eventBus, VirtualFileSystem vfs)
   {
      super();
      this.eventBus = eventBus;
      this.vfs = vfs;
      eventBus.addHandler(CreateJavaClassEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(ProjectOpenedEvent.TYPE, this);
      eventBus.addHandler(ProjectClosedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(ActiveProjectChangedEvent.TYPE, this);
   }

   /**
    * @see org.eclipse.jdt.client.event.CreateJavaClassHandler#onCreateJavaClass(org.eclipse.jdt.client.event.CreateJavaClassEvent)
    */
   @Override
   public void onCreateJavaClass(CreateJavaClassEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
      }
      
      IDE.getInstance().openView(display.asView());
      bindDisplay();
   }

   /**
    * 
    */
   private void bindDisplay()
   {
      display.cancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.createButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doCreate();
         }
      });

      display.classNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (event.getValue() != null && !event.getValue().isEmpty())
            {
               display.enableCreateButton(true);
            }
            else
               display.enableCreateButton(false);
         }
      });
      
      ((HasKeyPressHandlers)display.classNameField()).addKeyPressHandler(new KeyPressHandler()
      {
         @Override
         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
            {
               doCreate();
            }            
         }
      });
      
      
      List<String> types = new ArrayList<String>();
      for (JavaTypes t : JavaTypes.values())
      {
         types.add(t.toString());

      }
      display.setClassTypes(types);
      display.enableCreateButton(false);
      
           
      display.sourceFolderField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            List<String> packages = getPackagesInSourceFolder(event.getValue());
            display.setPackages(packages);
         }
      });

      
      ProjectItem projectItem = PackageExplorerPresenter.getInstance().getProjectItem();
      if (projectItem != null)
      {
         List<String> sourceFolders = new ArrayList<String>();
         for (ResourceDirectoryItem resourceDirectory : projectItem.getResourceDirectories())
         {
            sourceFolders.add(resourceDirectory.getName());
         }
         display.setSourceFolders(sourceFolders);         
      }
      
      showCurrentPackage();
      display.focusInClassNameField();
   }
   
   private List<String> getPackagesInSourceFolder(String sourceFolder)
   {
      List<String> packages = new ArrayList<String>();
      packages.add(0, DEFAULT_PACKAGE);      
      
      List<PackageItem> packageItems = new ArrayList<PackageItem>();
      ProjectItem projectItem = PackageExplorerPresenter.getInstance().getProjectItem();
      for (ResourceDirectoryItem resourceDirectoryItem : projectItem.getResourceDirectories())
      {
         if (sourceFolder.equals(resourceDirectoryItem.getName()))
         {
            packageItems.addAll(resourceDirectoryItem.getPackages());            
         }
      }

      for (PackageItem pi : packageItems)
      {
         String []parts = pi.getPackageName().split("\\.");
         String packageName = "";
         for (String part : parts)
         {
            packageName += (packageName.isEmpty() ? "" : ".") + part;
            
            if (!packages.contains(packageName))
            {
               packages.add(packageName);
            }
         }
      }
      
      return packages;
   }
   
   private void showCurrentPackage()
   {
      if (selectedItem == null)
      {
         return;
      }
      
      ResourceDirectoryItem resourceDirectory = null;
      
      ProjectItem projectItem = PackageExplorerPresenter.getInstance().getProjectItem();
      if (projectItem != null)
      {
         for (ResourceDirectoryItem rd : projectItem.getResourceDirectories())
         {
            if (selectedItem.getPath().startsWith(rd.getFolder().getPath()))
            {
               resourceDirectory = rd;
               break;
            }
         }
      }
      
      if (resourceDirectory != null)
      {
         display.sourceFolderField().setValue(resourceDirectory.getName());
      }
      
      List<String> packages = getPackagesInSourceFolder(display.sourceFolderField().getValue());
      if (packages.size() > 0)
      {
         display.setPackages(packages);            
      }
      
      if (resourceDirectory != null)
      {
         String packageName = parentFolder.getPath().substring(resourceDirectory.getFolder().getPath().length());
         packageName = packageName.replaceAll("/", "\\.");
         if (packageName.startsWith("."))
         {
            packageName = packageName.substring(1);
         }
         
         display.packageField().setValue(packageName);
      }
   }

   /**
    * 
    */
   private void doCreate()
   {
      if (display.classNameField().getValue() == null || display.classNameField().getValue().isEmpty())
      {
         return;
      }
      
      try
      {
         switch (JavaTypes.valueOf(display.classTypeField().getValue().toUpperCase()))
         {
            case CLASS :
               createClass(display.classNameField().getValue());
               break;

            case INTERFACE :
               createInterface(display.classNameField().getValue());
               break;

            case ENUM :
               createEnum(display.classNameField().getValue());
               break;

            case ANNOTATION :
               createAnnotation(display.classNameField().getValue());
               break;
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @param value
    */
   private void createAnnotation(String name)
   {
      StringBuilder content = new StringBuilder(getPackage());
      content.append("public @interface ").append(name).append(TYPE_CONTENT);
      createClassFile(name, content.toString());
   }

   /**
    * @param value
    */
   private void createEnum(String name)
   {
      StringBuilder content = new StringBuilder(getPackage());
      content.append("public enum ").append(name).append(TYPE_CONTENT);
      createClassFile(name, content.toString());

   }

   /**
    * @param value
    */
   private void createInterface(String name)
   {
      StringBuilder content = new StringBuilder(getPackage());
      content.append("public interface ").append(name).append(TYPE_CONTENT);
      createClassFile(name, content.toString());
   }

   /**
    * @param value
    */
   private void createClass(String name)
   {
      StringBuilder content = new StringBuilder(getPackage());
      content.append("public class ").append(name).append(TYPE_CONTENT);
      createClassFile(name, content.toString());
   }
   
   private void createClassFile(final String fileName, final String fileContent)
   {
      String sourceFolder = display.sourceFolderField().getValue();
      String packageName = display.packageField().getValue();
      
      String path = null;      
      ProjectItem projectItem = PackageExplorerPresenter.getInstance().getProjectItem();
      for (ResourceDirectoryItem resourceDirectoryItem : projectItem.getResourceDirectories())
      {
         if (sourceFolder.equals(resourceDirectoryItem.getName()))
         {
            path = resourceDirectoryItem.getFolder().getPath();
            break;     
         }
      }
      
      if (path == null)
      {
         return;
      }
      
      if (!DEFAULT_PACKAGE.equals(packageName))
      {
         String packagePath = packageName.replaceAll("\\.", "/");
         path += "/" + packagePath;
      }
      
      try
      {
         VirtualFileSystem.getInstance().getItemByPath(path,
            new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(new FolderModel())))
            {
               @Override
               protected void onSuccess(ItemWrapper result)
               {
                  classParentFolder = (FolderModel)result.getItem();
                  FileModel newFile = new FileModel(fileName + ".java", MimeType.APPLICATION_JAVA, fileContent, classParentFolder);
                  try
                  {
                     vfs.createFile(classParentFolder, new AsyncRequestCallback<FileModel>(new FileUnmarshaller(newFile))
                     {
                        @Override
                        protected void onSuccess(FileModel result)
                        {
                           IDE.getInstance().closeView(display.asView().getId());
                           result.setProject(project);
                           fileOpenedHandler = eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, CreateJavaClassPresenter.this);
                           eventBus.fireEvent(new OpenFileEvent(result));
                        }

                        @Override
                        protected void onFailure(Throwable exception)
                        {
                           eventBus.fireEvent(new ExceptionThrownEvent(exception));
                        }
                     });
                  }
                  catch (RequestException e)
                  {
                     eventBus.fireEvent(new ExceptionThrownEvent(e));
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }
   
   /**
    * @return
    */
   private String getPackage()
   {
      if (DEFAULT_PACKAGE.equals(display.packageField().getValue()))
      {
         return "";
      }
      
      String packageName = display.packageField().getValue();
      return "package " + packageName + ";\n\n";
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      project = event.getProject();
   }
   
   
   @Override
   public void onActiveProjectChanged(ActiveProjectChangedEvent event)
   {
      project = event.getProject();
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (!event.getSelectedItems().isEmpty())
      {
         selectedItem = event.getSelectedItems().get(0);
         
         Item item = event.getSelectedItems().get(0);
         if (item instanceof FolderModel)
            parentFolder = (FolderModel)item;
         else
            if(item instanceof ProjectModel)
               parentFolder = new FolderModel((Folder)item);
            else
            parentFolder = ((FileModel)item).getParent();
      }
      else
      {
         parentFolder = null;
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      project = null;
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(final EditorActiveFileChangedEvent event)
   {
      fileOpenedHandler.removeHandler();
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            IDE.fireEvent(new RefreshBrowserEvent(classParentFolder, event.getFile()));
         }
      });
   }

}
