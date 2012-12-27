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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.part.PartStackPresenter.PartStackEventHandler;
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
   PartStackView partStackView;

   @Mock
   PartStackUIResources resources;

   @Mock
   EventBus eventBus;

   @InjectMocks
   PartStackPresenter stack;

   @Mock(answer = Answers.RETURNS_DEEP_STUBS)
   PartPresenter part;

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
      AcceptsOneWidget container = mock(AcceptsOneWidget.class);
      // perform action
      stack.go(container);
      // verify view exposed to UI component
      verify(container).setWidget(eq(partStackView));
   }

   @Test
   public void shoudNotifyPartChanged()
   {
      PartStackPresenter.PartStackEventHandler handler = mock(PartStackPresenter.PartStackEventHandler.class);
      stack.setPartStackEventHandler(handler);
      when(part.getTitleImage()).thenReturn(null);

      stack.addPart(part);

      verify(handler).onActivePartChanged(eq(part));
   }

   @Test
   public void shoudDelegateSetFocusToDisplay()
   {
      stack.setFocus(true);
      verify(partStackView).setFocus(eq(true));
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

   @Test
   public void shoudNotifyActivatePart()
   {
      PartStackEventHandler handler = mock(PartStackEventHandler.class);

      PartPresenter part = mock(PartPresenter.class);
      PartPresenter part2 = mock(PartPresenter.class);
      
      when(part.onClose()).thenReturn(true);
      when(part2.onClose()).thenReturn(true);
      
      stack.setPartStackEventHandler(handler);
      
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
      
      reset(handler);
      // imitate close, second should activate
      stack.close(part);
      verify(handler).onActivePartChanged(eq(part2));
      
      reset(handler);
      // imitate close of the last one, active must be null
      stack.close(part2);
      verify(handler).onActivePartChanged((PartPresenter)isNull());
   }
}
