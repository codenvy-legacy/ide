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
package com.codenvy.ide.java.client.wizard;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenvy.ide.java.client.JavaClientBundle;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.wizard.newproject.CreateProjectHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 
 */
public class NewJavaProjectPagePresenterTest extends WizardsBaseTest
{

   @Mock
   private NewJavaProjectPageView view;

   @Mock
   private CreateProjectHandler createProjecthandler;

   private NewJavaProjectPagePresenter presenter;

   @Before
   @SuppressWarnings({"unchecked", "rawtypes"})
   public void setUp() throws Exception
   {
      ArgumentCaptor<AsyncCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(AsyncCallback.class);
      presenter = new NewJavaProjectPagePresenter(JavaClientBundle.INSTANCE, view, resourceProvider);
      presenter.setUpdateDelegate(updateDelegate);
      verify(resourceProvider).listProjects(callbackArgumentCaptor.capture());
      callbackArgumentCaptor.getValue().onSuccess(JsonCollections.createArray());
      when(view.getProjectName()).thenReturn("project");
      when(view.getSourceFolder()).thenReturn("src");
   }

   @Test
   public void testIsCompleted() throws Exception
   {
      presenter.checkProjectInput();
      assertThat(presenter.isCompleted()).isTrue();
   }

   @Test
   public void testGetNotice() throws Exception
   {
      when(view.getProjectName()).thenReturn("%*^%^%$^%");
      presenter.checkProjectInput();
      assertThat(presenter.getNotice()).contains("Incorrect project name.");
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testDoFinish() throws Exception
   {
      presenter.checkProjectInput();
      presenter.doFinish();

      verify(createProjecthandler).setProjectName(eq("project"));
      verify(createProjecthandler).create((AsyncCallback<Project>)any());
   }
}
