/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.part;

import com.codenvy.ide.api.constraints.Anchor;
import com.codenvy.ide.api.constraints.Constraints;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.event.EditorDirtyStateChangedEvent;
import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.parts.PartPresenter;
import com.codenvy.ide.api.parts.PartStack;
import com.codenvy.ide.api.parts.PartStackView;
import com.codenvy.ide.api.parts.PartStackView.TabItem;
import com.codenvy.ide.api.parts.PropertyListener;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerPartPresenter;
import com.codenvy.ide.workspace.WorkBenchPartController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements "Tab-like" UI Component, that accepts PartPresenters as child elements.
 * <p/>
 * PartStack support "focus" (please don't mix with GWT Widget's Focus feature). Focused PartStack will highlight active Part, notifying
 * user what component is currently active.
 *
 * @author Nikolay Zamosenchuk
 * @author Stéphane Daviet
 */
public class PartStackPresenter implements Presenter, PartStackView.ActionDelegate, PartStack {

    private static final int DEFAULT_SIZE = 200;

    private HashMap<PartPresenter, Double> partSizes = new HashMap<PartPresenter, Double>();

    /** list of parts */
    protected final Array<PartPresenter>     parts               = Collections.createArray();
    protected final Array<Integer>           viewPartPositions   = Collections.createArray();
    private         Map<String, Constraints> priorityPositionMap = new HashMap<>();
    /** view implementation */
    protected final PartStackView view;
    private final   EventBus      eventBus;
    protected boolean          partsClosable    = false;
    protected PropertyListener propertyListener = new PropertyListener() {

        @Override
        public void propertyChanged(PartPresenter source, int propId) {
            if (PartPresenter.TITLE_PROPERTY == propId) {
                updatePartTab(source);
            } else if (EditorPartPresenter.PROP_DIRTY == propId) {
                eventBus.fireEvent(new EditorDirtyStateChangedEvent(
                        (EditorPartPresenter)source));
            }
        }
    };
    /** current active part */
    protected PartPresenter           activePart;
    protected PartStackEventHandler   partStackHandler;
    /** Container for every new PartPresenter which will be added to this PartStack. */
    protected AcceptsOneWidget        partViewContainer;
    private   WorkBenchPartController workBenchPartController;

    @Inject
    public PartStackPresenter(EventBus eventBus,
                              PartStackEventHandler partStackEventHandler,
                              @Assisted final PartStackView view,
                              @Assisted WorkBenchPartController workBenchPartController) {
        this.view = view;
        this.eventBus = eventBus;
        partStackHandler = partStackEventHandler;
        this.workBenchPartController = workBenchPartController;
        partViewContainer = new AcceptsOneWidget() {
            @Override
            public void setWidget(IsWidget w) {
                view.getContentPanel().add(w);
            }
        };
        view.setDelegate(this);
    }


    /**
     * Update part tab, it's may be title, icon or tooltip
     *
     * @param part
     */
    private void updatePartTab(PartPresenter part) {
        if (!parts.contains(part)) {
            throw new IllegalArgumentException("This part stack not contains: " + part.getTitle());
        }
        int index = parts.indexOf(part);
        view.updateTabItem(index,
                           part.decorateIcon(
                                   part.getTitleSVGImage() != null ? new SVGImage(part.getTitleSVGImage()) : null),
                           part.getTitle(),
                           part.getTitleToolTip(),
                           part.getTitleWidget()
                          );
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        if (activePart != null) {
            view.setActiveTab(parts.indexOf(activePart));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus(boolean focused) {
        view.setFocus(focused);
    }

    /** {@inheritDoc} */
    @Override
    public void addPart(PartPresenter part) {
        addPart(part, null);
    }

    /** {@inheritDoc} */
    @Override
    public void addPart(PartPresenter part, Constraints constraint) {
        if (parts.contains(part)) {
            // part already exists
            // activate it
            setActivePart(part);
            // and return
            return;
        }
        if (part instanceof BasePresenter) {
            ((BasePresenter)part).setPartStack(this);
        }

        parts.add(part);
        viewPartPositions.add(parts.indexOf(part));
        partSizes.put(part, Double.valueOf(part.getSize()));

        part.addPropertyListener(propertyListener);
        // include close button
        SVGResource titleSVGResource = part.getTitleSVGImage();
        SVGImage titleSVGImage = null;
        if (titleSVGResource != null) {
            titleSVGImage = part.decorateIcon(new SVGImage(titleSVGResource));
        }
        TabItem tabItem =
                view.addTabButton(titleSVGImage,
                                  part.getTitle(),
                                  part.getTitleToolTip(),
                                  part.getTitleWidget(), partsClosable);
        bindEvents(tabItem, part);
        part.go(partViewContainer);
        sortPartsOnView(constraint);
        part.onOpen();
        // request focus
        onRequestFocus();
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsPart(PartPresenter part) {
        return parts.contains(part);
    }

    /** {@inheritDoc} */
    @Override
    public int getNumberOfParts() {
        return parts.size();
    }

    /** {@inheritDoc} */
    @Override
    public PartPresenter getActivePart() {
        return activePart;
    }

    /** {@inheritDoc} */
    @Override
    public void setActivePart(PartPresenter part) {
        if (activePart == part) {
            // request part stack to get the focus
            onRequestFocus();
            return;
        }

        // remember size of the previous active part
        if (activePart != null && workBenchPartController != null) {
            double size = workBenchPartController.getSize();
            partSizes.put(activePart, Double.valueOf(size));
        }

        activePart = part;

        if (part == null) {
            view.setActiveTab(-1);
            workBenchPartController.setHidden(true);
        } else {
            view.setActiveTab(parts.indexOf(activePart));
        }

        // request part stack to get the focus
        onRequestFocus();

        // notify handler, that part changed
        partStackHandler.onActivePartChanged(activePart);

        if (activePart != null && workBenchPartController != null) {
            workBenchPartController.setHidden(false);
            if (partSizes.containsKey(activePart)) {
                workBenchPartController.setSize(partSizes.get(activePart));
            } else {
                workBenchPartController.setSize(activePart.getSize());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void hidePart(PartPresenter part) {
        if (activePart == part) {
            if (workBenchPartController != null) {
//                partsSize = workBenchPartController.getSize();
                double size = workBenchPartController.getSize();
                partSizes.put(activePart, Double.valueOf(size));
                workBenchPartController.setHidden(true);
            }
            activePart = null;
            view.setActiveTab(-1);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removePart(PartPresenter part) {
        close(part);
    }

    /**
     * Close Part
     *
     * @param part
     */
    protected void close(PartPresenter part) {
        // may cancel close
        if (part.onClose()) {
            int partIndex = parts.indexOf(part);
            if (activePart == part) {
                PartPresenter newPart = null;
                for (int pos = parts.size() - 1; pos >= 0; pos--) {
                    if (parts.get(pos) instanceof ProjectExplorerPartPresenter) {
                        newPart = parts.get(pos);
                    }
                }
                setActivePart(newPart);
            }
            view.removeTab(partIndex);
            int viewPartPositionsIndex = viewPartPositions.indexOf(partIndex);
            if (viewPartPositionsIndex >= 0) {
                int lastPosOfViewPart = viewPartPositions.size() - 1;
                for (; viewPartPositionsIndex < lastPosOfViewPart; viewPartPositionsIndex++) {
                    viewPartPositions.set(viewPartPositions.get(viewPartPositionsIndex + 1), viewPartPositionsIndex);
                }
                viewPartPositions.remove(lastPosOfViewPart);
            }

            parts.remove(part);
            partSizes.remove(part);
            part.removePropertyListener(propertyListener);
        }
    }

    /**
     * Bind Activate and Close events to the Tab
     *
     * @param item
     * @param part
     */
    protected void bindEvents(final TabItem item, final PartPresenter part) {
        item.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (activePart == part) {
                    if (partsClosable) {
                        // request part stack to get the focus
                        onRequestFocus();
                    } else {
                        if (workBenchPartController != null) {
                            //partsSize = workBenchPartController.getSize();
                            double size = workBenchPartController.getSize();
                            partSizes.put(activePart, Double.valueOf(size));
                            workBenchPartController.setHidden(true);
                        }
                        activePart = null;
                        view.setActiveTab(-1);
                    }
                } else {
                    // make active
                    setActivePart(part);
                }
            }
        });

        item.addCloseHandler(new CloseHandler<PartStackView.TabItem>() {
            @Override
            public void onClose(CloseEvent<TabItem> event) {
                // close
                close(part);
            }
        });
    }

    /**
     * Returns the list of parts.
     *
     * @return {@link Array} array of parts
     */
    protected Array<PartPresenter> getParts() {
        return parts;
    }

    /** {@inheritDoc} */
    @Override
    public void onRequestFocus() {
        // notify partStackHandler
        // notify handler, that part changed
        partStackHandler.onRequestFocus(PartStackPresenter.this);
    }

    /** Handles PartStack actions */
    public interface PartStackEventHandler {
        /** PartStack is being clicked and requests Focus */
        void onActivePartChanged(PartPresenter part);

        /** PartStack is being clicked and requests Focus */
        void onRequestFocus(PartStack partStack);
    }

    /**
     * Sort parts depending on constraint.
     *
     * @param constraint
     */
    protected void sortPartsOnView(Constraints constraint) {
        int boofPartPosition;
        int partPositionsSize = viewPartPositions.size();
        int positionOfLastElement = viewPartPositions.get(partPositionsSize - 1);
        int lastPositionOfSorting = partPositionsSize - 1;
        PartPresenter checkPart;

        if (partPositionsSize > 1) {
            checkPart = parts.get(viewPartPositions.get(partPositionsSize - 2));
            Constraints previousConstraint = priorityPositionMap.get(checkPart.getTitle());
            if (previousConstraint != null && previousConstraint.myAnchor.equals(Anchor.LAST)) {
                boofPartPosition = viewPartPositions.get(partPositionsSize - 2);
                viewPartPositions.set(partPositionsSize - 2, viewPartPositions.get(partPositionsSize - 1));
                viewPartPositions.set(partPositionsSize - 1, boofPartPosition);
                lastPositionOfSorting = partPositionsSize - 2;
            }
        }
        if (constraint != null) {
            priorityPositionMap.put(parts.get(positionOfLastElement).getTitle(), constraint);
        } else if (priorityPositionMap.size() == 0) {
            return;
        }
        for (int labelOfPartsPos = 0; labelOfPartsPos < partPositionsSize; labelOfPartsPos++) {
            checkPart = parts.get(viewPartPositions.get(labelOfPartsPos));
            Constraints localeConstraint = priorityPositionMap.get(checkPart.getTitle());
            if (localeConstraint != null) {
                if (localeConstraint.myAnchor == Anchor.LAST) {
                    if (viewPartPositions.get(labelOfPartsPos) != positionOfLastElement) {
                        boofPartPosition = viewPartPositions.get(labelOfPartsPos);
                        for (int partPosition = labelOfPartsPos; partPosition < partPositionsSize - 1; partPosition++) {
                            viewPartPositions.set(partPosition, viewPartPositions.get(partPosition + 1));
                        }
                        viewPartPositions.set(partPositionsSize - 1, boofPartPosition);
                    }
                    continue;
                } else if (localeConstraint.myAnchor == Anchor.FIRST) {
                    if (viewPartPositions.get(labelOfPartsPos) != 0) {
                        boofPartPosition = viewPartPositions.get(labelOfPartsPos);
                        for (int partPosition = labelOfPartsPos; partPosition > 0; partPosition--) {
                            viewPartPositions.set(partPosition, viewPartPositions.get(partPosition - 1));
                        }
                        viewPartPositions.set(0, boofPartPosition);
                    }
                    continue;
                } else if (localeConstraint.myAnchor == Anchor.BEFORE) {
                    if (partPositionsSize > labelOfPartsPos + 1) {
                        if (parts.get(viewPartPositions.get(labelOfPartsPos + 1)).getTitle().equals(localeConstraint.myRelativeToActionId))
                            continue;
                    }
                } else {//Anchor.AFTER
                    if (labelOfPartsPos > 1) {
                        if (parts.get(viewPartPositions.get(labelOfPartsPos - 1)).getTitle().equals(localeConstraint.myRelativeToActionId))
                            continue;
                    }
                }
                if (labelOfPartsPos < lastPositionOfSorting) {
                    boofPartPosition = viewPartPositions.get(labelOfPartsPos);
                    for (int partPosition = labelOfPartsPos; partPosition < lastPositionOfSorting; partPosition++) {
                        viewPartPositions.set(partPosition, viewPartPositions.get(partPosition + 1));
                    }
                    viewPartPositions.set(lastPositionOfSorting, boofPartPosition);
                }
                boofPartPosition = viewPartPositions.get(labelOfPartsPos);
                for (int partPosition = lastPositionOfSorting; partPosition > 0; partPosition--) {
                    if (parts.get(viewPartPositions.get(partPosition - 1)).getTitle().equals(localeConstraint.myRelativeToActionId)) {
                        if (localeConstraint.myAnchor == Anchor.BEFORE) {
                            viewPartPositions.set(partPosition, boofPartPosition);
                        } else {
                            if (partPosition > 1) {
                                viewPartPositions.set(partPosition, viewPartPositions.get(partPosition - 1));
                                viewPartPositions.set(partPosition - 1, boofPartPosition);
                            }
                        }
                        break;
                    } else {
                        if (partPosition > 1) viewPartPositions.set(partPosition, viewPartPositions.get(partPosition - 1));
                    }
                }
            }
        }
        view.setTabpositions(viewPartPositions);
    }
}
