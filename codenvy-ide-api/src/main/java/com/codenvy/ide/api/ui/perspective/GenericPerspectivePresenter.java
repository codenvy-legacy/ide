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
package com.codenvy.ide.api.ui.perspective;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.parts.OutlinePart;
import com.codenvy.ide.api.parts.ProjectExplorerPart;
import com.codenvy.ide.api.parts.SearchPart;
import com.codenvy.ide.api.parts.WelcomePart;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * General-purpose Perspective, displaying all the PartStacks in a default manner:
 * Navigation at the left side;
 * Tooling at the right side;
 * Information at the bottom of the page;
 * Editors int center.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class GenericPerspectivePresenter extends PerspectivePresenter {
    private GenericPerspectiveView view;

    /**
     * Instantiate the Perspective
     *
     * @param view
     * @param editorPartStackPresenter
     * @param partStackProvider
     * @param outlinePart
     * @param consolePart
     * @param projectExplorerPart
     * @param welcomePart
     */
    @Inject
    public GenericPerspectivePresenter(GenericPerspectiveView view, EditorPartStack editorPartStackPresenter,
                                       Provider<PartStack> partStackProvider, OutlinePart outlinePart, ConsolePart consolePart,
                                       ProjectExplorerPart projectExplorerPart, WelcomePart welcomePart, SearchPart searchPart) {
        super(view, editorPartStackPresenter, partStackProvider);
        this.view = view;
        // show required parts
        openPart(welcomePart, PartStackType.EDITING);
        openPart(projectExplorerPart, PartStackType.NAVIGATION);
        openPart(outlinePart, PartStackType.TOOLING);
        openPart(consolePart, PartStackType.INFORMATION);
        openPart(searchPart, PartStackType.INFORMATION);
    }

    @Override
    public void openPart(PartPresenter part, PartStackType type) {
        PartStack destPartStack = partStacks.get(type.toString());
        destPartStack.addPart(part);
        if(type == PartStackType.NAVIGATION)
        {
            view.splitPanel.setWidgetSize(view.navPanel, 240);
        }
        if(type == PartStackType.TOOLING)
        {
            view.splitPanel.setWidgetSize(view.toolPanel, 240);
        }
        if(type == PartStackType.INFORMATION)
        {
            view.splitPanel.setWidgetSize(view.infoPanel, 240);
        }
    }
}
