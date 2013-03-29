/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.part;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import com.codenvy.ide.api.event.ActivePartChangedEvent;
import com.codenvy.ide.api.ui.perspective.PartPresenter;
import com.codenvy.ide.api.ui.perspective.PartStack;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Testing {@link FocusManager} functionality.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class TestFocusManager
{
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
   public void disarm()
   {
      // don't throw an exception if GWT.create() invoked
      GWTMockUtilities.disarm();
   }

   @After
   public void restore()
   {
      GWTMockUtilities.restore();
   }

   @Test
   public void shouldDropFocusFromPrevStack()
   {
      PartStackPresenter partStack = mock(PartStackPresenter.class);
      PartStackPresenter partStack2 = mock(PartStackPresenter.class);

      agent.setActivePartStack(partStack);
      reset(partStack);

      agent.setActivePartStack(partStack2);
      verify(partStack).setFocus(eq(false));
      verify(partStack2).setFocus(eq(true));
   }

   @Test
   public void shouldSetActivePartonStackAndSetFocus()
   {
      PartStackPresenter partStack = mock(PartStackPresenter.class);
      agent.setActivePartStack(partStack);

      verify(partStack).setFocus(eq(true));
   }

   @Test
   public void shoudFireEventOnChangePart()
   {
      PartPresenter part = mock(PartPresenter.class);
      // create Part Agent
      agent = new FocusManager(eventBus);
      agent.activePartChanged(part);

      // verify Event fired
      verify(eventBus).fireEvent(any(ActivePartChangedEvent.class));
   }

   @Test
   public void shoudFireEventOnChangePartStack()
   {
      // create Part Agent
      agent = new FocusManager(eventBus);

      PartStack partStack = mock(PartStackPresenter.class);
      agent.setActivePartStack(partStack); // focus requested

      // verify New Event generated
      verify(eventBus).fireEvent(any(ActivePartChangedEvent.class));
   }
}