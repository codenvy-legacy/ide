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
package com.codenvy.ide.part;

import com.codenvy.ide.api.event.ActivePartChangedEvent;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStack;
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
