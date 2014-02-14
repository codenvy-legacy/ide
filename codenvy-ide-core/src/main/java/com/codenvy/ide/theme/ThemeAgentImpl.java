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
package com.codenvy.ide.theme;

import com.codenvy.ide.api.ui.theme.Theme;
import com.codenvy.ide.api.ui.theme.ThemeAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;

import javax.validation.constraints.NotNull;

/**
 * Implementation of ThemeAgent
 * @author Evgen Vidolob
 */
public class ThemeAgentImpl  implements ThemeAgent{

    private StringMap<Theme> themes = Collections.createStringMap();

    private final Theme defaultTheme = new LightTheme();

    private String currentThemeId;

    public ThemeAgentImpl() {
        addTheme(defaultTheme);
        addTheme(new DarkTheme());
    }

    @Override
    public void addTheme(@NotNull Theme theme) {
        themes.put(theme.getId(), theme);
    }

    @Override
    public Theme getTheme(@NotNull String themeId) {
        if (themes.containsKey(themeId))
            return themes.get(themeId);
        return defaultTheme;
    }

    @Override
    public Theme getDefault() {
        return defaultTheme;
    }

    @Override
    public Array<Theme> getThemes() {
        return themes.getValues();
    }

    @Override
    public String getCurrentThemeId() {
        return currentThemeId;
    }

    /**
     * Sharing theme ID through "IDE3" object makes it readable from native JavaScript.
     * It's needed to display additional menu items in the same style as IDE
     *    (style of menu additions must depend on style of IDE).
     */
    @Override
    public native void setCurrentThemeId(String id) /*-{
        this.@com.codenvy.ide.theme.ThemeAgentImpl::currentThemeId = id;

        if ($wnd["IDE3"]) {
            $wnd["IDE3"].theme = id;
        }
    }-*/;
    
}
