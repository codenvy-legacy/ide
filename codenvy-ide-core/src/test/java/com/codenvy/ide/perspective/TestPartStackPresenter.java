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
package com.codenvy.ide.perspective;

import com.codenvy.ide.api.ui.perspective.PartPresenter;
import com.codenvy.ide.part.PartStackPresenter;
import com.codenvy.ide.part.PartStackUIResources;
import com.codenvy.ide.part.PartStackView;
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
import static org.mockito.Mockito.*;

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
    public void shoudNotifyPartChanged() {
        PartPresenter part = mock(PartPresenter.class);
        when(part.getTitleImage()).thenReturn(null);

        stack.addPart(part);

        verify(handler).onActivePartChanged(eq(part));
    }

    @Test
    public void shoudDelegateSetFocusToDisplay() {
        stack.setFocus(true);
        verify(partStackView).setFocus(eq(true));
    }

    @Test
    public void shoudAddPart() {
        PartPresenter part = mock(PartPresenter.class);
        stack.addPart(part);

        assertTrue("shoud contain part", stack.containsPart(part));
    }

    @Test
    public void shoudNotAddPartTwice() {
        PartPresenter part = mock(PartPresenter.class);
        stack.addPart(part);
        assertEquals("shoud contain 1 part", 1, stack.getNumberOfParts());

        stack.addPart(part);
        assertEquals("shoud contain 1 part", 1, stack.getNumberOfParts());
    }

    @Test
    public void shoudActivatePartOnAdd() {
        PartPresenter part = mock(PartPresenter.class);
        PartPresenter part2 = mock(PartPresenter.class);

        stack.addPart(part);
        assertEquals("shoud activate part", part, stack.getActivePart());

        stack.addPart(part2);
        assertEquals("shoud activate part2", part2, stack.getActivePart());
    }

    @Test
    public void shoudSetActivatePart() {
        PartPresenter part = mock(PartPresenter.class);
        PartPresenter part2 = mock(PartPresenter.class);

        stack.addPart(part);
        stack.addPart(part2);
        stack.setActivePart(part);
        assertEquals("shoud activate part", part, stack.getActivePart());
    }

    @Test
    public void shoudNotifyActivatePart() {
        PartPresenter part = mock(PartPresenter.class);
        PartPresenter part2 = mock(PartPresenter.class);

        when(part.onClose()).thenReturn(true);
        when(part2.onClose()).thenReturn(true);

        reset(handler);
        stack.addPart(part);
        verify(handler).onActivePartChanged(eq(part));

        reset(handler);
        // check another activated
        stack.addPart(part2);
        verify(handler).onActivePartChanged(eq(part2));

        reset(handler);
        // check first activated
        stack.setActivePart(part);
        verify(handler).onActivePartChanged(eq(part));
    }
}