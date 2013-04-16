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

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.parts.OutlinePart;
import com.codenvy.ide.api.parts.ProjectExplorerPart;
import com.codenvy.ide.api.parts.SearchPart;
import com.codenvy.ide.api.parts.WelcomePart;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * * General-purpose, displaying all the PartStacks in a default manner:
 * Navigation at the left side;
 * Tooling at the right side;
 * Information at the bottom of the page;
 * Editors int center.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class WorkBenchPresenter implements Presenter {

    protected final JsonStringMap<PartStack> partStacks = JsonCollections.createStringMap();
    private WorkBenchViewImpl view;

    /**
     * Instantiates the Perspective
     *
     * @param view
     * @param editorPartStackPresenter
     * @param stackPresenterFactory
     */
    @Inject
    public WorkBenchPresenter(WorkBenchViewImpl view, EditorPartStack editorPartStackPresenter,
                              PartStackPresenterFactory stackPresenterFactory, PartStackViewFactory partViewFactory,
                              OutlinePart outlinePart, ConsolePart consolePart,
                              ProjectExplorerPart projectExplorerPart, WelcomePart welcomePart, SearchPart searchPart) {
        this.view = view;

        partStacks.put(PartStackType.EDITING.toString(), editorPartStackPresenter);

        //TODO move to implementation
        PartStackView navigationView = partViewFactory.create(PartStackView.TabPosition.LEFT, view.leftPanel);

        PartStack navigationPartStack =
                stackPresenterFactory.create(navigationView, new WorkBenchPartControllerImpl(view.splitPanel, view.navPanel));
        partStacks.put(PartStackType.NAVIGATION.toString(), navigationPartStack);

        PartStackView informationView = partViewFactory.create(PartStackView.TabPosition.BELOW, view.bottomPanel);
        PartStack informationStack =
                stackPresenterFactory.create(informationView, new WorkBenchPartControllerImpl(view.splitPanel, view.infoPanel));
        partStacks.put(PartStackType.INFORMATION.toString(), informationStack);

        PartStackView toolingView = partViewFactory.create(PartStackView.TabPosition.RIGHT, view.rightPanel);
        PartStack toolingPartStack =
                stackPresenterFactory.create(toolingView, new WorkBenchPartControllerImpl(view.splitPanel, view.toolPanel));
        partStacks.put(PartStackType.TOOLING.toString(), toolingPartStack);

        openPart(welcomePart, PartStackType.EDITING);
        openPart(outlinePart, PartStackType.TOOLING);
        openPart(projectExplorerPart, PartStackType.NAVIGATION);
        openPart(consolePart, PartStackType.INFORMATION);
        openPart(searchPart, PartStackType.INFORMATION);
        setActivePart(projectExplorerPart);
    }

    public void removePart(PartPresenter part) {
        PartStack destPartStack = findPartStackByPart(part);
        if(destPartStack != null){
            destPartStack.removePart(part);
        }
    }

    public void hidePart(PartPresenter part) {
        PartStack destPartStack = findPartStackByPart(part);
        if(destPartStack != null){
            destPartStack.hidePart(part);
        }
    }

    /**
     * Reveals given Part and requests focus for it.
     *
     * @param part
     */
    public void setActivePart(PartPresenter part) {
        PartStack destPartStack = findPartStackByPart(part);
        if (destPartStack != null) {
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
    protected PartStack findPartStackByPart(PartPresenter part) {
        for (PartStackType partStackType : PartStackType.values()) {
            if (partStacks.get(partStackType.toString()).containsPart(part)) {
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
    public void openPart(PartPresenter part, PartStackType type) {
        PartStack destPartStack = partStacks.get(type.toString());
        destPartStack.addPart(part);
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        getPartStack(PartStackType.NAVIGATION).go(view.getNavigationPanel());
        getPartStack(PartStackType.EDITING).go(view.getEditorPanel());
        getPartStack(PartStackType.TOOLING).go(view.getToolPanel());
        getPartStack(PartStackType.INFORMATION).go(view.getInformationPanel());

        container.setWidget(view);
    }

    /**
     * Retrieves the instance of the {@link PartStack} for given {@link PartStackType}
     *
     * @param type
     * @return
     */
    protected PartStack getPartStack(PartStackType type) {
        return partStacks.get(type.toString());
    }

    /** Defines Part's position on the Screen */
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

}
