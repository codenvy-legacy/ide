/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
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
package com.codenvy.ide.selection;

import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.perspective.PartPresenter;
import com.codenvy.ide.api.ui.perspective.PropertyListener;
import com.codenvy.ide.core.event.ActivePartChangedEvent;
import com.codenvy.ide.core.event.ActivePartChangedHandler;
import com.codenvy.ide.core.event.SelectionChangedEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Implements {@link SelectionAgent}
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
public class SelectionAgentImpl implements ActivePartChangedHandler, PropertyListener, SelectionAgent
{

   private PartPresenter activePart;

   private final EventBus eventBus;

   @Inject
   public SelectionAgentImpl(EventBus eventBus)
   {
      this.eventBus = eventBus;
      // bind event listener
      eventBus.addHandler(ActivePartChangedEvent.TYPE, this);
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public Selection<?> getSelection()
   {
      return activePart != null ? activePart.getSelection() : null;
   }

   protected void notifySelectionChanged()
   {
      eventBus.fireEvent(new SelectionChangedEvent(getSelection()));
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void onActivePartChanged(ActivePartChangedEvent event)
   {
      // remove listener from previous active part
      if (activePart != null)
      {
         activePart.removePropertyListener(this);
      }
      // set new active part
      activePart = event.getActivePart();
      if (activePart != null)
      {
         activePart.addPropertyListener(this);
      }
      notifySelectionChanged();
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void propertyChanged(PartPresenter source, int propId)
   {
      // Check prperty and ensure came from active part
      if (propId == PartPresenter.SELECTION_PROPERTY && source == activePart)
      {
         notifySelectionChanged();
      }
   }

}
