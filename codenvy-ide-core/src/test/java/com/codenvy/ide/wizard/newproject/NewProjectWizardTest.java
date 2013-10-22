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
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.api.ui.wizard.paas.AbstractPaasPage;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.wizard.BaseWizardTest;
import com.codenvy.ide.wizard.newproject.pages.start.NewProjectPagePresenter;
import com.codenvy.ide.wizard.newproject.pages.template.ChooseTemplatePagePresenter;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static com.codenvy.ide.api.ui.wizard.Wizard.UpdateDelegate;
import static com.codenvy.ide.api.ui.wizard.WizardPage.CommitCallback;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PAAS;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.TEMPLATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

/**
 * Testing {@link NewProjectWizard} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class NewProjectWizardTest extends BaseWizardTest {
    @Mock
    private Provider<NewProjectPagePresenter>     newProjectPageProvider;
    @Mock
    private NewProjectPagePresenter               newProjectPage;
    @Mock
    private Provider<ChooseTemplatePagePresenter> chooseTemplatePageProvider;
    @Mock
    private ChooseTemplatePagePresenter           chooseTemplatePage;
    @Mock
    private WizardPage                            templatePage;
    @Mock
    private AbstractPaasPage                      paasPage;
    private WizardContext                         wizardContext;
    private Template                              template;
    private PaaS                                  paas;
    private PaaS                                  nonePaas;
    private PaaS                                  paasWithTemplate;
    private NewProjectWizard                      wizard;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        wizard = new NewProjectWizard(notificationManager);
        wizard.setUpdateDelegate(mock(UpdateDelegate.class));

        when(newProjectPageProvider.get()).thenReturn(newProjectPage);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                wizardContext = (WizardContext)arguments[0];
                return null;
            }
        }).when(newProjectPage).setContext((WizardContext)anyObject());
        when(newProjectPage.canSkip()).thenReturn(CAN_NOT_SKIP);
        when(newProjectPage.inContext()).thenReturn(IN_CONTEXT);

        when(chooseTemplatePageProvider.get()).thenReturn(chooseTemplatePage);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                wizardContext = (WizardContext)arguments[0];
                return null;
            }
        }).when(chooseTemplatePage).setContext((WizardContext)anyObject());

        wizard.addPage(newProjectPageProvider);
        wizard.addPage(chooseTemplatePageProvider);

        template = new Template("id", "title", null, "primaryNature", JsonCollections.createArray("secondaryNature"));

        JsonStringMap<JsonArray<String>> natures = JsonCollections.createStringMap();
        natures.put("primaryNature", JsonCollections.createArray("secondaryNature"));
        paas = new PaaS("id", "title", null, natures, false);
        nonePaas = new PaaS("id", "title", null, natures, false);
        paasWithTemplate = new PaaS("id", "title", null, natures, true);

        /** Add template pages to wizard. */
        Provider<? extends WizardPage> templatePageProvider = mock(Provider.class);
        when(templatePageProvider.get()).thenReturn(templatePage);

        wizard.addPageAfterChooseTemplate(templatePageProvider);
        wizard.addPageAfterChooseTemplate(templatePageProvider);

        /** Add PaaS pages to wizard. */
        Provider<? extends AbstractPaasPage> paasPageProvider = mock(Provider.class);
        when(paasPageProvider.get()).thenReturn(paasPage);

        wizard.addPaaSPage(paasPageProvider);
        wizard.addPaaSPage(paasPageProvider);
    }

    @Test
    public void testFlipToFirst() throws Exception {
        assertEquals(wizard.flipToFirst(), newProjectPage);

        verify(newProjectPage).setUpdateDelegate((UpdateDelegate)anyObject());
        verify(newProjectPage).setContext((WizardContext)anyObject());
    }

    @Test
    public void testFlipToFirstWhenSecondTime() throws Exception {
        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);

        assertEquals(wizard.flipToFirst(), newProjectPage);

        verify(newProjectPageProvider, times(2)).get();
        verify(newProjectPage, times(2)).setUpdateDelegate((UpdateDelegate)anyObject());
        verify(newProjectPage, times(2)).setContext((WizardContext)anyObject());
        assertNull(wizardContext.getData(TEMPLATE));
    }

    @Test
    public void testFlipToNextUseCase1() throws Exception {
        prepareTestCase1();

        assertEquals(wizard.flipToFirst(), newProjectPage);
    }

    @Test
    public void testFlipToPreviousUseCase1() throws Exception {
        prepareTestCase1();
        wizard.flipToFirst();

        assertNull(wizard.flipToPrevious());
    }

    @Test
    public void testHasNextUseCase1() throws Exception {
        prepareTestCase1();
        wizard.flipToFirst();

        assertEquals(wizard.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase1() throws Exception {
        prepareTestCase1();
        wizard.flipToFirst();

        assertEquals(wizard.hasPrevious(), HAS_NOT_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase1() throws Exception {
        prepareTestCase1();
        wizard.flipToFirst();
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);

        assertEquals(wizard.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase1() throws Exception {
        prepareTestCase1();

        prepareSuccessfulCommitCallback(newProjectPage);
        prepareSuccessfulCommitCallback(chooseTemplatePage);
        prepareSuccessfulCommitCallback(templatePage);

        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        wizard.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        verify(templatePage, times(2)).commit((CommitCallback)anyObject());
    }

    /** In case the wizard has just one next page (main page). Other page are skipped or not exist. */
    private void prepareTestCase1() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_SKIP);
        when(chooseTemplatePage.inContext()).thenReturn(IN_CONTEXT);

        when(templatePage.canSkip()).thenReturn(CAN_SKIP, CAN_SKIP);
        when(templatePage.inContext()).thenReturn(IN_CONTEXT, IN_CONTEXT);

        when(paasPage.canSkip()).thenReturn(CAN_SKIP, CAN_SKIP);
        when(paasPage.inContext()).thenReturn(NOT_CONTEXT, NOT_CONTEXT);
    }

    @Test
    public void testFlipToNextUseCase2() throws Exception {
        prepareTestCase2();
        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);

        assertEquals(wizard.flipToNext(), chooseTemplatePage);
    }

    @Test
    public void testFlipToPreviousUseCase2() throws Exception {
        prepareTestCase2();
        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        flipPages(wizard, 1);

        assertEquals(wizard.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase2() throws Exception {
        prepareTestCase2();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase2() throws Exception {
        prepareTestCase2();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(wizard.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase2() throws Exception {
        prepareTestCase2();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(wizard.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase2() throws Exception {
        prepareTestCase2();

        prepareSuccessfulCommitCallback(newProjectPage);
        prepareSuccessfulCommitCallback(chooseTemplatePage);
        prepareSuccessfulCommitCallback(templatePage);

        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        flipPages(wizard, 1);
        wizard.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        verify(templatePage, times(2)).commit((CommitCallback)anyObject());
    }

    /** In case the wizard has following next pages: main page and choose template page. Other page are skipped or not exist. */
    private void prepareTestCase2() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_NOT_SKIP);
        when(chooseTemplatePage.inContext()).thenReturn(IN_CONTEXT);

        when(templatePage.canSkip()).thenReturn(CAN_SKIP, CAN_SKIP);
        when(templatePage.inContext()).thenReturn(IN_CONTEXT, IN_CONTEXT);

        when(paasPage.canSkip()).thenReturn(CAN_SKIP, CAN_SKIP);
        when(paasPage.inContext()).thenReturn(NOT_CONTEXT, NOT_CONTEXT);
    }

    @Test
    public void testFlipToNextUseCase3() throws Exception {
        prepareTestCase3();

        assertEquals(wizard.flipToFirst(), newProjectPage);
        wizardContext.putData(PAAS, nonePaas);
        assertEquals(wizard.flipToNext(), chooseTemplatePage);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(wizard.flipToNext(), templatePage);
        assertEquals(wizard.flipToNext(), templatePage);
        assertNull(wizard.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase3() throws Exception {
        prepareTestCase3();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        flipPages(wizard, 1);
        wizardContext.putData(TEMPLATE, template);
        flipPages(wizard, 2);

        assertEquals(wizard.flipToPrevious(), templatePage);
        assertEquals(wizard.flipToPrevious(), chooseTemplatePage);
        assertEquals(wizard.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase3() throws Exception {
        prepareTestCase3();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase3() throws Exception {
        prepareTestCase3();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        assertEquals(wizard.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(wizard, 1);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase3() throws Exception {
        prepareTestCase3();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        when(templatePage.canSkip()).thenReturn(CAN_NOT_FINISH);
        flipPages(wizard, 1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        when(templatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(wizard.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase3() throws Exception {
        prepareTestCase3();

        prepareSuccessfulCommitCallback(newProjectPage);
        prepareSuccessfulCommitCallback(chooseTemplatePage);
        prepareSuccessfulCommitCallback(templatePage);

        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        flipPages(wizard, 3);
        wizard.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        verify(templatePage, times(2)).commit((CommitCallback)anyObject());
    }

    /**
     * In case the wizard has following next pages: main page, choose template page and template pages. Other page are skipped or not
     * exist.
     */
    private void prepareTestCase3() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_NOT_SKIP);
        when(chooseTemplatePage.inContext()).thenReturn(IN_CONTEXT);

        when(templatePage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
        when(templatePage.inContext()).thenReturn(IN_CONTEXT, IN_CONTEXT);

        when(paasPage.canSkip()).thenReturn(CAN_SKIP, CAN_SKIP);
        when(paasPage.inContext()).thenReturn(NOT_CONTEXT, NOT_CONTEXT);
    }

    @Test
    public void testFlipToNextUseCase4() throws Exception {
        prepareTestCase4();

        assertEquals(wizard.flipToFirst(), newProjectPage);
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(wizard.flipToNext(), templatePage);
        assertEquals(wizard.flipToNext(), templatePage);
        assertNull(wizard.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase4() throws Exception {
        prepareTestCase4();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        flipPages(wizard, 2);

        assertEquals(wizard.flipToPrevious(), templatePage);
        assertEquals(wizard.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase4() throws Exception {
        prepareTestCase4();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase4() throws Exception {
        prepareTestCase4();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(wizard.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase4() throws Exception {
        prepareTestCase4();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        when(templatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(wizard.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase4() throws Exception {
        prepareTestCase4();

        prepareSuccessfulCommitCallback(newProjectPage);
        prepareSuccessfulCommitCallback(chooseTemplatePage);
        prepareSuccessfulCommitCallback(templatePage);

        wizard.flipToFirst();
        wizardContext.putData(PAAS, nonePaas);
        wizardContext.putData(TEMPLATE, template);
        flipPages(wizard, 2);
        wizard.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        verify(templatePage, times(2)).commit((CommitCallback)anyObject());
    }

    /** In case the wizard has following next pages: main page and template pages. Other page are skipped or not exist. */
    private void prepareTestCase4() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_SKIP);
        when(chooseTemplatePage.inContext()).thenReturn(IN_CONTEXT);

        when(templatePage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
        when(templatePage.inContext()).thenReturn(IN_CONTEXT, IN_CONTEXT);

        when(paasPage.canSkip()).thenReturn(CAN_SKIP, CAN_SKIP);
        when(paasPage.inContext()).thenReturn(NOT_CONTEXT, NOT_CONTEXT);
    }

    @Test
    public void testFlipToNextUseCase5() throws Exception {
        prepareTestCase5();

        assertEquals(wizard.flipToFirst(), newProjectPage);
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.flipToNext(), chooseTemplatePage);
        assertEquals(wizard.flipToNext(), templatePage);
        assertEquals(wizard.flipToNext(), templatePage);
        assertEquals(wizard.flipToNext(), paasPage);
        assertEquals(wizard.flipToNext(), paasPage);
        assertNull(wizard.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase5() throws Exception {
        prepareTestCase5();

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(wizard, 5);

        assertEquals(wizard.flipToPrevious(), paasPage);
        assertEquals(wizard.flipToPrevious(), templatePage);
        assertEquals(wizard.flipToPrevious(), templatePage);
        assertEquals(wizard.flipToPrevious(), chooseTemplatePage);
        assertEquals(wizard.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase5() throws Exception {
        prepareTestCase5();

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase5() throws Exception {
        prepareTestCase5();

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase5() throws Exception {
        prepareTestCase5();

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        when(paasPage.isCompleted()).thenReturn(COMPLETED);
        when(templatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(wizard.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase5() throws Exception {
        prepareTestCase5();

        prepareSuccessfulCommitCallback(newProjectPage);
        prepareSuccessfulCommitCallback(chooseTemplatePage);
        prepareSuccessfulCommitCallback(templatePage);
        prepareSuccessfulCommitCallback(paasPage);

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(wizard, 5);
        wizard.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        verify(templatePage, times(2)).commit((CommitCallback)anyObject());
        verify(paasPage, times(2)).commit((CommitCallback)anyObject());
    }

    /** In case the wizard has following next pages: main page, choose template page, template pages and paas pages. */
    private void prepareTestCase5() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_NOT_SKIP);
        when(chooseTemplatePage.inContext()).thenReturn(IN_CONTEXT);

        when(templatePage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
        when(templatePage.inContext()).thenReturn(IN_CONTEXT, IN_CONTEXT);

        when(paasPage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
        when(paasPage.inContext()).thenReturn(IN_CONTEXT, IN_CONTEXT);
    }

    @Test
    public void testFlipToNextUseCase6() throws Exception {
        prepareTestCase6();

        assertEquals(wizard.flipToFirst(), newProjectPage);
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.flipToNext(), chooseTemplatePage);
        assertEquals(wizard.flipToNext(), paasPage);
        assertEquals(wizard.flipToNext(), paasPage);
        assertNull(wizard.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase6() throws Exception {
        prepareTestCase6();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, paas);
        flipPages(wizard, 3);

        assertEquals(wizard.flipToPrevious(), paasPage);
        assertEquals(wizard.flipToPrevious(), chooseTemplatePage);
        assertEquals(wizard.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase6() throws Exception {
        prepareTestCase6();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase6() throws Exception {
        prepareTestCase6();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase6() throws Exception {
        prepareTestCase6();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        when(paasPage.isCompleted()).thenReturn(COMPLETED);
        when(templatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(wizard.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase6() throws Exception {
        prepareTestCase6();

        prepareSuccessfulCommitCallback(newProjectPage);
        prepareSuccessfulCommitCallback(chooseTemplatePage);
        prepareSuccessfulCommitCallback(templatePage);
        prepareSuccessfulCommitCallback(paasPage);

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(wizard, 3);
        wizard.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        verify(templatePage, times(2)).commit((CommitCallback)anyObject());
        verify(paasPage, times(2)).commit((CommitCallback)anyObject());
    }

    /** In case the wizard has following next pages: main page,choose template page and paas pages. Template pages are skipped. */
    private void prepareTestCase6() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_NOT_SKIP);
        when(chooseTemplatePage.inContext()).thenReturn(IN_CONTEXT);

        when(templatePage.canSkip()).thenReturn(CAN_SKIP, CAN_SKIP);
        when(templatePage.inContext()).thenReturn(IN_CONTEXT, IN_CONTEXT);

        when(paasPage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
        when(paasPage.inContext()).thenReturn(IN_CONTEXT, IN_CONTEXT);
    }

    @Test
    public void testFlipToNextUseCase7() throws Exception {
        prepareTestCase7();

        assertEquals(wizard.flipToFirst(), newProjectPage);
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.flipToNext(), templatePage);
        assertEquals(wizard.flipToNext(), templatePage);
        assertEquals(wizard.flipToNext(), paasPage);
        assertEquals(wizard.flipToNext(), paasPage);
        assertNull(wizard.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase7() throws Exception {
        prepareTestCase7();

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(wizard, 4);

        assertEquals(wizard.flipToPrevious(), paasPage);
        assertEquals(wizard.flipToPrevious(), templatePage);
        assertEquals(wizard.flipToPrevious(), templatePage);
        assertEquals(wizard.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase7() throws Exception {
        prepareTestCase7();

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase7() throws Exception {
        prepareTestCase7();

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase7() throws Exception {
        prepareTestCase7();

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        when(paasPage.isCompleted()).thenReturn(COMPLETED);
        when(templatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(wizard.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase7() throws Exception {
        prepareTestCase7();

        prepareSuccessfulCommitCallback(newProjectPage);
        prepareSuccessfulCommitCallback(chooseTemplatePage);
        prepareSuccessfulCommitCallback(templatePage);
        prepareSuccessfulCommitCallback(paasPage);

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(wizard, 4);
        wizard.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        verify(templatePage, times(2)).commit((CommitCallback)anyObject());
        verify(paasPage, times(2)).commit((CommitCallback)anyObject());
    }

    /** In case the wizard has following next pages: main page, template pages and paas pages. Choose template page is skipped. */
    private void prepareTestCase7() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_SKIP);
        when(chooseTemplatePage.inContext()).thenReturn(IN_CONTEXT);

        when(templatePage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
        when(templatePage.inContext()).thenReturn(IN_CONTEXT, IN_CONTEXT);

        when(paasPage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
        when(paasPage.inContext()).thenReturn(IN_CONTEXT, IN_CONTEXT);
    }

    @Test
    public void testFlipToNextUseCase8() throws Exception {
        prepareTestCase8();

        assertEquals(wizard.flipToFirst(), newProjectPage);
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.flipToNext(), paasPage);
        assertEquals(wizard.flipToNext(), paasPage);
        assertNull(wizard.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase8() throws Exception {
        prepareTestCase8();

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(wizard, 2);

        assertEquals(wizard.flipToPrevious(), paasPage);
        assertEquals(wizard.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase8() throws Exception {
        prepareTestCase8();

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase8() throws Exception {
        prepareTestCase8();

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase8() throws Exception {
        prepareTestCase8();

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        when(paasPage.isCompleted()).thenReturn(COMPLETED);
        when(templatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(wizard.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase8() throws Exception {
        prepareTestCase8();

        prepareSuccessfulCommitCallback(newProjectPage);
        prepareSuccessfulCommitCallback(chooseTemplatePage);
        prepareSuccessfulCommitCallback(templatePage);
        prepareSuccessfulCommitCallback(paasPage);

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(wizard, 2);
        wizard.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage).commit((CommitCallback)anyObject());
        verify(templatePage, times(2)).commit((CommitCallback)anyObject());
        verify(paasPage, times(2)).commit((CommitCallback)anyObject());
    }

    /** In case the wizard has following next pages: main page, paas pages. Choose template page and template pages are skipped. */
    private void prepareTestCase8() {
        when(chooseTemplatePage.canSkip()).thenReturn(CAN_SKIP);
        when(chooseTemplatePage.inContext()).thenReturn(IN_CONTEXT);

        when(templatePage.canSkip()).thenReturn(CAN_SKIP, CAN_SKIP);
        when(templatePage.inContext()).thenReturn(IN_CONTEXT, IN_CONTEXT);

        when(paasPage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
        when(paasPage.inContext()).thenReturn(IN_CONTEXT, IN_CONTEXT);
    }

    @Test
    public void testFlipToNextUseCase9() throws Exception {
        prepareTestCase9();

        assertEquals(wizard.flipToFirst(), newProjectPage);
        wizardContext.putData(PAAS, paasWithTemplate);
        assertEquals(wizard.flipToNext(), paasPage);
        assertEquals(wizard.flipToNext(), paasPage);
        assertNull(wizard.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase9() throws Exception {
        prepareTestCase9();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        flipPages(wizard, 2);

        assertEquals(wizard.flipToPrevious(), paasPage);
        assertEquals(wizard.flipToPrevious(), newProjectPage);
    }

    @Test
    public void testHasNextUseCase9() throws Exception {
        prepareTestCase9();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase9() throws Exception {
        prepareTestCase9();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        assertEquals(wizard.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase9() throws Exception {
        prepareTestCase9();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(chooseTemplatePage.isCompleted()).thenReturn(COMPLETED);
        when(paasPage.isCompleted()).thenReturn(COMPLETED);
        when(templatePage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(wizard.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase9() throws Exception {
        prepareTestCase9();

        prepareSuccessfulCommitCallback(newProjectPage);
        prepareSuccessfulCommitCallback(paasPage);

        wizard.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        flipPages(wizard, 2);
        wizard.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(paasPage, times(2)).commit((CommitCallback)anyObject());
    }

    /** In case the wizard has following next pages: main page, paas pages. Choose template page and template pages aren't exist. */
    private void prepareTestCase9() {
        when(chooseTemplatePage.inContext()).thenReturn(NOT_CONTEXT);
        when(templatePage.inContext()).thenReturn(NOT_CONTEXT, NOT_CONTEXT);

        when(paasPage.canSkip()).thenReturn(CAN_NOT_SKIP, CAN_NOT_SKIP);
        when(paasPage.inContext()).thenReturn(IN_CONTEXT, IN_CONTEXT);
    }

    @Test
    public void testFlipToNextUseCase10() throws Exception {
        prepareTestCase10();

        assertEquals(wizard.flipToFirst(), newProjectPage);
        wizardContext.putData(PAAS, paasWithTemplate);
        assertNull(wizard.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase10() throws Exception {
        prepareTestCase10();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);

        assertNull(wizard.flipToPrevious());
    }

    @Test
    public void testHasNextUseCase10() throws Exception {
        prepareTestCase10();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        assertEquals(wizard.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase10() throws Exception {
        prepareTestCase10();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        assertEquals(wizard.hasPrevious(), HAS_NOT_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase10() throws Exception {
        prepareTestCase10();

        wizard.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        when(newProjectPage.isCompleted()).thenReturn(COMPLETED);
        when(paasPage.isCompleted()).thenReturn(COMPLETED);
        assertEquals(wizard.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase10() throws Exception {
        prepareTestCase10();

        prepareSuccessfulCommitCallback(newProjectPage);
        prepareSuccessfulCommitCallback(paasPage);

        wizard.flipToFirst();
        wizardContext.putData(PAAS, paasWithTemplate);
        wizard.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(paasPage, times(2)).commit((CommitCallback)anyObject());
    }

    /**
     * In case the wizard has following next pages: main page. Choose template page and template pages aren't exist. PaaS pages are
     * skipped.
     */
    private void prepareTestCase10() {
        when(chooseTemplatePage.inContext()).thenReturn(NOT_CONTEXT);
        when(templatePage.inContext()).thenReturn(NOT_CONTEXT, NOT_CONTEXT);

        when(paasPage.canSkip()).thenReturn(CAN_SKIP, CAN_SKIP);
        when(paasPage.inContext()).thenReturn(IN_CONTEXT, IN_CONTEXT);
    }

    @Test
    public void testNextPageWhenAddCustomPages() {
        prepareTestCase5();

        WizardPage page = mock(WizardPage.class);
        when(page.inContext()).thenReturn(IN_CONTEXT);
        Provider<? extends WizardPage> pageProvider = mock(Provider.class);
        when(pageProvider.get()).thenReturn(page);

        wizard.addPageAfterFirst(pageProvider);
        wizard.addPageAfterChooseTemplate(pageProvider);
        wizard.addPageBeforePaas(pageProvider);
        wizard.addPage(pageProvider);
        wizard.addPage(pageProvider, 2, false);
        wizard.addPage(pageProvider, 4, false);
        wizard.addPage(pageProvider, 8, false);

        assertEquals(wizard.flipToFirst(), newProjectPage);
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        assertEquals(wizard.flipToNext(), page);
        assertEquals(wizard.flipToNext(), page);
        assertEquals(wizard.flipToNext(), chooseTemplatePage);
        assertEquals(wizard.flipToNext(), page);
        assertEquals(wizard.flipToNext(), page);
        assertEquals(wizard.flipToNext(), templatePage);
        assertEquals(wizard.flipToNext(), templatePage);
        assertEquals(wizard.flipToNext(), page);
        assertEquals(wizard.flipToNext(), page);
        assertEquals(wizard.flipToNext(), paasPage);
        assertEquals(wizard.flipToNext(), paasPage);
        assertEquals(wizard.flipToNext(), page);
        assertNull(wizard.flipToNext());
    }

    @Test
    public void testOnFinishWhenFailure() throws Exception {
        prepareFailureCommitCallback(newProjectPage);

        wizard.flipToFirst();
        wizardContext.putData(TEMPLATE, template);
        wizardContext.putData(PAAS, paas);
        flipPages(wizard, 1);
        wizard.onFinish();

        verify(newProjectPage).commit((CommitCallback)anyObject());
        verify(chooseTemplatePage, never()).commit((CommitCallback)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }
}