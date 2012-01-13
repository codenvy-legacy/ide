/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
 * 
 */
public interface IdeEditorLocalizationConstant extends Messages
{
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
