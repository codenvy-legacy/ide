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
package com.codenvy.ide.wizard;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenvy.ide.wizard.WizardPagePresenter;
import com.codenvy.ide.wizard.WizardPresenter;
import com.codenvy.ide.wizard.WizardView;

import com.google.gwt.junit.GWTMockUtilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Testing {@link WizardPresenter} functionality
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class TestWizardPresenter
{
   private static final boolean HAS_NEXT_BUTTON = true;

   private static final boolean HAS_BACK_BUTTON = true;

   private static final boolean PAGE_IS_COMPLITED = true;

   private static final boolean CAN_FINISH = true;

   @Mock
   private WizardPagePresenter currentPage;

   @Mock
   private WizardView view;

   private WizardPresenter presenter;

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
      presenter = new WizardPresenter(currentPage, view);
   }

   @After
   public void restore()
   {
      GWTMockUtilities.restore();
   }

   /**
    * If press on Cancel button then must be call doCancel method on currentPage and close dialog. 
    */
   @Test
   public void shouldBeCallCancelAndClose()
   {
      presenter.onCancelClicked();

      verify(currentPage).doCancel();
      verify(view).close();
   }

   /**
    * If press on Finish button then must be call doFinish method on currentPage and close dialog.
    */
   @Test
   public void shouldBeCallFinishAndClose()
   {
      presenter.onFinishClicked();

      verify(currentPage).doFinish();
      verify(view).close();
   }

   /**
    * Check visible and enable properties for navigation button.
    */
   @Test
   public void checkVisibledAndEnabledNavigationBtn()
   {
      when(currentPage.canFinish()).thenReturn(CAN_FINISH);
      when(currentPage.isCompleted()).thenReturn(!PAGE_IS_COMPLITED);
      when(currentPage.hasNext()).thenReturn(HAS_NEXT_BUTTON);
      when(currentPage.hasPrevious()).thenReturn(HAS_BACK_BUTTON);

      //needs reset because following methods called 2 times
      reset(view);
      presenter.updateControls();

      verify(view).setBackButtonVisible(HAS_BACK_BUTTON);
      verify(view).setNextButtonVisible(HAS_NEXT_BUTTON);
      verify(view).setNextButtonEnabled(!PAGE_IS_COMPLITED);
      verify(view).setFinishButtonEnabled(CAN_FINISH && !PAGE_IS_COMPLITED);
   }

   /**
    * If sets new wizard page then must be set new previous page for current page and go to new page.
    */
   @Test
   public void shouldBeChangeCurrentWizardPage()
   {
      WizardPagePresenter newPage = mock(WizardPagePresenter.class);

      presenter.setPage(newPage);

      verify(newPage).setPrevious(currentPage);
      verify(newPage).go(view.getContentPanel());
   }
}