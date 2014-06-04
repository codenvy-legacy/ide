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
import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStack;
import com.codenvy.ide.api.ui.workspace.PartStackView;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

/**
 * Testing {@link FocusManager} functionality.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class TestFocusManager {
    @Mock
    EventBus eventBus;

    @Mock
    PartStackUIResources resources;

    @InjectMocks
    FocusManager agent;

    @Mock
    PartStackPresenter.PartStackEventHandler handler;

    @Mock
    PartStackView view;

    @InjectMocks
    PartStackPresenter stack;

    @Before
    public void disarm() {
        // don't throw an exception if GWT.create() invoked
        GWTMockUtilities.disarm();
    }

    @After
    public void restore() {
        GWTMockUtilities.restore();
    }

    @Test
    public void shouldDropFocusFromPrevStack() {
        PartStackPresenter partStack = mock(PartStackPresenter.class);
        PartStackPresenter partStack2 = mock(PartStackPresenter.class);

        agent.setActivePartStack(partStack);
        reset(partStack);

        agent.setActivePartStack(partStack2);
        verify(partStack).setFocus(eq(false));
        verify(partStack2).setFocus(eq(true));
    }

    @Test
    public void shouldSetActivePartonStackAndSetFocus() {
        PartStackPresenter partStack = mock(PartStackPresenter.class);
        agent.setActivePartStack(partStack);

        verify(partStack).setFocus(eq(true));
    }

    @Test
    public void shoudFireEventOnChangePart() {
        PartPresenter part = mock(PartPresenter.class);
        // create Part Agent
        agent = new FocusManager(eventBus);
        agent.activePartChanged(part);

        // verify Event fired
        verify(eventBus).fireEvent(any(ActivePartChangedEvent.class));
    }

    @Test
    public void shoudFireEventOnChangePartStack() {
        // create Part Agent
        agent = new FocusManager(eventBus);

        PartStack partStack = mock(PartStackPresenter.class);
        agent.setActivePartStack(partStack); // focus requested

        // verify New Event generated
        verify(eventBus).fireEvent(any(ActivePartChangedEvent.class));
    }
}