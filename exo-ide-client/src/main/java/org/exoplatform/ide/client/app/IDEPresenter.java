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
package org.exoplatform.ide.client.app;

import org.exoplatform.gwtframework.ui.client.command.ui.ToolbarBuilder;
import org.exoplatform.gwtframework.ui.client.toolbar.Toolbar;
import org.exoplatform.ide.client.app.api.Menu;
import org.exoplatform.ide.client.app.api.Perspective;
import org.exoplatform.ide.client.application.ControlsRegistration;
import org.exoplatform.ide.client.application.phases.LoadRegistryConfigurationPhase;
import org.exoplatform.ide.client.browser.BrowserPanel;
import org.exoplatform.ide.client.editor.EditorController;
import org.exoplatform.ide.client.framework.ui.event.ActivateViewEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewEx;
import org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.menu.RefreshMenuEvent;
import org.exoplatform.ide.client.menu.RefreshMenuHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDEPresenter implements RefreshMenuHandler, ViewOpenedHandler, ViewClosedHandler, ViewVisibilityChangedHandler
{

   public interface Display
   {

      Menu getMenu();

      Toolbar getToolbar();

      Toolbar getStatusbar();

      Perspective getPerspective();

   }

   private HandlerManager eventBus;

   private Display display;

   private ControlsRegistration controlsRegistration;

   public IDEPresenter(final HandlerManager eventBus, Display display, final ControlsRegistration controlsRegistration)
   {
      this.eventBus = eventBus;
      this.display = display;
      this.controlsRegistration = controlsRegistration;

      eventBus.addHandler(RefreshMenuEvent.TYPE, this);

      display.getPerspective().addViewOpenedHandler(this);
      display.getPerspective().addViewClosedHandler(this);
      display.getPerspective().addViewVisibilityChangedHandler(this);
      EditorController editorController = new EditorController();
      display.getPerspective().addViewVisibilityChangedHandler(editorController);

      new ToolbarBuilder(eventBus, display.getToolbar(), display.getStatusbar());

      new Timer()
      {
         @Override
         public void run()
         {
            //activate default view
            eventBus.fireEvent(new ActivateViewEvent(BrowserPanel.ID));
            new LoadRegistryConfigurationPhase(eventBus, controlsRegistration);
         }
      }.schedule(500);
   }

   public void openView(ViewEx view)
   {
      display.getPerspective().openView(view);
   }

   public void closeView(String viewId)
   {
      display.getPerspective().closeView(viewId);
   }

   @Override
   public void onRefreshMenu(RefreshMenuEvent event)
   {
      display.getMenu().refresh(controlsRegistration.getRegisteredControls(), eventBus);
   }

   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      eventBus.fireEvent(event);
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      eventBus.fireEvent(event);
   }

   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
      System.out.println("view visibility changed view [" + event.getView().getId() + "] visible [" + event.getView().isViewVisible() + "]");
   }

}
