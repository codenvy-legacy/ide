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

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.part.PartStackPresenter.FocusRequstHandler;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class PartAgent
{

   private final JsonStringMap<PartStackPresenter> partStacks = JsonCollections.createStringMap();

   private PartStackPresenter activePartStack;

   /**
    *
    * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
    *
    */
   private final class PartStackFocusRequstHandler implements FocusRequstHandler
   {
      private final PartStackPresenter partStack;

      /**
       * @param partStack
       */
      public PartStackFocusRequstHandler(PartStackPresenter partStack)
      {
         this.partStack = partStack;
      }

      @Override
      public void onRequestFocus()
      {
         setActivePartStack(partStack);
      }
   }

   /**
    * Defines Part's position on the Screen
    */
   public enum PartStackType {
      /** 
       * Contains navigation parts. Designed to navigate
       * by project, types, classes and any other entities.
       * Usually placed on the LEFT side of the IDE.
       */
      NAVIGATION,
      /** 
       * Contains informative parts. Designed to display
       * the state of the application, project or processes.
       * Usually placed on the BOTTOM side of the IDE.
       */
      INFORMATION,
      /** 
       * Contains editing parts. Designed to provide an
       * ability to edit any resources or settings. 
       * Usually placed in the CENTRAL part of the IDE.
       */
      EDITING,
      /** 
       * Contains tooling parts. Designed to provide handy
       * features and utilities, access to other services 
       * or any other features that are out of other PartType
       * scopes.  
       * Usually placed on the RIGHT side of the IDE.
       */
      TOOLING
   }

   /**
    * 
    */
   @Inject
   public PartAgent(Provider<PartStackPresenter> partStackProvider)
   {
      for (PartStackType partStackType : PartStackType.values())
      {
         PartStackPresenter partStack = partStackProvider.get();
         partStack.setFocusRequstHandler(new PartStackFocusRequstHandler(partStack));
         partStacks.put(partStackType.toString(), partStack);
      }
   }

   /**
    * Activate given part
    * 
    * @param part
    */
   public void setActivePart(PartPresenter part)
   {
      PartStackPresenter destPartStack = findPartStackByPart(part);
      if (destPartStack != null)
      {
         destPartStack.setActivePart(part);
         setActivePartStack(destPartStack);
      }
   }

   public void setActivePartStack(PartStackPresenter partStack)
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
   }

   /**
    * Add new Part
    * 
    * @param part
    * @param type
    */
   public void addPart(PartPresenter part, PartStackType type)
   {
      PartStackPresenter destPartStack = partStacks.get(type.toString());
      destPartStack.addPart(part);
      setActivePartStack(destPartStack);
   }

   /**
    * Get PartStack by Type
    * 
    * @param type
    * @return
    */
   public PartStackPresenter getPartStack(PartStackType type)
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
