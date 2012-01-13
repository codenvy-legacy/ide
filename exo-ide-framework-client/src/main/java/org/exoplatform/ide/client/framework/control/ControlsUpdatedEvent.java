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

import java.util.List;

import org.exoplatform.gwtframework.ui.client.command.Control;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class ControlsUpdatedEvent extends GwtEvent<ControlsUpdatedHandler>
{

   public static final GwtEvent.Type<ControlsUpdatedHandler> TYPE = new GwtEvent.Type<ControlsUpdatedHandler>();

   private List<Control> controls;

   public ControlsUpdatedEvent(List<Control> controls)
   {
      this.controls = controls;
   }

   public List<Control> getControls()
   {
      return controls;
   }

   @Override
   protected void dispatch(ControlsUpdatedHandler handler)
   {
      handler.onControlsUpdated(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ControlsUpdatedHandler> getAssociatedType()
   {
      return TYPE;
   }

}
