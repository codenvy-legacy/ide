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
package org.exoplatform.ide.part;

import org.exoplatform.ide.selection.Selection;
import org.exoplatform.ide.util.ListenerManager;
import org.exoplatform.ide.util.ListenerManager.Dispatcher;

/**
 * Abstract base implementation of all PartPresenter
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public abstract class AbstractPartPresenter implements PartPresenter
{

   private ListenerManager<PropertyListener> manager;

   private Selection<?> selection;

   /**
    * 
    */
   public AbstractPartPresenter()
   {
      manager = ListenerManager.create();
   }

   /**
   * @see org.exoplatform.ide.part.PartPresenter#onClose()
   */
   @Override
   public boolean onClose()
   {
      return false;
   }

   /**
    * @see org.exoplatform.ide.part.PartPresenter#onOpen()
    */
   @Override
   public void onOpen()
   {
   }

   /**
    * @see org.exoplatform.ide.part.PartPresenter#addPropertyListener(org.exoplatform.ide.part.PropertyListener)
    */
   @Override
   public void addPropertyListener(PropertyListener listener)
   {
      manager.add(listener);
   }

   /**
    * @see org.exoplatform.ide.part.PartPresenter#removePropertyListener(org.exoplatform.ide.part.PropertyListener)
    */
   @Override
   public void removePropertyListener(PropertyListener listener)
   {
      manager.remove(listener);
   }

   /**
    * Fires a property changed event.
    *
    * @param propId the id of the property that changed
    */
   protected void firePropertyChange(final int propId)
   {
      manager.dispatch(new Dispatcher<PropertyListener>()
      {

         @Override
         public void dispatch(PropertyListener listener)
         {
            listener.propertyChanged(AbstractPartPresenter.this, propId);
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Selection<?> getSelection()
   {
      return this.selection;
   }

   /**
    * Sets the Selection of the Part. It later can be accessible using {@link AbstractPartPresenter#getSelection()}
    * 
    * @param selection instance of Selection
    */
   public void setSelection(Selection<?> selection)
   {
      this.selection = selection;
      firePropertyChange(SELECTION_PROPERTY);
   }
}
