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
package org.exoplatform.ide.client.projectExplorer;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import org.exoplatform.ide.client.event.FileEvent;
import org.exoplatform.ide.client.projectExplorer.ProjectExplorerPresenter.Listener;
import org.exoplatform.ide.client.services.FileSystemServiceAsync;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 3, 2012  
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectExpolorerPresenterTest
{

   @Mock(answer = Answers.RETURNS_DEEP_STUBS)
   ProjectExplorerPresenter.Display display;

   @Spy
   EventBus eventBus = new SimpleEventBus();

   @Mock
   FileSystemServiceAsync fileSystemService;

   @InjectMocks
   ProjectExplorerPresenter explorerPresenter;

   @Before
   public void disarm()
   {
      // don't throw an exception if GWT.create() invoked
      GWTMockUtilities.disarm();
   }

   @After
   public void restore()
   {
      // 
      GWTMockUtilities.restore();
   }

   /**
    * Check that Presenter binds a double click handler
    */
   @Test
   public void shouldBindDoubleClickHandler()
   {

      verify(display).registerListener((Listener)any());
   }

   /**
    * Testing that doubleClick generates OpenFileEvent and fire it on EventBus
    */
   @Test
   public void shoudFireOpenFileOnDoubleClick()
   {
      // TODO
   }

   @Test
   public void shouldRetrieveFileListOnGo()
   {
      // TODO
   }
}
