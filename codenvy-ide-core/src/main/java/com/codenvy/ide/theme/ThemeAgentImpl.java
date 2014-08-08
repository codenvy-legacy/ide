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

import com.codenvy.ide.api.theme.Theme;
import com.codenvy.ide.api.theme.ThemeAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.google.gwt.storage.client.Storage;
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
