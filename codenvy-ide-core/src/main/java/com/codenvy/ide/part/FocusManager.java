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

import com.codenvy.ide.api.event.ActivePartChangedEvent;
import com.codenvy.ide.api.parts.PartPresenter;
import com.codenvy.ide.api.parts.PartStack;
import com.codenvy.ide.part.PartStackPresenter.PartStackEventHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;


/**
 * FocusManager is responsible for granting a focus for a stack when requested.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class FocusManager {
    private PartStack activePartStack;

    private final PartStackEventHandler partStackHandler = new PartFocusChangedHandler();

    private final EventBus eventBus;

    /** Handles PartStack Events */
    private final class PartFocusChangedHandler implements PartStackEventHandler {
        /** {@inheritDoc} */
        @Override
        public void onActivePartChanged(PartPresenter part) {
            activePartChanged(part);
        }

        /** {@inheritDoc} */
        @Override
        public void onRequestFocus(PartStack partStack) {
            setActivePartStack(partStack);
        }
    }

    /**
     * Provides a handler, that is injected into PartStack, for the FocusManager to be able to track
     * PartStack focus requests and changes of the active Part.
     *
     * @return instance of PartStackEventHandler
     */
    public PartStackEventHandler getPartStackHandler() {
        return partStackHandler;
    }

    /** Instantiates PartAgent with provided factory and event bus */
    @Inject
    public FocusManager(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Fires EventBus event, that informs about new active Part
     *
     * @param part
     *         instance that became active
     */
    protected void activePartChanged(PartPresenter part) {
        // fire event, active part changed
        eventBus.fireEvent(new ActivePartChangedEvent(part));
    }

    /**
     * Activate given Part Stack
     *
     * @param partStack
     */
    protected void setActivePartStack(PartStack partStack) {
        // nothing to do
        if (activePartStack == partStack || partStack == null) {
            return;
        }
        // drop focus from partStacks
        if (activePartStack != null) {
            activePartStack.setFocus(false);
        }
        // set part focused
        activePartStack = partStack;
        activePartStack.setFocus(true);

        activePartChanged(activePartStack.getActivePart());
    }

}
