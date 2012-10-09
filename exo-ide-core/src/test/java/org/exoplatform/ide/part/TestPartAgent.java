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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import com.google.web.bindery.event.shared.EventBus;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.inject.Provider;

import org.exoplatform.ide.api.ui.part.PartAgent.PartStackType;
import org.exoplatform.ide.core.event.ActivePartChangedEvent;
import org.exoplatform.ide.part.PartStackPresenter.PartStackEventHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * 
 * Testing {@link PartStackPresenter} functionality
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@RunWith(MockitoJUnitRunner.class)
public class TestPartAgent
{
   /**
    * 
    */
   private static final PartStackType SECOND_TYPE = PartStackType.TOOLING;

   /**
    * 
    */
   private static final PartStackType TYPE = PartStackType.EDITING;

   @Mock
   Provider<PartStackPresenter> provider;

   @Mock
   EventBus eventBus;

   @Mock
   PartStackResources resources;

   PartAgentPresenter agent;

   private PartStackPresenter editStack;

   @Before
   public void disarm()
   {
      // don't throw an exception if GWT.create() invoked
      GWTMockUtilities.disarm();
      when(provider.get()).thenReturn(mock(PartStackPresenter.class), mock(PartStackPresenter.class),
         mock(PartStackPresenter.class), mock(PartStackPresenter.class));
      agent = new PartAgentPresenter(provider, eventBus);
      editStack = agent.getPartStack(TYPE);
   }

   @After
   public void restore()
   {
      GWTMockUtilities.restore();
   }

   @Test
   public void shouldAddToStack()
   {
      PartPresenter part = mock(PartPresenter.class);
      agent.addPart(part, TYPE);
      // verify part added to proper stack
      verify(editStack).addPart(eq(part));
   }

   @Test
   public void shouldCallSetFocus()
   {
      agent.setActivePartStack(editStack);
      // verify focus granted
      verify(editStack).setFocus(eq(true));

      // check that second setActivePartStack has no effect
      reset(editStack);
      agent.setActivePartStack(editStack);
      verifyNoMoreInteractions(editStack);
   }

   @Test
   public void shouldDropFocusFromPrevStack()
   {
      agent.setActivePartStack(editStack);
      reset(editStack);
      PartStackPresenter toolStack = agent.getPartStack(SECOND_TYPE);

      agent.setActivePartStack(toolStack);
      verify(editStack).setFocus(eq(false));
      verify(toolStack).setFocus(eq(true));
   }

   @Test
   public void shouldSetActivePartonStackAndSetFocus()
   {
      PartPresenter part = mock(PartPresenter.class);
      agent.addPart(part, TYPE);
      // move focus else where
      agent.setActivePartStack(agent.getPartStack(SECOND_TYPE));
      reset(editStack);
      reset(part);
      when(editStack.containsPart(eq(part))).thenReturn(true);

      // try to make part active
      agent.setActivePart(part);
      verify(editStack).setActivePart(eq(part));
      verify(editStack).setFocus(eq(true));
   }

   @Test
   public void shoudFireEventonChangePart()
   {
      final PartPresenter part = mock(PartPresenter.class);
      PartStackPresenter partStack = mock(PartStackPresenter.class);

      when(provider.get()).thenReturn(partStack);

      // immediately call onActivePartChanged when handler is set
      doAnswer(new Answer<Object>()
      {
         int count = 0;

         @Override
         public Object answer(InvocationOnMock invocation) throws Throwable
         {
            // notify part changed only once
            if (count == 0)
            {
               PartStackEventHandler handler = (PartStackEventHandler)invocation.getArguments()[0];
               handler.onActivePartChanged(part);
            }
            count++;
            return null;
         }
      }).when(partStack).setPartStackEventHandler((PartStackEventHandler)any());

      // create Part Agent
      agent = new PartAgentPresenter(provider, eventBus);

      // verify Event fired
      verify(eventBus).fireEvent(any(ActivePartChangedEvent.class));
   }

}
