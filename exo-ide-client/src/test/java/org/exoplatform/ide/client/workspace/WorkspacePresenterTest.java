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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import org.exoplatform.ide.core.editor.EditorAgent;
import org.exoplatform.ide.core.expressions.ExpressionManager;
import org.exoplatform.ide.menu.MainMenuPresenter;
import org.exoplatform.ide.part.projectexplorer.ProjectExplorerPartPresenter;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 2, 2012  
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkspacePresenterTest
{
   @Mock
   WorkspaceView workspaceView;

   @Mock
   ProjectExplorerPartPresenter projectExpolorerPresenter;
   
   @Mock
   MainMenuPresenter mainMenuPresenter;
   
   @Mock
   ExpressionManager expressionManager;

   @Mock
   EditorAgent editorAgent;

   @Spy
   EventBus eventBus = new SimpleEventBus();

   @InjectMocks
   WorkspacePresenter wsPresenter;

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

   @Ignore
   @Test
   public void shouldBindEventHandler()
   {
      // verify add handler was called during class instantiation
      verify(wsPresenter.eventBus).addHandler((Type)any(), (EventHandler)any());
   }

   @Ignore
   @Test
   public void shouldExposeUItoContainer()
   {
      // setup container mock and display.asWidget return object
      AcceptsOneWidget container = mock(AcceptsOneWidget.class);
      Widget wsDisplayAsWidget = mock(Widget.class);
      when(wsPresenter.view.asWidget()).thenReturn(wsDisplayAsWidget);
      // perform action
      wsPresenter.go(container);
      // verify view exposed to UI component
      verify(container).setWidget(eq(wsDisplayAsWidget));
   }

   @Ignore
   @Test
   public void shouldExposeProjectExplorerOnGo()
   {
//      AcceptsOneWidget leftWorkspacePanel = mock(AcceptsOneWidget.class);
//      when(wsPresenter.view.getLeftPanel()).thenReturn(leftWorkspacePanel);
//      wsPresenter.go(mock(AcceptsOneWidget.class));
//      // verify ProjectView opened
//      verify(wsPresenter.projectExplorerPresenter).go(eq(leftWorkspacePanel));
   }

   @Ignore
   @Test
   public void shouldOpenEditorOnEvent()
   {
//      // setup
//      HasWidgets centerWorkspacePanel = mock(HasWidgets.class);
//      when(wsPresenter.display.getCenterPanel()).thenReturn(centerWorkspacePanel);
//      
//      String fileName = "fileName";
//      final String content = "dummyContent";
//      doAnswer(new Answer<Object>()
//      {
//
//         public Object answer(InvocationOnMock invocation) throws Throwable
//         {
//            ((AsyncCallback)invocation.getArguments()[1]).onSuccess(content);
//            return null;
//         }
//      }).when(fileSystemService).getFileContent(eq(fileName), (AsyncCallback<String>)any());
//
//      // fire OpenFile event
//      eventBus.fireEvent(new FileEvent(fileName, FileOperation.OPEN));
//
//      // verify editor got the content
//      verify(wsPresenter.editorPresenter).setText(eq(content));
//      // verify editor opened in correct place
//      verify(wsPresenter.editorPresenter).go(eq(centerWorkspacePanel));
   }

}
