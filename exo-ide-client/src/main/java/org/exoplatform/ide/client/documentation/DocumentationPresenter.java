/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.documentation;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.ide.client.documentation.control.ShowDocumentationControl;
import org.exoplatform.ide.client.documentation.event.ShowDocumentationEvent;
import org.exoplatform.ide.client.documentation.event.ShowDocumentationHandler;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.documentation.RegisterDocumentationEvent;
import org.exoplatform.ide.client.framework.documentation.RegisterDocumentationHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.framework.ui.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.event.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.vfs.File;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: DocumentationManager Jan 21, 2011 11:02:22 AM evgen $
 *
 */
public class DocumentationPresenter implements EditorActiveFileChangedHandler, ShowDocumentationHandler,
   ViewOpenedHandler, ViewClosedHandler, RegisterDocumentationHandler, ApplicationSettingsReceivedHandler
{

   public interface Display
   {
      void setDocumentationURL(String url);

      void bindClickHandlers();

      void removeHandlers();
   }

   private HandlerManager eventBus;

   private Display display;

   private ShowDocumentationControl control;

   private File activeFile;

   private ApplicationSettings settings;
   
   private boolean isClosedByUser = true;

   private Map<String, String> docs = new HashMap<String, String>();

   /**
    * @param eventBus
    */
   public DocumentationPresenter(HandlerManager eventBus)
   {

      this.eventBus = eventBus;
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(ShowDocumentationEvent.TYPE, this);
      eventBus.addHandler(ViewOpenedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(RegisterDocumentationEvent.TYPE, this);
      control = new ShowDocumentationControl();
      eventBus.fireEvent(new RegisterControlEvent(control, DockTarget.TOOLBAR));
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();

      boolean opened =
         settings.getValueAsBoolean("documentation") == null ? false : settings.getValueAsBoolean("documentation");

      if (activeFile != null)
      {
         if (docs.containsKey(activeFile.getContentType()))
         {
            if (opened)
            {
               openDocForm();
               control.setPrompt(ShowDocumentationControl.PROMPT_HIDE);
               control.setVisible(true);
               return;
            }
            else
            {
               control.setPrompt(ShowDocumentationControl.PROMPT_SHOW);
               control.setVisible(true);
               return;
            }
         }
      }

      control.setVisible(false);
      if (display != null)
      {
         display.removeHandlers();
         isClosedByUser = false;
         IDE.getInstance().closeView(DocumentationForm.ID);
      }

   }

   /**
    * 
    */
   private void openDocForm()
   {
      DocumentationForm view = new DocumentationForm(eventBus);
      display = view;
      display.setDocumentationURL(docs.get(activeFile.getContentType()));
      IDE.getInstance().openView(view);
   }

   /**
    * @see org.exoplatform.ide.client.documentation.event.ShowDocumentationHandler#onShowDocumentation(org.exoplatform.ide.client.documentation.event.ShowDocumentationEvent)
    */
   @Override
   public void onShowDocumentation(ShowDocumentationEvent event)
   {
      settings.setValue("documentation", event.isShow(), Store.COOKIES);
      eventBus.fireEvent(new SaveApplicationSettingsEvent(settings, SaveType.COOKIES));
      if (event.isShow())
      {
         openDocForm();
      }
      else
      {
         display.removeHandlers();
         IDE.getInstance().closeView(DocumentationForm.ID);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.event.ViewOpenedHandler#onViewOpened(org.exoplatform.ide.client.framework.ui.event.ViewOpenedEvent)
    */
   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      if (DocumentationForm.ID.equals(event.getViewId()) && display != null)
      {
         display.bindClickHandlers();
         control.setSelected(true);
         control.setEvent(new ShowDocumentationEvent(false));
         control.setPrompt(ShowDocumentationControl.PROMPT_HIDE);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (DocumentationForm.ID.equals(event.getViewId())&& isClosedByUser)
      {
         settings.setValue("documentation", false, Store.COOKIES);
         eventBus.fireEvent(new SaveApplicationSettingsEvent(settings, SaveType.COOKIES));
         control.setSelected(false);
         display.removeHandlers();
         display = null;
         control.setEvent(new ShowDocumentationEvent(true));
         control.setPrompt(ShowDocumentationControl.PROMPT_SHOW);
      }
      isClosedByUser = true;
   }

   /**
    * @see org.exoplatform.ide.client.framework.documentation.RegisterDocumentationHandler#onRegisterDocumentation(org.exoplatform.ide.client.framework.documentation.RegisterDocumentationEvent)
    */
   @Override
   public void onRegisterDocumentation(RegisterDocumentationEvent event)
   {
      docs.put(event.getMimeType(), event.getUrl());
   }

   /**
    * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent)
    */
   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      settings = event.getApplicationSettings();
   }

}
