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
package com.codenvy.ide.api.ui.theme;

/**
 *
 * @author Evgen Vidolob
 */
public interface Theme {

    /**
     * @return the theme id
     */
    String getId();

    /**
     * @return the description of the theme
     */
    String getDescription();

    String getHoverBackgroundColor();

    String getLightTextColor();

    String getLightestTextColor();

    String getLightGreyBackground();

    String getBarBackgroundColor();

    String getCompletedBackgroundColor();

    String getKeyboardSelectionBackgroundColor();

    String getAnchorColor();

    String getHoverBackground();

    String getSelectionBackground();

    String getInactiveTabBackground();

    String getActiveTabBackground();

    String getTabsPanelBackground();

    String getHighlightFocus();

    String getTabBorderColor();

    String getPartBackground();

    String getPartToolbar();

    String getPartToolbarActive();

    String getPartToolbarShadow();

    String getMainFontColor();

    String getDisabledMenuColor();

    String getDialogContentBackground();

    String getButtonTopColor();

    String getButtonColor();

    String getInputBackground();

    String getEditorBackgroundColor();

    String getEditorCurrentLineColor();

    String getEditorDefaultFontColor();

    String getEditorSelectionColor();

    String getEditorInactiveSelectionColor();

    String getEditorCursorColor();

    String getEditorGutterColor();

    String getEditorKeyWord();

    String getEditorAtom();

    String getEditorNumber();

    String getEditorDef();

    String getEditorVariable();

    String getEditorVariable2();

    String getEditorProperty();

    String getEditorOperator();

    String getEditorComment();

    String getEditorString();

    String getEditorMeta();

    String getEditorError();

    String getEditorBuiltin();

    String getEditorTag();

    String getEditorAttribute();

    String getEditorString2();

    String getWindowContentBackground();

    String getWindowHeaderBackground();
}
