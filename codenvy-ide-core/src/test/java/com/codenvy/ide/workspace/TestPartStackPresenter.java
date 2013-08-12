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

import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStackView;
import com.codenvy.ide.part.PartStackPresenter;
import com.codenvy.ide.part.PartStackUIResources;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link PartStackPresenter} functionality.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class TestPartStackPresenter {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    PartStackView partStackView;

    @Mock
    PartStackUIResources resources;

    @Mock
    EventBus eventBus;

    @Mock
    PartStackPresenter.PartStackEventHandler handler;

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
    public void shouldExposeUItoContainer() {
        // setup container mock and display.asWidget return object
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);
        // perform action
        stack.go(container);
        // verify view exposed to UI component
        verify(container).setWidget(eq(partStackView));
    }

    @Test
    public void shouldNotifyPartChanged() {
        PartPresenter part = mock(PartPresenter.class);
        when(part.getTitleImage()).thenReturn(null);

        stack.addPart(part);
        stack.setActivePart(part);

        verify(handler).onActivePartChanged(eq(part));
    }

    @Test
    public void shouldDelegateSetFocusToDisplay() {
        stack.setFocus(true);
        verify(partStackView).setFocus(eq(true));
    }

    @Test
    public void shouldAddPart() {
        PartPresenter part = mock(PartPresenter.class);
        stack.addPart(part);

        assertTrue("shoud contain part", stack.containsPart(part));
    }

    @Test
    public void shouldNotAddPartTwice() {
        PartPresenter part = mock(PartPresenter.class);
        stack.addPart(part);
        assertEquals("should contain 1 part", 1, stack.getNumberOfParts());

        stack.addPart(part);
        assertEquals("should contain 1 part", 1, stack.getNumberOfParts());
    }

    @Test
    public void shouldActivatePartOnAdd() {
        PartPresenter part = mock(PartPresenter.class);
        PartPresenter part2 = mock(PartPresenter.class);

        stack.addPart(part);
        stack.setActivePart(part);
        assertEquals("should activate part", part, stack.getActivePart());

        stack.addPart(part2);
        stack.setActivePart(part2);
        assertEquals("should activate part2", part2, stack.getActivePart());
    }

    @Test
    public void shouldSetActivatePart() {
        PartPresenter part = mock(PartPresenter.class);
        PartPresenter part2 = mock(PartPresenter.class);

        stack.addPart(part);
        stack.addPart(part2);
        stack.setActivePart(part);
        assertEquals("should activate part", part, stack.getActivePart());
    }

    @Test
    public void shouldNotifyActivatePart() {
        PartPresenter part = mock(PartPresenter.class);
        PartPresenter part2 = mock(PartPresenter.class);

        when(part.onClose()).thenReturn(true);
        when(part2.onClose()).thenReturn(true);

        reset(handler);
        stack.addPart(part);
        stack.setActivePart(part);
        verify(handler).onActivePartChanged(eq(part));

        reset(handler);
        // check another activated
        stack.addPart(part2);
        stack.setActivePart(part2);
        verify(handler).onActivePartChanged(eq(part2));

        reset(handler);
        // check first activated
        stack.setActivePart(part);
        verify(handler).onActivePartChanged(eq(part));
    }
}