/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.core.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event that notifies of changed Toggle item state Expressions.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class ChangeToggleItemStateEvent extends GwtEvent<ChangeToggleItemStateHandler>
{
   public static final GwtEvent.Type<ChangeToggleItemStateHandler> TYPE = new Type<ChangeToggleItemStateHandler>();

   private final int idExpression;

   /**
    * Create event.
    * 
    * @param idExpression
    */
   public ChangeToggleItemStateEvent(int idExpression)
   {
      this.idExpression = idExpression;
   }

   /**
    * Resturns expressions id.
    * 
    * @return
    */
   public int getIdExpression()
   {
      return idExpression;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ChangeToggleItemStateHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void dispatch(ChangeToggleItemStateHandler handler)
   {
      handler.onStateChanged(this);
   }
}