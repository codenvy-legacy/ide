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

import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.documentation.RegisterDocumentationEvent;
import org.exoplatform.ide.client.framework.documentation.RegisterDocumentationHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.model.settings.SettingsService;
import org.exoplatform.ide.vfs.client.model.FileModel;

import com.google.gwt.core.client.GWT;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: DocumentationManager Jan 21, 2011 11:02:22 AM evgen $
 * 
 */
public class DocumentationPresenter implements EditorActiveFileChangedHandler, ShowDocumentationHandler,
   ViewOpenedHandler, ViewClosedHandler, RegisterDocumentationHandler, ApplicationSettingsReceivedHandler
{

   public interface Display extends IsView
   {

      void setDocumentationURL(String url);

   }

   private Display display;

   private ShowDocumentationControl control;

   private FileModel activeFile;

   private ApplicationSettings settings;

   private Map<String, String> docs = new HashMap<String, String>();

   /**
    * @param eventBus
    */
   public DocumentationPresenter()
   {
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(ShowDocumentationEvent.TYPE, this);
      IDE.addHandler(ViewOpenedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(RegisterDocumentationEvent.TYPE, this);

      control = new ShowDocumentationControl();
      IDE.getInstance().addControl(control, Docking.TOOLBAR);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();

      if (settings == null)
         return;

      boolean isDocumentationOpened =
         settings.getValueAsBoolean("documentation") == null ? false : settings.getValueAsBoolean("documentation");

      if (activeFile != null)
      {
         if (docs.containsKey(activeFile.getMimeType()))
         {
            if (isDocumentationOpened)
            {
               openDocumentationView();
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
         IDE.getInstance().closeView(display.asView().getId());
      }
   }

   /**
    * 
    */
   private void openDocumentationView()
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         display.setDocumentationURL(docs.get(activeFile.getMimeType()));
         IDE.getInstance().openView(display.asView());
      }
      else
      {
         display.asView().setViewVisible();
      }
   }

   /**
    * @see org.exoplatform.ide.client.documentation.event.ShowDocumentationHandler#onShowDocumentation(org.exoplatform.ide.client.documentation.event.ShowDocumentationEvent)
    */
   @Override
   public void onShowDocumentation(ShowDocumentationEvent event)
   {
      settings.setValue("documentation", event.isShow(), Store.COOKIES);
      SettingsService.getInstance().saveSettingsToCookies(settings);
      if (event.isShow())
      {
         openDocumentationView();
      }
      else
      {
         if (display != null)
         {
            settings.setValue("documentation", false, Store.COOKIES);
            SettingsService.getInstance().saveSettingsToCookies(settings);
            IDE.getInstance().closeView(display.asView().getId());
         }
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.event.ViewOpenedHandler#onViewOpened(org.exoplatform.ide.client.framework.ui.api.event.event.ViewOpenedEvent)
    */
   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         control.setSelected(true);
         control.setEvent(new ShowDocumentationEvent(false));
         control.setPrompt(ShowDocumentationControl.PROMPT_HIDE);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
         control.setSelected(false);
         control.setEvent(new ShowDocumentationEvent(true));
         control.setPrompt(ShowDocumentationControl.PROMPT_SHOW);
      }
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
