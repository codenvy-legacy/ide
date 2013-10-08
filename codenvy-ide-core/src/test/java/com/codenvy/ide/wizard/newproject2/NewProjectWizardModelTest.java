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
package com.codenvy.ide.wizard.newproject2;

import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.wizard.newproject2.pages.start.NewProjectPagePresenter;
import com.codenvy.ide.wizard.newproject2.pages.template.TemplatePageFactory;
import com.codenvy.ide.wizard.newproject2.pages.template.TemplatePagePresenter;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.inject.Provider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static com.codenvy.ide.api.ui.wizard.WizardModel.UpdateDelegate;
import static com.codenvy.ide.api.ui.wizard.WizardPage.CommitCallback;
import static com.codenvy.ide.wizard.newproject2.NewProjectWizardModel.PAAS;
import static com.codenvy.ide.wizard.newproject2.NewProjectWizardModel.TEMPLATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

/**
 * Testing {@link NewProjectWizardModel functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class NewProjectWizardModelTest {
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
    private Provider<NewProjectPagePresenter> newProjectPageProvider;
    @Mock
    private NewProjectPagePresenter           newProjectPage;
    @Mock
    private TemplatePageFactory               templatePageFactory;
    @Mock
    private TemplatePagePresenter             chooseTemplatePage;
    @Mock
    private NotificationManager               notificationManager;
    @Mock
    private WizardPage                        templatePage;
    @Mock
    private WizardPage                        paasPage;
    private WizardContext                     wizardContext;
    private Template                          template;
    private PaaS                              paas;
    private NewProjectWizardModel             model;

    @Before
    public void disarm() {
        // don't throw an exception if GWT.create() invoked
        GWTMockUtilities.disarm();

        model = new NewProjectWizardModel(newProjectPageProvider, templatePageFactory, notificationManager);
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

        when(templatePageFactory.create((WizardContext)anyObject())).thenReturn(chooseTemplatePage);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                wizardContext = (WizardContext)arguments[0];
                return null;
            }
        }).when(chooseTemplatePage).setContext((WizardContext)anyObject());

        template = new Template("title", null, "primaryNature", JsonCollections.createArray("secondaryNature"));

        JsonStringMap<JsonArray<String>> natures = JsonCollections.createStringMap();
        natures.put("primaryNature", JsonCollections.createArray("secondaryNature"));
        paas = new PaaS("id", "title", null, natures);
    }

    @After
    public void restore() {
        GWTMockUtilities.restore();
    }

    /** Add PaaS pages to model. */
    @SuppressWarnings("unchecked")
    private void addPaasPages() {
        Provider<? extends WizardPage> paasPageProvider = mock(Provider.class);
        when(paasPageProvider.get()).thenReturn(paasPage);

        JsonArray<Provider<? extends WizardPage>> paasPages = JsonCollections.createArray(paasPageProvider, paasPageProvider);
        model.addPaaSPages(paas, paasPages);
    }

    /** Add template pages to model. */
    @SuppressWarnings("unchecked")
    private void addTemplatePages() {
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

    @Test
    public void testFlipToFirst() throws Exception {
        assertEquals(model.flipToFirst(), newProjectPage);

        verify(newProjectPage).setUpdateDelegate((UpdateDelegate)anyObject());
        verify(newProjectPage).setContext((WizardContext)anyObject());
    }

    @Test
    public void testFlipToNextUseCase1() throws Exception {
        prepareTestCase1();

        assertEquals(model.flipToFirst(), newProjectPage);
        assertNull(model.flipToNext());
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

    /** In case the model has just one next page (main page). Other page are skipped or not exist. */
    private void prepareTestCase1() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_SKIP);
    }

    @Test
    public void testFlipToNextUseCase2() throws Exception {
        prepareTestCase2();
        model.flipToFirst();

        assertEquals(model.flipToNext(), chooseTemplatePage);
    }

    @Test
    public void testFlipToPreviousUseCase2() throws Exception {
        prepareTestCase2();
        model.flipToFirst();
        flipPages(1);

        assertEquals(model.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase2() throws Exception {
        prepareTestCase2();

        model.flipToFirst();
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        assertEquals(model.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase2() throws Exception {
        prepareTestCase2();

        model.flipToFirst();
        assertEquals(model.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);

        flipPages(1);
        assertEquals(model.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase2() throws Exception {
        prepareTestCase2();

        model.flipToFirst();
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(model.canFinish(), CAN_FINISH);
    }

    /** In case the model has following next pages: main page and choose template page. Other page are skipped or not exist. */
    private void prepareTestCase2() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_NOT_SKIP);
    }

    @Test
    public void testFlipToNextUseCase3() throws Exception {
        prepareTestCase3();

        assertEquals(model.flipToFirst(), newProjectPage);
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

    /**
     * In case the model has following next pages: main page, choose template page and template pages. Other page are skipped or not
     * exist.
     */
    private void prepareTestCase3() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_NOT_SKIP);
        addTemplatePages();
    }

    @Test
    public void testFlipToNextUseCase4() throws Exception {
        prepareTestCase4();

        assertEquals(model.flipToFirst(), newProjectPage);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(model.flipToNext(), templatePage);
        assertEquals(model.flipToNext(), templatePage);
        assertNull(model.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase4() throws Exception {
        prepareTestCase4();

        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        flipPages(2);

        assertEquals(model.flipToPrevious(), templatePage);
        assertEquals(model.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase4() throws Exception {
        prepareTestCase4();

        model.flipToFirst();
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

    /** In case the model has following next pages: main page and template pages. Other page are skipped or not exist. */
    private void prepareTestCase4() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_SKIP);
        addTemplatePages();
    }

    @Test
    public void testFlipToNextUseCase5() throws Exception {
        prepareTestCase5();

        assertEquals(model.flipToFirst(), newProjectPage);
        assertEquals(model.flipToNext(), chooseTemplatePage);
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
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
        flipPages(1);
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(4);

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
        assertEquals(model.hasNext(), HAS_NEXT);
        flipPages(1);
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
    public void testHasPreviousUseCase5() throws Exception {
        prepareTestCase5();

        model.flipToFirst();
        assertEquals(model.hasPrevious(), HAS_NOT_PREVIOUS);
        flipPages(1);

        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
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
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
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

    /** In case the model has following next pages: main page, choose template page, template pages and paas pages. */
    private void prepareTestCase5() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_NOT_SKIP);
        addTemplatePages();
        addPaasPages();
    }

    @Test
    public void testFlipToNextUseCase6() throws Exception {
        prepareTestCase6();

        assertEquals(model.flipToFirst(), newProjectPage);
        assertEquals(model.flipToNext(), chooseTemplatePage);
        wizardContext.putData(PAAS, paas);
        assertEquals(model.flipToNext(), paasPage);
        assertEquals(model.flipToNext(), paasPage);
        assertNull(model.flipToNext());
    }


    @Test
    public void testFlipToPreviousUseCase6() throws Exception {
        prepareTestCase6();

        model.flipToFirst();
        flipPages(1);
        wizardContext.putData(PAAS, paas);
        flipPages(2);

        assertEquals(model.flipToPrevious(), paasPage);
        assertEquals(model.flipToPrevious(), chooseTemplatePage);
        assertEquals(model.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase6() throws Exception {
        prepareTestCase6();

        model.flipToFirst();
        assertEquals(model.hasNext(), HAS_NEXT);

        flipPages(1);
        wizardContext.putData(PAAS, paas);
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
        assertEquals(model.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(1);
        wizardContext.putData(PAAS, paas);
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
        assertEquals(model.canFinish(), CAN_NOT_FINISH);

        flipPages(1);
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

    /** In case the model has following next pages: main page, and paas pages. Template pages are skipped. */
    private void prepareTestCase6() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_NOT_SKIP);
        addPaasPages();
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

    /** In case the model has following next pages: main page, template pages and paas pages. Choose template page is skipped. */
    private void prepareTestCase7() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_SKIP);
        addTemplatePages();
        addPaasPages();
    }

    @Test
    public void testFlipToNextUseCase8() throws Exception {
        prepareTestCase8();

        assertEquals(model.flipToFirst(), newProjectPage);
        wizardContext.putData(PAAS, paas);
        assertEquals(model.flipToNext(), paasPage);
        assertEquals(model.flipToNext(), paasPage);
        assertNull(model.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase8() throws Exception {
        prepareTestCase8();

        model.flipToFirst();
        wizardContext.putData(PAAS, paas);
        flipPages(2);

        assertEquals(model.flipToPrevious(), paasPage);
        assertEquals(model.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase8() throws Exception {
        prepareTestCase8();

        model.flipToFirst();
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

    /** In case the model has following next pages: main page, paas pages. Choose template page and template pages are skipped. */
    private void prepareTestCase8() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_SKIP);
        addPaasPages();
    }

    @Test
    public void testOnCancel() throws Exception {
        model.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        model.onCancel();
        assertNull(wizardContext.getData(TEMPLATE));
    }

    @Test
    public void testOnFinishWhenSuccess() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                CommitCallback callback = (CommitCallback)arguments[0];
                callback.onSuccess();
                return null;
            }
        }).when(newProjectPage).commit((CommitCallback)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                CommitCallback callback = (CommitCallback)arguments[0];
                callback.onSuccess();
                return null;
            }
        }).when(chooseTemplatePage).commit((CommitCallback)anyObject());

        model.flipToFirst();
        flipPages(1);
        wizardContext.putData(TEMPLATE, template);
        model.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        assertNull(wizardContext.getData(TEMPLATE));
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
        flipPages(1);
        wizardContext.putData(TEMPLATE, template);
        model.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        assertNull(wizardContext.getData(TEMPLATE));
    }
}