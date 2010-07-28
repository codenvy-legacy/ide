/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.framework.control.event;

import org.exoplatform.gwtframework.ui.client.component.command.Control;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class RegisterControlEvent extends GwtEvent<RegisterControlHandler>
{

   public static final GwtEvent.Type<RegisterControlHandler> TYPE = new GwtEvent.Type<RegisterControlHandler>();

   private Control control;

   private boolean dockOnToolbar = false;

   private boolean rightDocking = false;

   public RegisterControlEvent(Control control)
   {
      this.control = control;
   }

   public RegisterControlEvent(Control control, boolean dockOnToolbar)
   {
      this.control = control;
      this.dockOnToolbar = dockOnToolbar;
   }

   public RegisterControlEvent(Control control, boolean dockOnToolbar, boolean rightDocking)
   {
      this.control = control;
      this.dockOnToolbar = dockOnToolbar;
      this.rightDocking = rightDocking;
   }

   public Control getControl()
   {
      return control;
   }

   public boolean isDockOnToolbar()
   {
      return dockOnToolbar;
   }

   public boolean isRightDocking()
   {
      return rightDocking;
   }

   @Override
   protected void dispatch(RegisterControlHandler handler)
   {
      handler.onRegisterControl(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<RegisterControlHandler> getAssociatedType()
   {
      return TYPE;
   }

}
