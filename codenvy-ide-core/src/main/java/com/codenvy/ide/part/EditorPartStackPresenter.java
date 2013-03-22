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
package com.codenvy.ide.part;

import com.codenvy.ide.editor.EditorPartPresenter;

import com.codenvy.ide.util.loging.Log;


import com.google.inject.Singleton;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * EditorPartStackPresenter is a special PartStackPresenter that is shared among all
 * Perspectives and used to display Editors. 
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
public class EditorPartStackPresenter extends PartStackPresenter implements EditorPartStack
{

   /**
    * @param view
    * @param partStackResources
    * @param eventBus
    */
   @Inject
   public EditorPartStackPresenter(PartStackView view, PartStackUIResources partStackResources, EventBus eventBus,
      PartStackEventHandler partStackEventHandler)
   {
      super(view, partStackResources, eventBus, partStackEventHandler);
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void addPart(PartPresenter part)
   {
      if (!(part instanceof EditorPartPresenter))
      {
         Log.warn(getClass(), "EditorPartStack is not intended to be used to open non-Editor Parts.");
      }
      super.addPart(part);
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void setActivePart(PartPresenter part)
   {
      if (!(part instanceof EditorPartPresenter))
      {
         Log.warn(getClass(), "EditorPartStack is not intended to be used to open non-Editor Parts.");
      }
      super.setActivePart(part);
   }

}
