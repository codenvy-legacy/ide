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
package org.exoplatform.ide.client.framework.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event for opening form to creat project.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CreateNewProjectEvent.java Dec 8, 2011 5:38:18 PM vereshchaka $
 */
public class CreateNewProjectEvent extends GwtEvent<CreateNewProjectHandler>
{

   public static final GwtEvent.Type<CreateNewProjectHandler> TYPE = new GwtEvent.Type<CreateNewProjectHandler>();

   @Override
   protected void dispatch(CreateNewProjectHandler handler)
   {
      handler.onCreateNewProject(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<CreateNewProjectHandler> getAssociatedType()
   {
      return TYPE;
   }

}
