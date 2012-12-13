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
package org.exoplatform.ide.wizard.newfolder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.resources.model.Folder;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.model.Resource;
import org.exoplatform.ide.wizard.WizardPagePresenter.WizardUpdateDelegate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Testing {@link NewFolderPagePresenter} functionality.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class TestNewFolderPagePresenter
{
   private static final boolean IS_COMPLITED = true;

   private static final boolean IS_FOLDER = true;

   private NewFolderPagePresenter presenter;

   private Project project;

   private NewFolderPageView view;

   @Before
   public void disarm()
   {
      // don't throw an exception if GWT.create() invoked
      GWTMockUtilities.disarm();

      setUp();
   }

   /**
    * Create general components for all test.
    */
   private void setUp()
   {
      ResourceProvider resourceProvider = mock(ResourceProvider.class);
      project = mock(Project.class);
      when(resourceProvider.getActiveProject()).thenReturn(project);

      view = mock(NewFolderPageView.class);

      presenter = new NewFolderPagePresenter("Caption", null, view, resourceProvider);
      presenter.setUpdateDelegate(mock(WizardUpdateDelegate.class));
   }

   @After
   public void restore()
   {
      GWTMockUtilities.restore();
   }

   /**
    * If folder name is empty then must be showed message about this situation.
    */
   @Test
   public void shouldBeEnterFolderNameMessage()
   {
      when(view.getFolderName()).thenReturn("");
      when(project.getChildren()).thenReturn(JsonCollections.<Resource> createArray());

      presenter.checkEnteredInformation();

      assertEquals(presenter.getNotice(), "The folder name can't be empty.");
      assertEquals(presenter.isCompleted(), !IS_COMPLITED);
   }

   /**
    * If folder name has incorrect symbol then must be showed message about this situation.
    */
   @Test
   public void shouldBeInvalidNameMessage()
   {
      when(view.getFolderName()).thenReturn("test*");
      when(project.getChildren()).thenReturn(JsonCollections.<Resource> createArray());

      presenter.checkEnteredInformation();

      assertEquals(presenter.getNotice(), "The folder name has incorrect symbol.");
      assertEquals(presenter.isCompleted(), !IS_COMPLITED);
   }

   /**
    * If folder name has incorrect extension then must be showed message about this situation.
    */
   @Test
   public void shouldBeFolderExistMessage()
   {
      String folderName = "test";

      Resource folder = mock(Folder.class);
      when(folder.getName()).thenReturn(folderName);
      when(folder.isFolder()).thenReturn(IS_FOLDER);

      JsonArray<Resource> children = JsonCollections.createArray();
      children.add(folder);

      when(view.getFolderName()).thenReturn(folderName);
      when(project.getChildren()).thenReturn(children);

      presenter.checkEnteredInformation();

      assertEquals(presenter.getNotice(), "The folder with same name already exists.");
      assertEquals(presenter.isCompleted(), !IS_COMPLITED);
   }

   /**
    * If folder name is correct then must be not showing any message.
    */
   @Test
   public void shouldBeCorrectName()
   {
      when(view.getFolderName()).thenReturn("test");
      when(project.getChildren()).thenReturn(JsonCollections.<Resource> createArray());

      presenter.checkEnteredInformation();

      assertEquals(presenter.getNotice(), null);
      assertEquals(presenter.isCompleted(), IS_COMPLITED);
   }

   /**
    * If calls doFinish method then must be call createFolder method.
    */
   @Test
   @SuppressWarnings("unchecked")
   public void shouldBeCallCreateFolder()
   {
      presenter.doFinish();

      verify(project).createFolder((Folder)anyObject(), anyString(), ((AsyncCallback<Folder>)anyObject()));
   }
}