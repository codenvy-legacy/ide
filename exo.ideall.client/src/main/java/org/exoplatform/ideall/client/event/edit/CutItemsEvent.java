/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.ideall.client.event.edit;

import com.google.gwt.event.shared.GwtEvent;



/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class CutItemsEvent extends GwtEvent<CutItemsHandler>
{
   public static final GwtEvent.Type<CutItemsHandler> TYPE = new GwtEvent.Type<CutItemsHandler>();

   @Override
   protected void dispatch(CutItemsHandler handler)
   {
      handler.onCutItems(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<CutItemsHandler> getAssociatedType()
   {
      return TYPE;
   }

}

