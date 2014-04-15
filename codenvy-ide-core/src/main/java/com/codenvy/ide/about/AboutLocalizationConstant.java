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
package com.codenvy.ide.about;

import com.google.gwt.i18n.client.Messages;


/**
 * Localization for About Codenvy dialog.
 * 
 * @author Ann Shumilova
 */
public interface AboutLocalizationConstant extends Messages {
    @Key("about.view.title")
    String aboutViewTitle();

    @Key("about.version")
    String aboutVersion();

    @Key("about.revision")
    String aboutRevision();

    @Key("about.buildtime")
    String aboutBuildTime();

    @Key("about.control.title")
    String aboutControlTitle();
}
