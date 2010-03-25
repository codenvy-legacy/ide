/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.editor.custom;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.editor.api.Editor;
import org.exoplatform.gwtframework.editor.api.EditorFactory;
import org.exoplatform.gwtframework.editor.api.EditorNotFoundException;
import org.exoplatform.gwtframework.ui.client.dialogs.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialogs.callback.BooleanValueReceivedCallback;
import org.exoplatform.ideall.client.event.file.OpenFileEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.settings.SettingsService;
import org.exoplatform.ideall.client.model.settings.event.ApplicationContextSavedEvent;
import org.exoplatform.ideall.client.model.settings.event.ApplicationContextSavedHandler;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentReceivedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentReceivedHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class OpenFileWithPresenter implements FileContentReceivedHandler, ApplicationContextSavedHandler
{

   public interface Display
   {

      void closeForm();

      EditorsListGrid getEditorsListGrid();

      HasValue<Boolean> getIsDefaultCheckItem();

      HasClickHandlers getOkButton();

      void enableOpenButton();

      HasClickHandlers getCancelButton();

   }

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Display display;

   private Handlers handlers;

   private Editor selectedEditor;

   public OpenFileWithPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      handlers = new Handlers(eventBus);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   public void bindDisplay(Display d)
   {
      handlers.addHandler(FileContentReceivedEvent.TYPE, this);
      handlers.addHandler(ApplicationContextSavedEvent.TYPE, this);

      display = d;
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent arg0)
         {
            display.closeForm();
         }
      });

      display.getEditorsListGrid().addDoubleClickHandler(new DoubleClickHandler()
      {

         public void onDoubleClick(DoubleClickEvent arg0)
         {
            tryOpenFile();
         }

      });
      display.getOkButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent arg0)
         {
            tryOpenFile();
         }
      });

      display.getEditorsListGrid().addSelectionHandler(new SelectionHandler<EditorInfo>()
      {

         public void onSelection(SelectionEvent<EditorInfo> event)
         {
            if (event.getSelectedItem() == selectedEditor)
            {
               return;
            }

            selectedEditor = event.getSelectedItem().getEditor();
            display.enableOpenButton();
         }

      });

      fillEditorListGrid();

   }

   private void fillEditorListGrid()
   {

      String mimeType = ((File)context.getSelectedItems(context.getSelectedNavigationPanel()).get(0)).getContentType();

      try
      {
         List<Editor> editorsItems = EditorFactory.getEditors(mimeType);

         List<EditorInfo> editorInfoItems = new ArrayList<EditorInfo>();

         Editor defaultEditor = null;

         if (context.getDefaultEditors().get(mimeType) != null)
         {

            String defaultEdotorDecription = context.getDefaultEditors().get(mimeType);

            for (Editor e : editorsItems)
            {
               if (e.getDescription().equals(defaultEdotorDecription))
               {
                  defaultEditor = e;
               }
            }

         }
         else
         {
            defaultEditor = EditorFactory.getDefaultEditor(mimeType);
         }

         for (Editor e : editorsItems)
         {
            if (e.getDescription().equals(defaultEditor.getDescription()))
            {
               editorInfoItems.add(new EditorInfo(e, true));
            }
            else
            {
               editorInfoItems.add(new EditorInfo(e, false));
            }
         }

         display.getEditorsListGrid().setValue(editorInfoItems);
      }
      catch (EditorNotFoundException e)
      {
         String message = "Can't find editor for type <b>" + mimeType + "</b>";
         eventBus.fireEvent(new ExceptionThrownEvent(new Exception(message)));
      }
   }

   private void openFile()
   {
      if (display.getIsDefaultCheckItem().getValue() == null || display.getIsDefaultCheckItem().getValue() == false)
      {
         context.setSelectedEditorDescription(selectedEditor.getDescription());
         eventBus.fireEvent(new OpenFileEvent((File)context.getSelectedItems(context.getSelectedNavigationPanel()).get(
            0)));
         display.closeForm();
      }
      else
      {
         String mimeType =
            ((File)context.getSelectedItems(context.getSelectedNavigationPanel()).get(0)).getContentType();

         context.getDefaultEditors().put(mimeType, selectedEditor.getDescription());

         SettingsService.getInstance().saveSetting(context);
      }
   }

   private void showDialog()
   {
      Dialogs.getInstance().ask(
         "Info",
         "Do you want to reopen file <b>"
            + context.getSelectedItems(context.getSelectedNavigationPanel()).get(0).getName()
            + "</b> in selected editor?", new BooleanValueReceivedCallback()
         {

            public void execute(Boolean value)
            {
               if (value == null)
               {
                  return;
               }

               if (value == true)
               {
                  //File file = (File)context.getSelectedItems().get(0);
                  //eventBus.fireEvent(new EditorCloseFileEvent(file));
                  openFile();
               }
               else
               {
                  display.closeForm();
               }
            }

         });
   }

   private void tryOpenFile()
   {
//      TODO
//      File file = (File)context.getSelectedItems(context.getSelectedNavigationPanel()).get(0);
//
//      if (context.getOpenedFiles().get(file.getPath()) != null)
//      {
//         showDialog();
//         return;
//      }
//
//      openFile();
   }

   public void onFileContentReceived(FileContentReceivedEvent event)
   {
      display.closeForm();
   }

   public void onApplicationContextSaved(ApplicationContextSavedEvent event)
   {
      eventBus
         .fireEvent(new OpenFileEvent((File)context.getSelectedItems(context.getSelectedNavigationPanel()).get(0)));
   }

}
