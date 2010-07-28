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

import java.util.List;

import org.exoplatform.gwtframework.ui.client.component.command.Control;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
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
