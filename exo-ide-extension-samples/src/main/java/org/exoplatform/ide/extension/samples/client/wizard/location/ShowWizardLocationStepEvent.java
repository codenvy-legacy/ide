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
package org.exoplatform.ide.extension.samples.client.wizard.location;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event, that calls <code>Select location</code> dialog window with navigation tree.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ShowWizardLocationStepEvent.java Sep 12, 2011 3:32:46 PM vereshchaka $
 */
public class ShowWizardLocationStepEvent extends GwtEvent<ShowWizardLocationStepHandler>
{
   public static final GwtEvent.Type<ShowWizardLocationStepHandler> TYPE = new GwtEvent.Type<ShowWizardLocationStepHandler>();
   
   public ShowWizardLocationStepEvent()
   {
   }

   @Override
   protected void dispatch(ShowWizardLocationStepHandler handler)
   {
      handler.onSelectLocation(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ShowWizardLocationStepHandler> getAssociatedType()
   {
      return TYPE;
   }
   
}
