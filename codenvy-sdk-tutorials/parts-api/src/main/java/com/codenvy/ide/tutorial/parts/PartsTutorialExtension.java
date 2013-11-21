package com.codenvy.ide.tutorial.parts;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.tutorial.parts.howto.TutorialHowToPresenter;
import com.codenvy.ide.tutorial.parts.part.MyPartFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.workspace.PartStackType.*;

/** Extension used to demonstrate the parts feature. */
@Singleton
@Extension(title = "Part tutorial", version = "1.0.0")
public class PartsTutorialExtension {

    @Inject
    public PartsTutorialExtension(WorkspaceAgent workspaceAgent,
                                  TutorialHowToPresenter howToPresenter,
                                  MyPartFactory myPartFactory) {
        workspaceAgent.openPart(howToPresenter, EDITING);

        PartPresenter myPartPresenter = myPartFactory.create("Part on the left 1");
        workspaceAgent.openPart(myPartPresenter, NAVIGATION);
        workspaceAgent.openPart(myPartFactory.create("Part on the left 2"), NAVIGATION);

        workspaceAgent.setActivePart(myPartPresenter);

        workspaceAgent.openPart(myPartFactory.create("Part on the right 1"), TOOLING);
        workspaceAgent.openPart(myPartFactory.create("Part on the right 2"), TOOLING);

        workspaceAgent.openPart(myPartFactory.create("Part at the bottom 1"), INFORMATION);
        workspaceAgent.openPart(myPartFactory.create("Part at the bottom 2"), INFORMATION);
    }
}