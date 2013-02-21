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
package org.exoplatform.ide.java.client.wizard;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.ide.java.client.JavaClientBundle;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.model.Property;
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
      verify(resourceProvider).createProject(eq("project"), (JsonArray<Property>)any(), (AsyncCallback<Project>)any());
   }
}
