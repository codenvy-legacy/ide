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
package org.exoplatform.ide.extension.samples.client.githubimport;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to show form to Import your projects from GitHub.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ShowImportFromGithubEvent.java Dec 7, 2011 3:19:59 PM vereshchaka $
 */
public class ShowImportFromGithubEvent extends GwtEvent<ShowImportFromGithubHandler>
{
   public static final GwtEvent.Type<ShowImportFromGithubHandler> TYPE =
      new GwtEvent.Type<ShowImportFromGithubHandler>();

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ShowImportFromGithubHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ShowImportFromGithubHandler handler)
   {
      handler.onShowImportFromGithub(this);
   }

}
