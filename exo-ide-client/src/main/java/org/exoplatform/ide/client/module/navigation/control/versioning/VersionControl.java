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
package org.exoplatform.ide.client.module.navigation.control.versioning;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.ui.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.event.ViewOpenedHandler;
import org.exoplatform.ide.client.panel.event.PanelDeselectedEvent;
import org.exoplatform.ide.client.panel.event.PanelDeselectedHandler;
import org.exoplatform.ide.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ide.client.panel.event.PanelSelectedHandler;
import org.exoplatform.ide.client.versioning.VersionContentForm;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 13, 2010 $
 *
 */
public class VersionControl extends SimpleControl implements IDEControl, ViewClosedHandler, ViewOpenedHandler,
   PanelDeselectedHandler, PanelSelectedHandler
{

   /**
    * @param id
    */
   public VersionControl(String id)
   {
      super(id);
      
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(ViewOpenedEvent.TYPE, this);
      eventBus.addHandler(PanelDeselectedEvent.TYPE, this);
      eventBus.addHandler(PanelSelectedEvent.TYPE, this);
   }
   
   /**
    * @see org.exoplatform.ide.client.panel.event.PanelSelectedHandler#onPanelSelected(org.exoplatform.ide.client.panel.event.PanelSelectedEvent)
    */
   public void onPanelSelected(PanelSelectedEvent event)
   {
      if (VersionContentForm.ID.equals(event.getPanelId()))
      {
         setVisible(true);
      }
   }

   /**
    * @see org.exoplatform.ide.client.panel.event.PanelDeselectedHandler#onPanelDeselected(org.exoplatform.ide.client.panel.event.PanelDeselectedEvent)
    */
   public void onPanelDeselected(PanelDeselectedEvent event)
   {
      if (VersionContentForm.ID.equals(event.getPanelId()))
      {
         setVisible(false);
      }
   }

   /**
    * @see org.exoplatform.ide.client.panel.event.PanelOpenedHandler#onPanelOpened(org.exoplatform.ide.client.panel.event.PanelOpenedEvent)
    */
   public void onViewOpened(ViewOpenedEvent event)
   {
      if (VersionContentForm.ID.equals(event.getViewId()))
      {
         setVisible(true);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.event.ViewClosedHandler#onPanelClosed(org.exoplatform.ide.client.framework.ui.event.ViewClosedEvent)
    */
   public void onViewClosed(ViewClosedEvent event)
   {
      if (VersionContentForm.ID.equals(event.getViewId()))
      {
         setVisible(false);
      }
   }
}
