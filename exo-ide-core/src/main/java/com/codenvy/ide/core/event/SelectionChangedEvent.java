/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.core.event;

import com.codenvy.ide.selection.Selection;

import com.google.gwt.event.shared.GwtEvent;


/**
 * Event that notifies of changed Core Expressions
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class SelectionChangedEvent extends GwtEvent<SelectionChangedHandler>
{
   public static Type<SelectionChangedHandler> TYPE = new Type<SelectionChangedHandler>();

   private final Selection<?> selection;

   /**
    * @param expressions the map of ID's and current values
    */
   public SelectionChangedEvent(Selection<?> selection)
   {
      this.selection = selection;
   }

   @Override
   public Type<SelectionChangedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return current selection
    */
   public Selection<?> getSelection()
   {
      return selection;
   }

   @Override
   protected void dispatch(SelectionChangedHandler handler)
   {
      handler.onSelectionChanged(this);
   }
}
