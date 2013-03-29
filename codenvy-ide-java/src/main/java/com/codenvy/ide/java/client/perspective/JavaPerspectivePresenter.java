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
package com.codenvy.ide.java.client.perspective;

import com.codenvy.ide.api.ui.perspective.EditorPartStack;
import com.codenvy.ide.api.ui.perspective.PartStack;

import com.codenvy.ide.api.parts.OutlinePart;

import com.codenvy.ide.api.parts.ConsolePart;

import com.codenvy.ide.api.parts.ProjectExplorerPart;

import com.codenvy.ide.api.ui.perspective.PerspectivePresenter;


import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;


/**
 * Standard Java Perspective
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
public class JavaPerspectivePresenter extends PerspectivePresenter
{
   /**
    * Setups and initializes Java Perspective
    * 
    * @param view
    * @param editorPartStackPresenter
    * @param partStackProvider
    * @param outlinePart
    * @param consolePart
    * @param projectExplorerPart
    */
   @Inject
   public JavaPerspectivePresenter(JavaPerspectiveView view, EditorPartStack editorPartStackPresenter,
      Provider<PartStack> partStackProvider, OutlinePart outlinePart, ConsolePart consolePart,
      ProjectExplorerPart projectExplorerPart)
   {
      super(view, editorPartStackPresenter, partStackProvider);
      openPart(projectExplorerPart, PartStackType.NAVIGATION);
      openPart(outlinePart, PartStackType.TOOLING);
      openPart(consolePart, PartStackType.INFORMATION);
   }
}
