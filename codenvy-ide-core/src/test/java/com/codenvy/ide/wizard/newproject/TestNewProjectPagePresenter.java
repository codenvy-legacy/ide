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
package com.codenvy.ide.wizard.newproject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter.WizardUpdateDelegate;

import com.codenvy.ide.paas.PaaSAgentImpl;
import com.codenvy.ide.wizard.WizardAgentImpl;
import com.codenvy.ide.wizard.newgenericproject.NewGenericProjectPagePresenter;
import com.codenvy.ide.wizard.newproject.NewProjectPagePresenter;
import com.codenvy.ide.wizard.newproject.NewProjectPageView;
import com.codenvy.ide.wizard.newproject.NewProjectWizardData;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.inject.Provider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Testing {@link NewProjectPagePresenter} functionality 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class TestNewProjectPagePresenter
{
   @Mock
   private WizardAgentImpl wizardAgent;

   @Mock
   private NewProjectPageView view;

   @Mock
   // TODO
   //   private WizardPagePresenter newPage;
   private NewGenericProjectPagePresenter newPage;

   private NewProjectPagePresenter presenter;

   @Before
   public void disarm()
   {
      // don't throw an exception if GWT.create() invoked
      GWTMockUtilities.disarm();
   }

   /**
    * Create general components for all test.
    */
   @SuppressWarnings("unchecked")
   private void setUp()
   {
      Provider<WizardPagePresenter> nextPage = mock(Provider.class);
      when(nextPage.get()).thenReturn(newPage);

      JsonArray<NewProjectWizardData> wizards = JsonCollections.createArray();
      wizards.add(new NewProjectWizardData("Title", "Description", "PrimaryType", null, nextPage, null));

      when(wizardAgent.getNewProjectWizards()).thenReturn(wizards);

      presenter = new NewProjectPagePresenter(wizardAgent, null, view, mock(PaaSAgentImpl.class));
      presenter.setUpdateDelegate(mock(WizardUpdateDelegate.class));
   }

   @After
   public void restore()
   {
      GWTMockUtilities.restore();
   }

   /**
    * If technology is selected then next page must be chosen technology page. 
    */
   @Test
   public void shouldBeFlipToChosenPage()
   {
      setUp();

      //selected registered technology
      presenter.onProjectTypeSelected(0);
      
      assertEquals(presenter.flipToNext(), newPage);
   }
}