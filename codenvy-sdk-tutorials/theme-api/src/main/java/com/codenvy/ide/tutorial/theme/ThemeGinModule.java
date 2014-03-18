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
package com.codenvy.ide.tutorial.theme;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.api.ui.theme.Theme;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.multibindings.GinMultibinder;

/**
 * @author Evgen Vidolob
 */
@ExtensionGinModule
public class ThemeGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        GinMultibinder<Theme> themeBinder = GinMultibinder.newSetBinder(binder(), Theme.class);
        themeBinder.addBinding().to(DarkThemeExt.class);
    }
}
