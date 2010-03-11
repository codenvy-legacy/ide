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
package org.exoplatform.ideall.client.model.gadget.event;

import org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class GadgetDeployResultEvent extends ServerExceptionEvent<GadgetDeployResultHandler>
{
   public static final Type<GadgetDeployResultHandler> TYPE = new Type<GadgetDeployResultHandler>(); 
   
   private Throwable exception;
   
   private String url;
   
   public GadgetDeployResultEvent(String url)
   {
      this.url = url;
   }
   
   @Override
   protected void dispatch(GadgetDeployResultHandler handler)
   {
      handler.onGadgetDeployResultReceived(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<GadgetDeployResultHandler> getAssociatedType()
   {
      return TYPE;
   }

   @Override
   public void setException(Throwable exception)
   {
      this.exception = exception;
   }
   
   public Throwable getException()
   {
      return exception;
   }
   
   public String getUrl()
   {
      return url;
   }
   
}
