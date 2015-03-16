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
package org.eclipse.che.ide.restore.actions;

import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.restore.AppState;
import org.eclipse.che.ide.restore.Callback;
import org.eclipse.che.ide.restore.AppStateComponent;

import java.util.Map;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
public class TestComponentAction extends Action implements AppStateComponent {
    @Override
    public void actionPerformed(ActionEvent e) {
        Map<String, String> parameters = e.getParameters();
        if (parameters == null) {
            return;
        }

        String stateAction = parameters.get("stateAction");

        if ("save".equals(stateAction)) {
            save(null, null);
        } else if ("restore".equals(stateAction)) {
            restore(null, null);
        }
    }

    @Override
    public void save(AppState appState, Callback callback) {
    }

    @Override
    public void restore(AppState appState, Callback callback) {
    }
}
