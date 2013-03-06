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
package com.codenvy.ide.client.projectExplorer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import com.codenvy.ide.part.projectexplorer.ProjectExplorerPartPresenter;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerView;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerView.ActionDelegate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

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
   ProjectExplorerView projectExplorerView;

   @Spy
   EventBus eventBus = new SimpleEventBus();

   @InjectMocks
   ProjectExplorerPartPresenter explorerPresenter;

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

      verify(projectExplorerView).setDelegate((ActionDelegate)any());
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
