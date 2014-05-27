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
package com.codenvy.ide.ext.web;

import com.google.gwt.i18n.client.Messages;

/**
 * Localization constants. Interface to represent the constants defined in resource bundle:
 * 'WebLocalizationConstant.properties'.
 *
 * @author Artem Zatsarynnyy
 */
public interface WebLocalizationConstant extends Messages {
    /* Actions */
    @Key("action.newCssFile.id")
    String newCssFileActionId();

    @Key("action.newCssFile.title")
    String newCssFileActionTitle();

    @Key("action.newCssFile.description")
    String newCssFileActionDescription();

    @Key("action.newLessFile.id")
    String newLessFileActionId();

    @Key("action.newLessFile.title")
    String newLessFileActionTitle();

    @Key("action.newLessFile.description")
    String newLessFileActionDescription();

    @Key("action.newHtmlFile.id")
    String newHtmlFileActionId();

    @Key("action.newHtmlFile.title")
    String newHtmlFileActionTitle();

    @Key("action.newHtmlFile.description")
    String newHtmlFileActionDescription();

    @Key("action.newJavaScriptFile.id")
    String newJavaScriptFileActionId();

    @Key("action.newJavaScriptFile.title")
    String newJavaScriptFileActionTitle();

    @Key("action.newJavaScriptFile.description")
    String newJavaScriptFileActionDescription();
}
