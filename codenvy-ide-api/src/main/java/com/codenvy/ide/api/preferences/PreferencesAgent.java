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
package com.codenvy.ide.api.preferences;

import com.codenvy.ide.api.extension.SDK;
import com.google.inject.Provider;

import javax.annotation.Nonnull;


/**
 * Provides add new preference page.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@SDK(title = "ide.api.ui.preferences")
public interface PreferencesAgent {
    /**
     * Adds a new preference page into preferences list.
     *
     * @param page
     *         page that need to be added
     */
    void addPage(@Nonnull Provider<? extends PreferencesPagePresenter> page);
}