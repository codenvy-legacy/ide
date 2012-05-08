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
package org.exoplatform.ide.extension.java.jdi.client.events;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.java.jdi.shared.Value;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: UpdateVariableValueInTreeEvent.java Apr 28, 2012 10:05:37 AM azatsarynnyy $
 *
 */
public class UpdateVariableValueInTreeEvent extends GwtEvent<UpdateVariableValueInTreeHandler>
{

   /**
    * Type used to register this event.
    */
   public static final GwtEvent.Type<UpdateVariableValueInTreeHandler> TYPE = new GwtEvent.Type<UpdateVariableValueInTreeHandler>();

   /**
    * Variable whose value need to update.
    */
   private Variable variable;

   /**
    * New variables value.
    */
   private Value value;

   public UpdateVariableValueInTreeEvent(Variable variable, Value value)
   {
      this.variable = variable;
      this.value = value;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<UpdateVariableValueInTreeHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(UpdateVariableValueInTreeHandler handler)
   {
      handler.onUpdateVariableValueInTree(this);
   }

   /**
    * Returns variable whose value need to update.
    * 
    * @return variable
    */
   public Variable getVariable()
   {
      return variable;
   }

   /**
    * Returns new variables value.
    * 
    * @return value
    */
   public Value getValue()
   {
      return value;
   }

}
