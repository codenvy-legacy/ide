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
import com.google.inject.Singleton;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class DarkTheme implements Theme {

    public static final String DARK_THEME_ID = "DarkTheme";

    @Override
    public String getId() {
        return DARK_THEME_ID;
    }

    @Override
    public String getDescription() {
        return "Dark Theme";
    }

    @Override
    public String getHoverBackgroundColor() {
        return "#D4E8FF";
    }


    @Override
    public String getKeyboardSelectionBackgroundColor() {
        return "#2f65ca";
    }

    @Override
    public String getSelectionBackground() {
        return "#256c9f";
    }

    @Override
    public String getInactiveTabBackground() {
        return "#484848";
    }

    @Override
    public String getInactiveTabBorderColor() {
        return "#353535";
    }

    @Override
    public String getActiveTabBackground() {
        return "#5a5c5c";
    }

    @Override
    public String getActiveTabBorderColor() {
        return "#232323";
    }

    @Override
    public String getTabFontColor() {
        return "#989898";
    }

    @Override
    public String getTabsPanelBackground() {
        return "#5a5c5c";
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
    public String getPartToolbarSeparatorTopColor() {
        return "#232323";
    }

    @Override
    public String getPartToolbarSeparatorBottomColor() {
        return "#878787";
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
        return "#373737";
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
        return "#256c9f";
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
        return "#0ab";
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
        return "#6AAF32";
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
    public String getCompletionPopupBackgroundColor() {
        return "#303030";
    }

    @Override
    public String getCompletionPopupBorderColor() {
        return "#484848";
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
    public String getWindowSeparatorColor() {
        return "#818181";
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
    public String getCaptionFontColor() {
        return "#888888";
    }

    @Override
    public String getFactoryLinkColor() {
        return "#60abe0";
    }

    @Override
    public String getConsolePanelColor() {
        return "#313131";
    }

    @Override
    public String getStatusPanelColor() {
        return "#404040";
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

    @Override
    public String getButtonTextShadow() {
        return "rgba(0, 0, 0, 1)";
    }

    @Override
    public String getTreeTextFileColor() {
        return "#dbdbdb";
    }

    @Override
    public String getTreeTextFolderColor() {
        return "#b4b4b4";
    }

    @Override
    public String getTreeTextShadow() {
        return "rgba(0, 0, 0, 0.5)";
    }

    @Override
    public String getTreeIconFileColor() {
        return "#b4b4b4";
    }

    @Override
    public String getBoxBorderColor() {
        return "#191c1e";
    }

    @Override
    public String getBoxTextColor() {
        return "#dbdbdb"; // same as main editor color ATM for dark
    }

    @Override
    public String getBoxInnerShadow() {
        return "0px 1px 2px 0px rgba(0, 0, 0, 0.19) inset";
    }

    @Override
    public String getBoxOuterShadow() {
        return "0px 1px 0px 0px rgba(223, 223, 223, 0.4)";
    }

    @Override
    public String getBoxBackgroundColor() {
        return getPartBackground();
    }

    @Override
    public String getBoxBackgroundColorDisabled() {
        return getTabsPanelBackground();
    }

    @Override
    public String getBoxTextShadow() {
        return "0px 1px 0px rgba(46, 46, 46, 0.5)";
    }

    @Override
    public String getButtonHoverTextColor() {
        return "#ffffff";
    }

    @Override
    public String getButtonHoverColor() {
        return "#484848";
    }

    @Override
    public String getSocialButtonColor() {
        return "#ffffff";
    }

    @Override
    public String getToolbarBackgroundColor() {
        return "#484848";
    }

    @Override
    public String getToolbarIconColor() {
        return "#dbdbdb";
    }

    @Override
    public String getTooltipBackgroundColor() { return "#202020";}

    @Override
    public String getProgressColor() {
        return "#ffffff";
    }

    @Override
    public String getSvgUrlShadow() {
        return "url(#shadowDarkTheme)";
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
    public String getDelimeterColor() {
        return "#2f2f2f";
    }

    @Override
    public String getLinkColor() {
        return "#acacac";
    }

    @Override
    public String getMinimizeIconColor() {
        return "#dbdbdb";
    }

    @Override
    public String getOutputFontColor() {
        return "#e6e6e6";
    }

    @Override
    public String getOutputLinkColor() {
        return "#61b7ef";
    }

    @Override
    public String getEditorInfoBackgroundColor() {
        return "#313335";
    }

    @Override
    public String getEditorInfoBorderColor() {
        return "#282828";
    }

    @Override
    public String getEditorInfoBorderShadowColor() {
        return "#424242";
    }

    @Override
    public String getEditorLineNumberColor() {
        return "#888888";
    }

    @Override
    public String getEditorSeparatorColor() {
        return "#888888";
    }
}
