/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.eclipse.jdt.client;

import com.google.gwt.event.shared.GwtEvent;

import org.eclipse.jdt.client.core.dom.CompilationUnit;

/**
 * Event occurs to update Java Outline. To handle it implement {@link UpdateOutlineHandler}.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 6, 2012 5:25:54 PM anya $
 * 
 */
public class UpdateOutlineEvent extends GwtEvent<UpdateOutlineHandler>
{

   /**
    * Type used to register event.
    */
   public static final GwtEvent.Type<UpdateOutlineHandler> TYPE = new GwtEvent.Type<UpdateOutlineHandler>();

   /**
    * Compilation unit with info for updating Outline.
    */
   private CompilationUnit compilationUnit;

   /**
    * @param compilationUnit compilation unit with info for updating Outline
    */
   public UpdateOutlineEvent(CompilationUnit compilationUnit)
   {
      this.compilationUnit = compilationUnit;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<UpdateOutlineHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(UpdateOutlineHandler handler)
   {
      handler.onUpdateOutline(this);
   }

   /**
    * @return {@link CompilationUnit} compilation unit with info for updating Outline
    */
   public CompilationUnit getCompilationUnit()
   {
      return compilationUnit;
   }
}
