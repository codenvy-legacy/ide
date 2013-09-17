/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.tutorials.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Localization constants. Interface represents the constants contained in resource bundle:
 * 'TutorialsLocalizationConstant.properties'.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: TutorialsLocalizationConstant.java Sep 16, 2013 12:40:17 PM azatsarynnyy $
 */
public interface TutorialsLocalizationConstant extends Messages {
    /* Actions */
    @Key("control.showTutorialPage.id")
    String showTutorialPageActionlId();

    @Key("control.showTutorialPage.text")
    String showTutorialPageActionText();

    @Key("control.showTutorialPage.description")
    String showTutorialPageActionDescription();
}
