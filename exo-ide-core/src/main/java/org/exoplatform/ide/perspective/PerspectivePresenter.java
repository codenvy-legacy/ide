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
package org.exoplatform.ide.perspective;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Provider;

import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.part.EditorPartStackPresenter;
import org.exoplatform.ide.part.PartPresenter;
import org.exoplatform.ide.part.PartStackPresenter;
import org.exoplatform.ide.presenter.Presenter;

/**
 * Abstract Perspective Presenter that should be subclassed in order to create new Perspective
 * Please refer to {@link GenericPerspectivePresenter} as a sample.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public abstract class PerspectivePresenter implements Presenter
{

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

   protected final PerspectiveView<?> view;

   protected final JsonStringMap<PartStackPresenter> partStacks = JsonCollections.createStringMap();

   /**
    * Instantiates the Perspective
    * 
    * @param view
    * @param editorPartStackPresenter
    * @param partStackProvider
    */
   public PerspectivePresenter(PerspectiveView<?> view, EditorPartStackPresenter editorPartStackPresenter,
      Provider<PartStackPresenter> partStackProvider)
   {
      this.view = view;

      for (PartStackType partStackType : PartStackType.values())
      {
         // use global Editor Part Stack
         PartStackPresenter partStack =
            (partStackType == PartStackType.EDITING) ? editorPartStackPresenter : partStackProvider.get();
         partStacks.put(partStackType.toString(), partStack);
      }

   }

   /**
    * Reveals given Part and requests focus for it.
    * 
    * @param part
    */
   public void setActivePart(PartPresenter part)
   {
      PartStackPresenter destPartStack = findPartStackByPart(part);
      if (destPartStack != null)
      {
         destPartStack.setActivePart(part);
         // will request focus for stack
      }
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

   /**
    * Opens new Part or shows already opened
    * 
    * @param part
    * @param type
    */
   public void openPart(PartPresenter part, PartStackType type)
   {
      PartStackPresenter destPartStack = partStacks.get(type.toString());
      destPartStack.addPart(part);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void go(AcceptsOneWidget container)
   {
      getPartStack(PartStackType.NAVIGATION).go(view.getNavigationPanel());
      getPartStack(PartStackType.EDITING).go(view.getEditorPanel());
      getPartStack(PartStackType.TOOLING).go(view.getToolPanel());
      getPartStack(PartStackType.INFORMATION).go(view.getInformationPanel());

      container.setWidget(view);
   }

   /**
    * Retrieves the instance of the {@link PartStackPresenter} for given {@link PartStackType}
    * 
    * @param type
    * @return
    */
   protected PartStackPresenter getPartStack(PartStackType type)
   {
      return partStacks.get(type.toString());
   }

}
