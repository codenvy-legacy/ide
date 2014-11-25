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
package com.codenvy.ide.jseditor.client.preference;

import com.google.gwt.i18n.client.Messages;

/**
 * I18n Constants for the preference window.
 *
 * @author "Mickaël Leduque"
 */
public interface EditorPrefLocalizationConstant extends Messages {

    @Key("editortype.title")
    String editorTypeTitle();

    @Key("editortype.category")
    String editorTypeCategory();

    @DefaultMessage("Editor preferences could not be saved.")
    String flushError();

    @DefaultMessage("Editor preferences saved.")
    String flushSuccess();

    @DefaultMessage("Editors")
    String editorsMappingSectionLabel();

    @DefaultMessage("Key Bindings")
    String keybindingsSectionLabel();

    @DefaultMessage("Default Editor")
    String defaultEditorLabel();
}
