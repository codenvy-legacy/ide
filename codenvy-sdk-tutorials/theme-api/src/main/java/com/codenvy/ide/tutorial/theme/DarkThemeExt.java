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

import com.codenvy.ide.theme.DarkTheme;

/**
 * @author Evgen Vidolob
 */
public class DarkThemeExt extends DarkTheme {

    @Override
    public String getId() {
        return "new theme id";
    }

    @Override
    public String getDescription() {
        return "New extended dark theme";
    }

    @Override
    public String getMainFontColor() {
        return "red";
    }

    @Override
    public String getPartBackground() {
        return "white";
    }

    @Override
    public String getTabsPanelBackground() {
        return "white";
    }
}
