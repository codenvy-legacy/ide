/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
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
     * PartStac focus requests and changes of the active Part.
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
