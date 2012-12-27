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
package org.exoplatform.ide.part;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.api.ui.part.PartAgent;
import org.exoplatform.ide.core.event.ActivePartChangedEvent;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.part.PartStackPresenter.PartStackEventHandler;

/**
 * Part Agent manages all the Part Stack available in application. It is responsible for granting a 
 * focus for a stack when it requests it and responsible for showing a part. It fires event when 
 * Active Part changes also (have a look at {@link ActivePartChangedEvent})
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
public class PartAgentPresenter implements PartAgent
{

   private final JsonStringMap<PartStackPresenter> partStacks = JsonCollections.createStringMap();

   private PartStackPresenter activePartStack;

   private final PartStackEventHandler partStackHandler = new ActivePartChangedHandler();

   private final EventBus eventBus;

   /** Handles PartStack Events */
   private final class ActivePartChangedHandler implements PartStackEventHandler
   {
      /**
      * {@inheritDoc}
      */
      @Override
      public void onActivePartChanged(PartPresenter part)
      {
         activePartChanged(part);
      }

      @Override
      public void onRequestFocus(PartStackPresenter partStack)
      {
         setActivePartStack(partStack);
      }
   }

   /**
    * Instantiates PartAgent with provided factory and event bus
    */
   @Inject
   public PartAgentPresenter(Provider<PartStackPresenter> partStackProvider, EventBus eventBus)
   {
      this.eventBus = eventBus;
      for (PartStackType partStackType : PartStackType.values())
      {
         PartStackPresenter partStack = partStackProvider.get();
         partStack.setPartStackEventHandler(partStackHandler);
         partStacks.put(partStackType.toString(), partStack);
      }
   }

   /**
    * @param part
    */
   public void activePartChanged(PartPresenter part)
   {
      // fire event, active part changed
      eventBus.fireEvent(new ActivePartChangedEvent(part));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setActivePart(PartPresenter part)
   {
      PartStackPresenter destPartStack = findPartStackByPart(part);
      if (destPartStack != null)
      {
         destPartStack.setActivePart(part);
         setActivePartStack(destPartStack);
      }
   }

   /**
    * Activate given Part Stack
    * @param partStack
    */
   protected void setActivePartStack(PartStackPresenter partStack)
   {
      // nothing to do
      if (activePartStack == partStack || partStack == null)
      {
         return;
      }
      // drop focus from active partStack
      if (activePartStack != null)
      {
         activePartStack.setFocus(false);
      }

      // set part focused
      activePartStack = partStack;
      activePartStack.setFocus(true);
      
      activePartChanged(activePartStack.getActivePart());
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void addPart(PartPresenter part, PartStackType type)
   {
      PartStackPresenter destPartStack = partStacks.get(type.toString());
      destPartStack.addPart(part);
      setActivePartStack(destPartStack);
   }

   /**
    * Expose PartStack of selected Type into given container
    * 
    * @param type
    * @param container
    */
   public void go(PartStackType type, AcceptsOneWidget container)
   {
      getPartStack(type).go(container);
   }

   /**
    * Get PartStack by Type
    * 
    * @param type
    * @return
    */
   protected PartStackPresenter getPartStack(PartStackType type)
   {
      return partStacks.get(type.toString());
   }

   /**
    * Find parent PartStack for given Part
    * 
    * @param part 
    * @return Parent PartStackPresenter or null if part not registered
    */
   protected PartStackPresenter findPartStackByPart(PartPresenter part)
   {
      for (PartStackType partStackType : PartStackType.values())
      {
         if (partStacks.get(partStackType.toString()).containsPart(part))
         {
            return partStacks.get(partStackType.toString());
         }
      }

      // not found
      return null;
   }

}
