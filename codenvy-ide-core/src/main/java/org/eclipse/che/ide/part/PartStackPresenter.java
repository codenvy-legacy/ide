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
package org.eclipse.che.ide.part;

import org.eclipse.che.ide.api.constraints.Anchor;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.event.EditorDirtyStateChangedEvent;
import org.eclipse.che.ide.api.mvp.Presenter;
import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.api.parts.PartStack;
import org.eclipse.che.ide.api.parts.PartStackView;
import org.eclipse.che.ide.api.parts.PartStackView.TabItem;
import org.eclipse.che.ide.api.parts.PropertyListener;
import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.Collections;

import org.eclipse.che.ide.part.projectexplorer.ProjectExplorerPartPresenter;
import org.eclipse.che.ide.workspace.WorkBenchPartController;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.ide.part.projectexplorer.ProjectExplorerPartPresenter;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    /** Handles PartStack actions */
    public interface PartStackEventHandler {
        /** PartStack is being clicked and requests Focus */
        void onRequestFocus(PartStack partStack);
    }

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
        TabItem tabItem =view.addTab(titleSVGImage, part.getTitle(), part.getTitleToolTip(),
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

        if (activePart != null && workBenchPartController != null) {
            workBenchPartController.setHidden(false);
            if (partSizes.containsKey(activePart)) {
                workBenchPartController.setSize(partSizes.get(activePart));
            } else {
                workBenchPartController.setSize(activePart.getSize());
            }
        }
    }

    /**
     * Gets all the parts registered.
     */
    public List<PartPresenter> getPartPresenters() {
        List<PartPresenter> presenters = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
            presenters.add(parts.get(i));
    }
        return presenters;
    }

    /** {@inheritDoc} */
    @Override
    public void hidePart(PartPresenter part) {
        if (activePart == part) {
            if (workBenchPartController != null) {
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
    protected void close(final PartPresenter part) {
        part.onClose(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(Void aVoid) {
                int partIndex = parts.indexOf(part);
                if (activePart == part) {
                    PartPresenter newActivePart = null;
                    for (PartPresenter tmpPart : parts.asIterable()) {
                        if (tmpPart instanceof ProjectExplorerPartPresenter) {
                            newActivePart = tmpPart;
                            break;
                        }
                    }
                    setActivePart(newActivePart);
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
        });
    }

    HandlerRegistration eventsBlocker;

    /**
     * Bind Activate and Close events to the Tab
     *
     * @param item
     * @param part
     */
    protected void bindEvents(final TabItem item, final PartPresenter part) {
        item.addCloseHandler(new CloseHandler<PartStackView.TabItem>() {
            @Override
            public void onClose(CloseEvent<TabItem> event) {
                close(part);
            }
        });

        item.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                /* Blocking any events excepting Mouse UP */
                eventsBlocker = Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
                    @Override
                    public void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                        if (event.getTypeInt() == Event.ONMOUSEUP) {
                            eventsBlocker.removeHandler();
                            return;
                        }

                        event.cancel();
                        event.getNativeEvent().preventDefault();
                        event.getNativeEvent().stopPropagation();
                    }
                });

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
        partStackHandler.onRequestFocus(PartStackPresenter.this);
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus(boolean focused) {
        view.setFocus(focused);
    }

    /**
     * Sort parts depending on constraint.
     *
     * @param constraint
     */
    protected void sortPartsOnView(Constraints constraint) {
        // TODO remake method of sorting
        int oldPartPosition;
        int partPositionsSize = viewPartPositions.size();
        int positionOfLastElement = viewPartPositions.get(partPositionsSize - 1);
        int lastPositionOfSorting = partPositionsSize - 1;
        PartPresenter checkPart;

        if (partPositionsSize > 1) {
            checkPart = parts.get(viewPartPositions.get(partPositionsSize - 2));
            Constraints previousConstraint = priorityPositionMap.get(checkPart.getTitle());
            if (previousConstraint != null && previousConstraint.myAnchor.equals(Anchor.LAST)) {
                oldPartPosition = viewPartPositions.get(partPositionsSize - 2);
                viewPartPositions.set(partPositionsSize - 2, viewPartPositions.get(partPositionsSize - 1));
                viewPartPositions.set(partPositionsSize - 1, oldPartPosition);
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
                        oldPartPosition = viewPartPositions.get(labelOfPartsPos);
                        for (int partPosition = labelOfPartsPos; partPosition < partPositionsSize - 1; partPosition++) {
                            viewPartPositions.set(partPosition, viewPartPositions.get(partPosition + 1));
                        }
                        viewPartPositions.set(partPositionsSize - 1, oldPartPosition);
                    }
                    continue;
                } else if (localeConstraint.myAnchor == Anchor.FIRST) {
                    if (viewPartPositions.get(labelOfPartsPos) != 0) {
                        oldPartPosition = viewPartPositions.get(labelOfPartsPos);
                        for (int partPosition = labelOfPartsPos; partPosition > 0; partPosition--) {
                            viewPartPositions.set(partPosition, viewPartPositions.get(partPosition - 1));
                        }
                        viewPartPositions.set(0, oldPartPosition);
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
                    oldPartPosition = viewPartPositions.get(labelOfPartsPos);
                    for (int partPosition = labelOfPartsPos; partPosition < lastPositionOfSorting; partPosition++) {
                        viewPartPositions.set(partPosition, viewPartPositions.get(partPosition + 1));
                    }
                    viewPartPositions.set(lastPositionOfSorting, oldPartPosition);
                }
                oldPartPosition = viewPartPositions.get(labelOfPartsPos);
                for (int partPosition = lastPositionOfSorting; partPosition > 0; partPosition--) {
                    if (parts.get(viewPartPositions.get(partPosition - 1)).getTitle().equals(localeConstraint.myRelativeToActionId)) {
                        if (localeConstraint.myAnchor == Anchor.BEFORE) {
                            viewPartPositions.set(partPosition, oldPartPosition);
                        } else {
                            if (partPosition > 1) {
                                viewPartPositions.set(partPosition, viewPartPositions.get(partPosition - 1));
                                viewPartPositions.set(partPosition - 1, oldPartPosition);
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
