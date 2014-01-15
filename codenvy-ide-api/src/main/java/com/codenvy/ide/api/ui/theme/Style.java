/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
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

/**
 * This class contains constants for style. Fields initialized from user preferences.
 * Static methods used for bridging with CssResources
 * @author Evgen Vidolob
 */
public class Style {

    private static Theme theme;

    public static void setTheme(Theme theme) {
        Style.theme = theme;
    }

    public static String getHoverBackgroundColor() {
        return theme.getHoverBackgroundColor();
    }

    public static String getLightTextColor() {
        return theme.getLightTextColor();
    }

    public static String getLightestTextColor() {
        return theme.getLightestTextColor();
    }

    public static String getLightGreyBackground() {
        return theme.getLightGreyBackground();
    }

    public static String getBarBackgroundColor() {
        return theme.getBarBackgroundColor();
    }

    public static String getCompletedBackgroundColor() {
        return theme.getCompletedBackgroundColor();
    }

    public static String getKeyboardSelectionBackgroundColor() {
        return theme.getKeyboardSelectionBackgroundColor();
    }

    public static String getAnchorColor() {
        return theme.getAnchorColor();
    }

    public static String getHoverBackground() {
        return theme.getHoverBackground();
    }

    public static String getSelectionBackground() {
        return theme.getSelectionBackground();
    }

    public static String getInactiveTabBackground() {
        return theme.getInactiveTabBackground();
    }

    public static String getActiveTabBackground() {
        return theme.getActiveTabBackground();
    }

    public static String getTabsPanelBackground() {
        return theme.getTabsPanelBackground();
    }

    public static String getHighlightFocus() {
        return theme.getHighlightFocus();
    }

    public static String getTabBorderColor() {
        return theme.getTabBorderColor();
    }

    public static String getPartBackground(){
        return theme.getPartBackground();
    }

    public static String getPartToolbar(){
        return theme.getPartToolbar();
    }

    public static String getPartToolbarActive(){
        return theme.getPartToolbarActive();
    }

    public static String getPartToolbarShadow() {
        return theme.getPartToolbarShadow();
    }

    public static String getMainFontColor() {
        return theme.getMainFontColor();
    }

    public static String getDeisabledMenuColor(){
        return theme.getDisabledMenuColor();
    }

    public static String getDialogContentBackground(){
        return theme.getDialogContentBackground();
    }

    public static String getButtonTopColor() {
        return theme.getButtonTopColor();
    }

    public static String getButtonColor() {
        return theme.getButtonColor();
    }

    public static String getInputBackground(){
        return theme.getInputBackground();
    }

}
