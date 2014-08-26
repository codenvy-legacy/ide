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
package com.codenvy.ide.jseditor.client;

import com.google.gwt.i18n.client.Constants;

/**
 * I18n Constants for the JsEditor module.
 * 
 * @author "Mickaël Leduque"
 */
public interface JsEditorConstants extends Constants {

    @DefaultStringValue("Default Editor")
    String defaultEditorDescription();

    @DefaultStringValue("Classic")
    String classicEditorDisplayName();

    @DefaultStringValue("Unidentified file")
    String infoPanelUnknownFileType();

    // space is meaningful
    @DefaultStringValue("Line ")
    String infoPaneLineLabel();

    // spaces and comma are meaningful
    @DefaultStringValue(", Char ")
    String infoPanelCharacterLabel();

    // space is meaningful
    @DefaultStringValue("Tab Size: ")
    String infoPaneTabSizeLabel();

    @DefaultStringValue("Editor: ")
    String infoPaneEditorLabel();

    @DefaultStringValue("Key bindings: ")
    String infoPaneKeybindingLabel();

    @DefaultStringValue("Unknown")
    String infoPanelUnknownEditorType();

    @DefaultStringValue("Unknown")
    String infoPanelUnknownKeybindings();

}
