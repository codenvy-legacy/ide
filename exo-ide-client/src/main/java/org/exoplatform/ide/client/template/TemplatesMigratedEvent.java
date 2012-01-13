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
package org.exoplatform.ide.client.template;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Used to notify all presenters, that are somehow linked with templates, that templates were migrated from registry to plain text
 * file on server.
 * <p/>
 * This event called, when all templates are moved from registry to plain text file on server.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: TemplatesMigrated.java Aug 1, 2011 12:21:44 PM vereshchaka $
 * 
 */
public class TemplatesMigratedEvent extends GwtEvent<TemplatesMigratedHandler>
{

   public static final GwtEvent.Type<TemplatesMigratedHandler> TYPE = new GwtEvent.Type<TemplatesMigratedHandler>();

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<TemplatesMigratedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(TemplatesMigratedHandler handler)
   {
      handler.onTemplatesMigrated(this);
   }

}
