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
package com.codenvy.ide.wizard.newgenericproject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter.WizardUpdateDelegate;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.wizard.newproject.CreateProjectHandler;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.rpc.AsyncCallback;

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

   @Mock
   private NewGenericProjectWizardResource resources;

   @Mock
   private CreateProjectHandler createProjecthandler;

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
         @Override
         public JsonArray<String> answer(InvocationOnMock invocation) throws Throwable
         {
            AsyncCallback<JsonArray<String>> callback = (AsyncCallback<JsonArray<String>>)invocation.getArguments()[0];
            // returns available projects
            callback.onSuccess(projects);
            return projects;
         }
      }).when(resourceProvider).listProjects((AsyncCallback<JsonArray<String>>)any());

      presenter = new NewGenericProjectPagePresenter(resources, view, resourceProvider);
      presenter.setUpdateDelegate(mock(WizardUpdateDelegate.class));
      presenter.setCreateProjectHandler(createProjecthandler);
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
      presenter = new NewGenericProjectPagePresenter(resources, view, resourceProvider);
      presenter.setCreateProjectHandler(createProjecthandler);

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
      presenter = new NewGenericProjectPagePresenter(resources, view, resourceProvider);
      presenter.setCreateProjectHandler(createProjecthandler);

      presenter.doFinish();

      verify(createProjecthandler).setProjectName(anyString());
      verify(createProjecthandler).create((AsyncCallback<Project>)any());
   }
}