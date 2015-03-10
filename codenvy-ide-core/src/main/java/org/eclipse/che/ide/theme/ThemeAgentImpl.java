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

import org.eclipse.che.ide.api.theme.Theme;
import org.eclipse.che.ide.api.theme.ThemeAgent;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.collections.StringMap;
import com.google.gwt.storage.client.Storage;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Implementation of ThemeAgent
 *
 * @author Evgen Vidolob
 */
public class ThemeAgentImpl implements ThemeAgent {

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
    public void addTheme(@Nonnull Theme theme) {
        themes.put(theme.getId(), theme);
    }

    @Override
    public Theme getTheme(@Nonnull String themeId) {
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
     * (style of menu additions must depend on style of IDE).
     */
    @Override
    public native void setCurrentThemeId(String id) /*-{
        this.@org.eclipse.che.ide.theme.ThemeAgentImpl::currentThemeId = id;

        if (typeof(Storage) !== "undefined") {
            localStorage.setItem(@org.eclipse.che.ide.theme.ThemeAgentImpl::THEME_STORAGE, id);
        }

        if ($wnd["IDE3"]) {
            $wnd["IDE3"].theme = id;
        }
    }-*/;

}
