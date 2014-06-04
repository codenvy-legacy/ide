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

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.event.EditorDirtyStateChangedEvent;
import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStack;
import com.codenvy.ide.api.ui.workspace.PartStackView;
import com.codenvy.ide.api.ui.workspace.PartStackView.TabItem;
import com.codenvy.ide.api.ui.workspace.PropertyListener;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.workspace.WorkBenchPartController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Implements "Tab-like" UI Component, that accepts PartPresenters as child elements.
 * It's designed to remove child from DOM, when it is hidden. So keeping DOM as small
 * as possible.
 * <p/>
 * PartStack support "focus" (please don't mix with GWT Widget's Focus feature).
 * Focused PartStack will highlight active Part, notifying user what component is
 * currently active.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class PartStackPresenter implements Presenter, PartStackView.ActionDelegate, PartStack {

    private static final int                  DEFAULT_SIZE = 200;
    /** list of parts */
    protected final      Array<PartPresenter> parts        = Collections.createArray();
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
                eventBus.fireEvent(new EditorDirtyStateChangedEvent((EditorPartPresenter)source));
            }
        }
    };
    /** current active part */
    protected PartPresenter           activePart;
    protected PartStackEventHandler   partStackHandler;
    private   WorkBenchPartController workBenchPartController;

    private Array<Double> partsSize = Collections.createArray();

    /** Creates PartStack with given instance of display and resources (CSS and Images) */
    @Inject
    public PartStackPresenter(EventBus eventBus,
                              PartStackEventHandler partStackEventHandler,
                              @Assisted PartStackView view,
                              @Assisted WorkBenchPartController workBenchPartController) {
        this.view = view;
        this.eventBus = eventBus;
        partStackHandler = partStackEventHandler;
        this.workBenchPartController = workBenchPartController;
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
        view.updateTabItem(index, part.getTitleImage(), part.getTitle(), part.getTitleToolTip(), part.getTitleWidget());
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        if (activePart != null) {
            activePart.go(view.getContentPanel());
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

        double partSize = part.getSize() <= 0 ? DEFAULT_SIZE : part.getSize();
        partsSize.add(partSize);
        if (workBenchPartController != null)
            workBenchPartController.setSize(partSize);
        part.addPropertyListener(propertyListener);
        // include close button
        ImageResource titleImage = part.getTitleImage();
        TabItem tabItem =
                view.addTabButton(titleImage == null ? null : new Image(titleImage), part.getTitle(), part.getTitleToolTip(), part.getTitleWidget(),
                                  partsClosable);
        bindEvents(tabItem, part);
//        setActivePart(part);
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
//            partsSize.set(parts.indexOf(part), workBenchPartController.getSize());
//            workBenchPartController.setHidden(true);
//            activePart = null;
//            view.setActiveTabButton(-1);
            return;
        }

        if (activePart != null && workBenchPartController != null) {
            partsSize.set(parts.indexOf(activePart), workBenchPartController.getSize());
        }
        activePart = part;
        AcceptsOneWidget contentPanel = view.getContentPanel();

        if (part == null) {
            view.setActiveTabButton(-1);
            workBenchPartController.setHidden(true);
        } else {
            view.setActiveTabButton(parts.indexOf(activePart));
            activePart.go(contentPanel);
        }
        // request part stack to get the focus
        onRequestFocus();
        // notify handler, that part changed
        partStackHandler.onActivePartChanged(activePart);

        if (activePart != null && workBenchPartController != null) {
            workBenchPartController.setHidden(false);
            workBenchPartController.setSize(partsSize.get(parts.indexOf(activePart)));
            activePart.onOpen();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void hidePart(PartPresenter part) {
        if (activePart == part) {
            setActivePart(null);
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
            view.removeTabButton(partIndex);
            parts.remove(part);
            view.getContentPanel().setWidget(null);
            partsSize.remove(partIndex);
            part.removePropertyListener(propertyListener);
            if (activePart == part) {
                //select another part
//                setActivePart(parts.isEmpty() ? null : parts.get(0));
                setActivePart(null);
            }
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
                    partsSize.set(parts.indexOf(part), workBenchPartController.getSize());
                    workBenchPartController.setHidden(true);
                    activePart = null;
                    view.setActiveTabButton(-1);

                    //workBenchPartController.setSize(partsSize.get(parts.indexOf(activePart)));
                    return;
                }

                // make active
                setActivePart(part);
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
}
