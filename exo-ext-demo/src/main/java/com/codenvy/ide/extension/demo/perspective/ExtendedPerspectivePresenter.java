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
package com.codenvy.ide.extension.demo.perspective;

import com.codenvy.ide.outline.OutlinePartPrenter;
import com.codenvy.ide.part.EditorPartStackPresenter;
import com.codenvy.ide.part.PartStackPresenter;
import com.codenvy.ide.part.console.ConsolePartPresenter;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerPartPresenter;
import com.codenvy.ide.perspective.PerspectivePresenter;

import com.codenvy.ide.extension.demo.perspective.ExtendedPerspectiveView.ExtendedPerspectiveActionDelegate;

import com.google.gwt.user.client.Window;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;


/**
 * Demo for Perspective API, that allows to create custom branded perspectives. This perspective shows
 * an ability 
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
public class ExtendedPerspectivePresenter extends PerspectivePresenter implements ExtendedPerspectiveActionDelegate
{
   /**
    * @param view
    */
   @Inject
   public ExtendedPerspectivePresenter(ExtendedPerspectiveView view, EditorPartStackPresenter editorPartStackPresenter,
      Provider<PartStackPresenter> partStackProvider, OutlinePartPrenter outlinePart, ConsolePartPresenter consolePart,
      ProjectExplorerPartPresenter projectExplorerPart)
   {
      super(view, editorPartStackPresenter, partStackProvider);
      // handle Extended View Actions
      view.setDelegate(this);

      // Open required Parts
      openPart(projectExplorerPart, PartStackType.NAVIGATION);
      openPart(outlinePart, PartStackType.TOOLING);
      openPart(consolePart, PartStackType.INFORMATION);
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void onGoogleAccountClick()
   {
      // handle actition for custom Perspective controls
      Window
         .alert("Perspective API allows 3rd Party developers to create a custom Perspectives with additional controls, "
            + "form or any other branded UI");
   }
}
