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
 * Interface to represent the constants contained in resource bundle: 'IdeOverwriteLocalizationConstant.properties'.
 * <p/>
 * Localization message for overwrite dialog.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdeOverwriteLocalizationConstant.java Nov 11, 2011 10:51:24 AM vereshchaka $
 */
public interface IdeOverwriteLocalizationConstant extends Messages {

    /*
     * Buttons
     */
    @Key("button.overwrite")
    String overwriteButton();

    @Key("button.rename")
    String renameButton();

    /*
     * OverwriteDialog
     */
    @Key("dialog.title")
    String dialogTitle();

    @Key("dialog.text.fileExists")
    String dialogTextFileExists();

    @Key("dialog.text.ask")
    String dialogAskToOverwrite();

    @Key("dialog.renameField.lable")
    String dialogRenameFieldLable();

}
