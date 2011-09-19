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
package org.exoplatform.ide.client.framework.control;

import org.exoplatform.gwtframework.ui.client.command.Control;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

@Deprecated
public class RegisterControlEvent extends GwtEvent<RegisterControlHandler>
{

   public static final GwtEvent.Type<RegisterControlHandler> TYPE = new GwtEvent.Type<RegisterControlHandler>();

   private Control control;

   private Docking docking = Docking.NONE;

   private boolean rightDocking = false;

   public RegisterControlEvent(Control control)
   {
      this.control = control;
   }

   public RegisterControlEvent(Control control, Docking docking)
   {
      this.control = control;
      this.docking = docking;
   }

   public RegisterControlEvent(Control control, Docking docking, boolean rightDocking)
   {
      this.control = control;
      this.docking = docking;
      this.rightDocking = rightDocking;
   }

   public Control getControl()
   {
      return control;
   }

   public Docking getDocking()
   {
      return docking;
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
