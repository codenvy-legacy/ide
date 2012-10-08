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
package org.exoplatform.ide.part;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.part.PartStackPresenter.FocusRequstHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * 
 * Testing {@link PartStackPresenter} functionality
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@RunWith(MockitoJUnitRunner.class)
public class TestPartStackPresenter
{
   @Mock(answer = Answers.RETURNS_DEEP_STUBS)
   PartStackPresenter.Display display;

   @Mock
   PartStackResources resources;

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
   public void shouldExposeUItoContainer()
   {
      // setup container mock and display.asWidget return object
      HasWidgets container = mock(HasWidgets.class);
      Widget displayAsWidget = mock(Widget.class);
      when(display.asWidget()).thenReturn(displayAsWidget);
      // perform action
      stack.go(container);
      // verify view exposed to UI component
      verify(container).add(eq(displayAsWidget));
   }

   @Test
   public void shoudDelegateFocusHandlerToDisplay()
   {
      FocusRequstHandler handler = mock(PartStackPresenter.FocusRequstHandler.class);
      stack.setFocusRequstHandler(handler);
      verify(display).setFocusRequstHandler(eq(handler));
   }

   @Test
   public void shoudDelegateSetFocusToDisplay()
   {
      stack.setFocus(true);
      verify(display).setFocus(eq(true));
   }

   @Test
   public void shoudAddPart()
   {
      PartPresenter part = mock(PartPresenter.class);
      stack.addPart(part);

      assertTrue("shoud contain part", stack.containsPart(part));
   }

   @Test
   public void shoudNotAddPartTwice()
   {
      PartPresenter part = mock(PartPresenter.class);
      stack.addPart(part);
      assertEquals("shoud contain 1 part", 1, stack.getNumberOfParts());

      stack.addPart(part);
      assertEquals("shoud contain 1 part", 1, stack.getNumberOfParts());
   }

   @Test
   public void shoudActivatePartOnAdd()
   {
      PartPresenter part = mock(PartPresenter.class);
      PartPresenter part2 = mock(PartPresenter.class);

      stack.addPart(part);

      assertEquals("shoud activate part", part, stack.getActivePart());

      stack.addPart(part2);
      assertEquals("shoud activate part2", part2, stack.getActivePart());
   }

   @Test
   public void shoudSetActivatePart()
   {
      PartPresenter part = mock(PartPresenter.class);
      PartPresenter part2 = mock(PartPresenter.class);

      stack.addPart(part);
      stack.addPart(part2);
      stack.setActivePart(part);
      assertEquals("shoud activate part", part, stack.getActivePart());
   }
}
