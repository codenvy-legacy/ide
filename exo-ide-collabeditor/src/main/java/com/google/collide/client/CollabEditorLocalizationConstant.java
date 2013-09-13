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
package com.google.collide.client;

/**
 * Interface to represent the constants contained in resource bundle: 'CollabEditorLocalizationConstant.properties'.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: CollabEditorLocalizationConstant.java Feb 6, 2013 3:04:53 PM azatsarynnyy $
 */
public interface CollabEditorLocalizationConstant extends com.google.gwt.i18n.client.Messages {

    // Controls
    @Key("control.collaborators.id")
    String collaboratorsControlId();

    @Key("control.collaborators.title")
    String collaboratorsControlTitle();

    @Key("control.collaborators.prompt.show")
    String collaboratorsControlPromptShow();

    @Key("control.collaborators.prompt.hide")
    String collaboratorsControlPromptHide();

}
