/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.workspace;

import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.mvp.Presenter;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.EditorPartStack;
import org.eclipse.che.ide.api.parts.OutlinePart;
import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.api.parts.PartStack;
import org.eclipse.che.ide.api.parts.PartStackType;
import org.eclipse.che.ide.api.parts.PartStackView;
import org.eclipse.che.ide.api.parts.ProjectExplorerPart;
import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.collections.StringMap;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;


/**
 * General-purpose, displaying all the PartStacks in a default manner:
 * Navigation at the left side;
 * Tooling at the right side;
 * Information at the bottom of the page;
 * Editors in the center.
 *
 * @author Nikolay Zamosenchuk
 */
@Singleton
public class WorkBenchPresenter implements Presenter {

    protected final StringMap<PartStack> partStacks = Collections.createStringMap();
    private WorkBenchViewImpl   view;
    private List<PartPresenter> activeParts;

    /**
     * Instantiates the Perspective
     *
     * @param view
     * @param editorPartStackPresenter
     * @param stackPresenterFactory
     * @param partViewFactory
     * @param outlinePart
     * @param projectExplorerPart
     * @param notificationManager
     */
    @Inject
    public WorkBenchPresenter(WorkBenchViewImpl view,
                              EditorPartStack editorPartStackPresenter,
                              PartStackPresenterFactory stackPresenterFactory,
                              PartStackViewFactory partViewFactory,
                              OutlinePart outlinePart,
                              ProjectExplorerPart projectExplorerPart,
                              NotificationManager notificationManager,
                              HideWidgetCallback hideWidgetCallback) {
        this.view = view;

        partStacks.put(PartStackType.EDITING.toString(), editorPartStackPresenter);

        //TODO move to implementation
        PartStackView navigationView = partViewFactory.create(PartStackView.TabPosition.LEFT, view.leftPanel);

        PartStack navigationPartStack =
                stackPresenterFactory.create(navigationView, new WorkBenchPartControllerImpl(view.splitPanel, view.navPanel,
                                                                                             hideWidgetCallback));
        partStacks.put(PartStackType.NAVIGATION.toString(), navigationPartStack);

        PartStackView informationView = partViewFactory.create(PartStackView.TabPosition.BELOW, view.bottomPanel);
        PartStack informationStack =
                stackPresenterFactory.create(informationView, new WorkBenchPartControllerImpl(view.splitPanel, view.infoPanel,
                                                                                              hideWidgetCallback));
        partStacks.put(PartStackType.INFORMATION.toString(), informationStack);

        PartStackView toolingView = partViewFactory.create(PartStackView.TabPosition.RIGHT, view.rightPanel);
        PartStack toolingPartStack =
                stackPresenterFactory.create(toolingView, new WorkBenchPartControllerImpl(view.splitPanel, view.toolPanel,
                                                                                          hideWidgetCallback));
        partStacks.put(PartStackType.TOOLING.toString(), toolingPartStack);

        openPart(outlinePart, PartStackType.TOOLING);
        openPart(projectExplorerPart, PartStackType.NAVIGATION);
        openPart(notificationManager, PartStackType.INFORMATION, Constraints.FIRST);

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

    public void expandEditorPart() {
        activeParts = new ArrayList<>();
        for (PartStack value : partStacks.getValues().asIterable()) {
            if (value instanceof EditorPartStack) {
                continue;
            }

            PartPresenter part = value.getActivePart();
            if (part != null) {
                value.hidePart(part);
                activeParts.add(part);
            }
        }
//        partStacks.iterate(new StringMap.IterationCallback<PartStack>() {
//            @Override
//            public void onIteration(String key, PartStack value) {
//
//            }
//        });
    }

    public void restoreEditorPart() {
        for (PartPresenter activePart : activeParts) {
            PartStack destPartStack = findPartStackByPart(activePart);
            PartPresenter newPart = destPartStack.getActivePart();
            if (newPart == null) {
                destPartStack.setActivePart(activePart);
            }
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
        openPart(part, type, null);
    }

    /**
     * Opens part with constraint
     *
     * @param part
     * @param type
     * @param type
     */
    public void openPart(PartPresenter part, PartStackType type, Constraints constraint) {
        PartStack destPartStack = partStacks.get(type.toString());
        destPartStack.addPart(part, constraint);
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
