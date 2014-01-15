/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.api.ui.theme;

import com.codenvy.ide.collections.Array;

import javax.validation.constraints.NotNull;

/**
 * Manages UI Themes
 * @author Evgen Vidolob
 */
public interface ThemeAgent {

    /**
     * Add new Theme
     * @param theme the theme
     */
    void addTheme(@NotNull Theme theme);

    /**
     * @param themeId the id of the theme
     * @return theme with theme id or default theme if theme not found
     */
    @NotNull
    Theme getTheme(@NotNull String themeId);

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
     *
     * @param id
     */
    void setCurrentThemeId(String id);
}
