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
package org.exoplatform.ide.client.messages;

import com.google.gwt.i18n.client.Messages;

/**
 * Interface to represent the constants contained in resource bundle: 'IdeEditorLocalizationConstant.properties'.
 * <p/>
 * Localization message for forms, that associated with editor.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdePreferencesLocalizationConstant.java Jun 3, 2011 12:58:29 PM vereshchaka $
 */
public interface IdeEditorLocalizationConstant extends Messages {
    /*
     * EditorsListGrid
     */
    @Key("editorsListGrid.column.editors")
    String editorsListGridEditorsColumn();

    @Key("editorListGrid.default")
    String editorsListGridDefault();

    /*
     * EditorFactory
     */
    @Key("codeMirror.textEditor")
    String codeMirrorTextEditor();

    /*
     * GoToLineForm
     */
    @Key("goToLine.title")
    String goToLineTitle();

    @Key("goToLine.goButton")
    String goToLineGoButton();

    /*
     * GoToLinePresenter
     */
    @Key("goToLine.label.enterLineNumber")
    String goToLineLabelEnterLineNumber(int lineNumber);

    /*
     * FindTextView
     */
    @Key("findText.title")
    String findTextTitle();

    @Key("findText.textField.find")
    String findTextFind();

    @Key("findText.textField.replaceWith")
    String findTextReplaceWith();

    @Key("findText.checkbox.caseSensitive")
    String findTextCaseSensitive();

    @Key("findText.button.find")
    String findTextFindButton();

    @Key("findText.button.replace")
    String findTextReplaceButton();

    @Key("findText.button.replaceFind")
    String findTextReplaceFindButton();

    @Key("findText.button.replaceAll")
    String findTextReplaceAllButton();

    @Key("findText.searchStatusLabel")
    String findTextSearchStatusLabel();

    @Key("findText.fieldValue.someText")
    String findTextFieldValueSomeText();

    @Key("findText.fieldValue.replateText")
    String findTextFieldValueReplaceText();

    /*
     * EditorController
     */
    @Key("editorController.dialogTitle.closeFile")
    String editorControllerAskCloseFile();

    @Key("editorController.fileIsReadOnly")
    String editorControllerFileIsReadOnly();

    @Key("editorController.fileTab.sourceView")
    String editorControllerFileTabSourceView();

    @Key("editorController.fileTab.designView")
    String editorControllerFileTabDesignView();

}
