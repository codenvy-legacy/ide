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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.framework.event.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.event.ProjectCreatedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileCallback;
import org.exoplatform.ide.client.framework.vfs.FileContentSaveCallback;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.extension.groovy.client.classpath.EnumSourceType;
import org.exoplatform.ide.extension.groovy.client.classpath.GroovyClassPathEntry;
import org.exoplatform.ide.extension.groovy.client.classpath.GroovyClassPathUtil;
import org.exoplatform.ide.extension.groovy.client.classpath.ui.event.AddSourceToBuildPathEvent;
import org.exoplatform.ide.extension.groovy.client.classpath.ui.event.AddSourceToBuildPathHandler;
import org.exoplatform.ide.extension.groovy.client.service.groovy.GroovyService;
import org.exoplatform.ide.extension.groovy.client.service.groovy.marshal.ClassPath;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Presenter for configuring class path file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 6, 2011 $
 *
 */
public class ConfigureBuildPathPresenter implements ProjectCreatedHandler, AddSourceToBuildPathHandler,
   ConfigurationReceivedSuccessfullyHandler, ItemsSelectedHandler, EditorFileOpenedHandler, EntryPointChangedHandler
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
   private File classPathFile;

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
   private Map<String, File> openedFiles;

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
      eventBus.addHandler(EntryPointChangedEvent.TYPE, this);
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
      getClassPathLocation(event.getProjectLocation());
   }

   /**
    * Get classpath file.
    * 
    * @param href - href of project (encoded)
    * @return {@link File} classpath file
    */
   private File createClasspathFile(String href)
   {
      href = (href.endsWith("/")) ? href : href + "/";
      String contentType = MimeType.APPLICATION_JSON;
      File newFile = new File(href + ".groovyclasspath");
      newFile.setContentType(contentType);
      newFile.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(contentType));      
      newFile.setNewFile(true);

      String path = GroovyClassPathUtil.formPathFromHref(href, restContext);
      GroovyClassPathEntry projectClassPathEntry = GroovyClassPathEntry.build(EnumSourceType.DIR.getValue(), path);
      List<GroovyClassPathEntry> groovyClassPathEntries = new ArrayList<GroovyClassPathEntry>();
      groovyClassPathEntries.add(projectClassPathEntry);

      String content = GroovyClassPathUtil.getClassPathJSON(groovyClassPathEntries);
      newFile.setContent(content);
      return newFile;
   }

   /**
    * Get the location of classpath file.
    */
   private void getClassPathLocation(String projectLocation)
   {
      if (projectLocation != null)
      {
         final File classpath = createClasspathFile(projectLocation);
         VirtualFileSystem.getInstance().saveContent(classpath, null,
            new FileContentSaveCallback()
            {

               @Override
               protected void onSuccess(FileData result)
               {
                  classPathFile(classpath.getHref());
               }
            });
      }
      else
      {

         if (selectedItem == null)
            return;
         GroovyService.getInstance().getClassPathLocation(selectedItem.getHref(), new AsyncRequestCallback<ClassPath>()
         {

            @Override
            protected void onSuccess(ClassPath result)
            {
               classPathFile(result.getLocation());
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               Dialogs.getInstance().showError("Classpath settings not found.<br> Probably you are not in project.");
            }
         });
      }
   }

   private void getFileProperties(File file)
   {
      VirtualFileSystem.getInstance().getProperties(file, new ItemPropertiesCallback()
      {

         @Override
         protected void onSuccess(Item result)
         {
            if (!(result instanceof File))
               return;

            getFileContent((File)result);
         }
      });
   }

   private void getFileContent(File file)
   {
      VirtualFileSystem.getInstance().getContent(file, new FileCallback()
      {

         @Override
         protected void onSuccess(File result)
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
      });
   }

   /**
    * Save classpath file.
    */
   private void doSave()
   {
      List<GroovyClassPathEntry> groovyClassPathEntries = display.getClassPathEntryListGrid().getValue();
      String content = GroovyClassPathUtil.getClassPathJSON(groovyClassPathEntries);
      classPathFile.setContent(content);
      VirtualFileSystem.getInstance().saveContent(classPathFile, null, new FileContentSaveCallback()
      {

         @Override
         protected void onSuccess(FileData result)
         {
            if (classPathFile.getHref().equals(result.getFile().getHref()))
            {
               closeView();
               if (openedFiles != null && openedFiles.containsKey(classPathFile.getHref()))
               {
                  eventBus.fireEvent(new EditorReplaceFileEvent(openedFiles.get(classPathFile.getHref()), classPathFile));
               }
            }
         }
      });
   }

   /**
    * Perform adding source.
    */
   private void doAddPath()
   {
      new ChooseSourcePathPresenter(eventBus, restContext);
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
    * @see org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler#onEntryPointChanged(org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent)
    */
   @Override
   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      currentEntryPoint = event.getEntryPoint();
      if (display != null)
      {
         display.setCurrentRepository(getRepositoryFromEntryPoint(event.getEntryPoint()));
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
   private void classPathFile(String href)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
      }
      
      IDE.getInstance().openView(display.asView());
      
      display.setCurrentRepository(getRepositoryFromEntryPoint(currentEntryPoint));
      display.getClassPathEntryListGrid().setValue(new ArrayList<GroovyClassPathEntry>());

      File file = new File(href);
      getFileProperties(file);
   }
}
