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
package org.eclipse.che.ide.api.theme;

import org.eclipse.che.ide.collections.Array;

import javax.annotation.Nonnull;

/**
 * Manages UI Themes
 *
 * @author Evgen Vidolob
 */
public interface ThemeAgent {

    /**
     * Add new Theme
     *
     * @param theme
     *         the theme
     */
    void addTheme(@Nonnull Theme theme);

    /**
     * @param themeId
     *         the id of the theme
     * @return theme with theme id or default theme if theme not found
     */
    @Nonnull
    Theme getTheme(@Nonnull String themeId);

    /**
     * @return default theme
     */
    Theme getDefault();

    /**
     * @return all known themes
     */
    Array<Theme> getThemes();

    /**
     * @return current theme
     */
    String getCurrentThemeId();

    /**
     * @param id
     */
    void setCurrentThemeId(String id);
}
