/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
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
package com.codenvy.ide.selection;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import com.codenvy.ide.core.event.ActivePartChangedEvent;
import com.codenvy.ide.core.event.SelectionChangedEvent;
import com.codenvy.ide.core.event.SelectionChangedHandler;
import com.codenvy.ide.part.AbstractPartPresenter;
import com.codenvy.ide.part.PartPresenter;
import com.codenvy.ide.selection.Selection;
import com.codenvy.ide.selection.SelectionAgentImpl;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test covers {@link SelectionAgentImpl} functionality.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@RunWith(MockitoJUnitRunner.class)
public class TestSelectionAgent
{

   private EventBus eventBus = new SimpleEventBus();

   private SelectionAgentImpl agent = new SelectionAgentImpl(eventBus);

   @Mock
   private PartPresenter part;

   @Mock
   private Selection selection;

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

   /**
    * Check proper Selection returned when part changed
    */
   @Test
   public void shouldChangeSelectionAfterPartGetsActivated()
   {
      when(part.getSelection()).thenReturn(selection);

      // fire event, for agent to get information about active part
      eventBus.fireEvent(new ActivePartChangedEvent(part));

      assertEquals("Agent should return proper Selection", selection, agent.getSelection());
   }

   /**
    * Event should be fired, when active part changed
    */
   @Test
   public void shouldFireEventWhenPartChanged()
   {
      when(part.getSelection()).thenReturn(selection);
      SelectionChangedHandler handler = mock(SelectionChangedHandler.class);
      eventBus.addHandler(SelectionChangedEvent.TYPE, handler);

      // fire event, for agent to get information about active part
      eventBus.fireEvent(new ActivePartChangedEvent(part));

      verify(handler).onSelectionChanged((SelectionChangedEvent)any());
   }

   /**
    * If selection chang in active part, Selection Agent should fire
    * an Event
    */
   @Test
   public void shouldFireEventWhenSelectionInActivePartChanged()
   {

      AbstractPartPresenter part = new AbstractPartPresenter()
      {
         @Override
         public void go(AcceptsOneWidget container)
         {
         }

         @Override
         public String getTitleToolTip()
         {
            return null;
         }

         @Override
         public ImageResource getTitleImage()
         {
            return null;
         }

         @Override
         public String getTitle()
         {
            return null;
         }
      };

      // fire event, for agent to get information about active part
      eventBus.fireEvent(new ActivePartChangedEvent(part));
      SelectionChangedHandler handler = mock(SelectionChangedHandler.class);
      eventBus.addHandler(SelectionChangedEvent.TYPE, handler);

      part.setSelection(mock(Selection.class));

      verify(handler).onSelectionChanged((SelectionChangedEvent)any());
   }

   /**
    * If selection chang in non-active part, no events should be fired by 
    * Selection Agent
    */
   @Test
   public void shouldNOTFireEventWhenSelectionInNONActivePartChanged()
   {

      AbstractPartPresenter firstPart = new AbstractPartPresenter()
      {
         @Override
         public void go(AcceptsOneWidget container)
         {
         }

         @Override
         public String getTitleToolTip()
         {
            return null;
         }

         @Override
         public ImageResource getTitleImage()
         {
            return null;
         }

         @Override
         public String getTitle()
         {
            return null;
         }
      };

      // fire event, for agent to get information about active part
      eventBus.fireEvent(new ActivePartChangedEvent(firstPart));
      // change part
      eventBus.fireEvent(new ActivePartChangedEvent(mock(PartPresenter.class)));

      SelectionChangedHandler handler = mock(SelectionChangedHandler.class);
      eventBus.addHandler(SelectionChangedEvent.TYPE, handler);

      // call setSelection on the first Part.
      firstPart.setSelection(mock(Selection.class));

      verifyZeroInteractions(handler);
   }

}
