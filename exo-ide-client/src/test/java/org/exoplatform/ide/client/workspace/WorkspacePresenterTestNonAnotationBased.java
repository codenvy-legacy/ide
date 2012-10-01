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
package org.exoplatform.ide.client.workspace;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 2, 2012  
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkspacePresenterTestNonAnotationBased
{

   WorkspacePeresenter wsPresenter;

   @Before
   public void disarm()
   {
      // don't throw an exception if GWT.create() invoked
      GWTMockUtilities.disarm();
      // workspace presenter will be an object that calls real methods, not the mocked ones
      wsPresenter = mock(WorkspacePeresenter.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
      // initialize service with a mock
      wsPresenter.eventBus = mock(EventBus.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
      // initialize display mock
      wsPresenter.display = mock(WorkspacePeresenter.Display.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));

   }

   @After
   public void restore()
   {
      // 
      GWTMockUtilities.restore();
   }

   @Test
   public void shouldBindEventHandler()
   {
      wsPresenter.bind();
      verify(wsPresenter.eventBus).addHandler((Type)any(), (EventHandler)any());
   }

   @Test
   public void shouldOpenEditor()
   {

   }

}
