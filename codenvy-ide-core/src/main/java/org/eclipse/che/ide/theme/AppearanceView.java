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
package org.eclipse.che.ide.theme;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.theme.Theme;
import org.eclipse.che.ide.collections.Array;

/**
 * @author Evgen Vidolob
 */
public interface AppearanceView extends View<AppearanceView.ActionDelegate> {

    void setThemes(Array<Theme> themes, String currentThemeId);

    public interface ActionDelegate {

        void themeSelected(String themeId);
    }
}
