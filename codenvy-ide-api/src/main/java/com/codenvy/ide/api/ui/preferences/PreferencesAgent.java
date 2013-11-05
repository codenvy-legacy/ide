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
package com.codenvy.ide.api.ui.preferences;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.extension.SDK;
import com.google.inject.Provider;


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
    void addPage(@NotNull Provider<? extends PreferencesPagePresenter> page);
}