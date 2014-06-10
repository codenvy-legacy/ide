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
package com.codenvy.ide.toolbar;

import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.Presentation;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

/** @author Evgen Vidolob */
public class PresentationFactory {
    private final HashMap<Action, Presentation> myAction2Presentation;

    public PresentationFactory() {
        myAction2Presentation = new HashMap<>();
    }

    public final Presentation getPresentation(@NotNull Action action) {
        Presentation presentation = myAction2Presentation.get(action);
        if (presentation == null) {
            presentation = action.getTemplatePresentation().clone();
            myAction2Presentation.put(action, processPresentation(presentation));
        }
        return presentation;
    }

    protected Presentation processPresentation(Presentation presentation) {
        return presentation;
    }

    public void reset() {
        myAction2Presentation.clear();
    }
}
