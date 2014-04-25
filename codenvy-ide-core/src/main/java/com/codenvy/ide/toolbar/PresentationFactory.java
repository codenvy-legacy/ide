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
