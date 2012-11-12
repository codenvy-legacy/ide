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
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
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

   public interface Display extends IsView
   {
      String ID = "ideCreateJavaClass";

      HasClickHandlers getCancelButton();

      HasClickHandlers getCreateButton();

      HasValue<String> getNameField();

      HasValue<String> getTypeSelect();

      void setTypes(Collection<String> types);

      void setCreateButtonEnabled(boolean enabled);
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

   private final IDE ide;

   private final VirtualFileSystem vfs;

   private HandlerRegistration fileOpenedHandler;

   /**
    * @param eventBus
    */
   public CreateJavaClassPresenter(HandlerManager eventBus, VirtualFileSystem vfs, IDE ide)
   {
      super();
      this.eventBus = eventBus;
      this.vfs = vfs;
      this.ide = ide;
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
      ide.openView(display.asView());
      bind();
   }

   /**
    * 
    */
   private void bind()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            ide.closeView(Display.ID);
         }
      });

      display.getCreateButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doCreate();
         }
      });

      display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (event.getValue() != null && !event.getValue().isEmpty())
            {
               display.setCreateButtonEnabled(true);
            }
            else
               display.setCreateButtonEnabled(false);
         }
      });

      display.setCreateButtonEnabled(false);
      List<String> types = new ArrayList<String>();
      for (JavaTypes t : JavaTypes.values())
      {
         types.add(t.toString());

      }
      display.setTypes(types);
   }

   /**
    * 
    */
   private void doCreate()
   {
      try
      {
         switch (JavaTypes.valueOf(display.getTypeSelect().getValue().toUpperCase()))
         {
            case CLASS :
               createClass(display.getNameField().getValue());
               break;

            case INTERFACE :
               createInterface(display.getNameField().getValue());
               break;

            case ENUM :
               createEnum(display.getNameField().getValue());
               break;

            case ANNOTATION :
               createAnnotation(display.getNameField().getValue());
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
      createFile(name, content.toString());
   }

   /**
    * @param value
    */
   private void createEnum(String name)
   {
      StringBuilder content = new StringBuilder(getPackage());
      content.append("public enum ").append(name).append(TYPE_CONTENT);
      createFile(name, content.toString());

   }

   /**
    * @param value
    */
   private void createInterface(String name)
   {
      StringBuilder content = new StringBuilder(getPackage());
      content.append("public interface ").append(name).append(TYPE_CONTENT);
      createFile(name, content.toString());
   }

   /**
    * @param value
    */
   private void createClass(String name)
   {
      StringBuilder content = new StringBuilder(getPackage());
      content.append("public class ").append(name).append(TYPE_CONTENT);
      createFile(name, content.toString());
   }

   /**
    * @param string
    */
   private void createFile(String name, String content)
   {
      FileModel newFile = new FileModel(name + ".java", MimeType.APPLICATION_JAVA, content, parentFolder);
      try
      {
         vfs.createFile(parentFolder, new AsyncRequestCallback<FileModel>(new FileUnmarshaller(newFile))
         {

            @Override
            protected void onSuccess(FileModel result)
            {
               ide.closeView(Display.ID);
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

   /**
    * @return
    */
   private String getPackage()
   {
      String sourcePath =
         project.hasProperty("sourceFolder") ? (String)project.getPropertyValue("sourceFolder") : DEFAULT_SOURCE_FOLDER;
      String parentPath = parentFolder.getPath().endsWith("/") ? parentFolder.getPath() : parentFolder.getPath() + "/";
      String packageText = parentPath.substring((project.getPath() + "/" + sourcePath + "/").length());
      if (packageText.isEmpty())
         return "";
      if (packageText.endsWith("/"))
         packageText = packageText.substring(0, packageText.length() - 1);
      packageText = packageText.replaceAll("/", ".");;
      return "package " + packageText + ";\n\n";

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
         parentFolder = null;
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView().getId().equals(Display.ID))
         display = null;
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
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      fileOpenedHandler.removeHandler();
      //eventBus.fireEvent(new GoToFolderEvent());
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            IDE.fireEvent(new RefreshBrowserEvent(parentFolder));
         }
      });
   }

}
