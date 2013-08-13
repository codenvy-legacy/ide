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
package com.codenvy.ide.ext.java.client.templates.api;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 11:29:34 AM 34360 2009-07-22 23:58:59Z evgen $
 */
public interface TextTemplateMessages extends Messages {
    TextTemplateMessages MESSAGES = GWT.create(TextTemplateMessages.class);

    @Key("TemplateTranslator.error.incomplete.variable")
    String errorIncompleteVariable();

    @Key("TemplateTranslator.error.invalid.identifier")
    String errorInvalidIdentifier();

    @Key("TemplateTranslator.error.incompatible.type")
    String errorIncompatibleTtype(String type);

    @Key("GlobalVariables.variable.description.cursor")
    String variableDescriptionCursor();

    @Key("GlobalVariables.variable.description.dollar")
    String variableDescriptionDollar();

    @Key("GlobalVariables.variable.description.date")
    String variableDescriptionDate();

    @Key("GlobalVariables.variable.description.year")
    String variableDescriptionYear();

    @Key("GlobalVariables.variable.description.time")
    String variableDescriptionTime();

    @Key("GlobalVariables.variable.description.user")
    String variableDescriptionUser();

    @Key("GlobalVariables.variable.description.selectedWord")
    String variableDescriptionSelectedWord();

    @Key("GlobalVariables.variable.description.selectedLines")
    String variableDescriptionSelectedLines();

}
