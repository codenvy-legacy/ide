/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.workspace;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.parts.*;
import com.codenvy.ide.api.ui.workspace.*;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.JsonStringMap;
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

    protected final JsonStringMap<PartStack> partStacks = Collections.createStringMap();
    private WorkBenchViewImpl view;

    /**
     * Instantiates the Perspective
     *
     * @param view
     * @param editorPartStackPresenter
     * @param stackPresenterFactory
     */
    @Inject
    public WorkBenchPresenter(WorkBenchViewImpl view,
                              EditorPartStack editorPartStackPresenter,
                              PartStackPresenterFactory stackPresenterFactory,
                              PartStackViewFactory partViewFactory,
                              OutlinePart outlinePart,
                              ConsolePart consolePart,
                              ProjectExplorerPart projectExplorerPart,
                              WelcomePart welcomePart,
                              SearchPart searchPart) {
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
        if (destPartStack != null) {
            destPartStack.removePart(part);
        }
    }

    public void hidePart(PartPresenter part) {
        PartStack destPartStack = findPartStackByPart(part);
        if (destPartStack != null) {
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
}