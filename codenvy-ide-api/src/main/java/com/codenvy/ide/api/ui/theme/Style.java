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
 * This class contains constants for style. Fields initialized from user preferences. Static methods used for bridging with CssResources
 * 
 * @author Evgen Vidolob
 */
public class Style {

    public static Theme theme;

    public static void setTheme(Theme theme) {
        Style.theme = theme;
    }

    public static String getHoverBackgroundColor() {
        return theme.getHoverBackgroundColor();
    }

    public static String getKeyboardSelectionBackgroundColor() {
        return theme.getKeyboardSelectionBackgroundColor();
    }

    public static String getSelectionBackground() {
        return theme.getSelectionBackground();
    }

    public static String getInactiveTabBackground() {
        return theme.getInactiveTabBackground();
    }

    public static String getInactiveTabBorderColor() {
        return theme.getInactiveTabBorderColor();
    }

    public static String getActiveTabBackground() {
        return theme.getActiveTabBackground();
    }

    public static String getActiveTabBorderColor() {
        return theme.getActiveTabBorderColor();
    }

    public static String getTabsPanelBackground() {
        return theme.getTabsPanelBackground();
    }

    public static String getTabBorderColor() {
        return theme.getTabBorderColor();
    }

    public static String getPartBackground() {
        return theme.getPartBackground();
    }

    public static String getPartToolbar() {
        return theme.getPartToolbar();
    }

    public static String getPartToolbarActive() {
        return theme.getPartToolbarActive();
    }

    public static String getPartToolbarShadow() {
        return theme.getPartToolbarShadow();
    }

    public static String getMainFontColor() {
        return theme.getMainFontColor();
    }

    public static String getRadioButtonBackgroundColor() {
        return theme.getRadioButtonBackgroundColor();
    }

    public static String getDeisabledMenuColor() {
        return theme.getDisabledMenuColor();
    }

    public static String getDialogContentBackground() {
        return theme.getDialogContentBackground();
    }

    public static String getButtonTopColor() {
        return theme.getButtonTopColor();
    }

    public static String getButtonColor() {
        return theme.getButtonColor();
    }

    public static String getNotableButtonTopColor() {
        return theme.getNotableButtonTopColor();
    }

    public static String getNotableButtonColor() {
        return theme.getNotableButtonColor();
    }

    public static String getInputBackground() {
        return theme.getInputBackground();
    }

    public static String getEditorBackgroundColor() {
        return theme.getEditorBackgroundColor();
    }

    public static String getEditorCurrentLineColor() {
        return theme.getEditorCurrentLineColor();
    }

    public static String getEditorDefaultFontColor() {
        return theme.getEditorDefaultFontColor();
    }

    public static String getEditorSelectionColor() {
        return theme.getEditorSelectionColor();
    }

    public static String getEditorInactiveSelectionColor() {
        return theme.getEditorInactiveSelectionColor();
    }

    public static String getEditorCursorColor() {
        return theme.getEditorCursorColor();
    }

    public static String getEditorGutterColor() {
        return theme.getEditorGutterColor();
    }

    // syntax
    public static String getEditorKeyWord() {
        return theme.getEditorKeyWord();
    }

    public static String getEditorAtom() {
        return theme.getEditorAtom();
    }

    public static String getEditorNumber() {
        return theme.getEditorNumber();
    }

    public static String getEditorDef() {
        return theme.getEditorDef();
    }

    public static String getEditorVariable() {
        return theme.getEditorVariable();
    }

    public static String getEditorVariable2() {
        return theme.getEditorVariable2();
    }

    public static String getEditorProperty() {
        return theme.getEditorProperty();
    }

    public static String getEditorOperator() {
        return theme.getEditorOperator();
    }

    public static String getEditorComment() {
        return theme.getEditorComment();
    }

    public static String getEditorString() {
        return theme.getEditorString();
    }

    public static String getEditorString2() {
        return theme.getEditorString2();
    }

    public static String getEditorMeta() {
        return theme.getEditorMeta();
    }

    public static String getEditorError() {
        return theme.getEditorError();
    }

    public static String getEditorBuiltin() {
        return theme.getEditorBuiltin();
    }

    public static String getEditorTag() {
        return theme.getEditorTag();
    }

    public static String getEditorAttribute() {
        return theme.getEditorAttribute();
    }

    public static String getWindowContentBackground() {
        return theme.getWindowContentBackground();
    }

    public static String getWindowHeaderBackground() {
        return theme.getWindowHeaderBackground();
    }

    public static String getWizardStepsColor() {
        return theme.getWizardStepsColor();
    }

    public static String getWizardStepsBorderColor() {
        return theme.getWizardStepsBorderColor();
    }

    public static String getWelcomeFontColor() {
        return theme.getWelcomeFontColor();
    }

    public static String getConsolePanelColor() {
        return theme.getConsolePanelColor();
    }

    public static String getCellOddRow() {
        return theme.getCellOddRowColor();
    }

    public static String getCellEvenRow() {
        return theme.getCellOddEvenColor();
    }

    public static String getCellKeyboardSelectedRow() {
        return theme.getCellKeyboardSelectedRowColor();
    }

    public static String getCellHoveredRow() {
        return theme.getCellHoveredRow();
    }

    public static String getMainMenuBkgColor() {
        return theme.getMainMenuBkgColor();
    }

    public static String getMainMenuFontColor() {
        return theme.getMainMenuFontColor();
    }

    public static String getTabBorderShadow() {
        return theme.getTabBorderShadow();
    }

    public static String getButtonTextShadow() {
        return theme.getButtonTextShadow();
    }

    public static String getTreeTextFileColor() {
        return theme.getTreeTextFileColor();
    }

    public static String getTreeTextFolderColor() {
        return theme.getTreeTextFolderColor();
    }

    public static String getTreeTextShadow() {
        return theme.getTreeTextShadow();
    }

    public static String getBoxBorderColor() {
        return theme.getBoxBorderColor();
    }

    public static String getBoxTextColor() {
        return theme.getBoxTextColor();
    }

    public static String getBoxInnerShadow() {
        return theme.getBoxInnerShadow();
    }

    public static String getBoxOuterShadow() {
        return theme.getBoxOuterShadow();
    }

    public static String getBoxBackgroundColor() {
        return theme.getBoxBackgroundColor();
    }

    public static String getBoxBackgroundColorDisabled() {
        return theme.getBoxBackgroundColorDisabled();
    }

    public static String getBoxTextShadow() {
        return theme.getBoxTextShadow();
    }
}
