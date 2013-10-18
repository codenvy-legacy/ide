/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.wizard.newproject;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.wizard.newproject.pages.start.NewProjectPagePresenter;
import com.codenvy.ide.wizard.newproject.pages.template.ChooseTemplatePagePresenter;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static com.codenvy.ide.api.ui.wizard.Wizard.UpdateDelegate;
import static com.codenvy.ide.api.ui.wizard.WizardPage.CommitCallback;
import static com.codenvy.ide.wizard.newproject.NewProjectWizard.PAAS;
import static com.codenvy.ide.wizard.newproject.NewProjectWizard.TEMPLATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

/**
 * Testing {@link NewProjectWizard} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class NewProjectWizardTest {
    public static final boolean CAN_SKIP         = true;
    public static final boolean CAN_NOT_SKIP     = false;
    public static final boolean HAS_NEXT         = true;
    public static final boolean HAS_NOT_NEXT     = false;
    public static final boolean HAS_PREVIOUS     = true;
    public static final boolean HAS_NOT_PREVIOUS = false;
    public static final boolean CAN_FINISH       = true;
    public static final boolean CAN_NOT_FINISH   = false;
    public static final boolean COMPLETED        = true;
    @Mock
    private Provider<NewProjectPagePresenter>     newProjectPageProvider;
    @Mock
    private NewProjectPagePresenter               newProjectPage;
    @Mock
    private Provider<ChooseTemplatePagePresenter> templatePageProvider;
    @Mock
    private ChooseTemplatePagePresenter           chooseTemplatePage;
    @Mock
    private NotificationManager                   notificationManager;
    @Mock
    private WizardPage                            templatePage;
    @Mock
    private WizardPage                            paasPage;
    private WizardContext                         wizardContext;
    private Template                              template;
    private PaaS                                  paas;
    private PaaS                                  nonePaas;
    private PaaS                                  paasWithTemplate;
    private NewProjectWizard                      model;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        model = new NewProjectWizard(newProjectPageProvider, templatePageProvider, notificationManager);
        model.setUpdateDelegate(mock(UpdateDelegate.class));

        when(newProjectPageProvider.get()).thenReturn(newProjectPage);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                wizardContext = (WizardContext)arguments[0];
                return null;
            }
        }).when(newProjectPage).setContext((WizardContext)anyObject());

        when(templatePageProvider.get()).thenReturn(chooseTemplatePage);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                wizardContext = (WizardContext)arguments[0];
                return null;
            }
        }).when(chooseTemplatePage).setContext((WizardContext)anyObject());

        template = new Template("id", "title", null, "primaryNature", JsonCollections.createArray("secondaryNature"));

        JsonStringMap<JsonArray<String>> natures = JsonCollections.createStringMap();
        natures.put("primaryNature", JsonCollections.createArray("secondaryNature"));
        paas = new PaaS("id", "title", null, natures, false);
        nonePaas = new PaaS("id", "title", null, natures, false);
        paasWithTemplate = new PaaS("id", "title", null, natures, true);

        /** Add PaaS pages to model. */
        Provider<? extends WizardPage> paasPageProvider = mock(Provider.class);
        when(paasPageProvider.get()).thenReturn(paasPage);

        JsonArray<Provider<? extends WizardPage>> paasPages = JsonCollections.createArray(paasPageProvider, paasPageProvider);
        model.addPaaSPages(paas, paasPages);
        model.addPaaSPages(paasWithTemplate, paasPages);

        /** Add template pages to model. */
        Provider<? extends WizardPage> templatePageProvider = mock(Provider.class);
        when(templatePageProvider.get()).thenReturn(templatePage);

        JsonArray<Provider<? extends WizardPage>> templatePages = JsonCollections.createArray(templatePageProvider, templatePageProvider);
        model.addTemplatePages(template, templatePages);
    }

    /**
     * Flip pages into model.
     *
     * @param count
     *         count of pages how many need to flip
     */
    private void flipPages(int count) {
        for (int i = 0; i < count; i++) {
            model.flipToNext();
        }
    }

    /**
     * Prepare commit callback on mock wizard page. Commit callback usually return success.
     *
     * @param page
     *         page that need to prepare
     */
    private void prepareCommitCallback(WizardPage page) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                CommitCallback callback = (CommitCallback)arguments[0];
                callback.onSuccess();
                return null;
            }
        }).when(page).commit((CommitCallback)anyObject());
    }

    @Test
    public void testFlipToFirst() throws Exception {
        assertEquals(model.flipToFirst(), newProjectPage);

        verify(newProjectPage).setUpdateDelegate((UpdateDelegate)anyObject());
        verify(newProjectPage).setContext((WizardContext)anyObject());
    }

    @Test
    public void testFlipToFirstWhenSecondTime() throws Exception {
        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);

        assertEquals(model.flipToFirst(), newProjectPage);

        verify(newProjectPageProvider, times(2)).get();
        verify(newProjectPage, times(2)).setUpdateDelegate((UpdateDelegate)anyObject());
        verify(newProjectPage, times(2)).setContext((WizardContext)anyObject());
        assertNull(wizardContext.getData(TEMPLATE));
    }

    @Test
    public void testFlipToNextUseCase1() throws Exception {
        prepareTestCase1();

        assertEquals(model.flipToFirst(), newProjectPage);
    }

    @Test
    public void testFlipToPreviousUseCase1() throws Exception {
        prepareTestCase1();
        model.flipToFirst();

        assertNull(model.flipToPrevious());
    }

    @Test
    public void testHasNextUseCase1() throws Exception {
        prepareTestCase1();
        model.flipToFirst();

        assertEquals(model.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase1() throws Exception {
        prepareTestCase1();
        model.flipToFirst();

        assertEquals(model.hasPrevious(), HAS_NOT_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase1() throws Exception {
        prepareTestCase1();
        model.flipToFirst();
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);

        assertEquals(model.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase1() throws Exception {
        prepareTestCase1();

        prepareCommitCallback(newProjectPage);
        prepareCommitCallback(chooseTemplatePage);
        prepareCommitCallback(templatePage);

        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        model.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        verify(templatePage, times(2)).commit((CommitCallback)anyObject());
    }

    /** In case the model has just one next page (main page). Other page are skipped or not exist. */
    private void prepareTestCase1() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_SKIP);
        when(templatePage.canSkip()).thenReturn(CAN_SKIP, CAN_SKIP);
        when(paasPage.canSkip()).thenReturn(CAN_SKIP, CAN_SKIP);
    }

    @Test
    public void testFlipToNextUseCase2() throws Exception {
        prepareTestCase2();
        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);

        assertEquals(model.flipToNext(), chooseTemplatePage);
    }

    @Test
    public void testFlipToPreviousUseCase2() throws Exception {
        prepareTestCase2();
        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        flipPages(1);

        assertEquals(model.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase2() throws Exception {
        prepareTestCase2();

        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase2() throws Exception {
        prepareTestCase2();

        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(model.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase2() throws Exception {
        prepareTestCase2();

        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(model.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase2() throws Exception {
        prepareTestCase2();

        prepareCommitCallback(newProjectPage);
        prepareCommitCallback(chooseTemplatePage);
        prepareCommitCallback(templatePage);

        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        flipPages(1);
        model.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        verify(templatePage, times(2)).commit((CommitCallback)anyObject());
    }

    /** In case the model has following next pages: main page and choose template page. Other page are skipped or not exist. */
    private void prepareTestCase2() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_NOT_SKIP);
        when(templatePage.canSkip()).thenReturn(CAN_SKIP, CAN_SKIP);
    }

    @Test
    public void testFlipToNextUseCase3() throws Exception {
        prepareTestCase3();

        assertEquals(model.flipToFirst(), newProjectPage);
        wizardContext.putData(PAAS, nonePaas);
        assertEquals(model.flipToNext(), chooseTemplatePage);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(model.flipToNext(), templatePage);
        assertEquals(model.flipToNext(), templatePage);
        assertNull(model.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase3() throws Exception {
        prepareTestCase3();

        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        flipPages(1);
        wizardContext.putData(TEMPLATE, template);
        flipPages(2);

        assertEquals(model.flipToPrevious(), templatePage);
        assertEquals(model.flipToPrevious(), chooseTemplatePage);
        assertEquals(model.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase3() throws Exception {
        prepareTestCase3();

        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase3() throws Exception {
        prepareTestCase3();

        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        assertEquals(model.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(1);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase3() throws Exception {
        prepareTestCase3();

        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        when(templatePage.canSkip()).thenReturn(CAN_NOT_FINISH);
        flipPages(1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        when(templatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(model.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase3() throws Exception {
        prepareTestCase3();

        prepareCommitCallback(newProjectPage);
        prepareCommitCallback(chooseTemplatePage);
        prepareCommitCallback(templatePage);

        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        flipPages(3);
        model.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        verify(templatePage, times(2)).commit((CommitCallback)anyObject());
    }

    /**
     * In case the model has following next pages: main page, choose template page and template pages. Other page are skipped or not
     * exist.
     */
    private void prepareTestCase3() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_NOT_SKIP);
        when(templatePage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
    }

    @Test
    public void testFlipToNextUseCase4() throws Exception {
        prepareTestCase4();

        assertEquals(model.flipToFirst(), newProjectPage);
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(model.flipToNext(), templatePage);
        assertEquals(model.flipToNext(), templatePage);
        assertNull(model.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase4() throws Exception {
        prepareTestCase4();

        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        flipPages(2);

        assertEquals(model.flipToPrevious(), templatePage);
        assertEquals(model.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase4() throws Exception {
        prepareTestCase4();

        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase4() throws Exception {
        prepareTestCase4();

        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(model.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase4() throws Exception {
        prepareTestCase4();

        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        when(templatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(model.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase4() throws Exception {
        prepareTestCase4();

        prepareCommitCallback(newProjectPage);
        prepareCommitCallback(chooseTemplatePage);
        prepareCommitCallback(templatePage);

        model.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        flipPages(2);
        model.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        verify(templatePage, times(2)).commit((CommitCallback)anyObject());
    }

    /** In case the model has following next pages: main page and template pages. Other page are skipped or not exist. */
    private void prepareTestCase4() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_SKIP);
        when(templatePage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
    }

    @Test
    public void testFlipToNextUseCase5() throws Exception {
        prepareTestCase5();

        assertEquals(model.flipToFirst(), newProjectPage);
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(model.flipToNext(), chooseTemplatePage);
        assertEquals(model.flipToNext(), templatePage);
        assertEquals(model.flipToNext(), templatePage);
        assertEquals(model.flipToNext(), paasPage);
        assertEquals(model.flipToNext(), paasPage);
        assertNull(model.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase5() throws Exception {
        prepareTestCase5();

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(5);

        assertEquals(model.flipToPrevious(), paasPage);
        assertEquals(model.flipToPrevious(), templatePage);
        assertEquals(model.flipToPrevious(), templatePage);
        assertEquals(model.flipToPrevious(), chooseTemplatePage);
        assertEquals(model.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase5() throws Exception {
        prepareTestCase5();

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase5() throws Exception {
        prepareTestCase5();

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(model.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase5() throws Exception {
        prepareTestCase5();

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        when(paasPage.isCompleted()).thenReturn(COMPLETED);
        when(templatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(model.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase5() throws Exception {
        prepareTestCase5();

        prepareCommitCallback(newProjectPage);
        prepareCommitCallback(chooseTemplatePage);
        prepareCommitCallback(templatePage);
        prepareCommitCallback(paasPage);

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(5);
        model.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        verify(templatePage, times(2)).commit((CommitCallback)anyObject());
        verify(paasPage, times(2)).commit((CommitCallback)anyObject());
    }

    /** In case the model has following next pages: main page, choose template page, template pages and paas pages. */
    private void prepareTestCase5() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_NOT_SKIP);
        when(templatePage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
        when(paasPage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
    }

    @Test
    public void testFlipToNextUseCase6() throws Exception {
        prepareTestCase6();

        assertEquals(model.flipToFirst(), newProjectPage);
        wizardContext.putData(PAAS, paas);
        assertEquals(model.flipToNext(), chooseTemplatePage);
        assertEquals(model.flipToNext(), paasPage);
        assertEquals(model.flipToNext(), paasPage);
        assertNull(model.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase6() throws Exception {
        prepareTestCase6();

        model.flipToFirst();
        wizardContext.putData(PAAS, paas);
        flipPages(3);

        assertEquals(model.flipToPrevious(), paasPage);
        assertEquals(model.flipToPrevious(), chooseTemplatePage);
        assertEquals(model.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase6() throws Exception {
        prepareTestCase6();

        model.flipToFirst();
        wizardContext.putData(PAAS, paas);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase6() throws Exception {
        prepareTestCase6();

        model.flipToFirst();
        wizardContext.putData(PAAS, paas);
        assertEquals(model.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase6() throws Exception {
        prepareTestCase6();

        model.flipToFirst();
        wizardContext.putData(PAAS, paas);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        when(paasPage.isCompleted()).thenReturn(COMPLETED);
        when(templatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(model.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase6() throws Exception {
        prepareTestCase6();

        prepareCommitCallback(newProjectPage);
        prepareCommitCallback(chooseTemplatePage);
        prepareCommitCallback(templatePage);
        prepareCommitCallback(paasPage);

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(3);
        model.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        verify(templatePage, times(2)).commit((CommitCallback)anyObject());
        verify(paasPage, times(2)).commit((CommitCallback)anyObject());
    }

    /** In case the model has following next pages: main page,choose template page and paas pages. Template pages are skipped. */
    private void prepareTestCase6() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_NOT_SKIP);
        when(templatePage.canSkip()).thenReturn(CAN_SKIP, CAN_SKIP);
        when(paasPage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
    }

    @Test
    public void testFlipToNextUseCase7() throws Exception {
        prepareTestCase7();

        assertEquals(model.flipToFirst(), newProjectPage);
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(model.flipToNext(), templatePage);
        assertEquals(model.flipToNext(), templatePage);
        assertEquals(model.flipToNext(), paasPage);
        assertEquals(model.flipToNext(), paasPage);
        assertNull(model.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase7() throws Exception {
        prepareTestCase7();

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(4);

        assertEquals(model.flipToPrevious(), paasPage);
        assertEquals(model.flipToPrevious(), templatePage);
        assertEquals(model.flipToPrevious(), templatePage);
        assertEquals(model.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase7() throws Exception {
        prepareTestCase7();

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase7() throws Exception {
        prepareTestCase7();

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(model.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase7() throws Exception {
        prepareTestCase7();

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        when(paasPage.isCompleted()).thenReturn(COMPLETED);
        when(templatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(model.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase7() throws Exception {
        prepareTestCase7();

        prepareCommitCallback(newProjectPage);
        prepareCommitCallback(chooseTemplatePage);
        prepareCommitCallback(templatePage);
        prepareCommitCallback(paasPage);

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(4);
        model.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        verify(templatePage, times(2)).commit((CommitCallback)anyObject());
        verify(paasPage, times(2)).commit((CommitCallback)anyObject());
    }

    /** In case the model has following next pages: main page, template pages and paas pages. Choose template page is skipped. */
    private void prepareTestCase7() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_SKIP);
        when(templatePage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
        when(paasPage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
    }

    @Test
    public void testFlipToNextUseCase8() throws Exception {
        prepareTestCase8();

        assertEquals(model.flipToFirst(), newProjectPage);
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(model.flipToNext(), paasPage);
        assertEquals(model.flipToNext(), paasPage);
        assertNull(model.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase8() throws Exception {
        prepareTestCase8();

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(2);

        assertEquals(model.flipToPrevious(), paasPage);
        assertEquals(model.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase8() throws Exception {
        prepareTestCase8();

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase8() throws Exception {
        prepareTestCase8();

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(model.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase8() throws Exception {
        prepareTestCase8();

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        when(paasPage.isCompleted()).thenReturn(COMPLETED);
        when(templatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(model.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase8() throws Exception {
        prepareTestCase8();

        prepareCommitCallback(newProjectPage);
        prepareCommitCallback(chooseTemplatePage);
        prepareCommitCallback(templatePage);
        prepareCommitCallback(paasPage);

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(2);
        model.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        verify(templatePage, times(2)).commit((CommitCallback)anyObject());
        verify(paasPage, times(2)).commit((CommitCallback)anyObject());
    }

    /** In case the model has following next pages: main page, paas pages. Choose template page and template pages are skipped. */
    private void prepareTestCase8() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_SKIP);
        when(templatePage.canSkip()).thenReturn(CAN_SKIP, CAN_SKIP);
        when(paasPage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
    }

    @Test
    public void testFlipToNextUseCase9() throws Exception {
        prepareTestCase9();

        assertEquals(model.flipToFirst(), newProjectPage);
        wizardContext.putData(PAAS, paasWithTemplate);
        assertEquals(model.flipToNext(), paasPage);
        assertEquals(model.flipToNext(), paasPage);
        assertNull(model.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase9() throws Exception {
        prepareTestCase9();

        model.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        flipPages(2);

        assertEquals(model.flipToPrevious(), paasPage);
        assertEquals(model.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase9() throws Exception {
        prepareTestCase9();

        model.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase9() throws Exception {
        prepareTestCase9();

        model.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        assertEquals(model.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase9() throws Exception {
        prepareTestCase9();

        model.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        when(paasPage.isCompleted()).thenReturn(COMPLETED);
        when(templatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(model.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase9() throws Exception {
        prepareTestCase9();

        prepareCommitCallback(newProjectPage);
        prepareCommitCallback(paasPage);

        model.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        flipPages(2);
        model.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(paasPage, times(2)).commit((CommitCallback)anyObject());
    }

    /** In case the model has following next pages: main page, paas pages. Choose template page and template pages aren't exist. */
    private void prepareTestCase9() {
        when(paasPage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
    }

    @Test
    public void testFlipToNextUseCase10() throws Exception {
        prepareTestCase10();

        assertEquals(model.flipToFirst(), newProjectPage);
        wizardContext.putData(PAAS, paasWithTemplate);
        assertNull(model.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase10() throws Exception {
        prepareTestCase10();

        model.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);

        assertNull(model.flipToPrevious());
    }

    @Test
    public void testHasNextUseCase10() throws Exception {
        prepareTestCase10();

        model.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        assertEquals(model.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase10() throws Exception {
        prepareTestCase10();

        model.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        assertEquals(model.hasPrevious(), HAS_NOT_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase10() throws Exception {
        prepareTestCase10();

        model.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(paasPage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(model.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase10() throws Exception {
        prepareTestCase10();

        prepareCommitCallback(newProjectPage);
        prepareCommitCallback(paasPage);

        model.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        model.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(paasPage, times(2)).commit((CommitCallback)anyObject());
    }

    /**
     * In case the model has following next pages: main page. Choose template page and template pages aren't exist. PaaS pages are
     * skipped.
     */
    private void prepareTestCase10() {
        when(paasPage.canSkip()).thenReturn(CAN_SKIP, CAN_SKIP);
    }

    @Test
    public void testOnFinishWhenFailure() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                CommitCallback callback = (CommitCallback)arguments[0];
                callback.onFailure(mock(Throwable.class));
                return null;
            }
        }).when(newProjectPage).commit((CommitCallback)anyObject());

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(1);
        model.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage, never()).commit((CommitCallback)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }
}