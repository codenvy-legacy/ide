/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.extension.groovy.client.classpath.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.framework.event.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.event.ProjectCreatedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.extension.groovy.client.classpath.EnumSourceType;
import org.exoplatform.ide.extension.groovy.client.classpath.GroovyClassPathEntry;
import org.exoplatform.ide.extension.groovy.client.classpath.GroovyClassPathUtil;
import org.exoplatform.ide.extension.groovy.client.classpath.ui.event.AddSourceToBuildPathEvent;
import org.exoplatform.ide.extension.groovy.client.classpath.ui.event.AddSourceToBuildPathHandler;
import org.exoplatform.ide.extension.groovy.client.service.groovy.GroovyService;
import org.exoplatform.ide.extension.groovy.client.service.groovy.marshal.ClassPath;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.FileUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Presenter for configuring class path file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 6, 2011 $
 *
 */
public class ConfigureBuildPathPresenter implements ProjectCreatedHandler, AddSourceToBuildPathHandler,
   ConfigurationReceivedSuccessfullyHandler, ItemsSelectedHandler, EditorFileOpenedHandler, VfsChangedHandler
{
   public interface Display extends IsView
   {
      /**
       * Get add source button.
       * 
       * @return {@link HasClickHandlers} add source button
       */
      HasClickHandlers getAddButton();

      /**
       * Get remove source button.
       * 
       * @return {@link HasClickHandlers} remove source button
       */
      HasClickHandlers getRemoveButton();

      /**
       * Get save classpath button.
       * 
       * @return {@link HasClickHandlers} save classpath button
       */
      HasClickHandlers getSaveButton();

      /**
       * Get cancel button.
       * 
       * @return {@link HasClickHandlers} cancel button
       */
      HasClickHandlers getCancelButton();

      ListGridItem<GroovyClassPathEntry> getClassPathEntryListGrid();

      /**
       * Change the state of remove button.
       * 
       * @param isEnabled is enabled or not
       */
      void enableRemoveButton(boolean isEnabled);

      List<GroovyClassPathEntry> getSelectedItems();

      void setCurrentRepository(String repository);

   }

   /**
    * Display.
    */
   private Display display;

   /**
    * Handler manager.
    */
   private HandlerManager eventBus;

   /**
    * Classpath file.
    */
   private FileModel classPathFile;

   /**
    * REST context.
    */
   private String restContext;

   /**
    * Current entry point.
    */
   private String currentEntryPoint;

   /**
    * Selected items in browser tree.
    */
   private Item selectedItem;

   /**
    * Opened file in editor.
    */
   private Map<String, FileModel> openedFiles;
   
   private VirtualFileSystemInfo vfsInfo;

   /**
    * @param eventBus
    */
   public ConfigureBuildPathPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(ProjectCreatedEvent.TYPE, this);
      eventBus.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(VfsChangedEvent.TYPE, this);
      eventBus.addHandler(AddSourceToBuildPathEvent.TYPE, this);
   }

   /**
    * Bind presenter with pointed display.
    * 
    * @param d display
    */
   public void bindDisplay()
   {
      display.getClassPathEntryListGrid().addSelectionHandler(new SelectionHandler<GroovyClassPathEntry>()
      {

         public void onSelection(SelectionEvent<GroovyClassPathEntry> event)
         {
            checkRemoveButtonState();
         }
      });

      display.getAddButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            doAddPath();
         }

      });

      display.getRemoveButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            doRemove(display.getSelectedItems());
            checkRemoveButtonState();
         }

      });

      display.getSaveButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            doSave();
         }

      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            closeView();
         }

      });
   }

   
   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }
   
   /**
    * Do remove the source(s).
    * 
    * @param itemsToRemove
    */
   private void doRemove(List<GroovyClassPathEntry> itemsToRemove)
   {
      List<GroovyClassPathEntry> groovyClassPathEntries = display.getClassPathEntryListGrid().getValue();
      groovyClassPathEntries.removeAll(itemsToRemove);
      display.getClassPathEntryListGrid().setValue(groovyClassPathEntries);
   }

   /**
    * @see org.exoplatform.ide.client.framework.event.ProjectCreatedHandler.groovy.event.ConfigureBuildPathHandler#onConfigureBuildPath(org.exoplatform.ide.client.framework.event.ProjectCreatedEvent.groovy.event.ConfigureBuildPathEvent)
    */
   public void onConfigureBuildPath(ProjectCreatedEvent event)
   {
      //TODO
//      getClassPathLocation(event.getProject());
   }

   /**
    * Get classpath file.
    * 
    * @param parent - folder of project (encoded)
    * @return {@link File} classpath file
    */
   private FileModel createClasspathFile(FolderModel parent)
   {
      String path = GroovyClassPathUtil.formPathFromHref(parent.getPath(), restContext);
      GroovyClassPathEntry projectClassPathEntry = GroovyClassPathEntry.build(EnumSourceType.DIR.getValue(), path);
      List<GroovyClassPathEntry> groovyClassPathEntries = new ArrayList<GroovyClassPathEntry>();
      groovyClassPathEntries.add(projectClassPathEntry);
      String content = GroovyClassPathUtil.getClassPathJSON(groovyClassPathEntries);
      String contentType = MimeType.APPLICATION_JSON;
      FileModel newFile = new FileModel(".groovyclasspath", contentType, content, parent);
      return newFile;
   }

   /**
    * Get the location of classpath file.
    */
   private void getClassPathLocation(FolderModel projectFolder)
   {
      if (projectFolder != null)
      {
         final FileModel classpath = createClasspathFile(projectFolder);
         try
         {
            VirtualFileSystem.getInstance().createFile(
               projectFolder,
               new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<FileModel>(new FileUnmarshaller(
                  classpath))
               {

                  @Override
                  protected void onSuccess(FileModel result)
                  {
                     classPathFile(result);
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {

                  }
               });
         }
         catch (RequestException e)
         {
            e.printStackTrace();
         }
      }
      else
      {

         if (selectedItem == null)
            return;
         GroovyService.getInstance().getClassPathLocation(selectedItem.getId(), new AsyncRequestCallback<ClassPath>()
         {

            @Override
            protected void onSuccess(ClassPath result)
            {
               //TODO
               //               classPathFile(result.getLocation());
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               Dialogs.getInstance().showError("Classpath settings not found.<br> Probably you are not in project.");
            }
         });
      }
   }

   private void getFileProperties(FileModel file)
   {
      try
      {
         VirtualFileSystem.getInstance().getItemByLocation(file.getLinkByRelation(Link.REL_SELF).getHref(), new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<FileModel>(new FileUnmarshaller(file))
         {
            
            @Override
            protected void onSuccess(FileModel result)
            {
               if (!(result instanceof FileModel))
                  return;

               getFileContent((FileModel)result);
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
         e.printStackTrace();
         eventBus.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void getFileContent(FileModel file)
   {
      try
      {
         VirtualFileSystem.getInstance().getContent(
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<FileModel>(
               new FileContentUnmarshaller(file))
            {

               @Override
               protected void onSuccess(FileModel result)
               {
                  classPathFile = result;
                  if (classPathFile != null && !classPathFile.getContent().isEmpty())
                  {
                     List<GroovyClassPathEntry> groovyClassPathEntries =
                        GroovyClassPathUtil.getClassPathEntries(classPathFile.getContent());
                     display.getClassPathEntryListGrid().setValue(groovyClassPathEntries);
                     checkRemoveButtonState();
                  }
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
         e.printStackTrace();
         eventBus.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Save classpath file.
    */
   private void doSave()
   {
      List<GroovyClassPathEntry> groovyClassPathEntries = display.getClassPathEntryListGrid().getValue();
      String content = GroovyClassPathUtil.getClassPathJSON(groovyClassPathEntries);
      classPathFile.setContent(content);
      try
      {
         VirtualFileSystem.getInstance().updateContent(classPathFile,
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<FileModel>()
            {

               @Override
               protected void onSuccess(FileModel result)
               {
                  if (classPathFile.getId().equals(result.getId()))
                  {
                     closeView();
                     if (openedFiles != null && openedFiles.containsKey(classPathFile.getId()))
                     {
                        eventBus.fireEvent(new EditorReplaceFileEvent(openedFiles.get(classPathFile.getId()),
                           classPathFile));
                     }
                  }

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
         e.printStackTrace();
         eventBus.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Perform adding source.
    */
   private void doAddPath()
   {
      new ChooseSourcePathPresenter(eventBus, (FolderModel)vfsInfo.getRoot());
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.event.AddSourceToBuildPathHandler#onAddSourceToBuildPath(org.exoplatform.ide.client.module.groovy.classpath.ui.event.AddSourceToBuildPathEvent)
    */
   public void onAddSourceToBuildPath(AddSourceToBuildPathEvent event)
   {
      List<GroovyClassPathEntry> oldClassPathEntries = display.getClassPathEntryListGrid().getValue();

      for (GroovyClassPathEntry classPathEntry : event.getClassPathEntries())
      {
         boolean exists = false;
         for (GroovyClassPathEntry oldClassPathEntry : oldClassPathEntries)
         {
            if (oldClassPathEntry.getPath().equals(classPathEntry.getPath()))
            {
               exists = true;
               break;
            }
         }
         if (!exists)
         {
            oldClassPathEntries.add(classPathEntry);
         }
      }

      display.getClassPathEntryListGrid().setValue(oldClassPathEntries);
      checkRemoveButtonState();
   }

   /**
    * @see org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent)
    */
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      restContext = event.getConfiguration().getContext();
   }

   /**
    * Check remove button enable state.
    */
   private void checkRemoveButtonState()
   {
      boolean isEnabled = display.getSelectedItems().size() > 0;
      display.enableRemoveButton(isEnabled);
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems() != null && event.getSelectedItems().size() == 1)
      {
         selectedItem = event.getSelectedItems().get(0);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
    */
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      //TODO  check changes here:
      currentEntryPoint = (event.getVfsInfo() != null) ? event.getVfsInfo().getId() : null;
      vfsInfo = event.getVfsInfo();
      if (display != null)
      {
         display.setCurrentRepository(getRepositoryFromEntryPoint(currentEntryPoint));
      }
   }

   private String getRepositoryFromEntryPoint(String entryPoint)
   {
      if (entryPoint == null)
         return null;
      String context = restContext + GroovyClassPathUtil.WEBDAV_CONTEXT;
      int index = entryPoint.indexOf(context);
      String path = (index >= 0) ? entryPoint.substring(index + context.length()) : null;
      if (path == null)
         return null;
      path = path.startsWith("/") ? path.substring(1) : path;
      index = path.indexOf("/");
      return (index >= 0) ? path.substring(0, index) : null;
   }

   /**
    * @param result
    */
   private void classPathFile(FileModel file)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
      }
      
      IDE.getInstance().openView(display.asView());
      
      display.setCurrentRepository(getRepositoryFromEntryPoint(currentEntryPoint));
      display.getClassPathEntryListGrid().setValue(new ArrayList<GroovyClassPathEntry>());
      getFileProperties(file);
   }
}
