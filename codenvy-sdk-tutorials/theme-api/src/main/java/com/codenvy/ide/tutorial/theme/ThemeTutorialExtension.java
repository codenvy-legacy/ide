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
package com.codenvy.ide.tutorial.theme;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.tutorial.theme.howto.TutorialHowToPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Evgen Vidolob
 */
@Singleton
@Extension(title = "Theme tutorial", version = "1.0.0")
public class ThemeTutorialExtension {

    @Inject
    public ThemeTutorialExtension(WorkspaceAgent workspaceAgent,
                                  TutorialHowToPresenter howToPresenter) {
        workspaceAgent.openPart(howToPresenter, PartStackType.EDITING);
    }
}
