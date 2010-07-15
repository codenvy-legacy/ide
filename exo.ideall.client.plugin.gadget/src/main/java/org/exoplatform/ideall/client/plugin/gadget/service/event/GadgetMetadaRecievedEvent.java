/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.ideall.client.plugin.gadget.service.event;

import org.exoplatform.ideall.client.plugin.gadget.service.GadgetMetadata;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class GadgetMetadaRecievedEvent extends GwtEvent<GadgetMetadaRecievedHandler>
{

   public static GwtEvent.Type<GadgetMetadaRecievedHandler> TYPE = new Type<GadgetMetadaRecievedHandler>();

   private GadgetMetadata metadata;

   public GadgetMetadaRecievedEvent(GadgetMetadata metadata)
   {
      this.metadata = metadata;
   }

   @Override
   protected void dispatch(GadgetMetadaRecievedHandler handler)
   {
      handler.onMetadataRecieved(this);
   }

   @Override
   public GwtEvent.Type<GadgetMetadaRecievedHandler> getAssociatedType()
   {
      return TYPE;
   }

   public GadgetMetadata getMetadata()
   {
      return metadata;
   }
   
}
