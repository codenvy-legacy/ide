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
import com.google.inject.Singleton;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class LightTheme implements Theme {

    public static final String LIGHT_THEME_ID = "LightTheme";

    @Override
    public String getId() {
        return LIGHT_THEME_ID;
    }

    @Override
    public String getDescription() {
        return "Light theme";
    }

    @Override
    public String getHoverBackgroundColor() {
        return "#D4E8FF";
    }

    @Override
    public String getKeyboardSelectionBackgroundColor() {
        return "#C3DEFF";
    }

    @Override
    public String getSelectionBackground() {
        return "#C3DEFF";
    }

    @Override
    public String getInactiveTabBackground() {
        return "#d6d6d6";
    }

    @Override
    public String getInactiveTabBorderColor() {
        return "#353535";
    }

    @Override
    public String getActiveTabBackground() {
        return "#ffffff";
    }

    @Override
    public String getActiveTabBorderColor() {
        return "#232323";
    }

    public String getTabFontColor() {
        return "#989898";
    }

    @Override
    public String getTabsPanelBackground() {
        return "#f6f6f6";
    }

    @Override
    public String getTabBorderColor() {
        return "#bdbdbd";
    }

    @Override
    public String getPartBackground() {
        return "#ffffff";
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
    public String getPartToolbarSeparatorTopColor() {
        return "#FFFFFF";
    }

    @Override
    public String getPartToolbarSeparatorBottomColor() {
        return "#AAAAAA";
    }

    @Override
    public String getMainFontColor() {
        return "#222222";
    }

    @Override
    public String getRadioButtonBackgroundColor() {
        return "#BDBDBD";
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

    @Override
    public String getEditorBackgroundColor() {
        return "white";
    }

    @Override
    public String getEditorCurrentLineColor() {
        return "#E8F2FF";
    }

    @Override
    public String getEditorDefaultFontColor() {
        return "black";
    }

    @Override
    public String getEditorSelectionColor() {
        return "#d4e2ff";
    }

    @Override
    public String getEditorInactiveSelectionColor() {
        return "#d4d4d4";
    }

    @Override
    public String getEditorCursorColor() {
        return "black";
    }

    @Override
    public String getEditorGutterColor() {
        return "#eee";
    }

    @Override
    public String getEditorKeyWord() {
        return "#708";
    }

    @Override
    public String getEditorAtom() {
        return "#219";
    }

    @Override
    public String getEditorNumber() {
        return "#164";
    }

    @Override
    public String getEditorDef() {
        return "#00f";
    }

    @Override
    public String getEditorVariable() {
        return "black";
    }

    @Override
    public String getEditorVariable2() {
        return "#05a";
    }

    @Override
    public String getEditorProperty() {
        return "black";
    }

    @Override
    public String getEditorOperator() {
        return "black";
    }

    @Override
    public String getEditorComment() {
        return "#a50";
    }

    @Override
    public String getEditorString() {
        return "#a11";
    }

    @Override
    public String getEditorMeta() {
        return "#049";
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
        return "#170";
    }

    @Override
    public String getEditorAttribute() {
        return "#00c";
    }

    @Override
    public String getEditorString2() {
        return "#f50";
    }

    @Override
    public String getCompletionPopupBackgroundColor() {
        return "#303030";
    }

    @Override
    public String getCompletionPopupBorderColor() {
        return "#484848";
    }
    
    @Override
    public String getWindowContentBackground() {
        return getDialogContentBackground();
    }

    @Override
    public String getWindowHeaderBackground() {
        return getTabBorderColor();
    }
    
    @Override
    public String getWindowSeparatorColor() {
        return "#818181";
    }

    @Override
    public String getWizardStepsColor() {
        return "#DBDBDB";
    }

    @Override
    public String getWizardStepsBorderColor() {
        return "#BDBDBD";
    }

    @Override
    public String getWelcomeFontColor() {
        return "#5E5E5E";
    }

    @Override
    public String getConsolePanelColor() {
        return getTabsPanelBackground();
    }

    @Override
    public String getCellOddRowColor() {
        return "#f3f7fb";
    }

    @Override
    public String getCellOddEvenColor() {
        return "#ffffff";
    }

    @Override
    public String getCellKeyboardSelectedRowColor() {
        return "#ffc";
    }

    @Override
    public String getCellHoveredRow() {
        return "#eee";
    }

    @Override
    public String getMainMenuBkgColor() {
        return "#f6f6f6";
    }

    @Override
    public String getMainMenuFontColor() {
        return "#222222";
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
        return "rgba(198, 205, 209, 0.5)";
    }

    @Override
    public String getButtonTextShadow() {
        return "rgba(188, 195, 199, 0.5)";
    }

    @Override
    public String getTreeTextFileColor() {
        return "#7b7b7b";
    }

    @Override
    public String getTreeTextFolderColor() {
        return "#606060";
    }

    @Override
    public String getTreeTextShadow() {
        return "rgba(255, 255, 255, 0.5)";
    }
    
    @Override
    public String getTreeIconFileColor() {
        return "#606060";
    }
    
    @Override
    public String getBoxBorderColor() {
        return "#aaaaaa";
    }

    @Override
    public String getBoxTextColor() {
        return "#606060";
    }


    @Override
    public String getBoxInnerShadow() {
        return "0px 1px 2px 0px #777777 inset";
    }

    @Override
    public String getBoxOuterShadow() {
        return "0px 1px 0px 0px #7a7a7a";
    }

    @Override
    public String getBoxBackgroundColor() {
        return "#e2e2e2";
    }

    @Override
    public String getBoxBackgroundColorDisabled() {
        return getTabsPanelBackground();
    }

    @Override
    public String getBoxTextShadow() {
        return "0px 1px 0px #ffffff";
    }

    public String getButtonHoverTextColor() {
        return "#000000";
    }

    @Override
    public String getToolbarBackgroundColor() {
        return " #EAEAEA";
    }

    @Override
    public String getToolbarIconColor() {
        return "#606060";
    }

    @Override
    public String getProgressColor() {
        return "#000000";
    }

    @Override
    public String getSvgUrlShadow() {
        return "url(#shadowLightTheme)";
    }
    
    @Override
    public String getSuccessEventColor() {
        return "#7dc878";
    }
    
    @Override
    public String getErrorEventColor() {
        return "#e25252";
    }
    
    @Override
    public String getEventDelimeterColor() {
        return "#2f2f2f";
    }
    
    @Override
    public String getLinkColor() {
        return "#acacac";
    }
    
    @Override
    public String getMinimizeIconColor() {
        return "#7b7b7b";
    }
}