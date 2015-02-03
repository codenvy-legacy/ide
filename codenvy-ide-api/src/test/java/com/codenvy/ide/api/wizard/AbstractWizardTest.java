/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.wizard;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.Nonnull;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testing {@link AbstractWizard}.
 *
 * @author Andrey Plotnikov
 * @author Artem Zatsarynnyy
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractWizardTest {
    @Mock
    private WizardPage<Void>     page1;
    @Mock
    private WizardPage<Void>     page2;
    @Mock
    private WizardPage<Void>     page3;
    @Mock
    private WizardPage<Void>     page4;
    private AbstractWizard<Void> wizard;

    @Before
    public void setUp() {
        wizard = new DummyWizard(null);
        wizard.setUpdateDelegate(mock(Wizard.UpdateDelegate.class));
    }

    @Test
    public void testAddPage() throws Exception {
        wizard.addPage(page1);
        wizard.addPage(page2);
        wizard.addPage(page3);

        assertEquals(page1, wizard.navigateToFirst());
        assertEquals(page2, wizard.navigateToNext());
        assertEquals(page3, wizard.navigateToNext());
        assertNull(wizard.navigateToNext());
    }

    @Test
    public void testAddPageByIndex() throws Exception {
        wizard.addPage(page1);
        wizard.addPage(page3);
        wizard.addPage(page2, 1, false);

        assertEquals(page1, wizard.navigateToFirst());
        assertEquals(page2, wizard.navigateToNext());
        assertEquals(page3, wizard.navigateToNext());
        assertNull(wizard.navigateToNext());
    }

    @Test
    public void testAddPageWithReplace() throws Exception {
        wizard.addPage(page1);
        wizard.addPage(page3);
        wizard.addPage(page2, 1, true);

        assertEquals(page1, wizard.navigateToFirst());
        assertEquals(page2, wizard.navigateToNext());
        assertNull(wizard.navigateToNext());
    }

    @Test
    public void testNavigateToFirstWhenNeedToSkipFirstPages() throws Exception {
        when(page1.canSkip()).thenReturn(true);

        wizard.addPage(page1);
        wizard.addPage(page2);
        wizard.addPage(page3);

        assertEquals(page2, wizard.navigateToFirst());
    }

    @Test
    public void testNavigateToFirst() throws Exception {
        wizard.addPage(page1);

        assertEquals(page1, wizard.navigateToFirst());
    }

    @Test
    public void testCanCompleteWhenAllPagesIsCompleted() throws Exception {
        when(page1.isCompleted()).thenReturn(true);
        when(page2.isCompleted()).thenReturn(true);
        when(page3.isCompleted()).thenReturn(true);

        wizard.addPage(page1);
        wizard.addPage(page2);
        wizard.addPage(page3);

        assertEquals(true, wizard.canComplete());
    }

    @Test
    public void testCanCompleteWhenSomePageIsNotCompleted() throws Exception {
        when(page1.isCompleted()).thenReturn(true);
        when(page2.isCompleted()).thenReturn(false);
        when(page3.isCompleted()).thenReturn(true);

        wizard.addPage(page1);
        wizard.addPage(page2);
        wizard.addPage(page3);

        assertEquals(false, wizard.canComplete());
    }

    @Test
    public void testNavigateToNextUseCase1() throws Exception {
        prepareTestCase1();

        assertEquals(page1, wizard.navigateToFirst());
        assertEquals(page2, wizard.navigateToNext());
        assertEquals(page4, wizard.navigateToNext());
        assertNull(wizard.navigateToNext());
    }

    @Test
    public void testNavigateToPreviousUseCase1() throws Exception {
        prepareTestCase1();

        wizard.navigateToFirst();
        navigatePages(wizard, 2);

        assertEquals(page2, wizard.navigateToPrevious());
        assertEquals(page1, wizard.navigateToPrevious());
    }

    @Test
    public void testHasNextUseCase1() throws Exception {
        prepareTestCase1();

        wizard.navigateToFirst();
        assertEquals(true, wizard.hasNext());

        navigatePages(wizard, 1);
        assertEquals(true, wizard.hasNext());

        navigatePages(wizard, 1);
        assertEquals(false, wizard.hasNext());
    }

    @Test
    public void testHasPreviousUseCase1() throws Exception {
        prepareTestCase1();

        wizard.navigateToFirst();
        assertEquals(false, wizard.hasPrevious());

        navigatePages(wizard, 1);
        assertEquals(true, wizard.hasPrevious());

        navigatePages(wizard, 1);
        assertEquals(true, wizard.hasPrevious());
    }

    /** In case the wizard has got 3 skipped pages and 1 not skipped page. */
    private void prepareTestCase1() {
        when(page1.canSkip()).thenReturn(false);
        when(page1.isCompleted()).thenReturn(true);

        when(page2.canSkip()).thenReturn(false);
        when(page2.isCompleted()).thenReturn(true);

        when(page3.canSkip()).thenReturn(true);
        when(page3.isCompleted()).thenReturn(true);

        when(page4.canSkip()).thenReturn(false);
        when(page4.isCompleted()).thenReturn(true);

        wizard.addPage(page1);
        wizard.addPage(page2);
        wizard.addPage(page3);
        wizard.addPage(page4);
    }

    @Test
    public void testNavigateToNextUseCase2() throws Exception {
        prepareTestCase2();

        assertEquals(page1, wizard.navigateToFirst());
        assertEquals(page2, wizard.navigateToNext());
        assertNull(wizard.navigateToNext());
    }

    @Test
    public void testNavigateToPreviousUseCase2() throws Exception {
        prepareTestCase2();

        wizard.navigateToFirst();
        navigatePages(wizard, 1);

        assertEquals(page1, wizard.navigateToPrevious());
    }

    @Test
    public void testHasNextUseCase2() throws Exception {
        prepareTestCase2();

        wizard.navigateToFirst();
        assertEquals(true, wizard.hasNext());

        navigatePages(wizard, 1);
        assertEquals(false, wizard.hasNext());
    }

    @Test
    public void testHasPreviousUseCase2() throws Exception {
        prepareTestCase2();

        wizard.navigateToFirst();
        assertEquals(false, wizard.hasPrevious());

        navigatePages(wizard, 1);
        assertEquals(true, wizard.hasPrevious());
    }

    /** In case the wizard has got 2 not skipped pages and 2 skipped page. */
    private void prepareTestCase2() {
        when(page1.canSkip()).thenReturn(false);
        when(page1.isCompleted()).thenReturn(true);

        when(page2.canSkip()).thenReturn(false);
        when(page2.isCompleted()).thenReturn(true);

        when(page3.canSkip()).thenReturn(true);
        when(page3.isCompleted()).thenReturn(true);

        when(page4.canSkip()).thenReturn(true);
        when(page4.isCompleted()).thenReturn(true);

        wizard.addPage(page1);
        wizard.addPage(page2);
        wizard.addPage(page3);
        wizard.addPage(page4);
    }

    private void navigatePages(Wizard wizard, int count) {
        for (int i = 0; i < count; i++) {
            wizard.navigateToNext();
        }
    }

    private class DummyWizard extends AbstractWizard<Void> {
        DummyWizard(Void dataObject) {
            super(dataObject);
        }

        @Override
        public void complete(@Nonnull CompleteCallback callback) {
            // do nothing
        }
    }
}