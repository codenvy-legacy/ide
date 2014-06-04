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
package com.codenvy.ide.actions.find;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.ui.action.Action;

import java.util.Map;

/**
 * @author Evgen Vidolob
 */
public interface FindActionView extends View<FindActionView.ActionDelegate> {
    void focusOnInput();

    void show();

    void hide();

    String getName();

    void showActions(Map<Action, String> actions);

    void hideActions();

    boolean getCheckBoxState();

    public interface ActionDelegate {

        void nameChanged(String name, boolean checkBoxState);

        void onClose();

        void onActionSelected(Action action);
    }
}
