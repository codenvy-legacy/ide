/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.tutorial.parts;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.tutorial.parts.howto.TutorialHowToPresenter;
import com.codenvy.ide.tutorial.parts.part.MyPartFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.workspace.PartStackType.EDITING;
import static com.codenvy.ide.api.ui.workspace.PartStackType.INFORMATION;
import static com.codenvy.ide.api.ui.workspace.PartStackType.NAVIGATION;
import static com.codenvy.ide.api.ui.workspace.PartStackType.TOOLING;

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