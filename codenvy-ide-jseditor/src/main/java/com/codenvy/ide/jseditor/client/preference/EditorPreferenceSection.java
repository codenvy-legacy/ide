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
package com.codenvy.ide.jseditor.client.preference;

import com.codenvy.ide.api.mvp.Presenter;

/** Presenter for a section of the editor preferences page. */
public interface EditorPreferenceSection extends Presenter {

    /** Performs any actions appropriate in response to the user having pressed the Apply button. */
    void doApply();

    /** Tells if the content of the section has been changed. */
    boolean isDirty();

    /** Sets the editor page presenter that owns the section. */
    void setParent(ParentPresenter parent);

    /** Interface for the parent presenter that owns the section. */
    public interface ParentPresenter {
        /** Asks to trigger a dirty state action. */
        void signalDirtyState();
    }
}
