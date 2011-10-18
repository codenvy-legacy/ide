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
package org.exoplatform.ide.extension.samples.client.wizard;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to notify, that project creation finished:
 * Finish button (success finish) or cancel button (creation was cancesls) 
 * was pressed.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ProjectCreationFinishedEvent.java Sep 9, 2011 11:09:35 AM vereshchaka $
 */
public class ProjectCreationFinishedEvent extends GwtEvent<ProjectCreationFinishedHandler>
{
   
   /**
    * Is project creation canceled (cancel button was clicked)
    * or finish button was clicked.
    */
   private boolean canceled;
   
   public ProjectCreationFinishedEvent(boolean canceled)
   {
      this.canceled = canceled;
   }
   
   public static final GwtEvent.Type<ProjectCreationFinishedHandler> TYPE = new GwtEvent.Type<ProjectCreationFinishedHandler>();

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ProjectCreationFinishedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ProjectCreationFinishedHandler handler)
   {
      handler.onProjectCreationFinished(this);
   }
   
   /**
    * @return the canceled
    */
   public boolean isCanceled()
   {
      return canceled;
   }
}
