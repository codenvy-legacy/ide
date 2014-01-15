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

/**
 * @author Evgen Vidolob
 */
public class DarkTheme implements Theme {

    public static final String DARK_THEME_ID = "DarkTheme";

    @Override
    public String getId() {
        return DARK_THEME_ID;
    }

    @Override
    public String getDescription() {
        return "Dark theme";
    }

    @Override
    public String getHoverBackgroundColor() {
        return "#D4E8FF";
    }

    @Override
    public String getLightTextColor() {
        return "#999";
    }

    @Override
    public String getLightestTextColor() {
        return "#BCBCBC";
    }

    @Override
    public String getLightGreyBackground() {
        return "#F1F1F1";
    }

    @Override
    public String getBarBackgroundColor() {
        return "#f5f5f5";
    }

    @Override
    public String getCompletedBackgroundColor() {
        return "#f9edbe";
    }

    @Override
    public String getKeyboardSelectionBackgroundColor() {
        return "#2f65ca";
    }

    @Override
    public String getAnchorColor() {
        return "#1155CC";
    }

    @Override
    public String getHoverBackground() {
        return "#86acc9";
    }

    @Override
    public String getSelectionBackground() {
        return "#2f65ca";
    }

    @Override
    public String getInactiveTabBackground() {
        return "#5a5c5c";
    }

    @Override
    public String getActiveTabBackground() {
        return "#2d2f30";
    }

    @Override
    public String getTabsPanelBackground() {
        return "#5a5c5c";
    }

    @Override
    public String getHighlightFocus() {
        return "#92c1f0";
    }

    @Override
    public String getTabBorderColor() {
        return "#1f1f1f";
    }

    @Override
    public String getPartBackground() {
        return "#474747";
    }

    @Override
    public String getPartToolbar() {
        return "#656565";
    }

    @Override
    public String getPartToolbarActive() {
        return "414c5e";
    }

    @Override
    public String getPartToolbarShadow() {
        return "rgba(50,50,50, 0.75)";
    }

    @Override
    public String getMainFontColor() {
        return "#dbdbdb";
    }

    @Override
    public String getDisabledMenuColor() {
        return "#808080";
    }

    @Override
    public String getDialogContentBackground() {
        return "#656565";
    }

    @Override
    public String getButtonTopColor() {
        return "#646464";
    }

    @Override
    public String getButtonColor() {
        return "#515151";
    }

    @Override
    public String getInputBackground() {
        return "#272727";
    }
}
