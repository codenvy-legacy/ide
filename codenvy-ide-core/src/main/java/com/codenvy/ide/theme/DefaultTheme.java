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
public class DefaultTheme implements Theme{

    public static final String DEFAULT_THEME_ID = "DefaultTheme";

    private static String tabBorderColor = "#bdbdbd";

    private static String inactiveTabBackground = "#d6d6d6";

    private static String activeTabBackground = "#ffffff";

    private static String tabsPanelBackground = "#f6f6f6";

    private static String highlightFocus = "#92c1f0";

    private static String hoverBackgroundColor = "#D4E8FF";

    private static String lightTextColor = "#999";

    private static String lightestTextColor = "#BCBCBC";

    private static String lightGreyBackground = "#F1F1F1";

    private static String barBackgroundColor = "#f5f5f5";

    private static String completedBackgroundColor = "#f9edbe";

    private static String keyboardSelectionBackgroundColor = "#C3DEFF";

    private static String anchorColor = "#1155CC";

    private static String hoverBackground = "#D4E8FF";

    private static String selectionBackground = "#C3DEFF";
    private String partBackground = "#ffffff";

    @Override
    public String getId() {
        return DEFAULT_THEME_ID;
    }

    @Override
    public String getDescription() {
        return "Default theme";
    }

    @Override
    public String getHoverBackgroundColor() {
        return hoverBackground;
    }

    @Override
    public String getLightTextColor() {
        return lightTextColor;
    }

    @Override
    public String getLightestTextColor() {
        return lightestTextColor;
    }

    @Override
    public String getLightGreyBackground() {
        return lightGreyBackground;
    }

    @Override
    public String getBarBackgroundColor() {
        return barBackgroundColor;
    }

    @Override
    public String getCompletedBackgroundColor() {
        return completedBackgroundColor;
    }

    @Override
    public String getKeyboardSelectionBackgroundColor() {
        return keyboardSelectionBackgroundColor;
    }

    @Override
    public String getAnchorColor() {
        return anchorColor;
    }

    @Override
    public String getHoverBackground() {
        return hoverBackgroundColor;
    }

    @Override
    public String getSelectionBackground() {
        return selectionBackground;
    }

    @Override
    public String getInactiveTabBackground() {
        return inactiveTabBackground;
    }

    @Override
    public String getActiveTabBackground() {
        return activeTabBackground;
    }

    @Override
    public String getTabsPanelBackground() {
        return tabsPanelBackground;
    }

    @Override
    public String getHighlightFocus() {
        return highlightFocus;
    }

    @Override
    public String getTabBorderColor() {
        return tabBorderColor;
    }

    @Override
    public String getPartBackground() {
        return partBackground;
    }

    @Override
    public String getPartToolbar() {
        return "#D6D6D6";
    }

    @Override
    public String getPartToolbarActive() {
        return "rgba(195,222,255,1)";
    }

    @Override
    public String getPartToolbarShadow() {
        return getTabBorderColor();
    }

    @Override
    public String getMainFontColor() {
        return "#222222";
    }

    @Override
    public String getDisabledMenuColor() {
        return "#AAAAAA";
    }

    @Override
    public String getDialogContentBackground() {
        return "#FFFFFF";
    }

    @Override
    public String getButtonTopColor() {
        return "#D6D6D6";
    }

    @Override
    public String getButtonColor() {
        return "#D6D6D6";
    }

    @Override
    public String getInputBackground() {
        return "#FFFFFF";
    }
}
