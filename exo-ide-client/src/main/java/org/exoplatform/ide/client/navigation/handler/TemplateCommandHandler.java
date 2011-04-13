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
package org.exoplatform.ide.client.navigation.handler;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.TemplateList;
import org.exoplatform.ide.client.model.template.TemplateNative;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.navigation.event.CreateFileFromTemplateEvent;
import org.exoplatform.ide.client.navigation.event.CreateFileFromTemplateHandler;
import org.exoplatform.ide.client.template.CreateFileFromTemplatePresenter;
import org.exoplatform.ide.client.template.CreateFromTemplateDisplay;
import org.exoplatform.ide.client.template.ui.CreateFileFromTemplateForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Handler for "Create file from template" command.
 * 
 * Handler "Create file from template" event and open form.
 * 
 * Also handlers events to store selected items and opened files.
 * This data is needed for "Create file from template" presenter.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TemplateCommandHandler implements CreateFileFromTemplateHandler, ItemsSelectedHandler,
   EditorFileOpenedHandler, EditorFileClosedHandler, ConfigurationReceivedSuccessfullyHandler
{

   private HandlerManager eventBus;

   private List<Item> selectedItems = new ArrayList<Item>();

   private Map<String, File> openedFiles = new HashMap<String, File>();

   private String restContext;

   public TemplateCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(CreateFileFromTemplateEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
    */
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
    */
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent)
    */
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      restContext = event.getConfiguration().getContext();
   }

   /**
    * @see org.exoplatform.ide.client.navigation.event.CreateFileFromTemplateHandler#onCreateFileFromTemplate(org.exoplatform.ide.client.navigation.event.CreateFileFromTemplateEvent)
    */
   public void onCreateFileFromTemplate(CreateFileFromTemplateEvent event)
   {
      final TemplateList defaultTemplates = new TemplateList();
//      TemplateList defaultTemplates = TemplateServiceImpl.getDefaultTemplates();
      TemplateService.getInstance().getTemplateList("file", new AsyncRequestCallback<List<TemplateNative>>()
         {

            @Override
            protected void onSuccess(List<TemplateNative> result)
            {
               for (TemplateNative tn : result)
               {
                  defaultTemplates.getTemplates().add(new FileTemplate(
                     tn.getName(), tn.getDescription(), tn.getMimeType(), true));
               }
               
               TemplateService.getInstance().getTemplates(new AsyncRequestCallback<TemplateList>()
                  {

                     @Override
                     protected void onSuccess(TemplateList result)
                     {
                        defaultTemplates.getTemplates().addAll(result.getTemplates());
                        CreateFileFromTemplatePresenter createFilePresenter =
                           new CreateFileFromTemplatePresenter(eventBus, selectedItems, defaultTemplates.getTemplates(), openedFiles);
                        CreateFromTemplateDisplay<FileTemplate> createFileDisplay =
                           new CreateFileFromTemplateForm(eventBus, defaultTemplates.getTemplates(), createFilePresenter);
                        createFilePresenter.bindDisplay(createFileDisplay);
                     }

                     @Override
                     protected void onFailure(Throwable exception)
                     {
                        eventBus.fireEvent(new ExceptionThrownEvent(exception));
                     }
                  });
               
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               eventBus.fireEvent(new ExceptionThrownEvent(exception));
            }
         });
      
      /* TemplateService.getInstance().getTemplates(new AsyncRequestCallback<TemplateList>()
       {
          
          @Override
          protected void onSuccess(TemplateList result)
          {
             CreateFileFromTemplatePresenter createFilePresenter =
                new CreateFileFromTemplatePresenter(eventBus, selectedItems, result.getTemplates(), openedFiles);
             CreateFromTemplateDisplay<FileTemplate> createFileDisplay =
                new CreateFileFromTemplateForm(eventBus, result.getTemplates(), createFilePresenter);
             createFilePresenter.bindDisplay(createFileDisplay);
          }
          
          @Override
          protected void onFailure(Throwable exception)
          {
             eventBus.fireEvent(new ExceptionThrownEvent(exception));
          }
       });*/
   }

}
