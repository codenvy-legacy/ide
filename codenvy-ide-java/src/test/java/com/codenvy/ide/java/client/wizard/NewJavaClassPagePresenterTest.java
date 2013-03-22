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

import com.codenvy.ide.core.editor.EditorAgent;

import com.codenvy.ide.java.client.projectmodel.CompilationUnit;
import com.codenvy.ide.java.client.wizard.NewJavaClassPagePresenter;
import com.codenvy.ide.java.client.wizard.NewJavaClassPageView;

import com.codenvy.ide.api.selection.SelectionAgent;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 
 */
public class NewJavaClassPagePresenterTest extends WizardsBaseTest
{

   @Mock
   private NewJavaClassPageView view;

   @Mock
   private EditorAgent editorAgent;

   @Mock
   private SelectionAgent selectionAgent;

   private NewJavaClassPagePresenter presenter;

   @Before
   public void setUp()
   {
      presenter = new NewJavaClassPagePresenter(view, resourceProvider, editorAgent, selectionAgent);
      presenter.setUpdateDelegate(updateDelegate);
      when(view.getClassName()).thenReturn("MyClass");
      when(view.getClassType()).thenReturn("Class");
   }

   @Test
   public void testIsCompleted() throws Exception
   {
      presenter.checkTypeName();
      assertThat(presenter.isCompleted()).isTrue();
   }

   @Test
   public void testCheckTypeName() throws Exception
   {
      when(view.getClassName()).thenReturn("@$@#$@!!!");
      presenter.checkTypeName();
      assertThat(presenter.canFinish()).isFalse();
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testDoFinish() throws Exception
   {
      presenter.checkTypeName();
      presenter.doFinish();
      verify(project).createCompilationUnit(eq(sourceFolder), eq("MyClass.java"), contains("class MyClass"),
         (AsyncCallback<CompilationUnit>)any());
   }
}
