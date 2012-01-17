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
package org.exoplatform.ide.client.template;

import com.google.gwt.http.client.RequestException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateServiceImpl;
import org.exoplatform.ide.client.navigation.control.SaveFileAsTemplateControl;
import org.exoplatform.ide.client.navigation.event.SaveFileAsTemplateEvent;
import org.exoplatform.ide.client.navigation.event.SaveFileAsTemplateHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * Presenter for Save as Template view.
 * 
 * Save file as template.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: $
 * 
 */
public class SaveAsTemplatePresenter implements SaveFileAsTemplateHandler, ViewClosedHandler, TemplatesMigratedHandler,
   EditorActiveFileChangedHandler
{

   public interface Display extends IsView
   {

      HasValue<String> getTypeField();

      HasValue<String> getNameField();

      HasValue<String> getDescriptionField();

      HasClickHandlers getSaveButton();

      HasClickHandlers getCancelButton();

      void disableSaveButton();

      void enableSaveButton();

      void focusInNameField();

   }

   private static final String ENTER_TEMPLATE_NAME = IDE.TEMPLATE_CONSTANT.saveAsTemplateEnterNameFirst();

   private static final String TEMPLATE_CREATED = IDE.TEMPLATE_CONSTANT.saveAsTemplateCreated();

   private static final String OPEN_FILE_FOR_TEMPLATE = IDE.TEMPLATE_CONSTANT.saveAsTemplateOpenFileForTemplate();

   private Display display;

   private Template templateToCreate;

   private FileModel activeFile;

   /**
    * Flag, to indicate, were templates moved from registry to plain text file on server.
    */
   private boolean isTemplatesMigrated = false;

   public SaveAsTemplatePresenter()
   {
      IDE.getInstance().addControl(new SaveFileAsTemplateControl());

      IDE.addHandler(SaveFileAsTemplateEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(TemplatesMigratedEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            String value = event.getValue();

            if (value == null || value.length() == 0)
            {
               display.disableSaveButton();
            }
            else
            {
               display.enableSaveButton();
            }
         }
      });

      display.getSaveButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            createTemplate();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            closeView();
         }
      });

      display.getTypeField().setValue(activeFile.getMimeType());
      display.disableSaveButton();

   }

   void createTemplate()
   {
      String name = display.getNameField().getValue().trim();
      if ("".equals(name))
      {
         Dialogs.getInstance().showError(ENTER_TEMPLATE_NAME);
         return;
      }

      String description = "";
      if (display.getDescriptionField().getValue() != null)
      {
         description = display.getDescriptionField().getValue();
      }

      templateToCreate = new FileTemplate(activeFile.getMimeType(), name, description, activeFile.getContent(), null);

      try
      {
         TemplateServiceImpl.getInstance().addFileTemplate((FileTemplate)templateToCreate,
            new AsyncRequestCallback<FileTemplate>()
            {
               @Override
               protected void onSuccess(FileTemplate result)
               {
                  closeView();
                  Dialogs.getInstance().showInfo(TEMPLATE_CREATED);
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

   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * @see org.exoplatform.ide.client.navigation.event.SaveFileAsTemplateHandler#onSaveFileAsTemplate(org.exoplatform.ide.client.navigation.event.SaveFileAsTemplateEvent)
    */
   @Override
   public void onSaveFileAsTemplate(SaveFileAsTemplateEvent event)
   {
      if (isTemplatesMigrated)
      {
         openView();
      }
      else
      {
         IDE.fireEvent(new MigrateTemplatesEvent(new TemplatesMigratedCallback()
         {
            @Override
            public void onTemplatesMigrated()
            {
               openView();
            }
         }));
      }
   }

   private void openView()
   {
      if (activeFile == null)
      {
         IDE.fireEvent(new ExceptionThrownEvent(OPEN_FILE_FOR_TEMPLATE));
         return;
      }
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView(d.asView());
         bindDisplay(d);
         display.focusInNameField();
      }
      else
      {
         IDE.fireEvent(new ExceptionThrownEvent("Display SaveAsTemplate must be null"));
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
    * @see org.exoplatform.ide.client.template.TemplatesMigratedHandler#onTemplatesMigrated(org.exoplatform.ide.client.template.TemplatesMigratedEvent)
    */
   @Override
   public void onTemplatesMigrated(TemplatesMigratedEvent event)
   {
      isTemplatesMigrated = true;
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      this.activeFile = event.getFile();
   }

}
