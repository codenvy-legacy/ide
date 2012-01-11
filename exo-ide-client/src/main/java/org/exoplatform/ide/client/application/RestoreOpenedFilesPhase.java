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
package org.exoplatform.ide.client.application;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.util.Log;
import org.exoplatform.ide.client.editor.EditorFactory;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorChangeActiveFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.model.settings.Settings;
import org.exoplatform.ide.editor.api.EditorProducer;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RestoreOpenedFilesPhase implements ExceptionThrownHandler, EditorActiveFileChangedHandler,
   ProjectOpenedHandler
{

   public static final int SCHEDULE_START = 100;

   private RestoreOpenedFilesPhase instance;

   private HandlerManager eventBus;

   private ApplicationSettings applicationSettings;

   private List<String> filesToLoad;

   private Map<String, FileModel> openedFiles = new LinkedHashMap<String, FileModel>();

   private FileModel fileToLoad;

   private Map<String, String> defaultEditors;

   private List<String> filesToOpen;

   private String activeFileURL;

   private boolean isLoadingOpenedFiles = false;

   private boolean isRestoringOpenedFiles = false;
   
   private ProjectModel restoredProject;

   public RestoreOpenedFilesPhase(HandlerManager eventBus, ApplicationSettings applicationSettings)
   {
      this.eventBus = eventBus;
      this.applicationSettings = applicationSettings;

      instance = this;

      //TODO eventBus.fireEvent(new EnableStandartErrorsHandlingEvent(false));

      eventBus.addHandler(ExceptionThrownEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);

      rememberFilesToBeRestored();
      isLoadingOpenedFiles = false;
      isRestoringOpenedFiles = false;

      new Timer()
      {
         @Override
         public void run()
         {
            execute();
         }
      }.schedule(SCHEDULE_START);
   }

   protected void execute()
   {
      String openedProjectId = applicationSettings.getValueAsString(Settings.OPENED_PROJECT);

      if (openedProjectId == null || openedProjectId.isEmpty())
      {
         lazyRestoreOpenedFiles();
      }
      else
      {
         try
         {
            final ProjectModel project = new ProjectModel();
            project.setId(openedProjectId);

            VirtualFileSystem.getInstance().getItemById(openedProjectId,
               new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(project)))
               {
                  @Override
                  protected void onSuccess(ItemWrapper result)
                  {
                     final Item item = result.getItem();
                     IDE.addHandler(ProjectOpenedEvent.TYPE, instance);
                     Scheduler.get().scheduleDeferred(new ScheduledCommand()
                     {
                        @Override
                        public void execute()
                        {
                           IDE.fireEvent(new OpenProjectEvent((ProjectModel)item));
                        }
                     });
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                  }
               });

         }
         catch (Exception e)
         {
         }
      }

   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      IDE.removeHandler(ProjectOpenedEvent.TYPE, instance);
      
      restoredProject = event.getProject();

      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            lazyRestoreOpenedFiles();
         }
      });
   }

   private void rememberFilesToBeRestored()
   {
      if (!applicationSettings.containsKey(Settings.OPENED_FILES))
      {
         applicationSettings.setValue(Settings.OPENED_FILES, new ArrayList<String>(), Store.SERVER);
      }

      filesToLoad = new ArrayList<String>();
      filesToLoad.addAll(applicationSettings.getValueAsList(Settings.OPENED_FILES));

      if (!applicationSettings.containsKey(Settings.DEFAULT_EDITORS))
      {
         applicationSettings.setValue(Settings.DEFAULT_EDITORS, new LinkedHashMap<String, String>(), Store.SERVER);
      }

      defaultEditors = new LinkedHashMap<String, String>();
      defaultEditors.putAll(applicationSettings.getValueAsMap(Settings.DEFAULT_EDITORS));

      activeFileURL = applicationSettings.getValueAsString(Settings.ACTIVE_FILE);
   }

   protected void lazyRestoreOpenedFiles()
   {
      isLoadingOpenedFiles = true;
      isRestoringOpenedFiles = false;

      preloadNextFile();
   }

   private void preloadNextFile()
   {
      if (filesToLoad.size() == 0)
      {
         isLoadingOpenedFiles = false;
         openFilesInEditor();
         return;
      }

      String fileId = filesToLoad.get(0);
      fileToLoad = new FileModel();
      fileToLoad.setId(fileId);
      filesToLoad.remove(0);
      try
      {
         VirtualFileSystem.getInstance().getItemById(fileId,
            new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(fileToLoad)))
            {
               @Override
               protected void onFailure(Throwable exception)
               {
                  preloadNextFile();
               }

               @Override
               protected void onSuccess(ItemWrapper result)
               {
                  FileModel file = (FileModel)result.getItem();
                  file.setContentChanged(false);
                  try
                  {
                     VirtualFileSystem.getInstance().getContent(
                        new AsyncRequestCallback<FileModel>(new FileContentUnmarshaller(file))
                        {
                           @Override
                           protected void onSuccess(FileModel result)
                           {
                              openedFiles.put(result.getId(), result);
                              preloadNextFile();
                           }

                           @Override
                           protected void onFailure(Throwable exception)
                           {
                              eventBus.fireEvent(new ExceptionThrownEvent(exception));
                              preloadNextFile();
                           }
                        });
                  }
                  catch (RequestException e)
                  {
                     eventBus.fireEvent(new ExceptionThrownEvent(e));
                  }
               }

            });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void openFilesInEditor()
   {
      eventBus.fireEvent(new EnableStandartErrorsHandlingEvent());

      isRestoringOpenedFiles = true;
      filesToOpen = new ArrayList<String>(openedFiles.keySet());
      openNextFileInEditor();
   }

   private void openNextFileInEditor()
   {
      if (filesToOpen.size() == 0)
      {
         isRestoringOpenedFiles = false;

         new Timer()
         {
            @Override
            public void run()
            {
               changeActiveFile();
            }
         }.schedule(100);

         return;
      }

      String fileURL = filesToOpen.get(0);
      filesToOpen.remove(0);
      
      FileModel file = openedFiles.get(fileURL);
      file.setProject(restoredProject);
      try
      {
         String editorDescription = defaultEditors.get(file.getMimeType());
         EditorProducer producer = EditorFactory.getEditorProducer(file.getMimeType(), editorDescription);
         eventBus.fireEvent(new EditorOpenFileEvent(file, producer));
      }
      catch (EditorNotFoundException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void changeActiveFile()
   {
      if (activeFileURL != null)
      {
         FileModel activeFile = openedFiles.get(activeFileURL);
         if (activeFile != null)
         {
            eventBus.fireEvent(new EditorChangeActiveFileEvent(activeFile));
         }
      }
   }

   public void onError(ExceptionThrownEvent event)
   {
      if (isLoadingOpenedFiles)
      {
         preloadNextFile();
         return;
      }
   }

   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (isLoadingOpenedFiles)
      {
         preloadNextFile();
      }

      if (isRestoringOpenedFiles)
      {
         openNextFileInEditor();
         return;
      }
   }

}
