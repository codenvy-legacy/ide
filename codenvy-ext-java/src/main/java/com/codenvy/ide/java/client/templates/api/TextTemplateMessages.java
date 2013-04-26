/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.java.client.templates.api;

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
