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
package org.exoplatform.ide.client.navigation.control.newitem;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.navigator.NavigatorPresenter;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.ImageResource;

/* 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"administrators", "developers"})
public class NewFileCommand extends SimpleControl implements IDEControl, ViewVisibilityChangedHandler,
   VfsChangedHandler
{

   protected boolean browserSelected = true;

   protected String entryPoint;

   public NewFileCommand(String id, String title, String prompt, String icon, GwtEvent<?> event)
   {
      super(id);
      setTitle(title);
      setPrompt(prompt);
      setIcon(icon);
      setEvent(event);
      setEnabled(true);
   }

   public NewFileCommand(String id, String title, String prompt, ImageResource normalIcon, ImageResource disabledIcon,
      GwtEvent<?> event)
   {
      super(id);
      setTitle(title);
      setPrompt(prompt);
      setImages(normalIcon, disabledIcon);
      setEvent(event);
      setEnabled(true);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(ViewVisibilityChangedEvent.TYPE, this);
      eventBus.addHandler(VfsChangedEvent.TYPE, this);

      updateEnabling();
   }

   protected void updateEnabling()
   {
      if (entryPoint == null)
      {
         setVisible(false);
         setEnabled(false);
         return;
      }

      setVisible(true);

      if (browserSelected)
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }

   public void onVfsChanged(VfsChangedEvent event)
   {
      entryPoint = (event.getVfsInfo() != null) ? event.getVfsInfo().getId() : null;
      updateEnabling();
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent)
    */
   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
      if (event.getView() instanceof NavigatorPresenter.Display)
      {
         browserSelected = event.getView().isViewVisible();
         updateEnabling();
      }
   }

}
