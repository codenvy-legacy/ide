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
package com.codenvy.ide.theme;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.theme.Theme;
import com.codenvy.ide.collections.Array;

/**
 * @author Evgen Vidolob
 */
public interface AppearanceView extends View<AppearanceView.ActionDelegate> {

    void setThemes(Array<Theme> themes, String currentThemeId);

    public interface ActionDelegate {

        void themeSelected(String themeId);
    }
}
