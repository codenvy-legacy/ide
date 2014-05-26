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
package com.codenvy.ide.ext.java.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Localization constants. Interface to represent the constants defined in resource bundle:
 * 'JavaLocalizationConstant.properties'.
 *
 * @author Artem Zatsarynnyy
 */
public interface JavaLocalizationConstant extends Messages {
    /* NewJavaClassView */
    @Key("title")
    String title();

    @Key("ok")
    String buttonOk();

    @Key("cancel")
    String buttonCancel();

    /* Actions */
    @Key("action.newClass.id")
    String actionNewClassId();

    @Key("action.newClass.title")
    String actionNewClassTitle();

    @Key("action.newClass.description")
    String actionNewClassDescription();

    @Key("action.newPackage.id")
    String actionNewPackageId();

    @Key("action.newPackage.title")
    String actionNewPackageTitle();

    @Key("action.newPackage.description")
    String actionNewPackageDescription();

}
