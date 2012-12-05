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
package org.exoplatform.ide.wizard.newproject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.inject.Provider;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.wizard.WizardPagePresenter;
import org.exoplatform.ide.wizard.WizardPagePresenter.WizardUpdateDelegate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Testing {@link NewProjectPagePresenter} functionality 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class TestNewProjectPagePresenter
{

   private NewProjectWizardAgentImpl wizardAgent;

   private NewProjectPageView view;

   private NewProjectPagePresenter presenter;

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
      view = mock(NewProjectPageView.class);
      wizardAgent = mock(NewProjectWizardAgentImpl.class);

      presenter = new NewProjectPagePresenter(wizardAgent, mock(NewProjectWizardResource.class), view);
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
      // create registered wizard
      final WizardPagePresenter newPage = mock(WizardPagePresenter.class);
      Provider<WizardPagePresenter> nextPage = new Provider<WizardPagePresenter>()
      {
         public WizardPagePresenter get()
         {
            return newPage;
         }
      };
      JsonArray<NewProjectWizardData> wizards = JsonCollections.createArray();
      wizards.add(new NewProjectWizardData("Title", "Description", "PrimaryType", null, nextPage, null));

      when(wizardAgent.getWizards()).thenReturn(wizards);

      //selected registered technology
      presenter.onButtonPressed(0);
      
      verify(wizardAgent).getWizards();
      assertEquals(presenter.flipToNext(), newPage);
   }
}