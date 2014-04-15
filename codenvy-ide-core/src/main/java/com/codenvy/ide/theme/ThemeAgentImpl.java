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
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;
import com.google.inject.Inject;

import javax.validation.constraints.NotNull;

import java.util.Set;

/**
 * Implementation of ThemeAgent
 * @author Evgen Vidolob
 */
public class ThemeAgentImpl  implements ThemeAgent{
    
    public static final String THEME_STORAGE = "codenvy-theme";
    
    private StringMap<Theme> themes = Collections.createStringMap();

    private final Theme defaultTheme;

    private String currentThemeId;

    @Inject
    public ThemeAgentImpl(Set<Theme> theme, DarkTheme darkTheme) {
        defaultTheme = darkTheme;
        for (Theme t : theme) {
            addTheme(t);
        }
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
        if (currentThemeId == null && Storage.isLocalStorageSupported()
            && Storage.getLocalStorageIfSupported().getItem(THEME_STORAGE) != null) {
            setCurrentThemeId(Storage.getLocalStorageIfSupported().getItem(THEME_STORAGE));
        }
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
        
        if(typeof(Storage)!=="undefined") {
            localStorage.setItem(@com.codenvy.ide.theme.ThemeAgentImpl::THEME_STORAGE, id);
        }
         
        if ($wnd["IDE3"]) {
            $wnd["IDE3"].theme = id;
        }
    }-*/;
    
}
