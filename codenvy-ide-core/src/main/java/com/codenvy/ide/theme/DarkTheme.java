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
        return "#484848";
    }

    @Override
    public String getActiveTabBackground() {
        return "#5a5c5c";
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
        return "#232323";
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
        return "#414c5e";
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
    public String getRadioButtonBackgroundColor() {
        return "#BDBDBD";
    }

    @Override
    public String getButtonColor() {
        return "#515151";
    }

    @Override
    public String getInputBackground() {
        return "#272727";
    }

    @Override
    public String getEditorBackgroundColor() {
        return "#383838";
    }

    @Override
    public String getEditorCurrentLineColor() {
        return "#424242";
    }

    @Override
    public String getEditorDefaultFontColor() {
        return "#A9B7C6";
    }

    @Override
    public String getEditorSelectionColor() {
        return "#214283";
    }

    @Override
    public String getEditorInactiveSelectionColor() {
        return "#d4d4d4";
    }

    @Override
    public String getEditorCursorColor() {
        return getEditorDefaultFontColor();
    }

    @Override
    public String getEditorGutterColor() {
        return "#313335";
    }

    @Override
    public String getEditorKeyWord() {
        return "#cc7832";
    }

    @Override
    public String getEditorAtom() {
        return "#9876aa";
    }

    @Override
    public String getEditorNumber() {
        return "#6897bb";
    }

    @Override
    public String getEditorDef() {
        return "#A7E600";
    }

    @Override
    public String getEditorVariable() {
        return getEditorDefaultFontColor();
    }

    @Override
    public String getEditorVariable2() {
        return "#05a";
    }

    @Override
    public String getEditorProperty() {
        return getEditorDefaultFontColor();
    }

    @Override
    public String getEditorOperator() {
        return getEditorDefaultFontColor();
    }

    @Override
    public String getEditorComment() {
        return "#629755";
    }

    @Override
    public String getEditorString() {
        return "#6A8759";
    }

    @Override
    public String getEditorMeta() {
        return "#BBB529";
    }

    @Override
    public String getEditorError() {
        return "#f00";
    }

    @Override
    public String getEditorBuiltin() {
        return "#30a";
    }

    @Override
    public String getEditorTag() {
        return "#E8BF6A";
    }

    @Override
    public String getEditorAttribute() {
        return "rgb(152,118,170)";
    }

    @Override
    public String getEditorString2() {
        return "#CC7832";
    }

    @Override
    public String getWindowContentBackground() {
        return "#373737";
    }

    @Override
    public String getWindowHeaderBackground() {
        return "#656565";
    }

    @Override
    public String getWizardStepsColor() {
        return "#222222";
    }

    @Override
    public String getWizardStepsBorderColor() {
        return "#000000";
    }

    @Override
    public String getWelcomeFontColor() {
        return getMainFontColor();
    }

    @Override
    public String getConsolePanelColor() {
        return "#313131";
    }

    @Override
    public String getCellOddRowColor() {
        return "#424242";
    }

    @Override
    public String getCellOddEvenColor() {
        return "#373737";
    }

    @Override
    public String getCellKeyboardSelectedRowColor() {
        return "#214283";
    }

    @Override
    public String getCellHoveredRow() {
        return getHoverBackgroundColor();
    }

    @Override
    public String getMainMenuBkgColor() {
        return "#404040";
    }

    @Override
    public String getMainMenuFontColor() {
        return "#dbdbdb";
    }

    @Override
    public String getNotableButtonTopColor() {
        return "#dbdbdb";
    }

    @Override
    public String getNotableButtonColor() {
        return "#2d6ba3";
    }

    @Override
    public String getTabBorderShadow() {
        return "rgba(188, 195, 199, 0.5)";
    }
}
