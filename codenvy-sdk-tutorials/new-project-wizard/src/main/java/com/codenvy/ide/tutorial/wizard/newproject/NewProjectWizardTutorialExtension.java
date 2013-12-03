package com.codenvy.ide.tutorial.wizard.newproject;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.api.ui.wizard.paas.AbstractPaasPage;
import com.codenvy.ide.api.ui.wizard.template.AbstractTemplatePage;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.JsonStringMap;
import com.codenvy.ide.resources.ProjectTypeAgent;
import com.codenvy.ide.tutorial.wizard.newproject.pages.paas.PaasPageProvider;
import com.codenvy.ide.tutorial.wizard.newproject.pages.page.PageProvider;
import com.codenvy.ide.tutorial.wizard.newproject.pages.template.TemplatePageProvider;
import com.codenvy.ide.tutorial.wizard.newproject.part.TutorialHowToPresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.workspace.PartStackType.EDITING;

/** Extension used to demonstrate the New project wizard feature. */
@Singleton
@Extension(title = "New Project Wizard tutorial", version = "1.0.0")
public class NewProjectWizardTutorialExtension {
    public static final String MY_PROJECT_TYPE = "My project type";
    public static final String MY_TEMPLATE_1   = "My template 1";
    public static final String MY_TEMPLATE_2   = "My template 2";
    public static final String MY_PAAS_1       = "My PaaS 1";
    public static final String MY_PAAS_2       = "My PaaS 2";

    @Inject
    public NewProjectWizardTutorialExtension(NewProjectWizard newProjectWizard,
                                             ProjectTypeAgent projectTypeAgent,
                                             TemplateAgent templateAgent,
                                             PaaSAgent paasAgent,
                                             WorkspaceAgent workspaceAgent,
                                             TutorialHowToPresenter howToPresenter) {
        workspaceAgent.openPart(howToPresenter, EDITING);

        projectTypeAgent.register(MY_PROJECT_TYPE,
                                  MY_PROJECT_TYPE,
                                  null,
                                  MY_PROJECT_TYPE,
                                  Collections.<String>createArray());

        templateAgent.register(MY_TEMPLATE_1,
                               MY_TEMPLATE_1,
                               null,
                               MY_PROJECT_TYPE,
                               Collections.<String>createArray(),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(
                                       new TemplatePageProvider("Template 1 page", MY_TEMPLATE_1)));
        templateAgent.register(MY_TEMPLATE_2,
                               MY_TEMPLATE_2,
                               null,
                               MY_PROJECT_TYPE,
                               Collections.<String>createArray(),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(
                                       new TemplatePageProvider("Template 2 page", MY_TEMPLATE_2)));

        JsonStringMap<Array<String>> natures = Collections.createStringMap();
        natures.put(MY_PROJECT_TYPE, Collections.<String>createArray());

        Array<Provider<? extends AbstractPaasPage>> wizardPages = Collections.createArray();
        wizardPages.add(new PaasPageProvider("PaaS 1 page", MY_PAAS_1));

        paasAgent.register(MY_PAAS_1, MY_PAAS_1, null, natures, wizardPages, false);

        JsonStringMap<Array<String>> natures2 = Collections.createStringMap();
        natures2.put(MY_PROJECT_TYPE, Collections.<String>createArray());

        Array<Provider<? extends AbstractPaasPage>> wizardPages2 = Collections.createArray();
        wizardPages2.add(new PaasPageProvider("PaaS 2 page", MY_PAAS_2));

        paasAgent.register(MY_PAAS_2, MY_PAAS_2, null, natures2, wizardPages2, false);

        newProjectWizard.addPageAfterFirst(new PageProvider("Page after first"));
        newProjectWizard.addPageAfterChooseTemplate(new PageProvider("Page after choose template"));
        newProjectWizard.addPageBeforePaas(new PageProvider("Page before PaaS"));
        newProjectWizard.addPage(new PageProvider("Page after PaaS"));
    }
}