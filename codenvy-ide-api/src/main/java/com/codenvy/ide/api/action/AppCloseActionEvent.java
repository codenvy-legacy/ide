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
package com.codenvy.ide.api.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author Sergii Leschenko
 */
public class AppCloseActionEvent extends ActionEvent {
    private String cancelMessage;

    public AppCloseActionEvent(@Nonnull String place, @Nonnull Presentation presentation, ActionManager actionManager, int modifiers) {
        super(place, presentation, actionManager, modifiers);
    }

    public AppCloseActionEvent(@Nonnull String place, @Nonnull Presentation presentation, ActionManager actionManager, int modifiers,
                               @Nullable Map<String, String> parameters) {
        super(place, presentation, actionManager, modifiers, parameters);
    }

    public String getCancelMessage() {
        return cancelMessage;
    }

    public void setCancelMessage(String cancelMessage) {
        this.cancelMessage = cancelMessage;
    }
}
