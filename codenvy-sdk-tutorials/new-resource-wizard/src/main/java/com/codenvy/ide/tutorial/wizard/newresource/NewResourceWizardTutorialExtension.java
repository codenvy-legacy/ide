package com.codenvy.ide.tutorial.wizard.newresource;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.wizard.DefaultWizard;
import com.codenvy.ide.api.ui.wizard.newresource.NewResource;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceAgent;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.tutorial.wizard.newresource.page.MyResourcePagePresenter;
import com.codenvy.ide.tutorial.wizard.newresource.part.TutorialHowToPresenter;
import com.codenvy.ide.tutorial.wizard.newresource.provider.MyResourceProvider;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.workspace.PartStackType.EDITING;

/** Extension used to demonstrate the New resource wizard feature. */
@Singleton
@Extension(title = "New Resource Wizard tutorial", version = "1.0.0")
public class NewResourceWizardTutorialExtension {

    @Inject
    public NewResourceWizardTutorialExtension(WorkspaceAgent workspaceAgent,
                                              TutorialHowToPresenter howToPresenter,
                                              NewResourceAgent newResourceAgent,
                                              MyResourceProvider myResourceProvider,
                                              @NewResource DefaultWizard newResourceWizard,
                                              Provider<MyResourcePagePresenter> myResourcePage) {
        workspaceAgent.openPart(howToPresenter, EDITING);

        newResourceAgent.register(myResourceProvider);
        newResourceWizard.addPage(myResourcePage);
    }
}