/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.wizard.newgenericproject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.model.Property;
import org.exoplatform.ide.wizard.WizardPagePresenter.WizardUpdateDelegate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * Testing {@link NewGenericProjectPagePresenter} functionality
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class TestNewGenericProjectPagePresenter
{
   private static final boolean CAN_FINISH = true;

   @Mock
   private NewGenericProjectPageView view;

   @Mock
   private ResourceProvider resourceProvider;

   private NewGenericProjectPagePresenter presenter;

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
    * If creates project with name what already exists then must be showed message about this situation.
    */
   @Test
   public void shouldBeSameProjectException()
   {
      String projectName = "Test";
      JsonArray<String> projects = JsonCollections.createArray();
      projects.add(projectName);
      
      createPresenter(projects);

      when(view.getProjectName()).thenReturn(projectName);

      presenter.checkProjectName();

      assertEquals(presenter.getNotice(), "Project with this name already exists.");
      assertEquals(presenter.isCompleted(), !CAN_FINISH);
   }

   /**
    * Creates presenter with given instance of view, resource provider and list of available projects.  
    * 
    * @param projects available projects
    */
   @SuppressWarnings("unchecked")
   private void createPresenter(final JsonArray<String> projects)
   {
      // create answer in response for calls get all available projects.
      doAnswer(new Answer<JsonArray<String>>()
      {
         public JsonArray<String> answer(InvocationOnMock invocation) throws Throwable
         {
            AsyncCallback<JsonArray<String>> callback = (AsyncCallback<JsonArray<String>>)invocation.getArguments()[0];
            // returns available projects
            callback.onSuccess(projects);
            return projects;
         }
      }).when(resourceProvider).listProjects((AsyncCallback<JsonArray<String>>)any());

      presenter = new NewGenericProjectPagePresenter(mock(ImageResource.class), view, resourceProvider);
      presenter.setUpdateDelegate(mock(WizardUpdateDelegate.class));
   }

   /**
    * If project's name is empty then must be showed message about this situation.
    */
   @Test
   public void shouldBeEnterProjectNameMessage()
   {
      String projectName = "";

      // available projects are absent
      createPresenter(JsonCollections.<String> createArray());

      when(view.getProjectName()).thenReturn(projectName);

      presenter.checkProjectName();

      assertEquals(presenter.getNotice(), "Please, enter a project name.");
      assertEquals(presenter.isCompleted(), !CAN_FINISH);
   }

   /**
    * If project's name has incorrect symbol then must be showed message about this situation.
    */
   @Test
   public void shouldBeIncorrectNameMessage()
   {
      String projectName = "Test/";

      // available projects are absent
      createPresenter(JsonCollections.<String> createArray());

      when(view.getProjectName()).thenReturn(projectName);

      presenter.checkProjectName();

      assertEquals(presenter.getNotice(), "Incorrect project name.");
      assertEquals(presenter.isCompleted(), !CAN_FINISH);
   }

   /**
    * If project's name is correct then must be not showing any message.
    */
   @Test
   public void shouldBeCorrectProjectName()
   {
      String projectName = "Test";

      // available projects are absent
      createPresenter(JsonCollections.<String> createArray());

      when(view.getProjectName()).thenReturn(projectName);

      presenter.checkProjectName();

      assertEquals(presenter.getNotice(), null);
      assertEquals(presenter.isCompleted(), CAN_FINISH);
   }

   /**
    * If the presenter don't receive list of projects then must be showed message about this situation.
    */
   @Test
   public void shouldBeWaitProjectListMessage()
   {
      String projectName = "Test";

      // create presenter
      presenter = new NewGenericProjectPagePresenter(mock(ImageResource.class), view, resourceProvider);

      when(view.getProjectName()).thenReturn(projectName);

      assertEquals(presenter.getNotice(), "Please wait, checking project list");
      assertEquals(presenter.isCompleted(), !CAN_FINISH);
   }

   /**
    * If calls doFinish method then must be call createProject method.
    */
   @Test
   @SuppressWarnings("unchecked")
   public void shouldBeCallCreateProject()
   {
      // create presenter
      presenter = new NewGenericProjectPagePresenter(mock(ImageResource.class), view, resourceProvider);

      presenter.doFinish();

      verify(resourceProvider).createProject(anyString(), (JsonArray<Property>)any(), (AsyncCallback<Project>)any());
   }
}