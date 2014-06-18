/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.templates.api;

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
