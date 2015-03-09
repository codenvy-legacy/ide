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
package org.eclipse.che.ide.toolbar;

import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.Presentation;

import javax.annotation.Nonnull;
import java.util.HashMap;

/** @author Evgen Vidolob */
public class PresentationFactory {
    private final HashMap<Action, Presentation> myAction2Presentation;

    public PresentationFactory() {
        myAction2Presentation = new HashMap<>();
    }

    public final Presentation getPresentation(@Nonnull Action action) {
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
