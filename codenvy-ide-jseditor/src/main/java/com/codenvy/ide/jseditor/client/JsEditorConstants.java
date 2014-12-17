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

import com.google.gwt.i18n.client.Messages;

/**
 * I18n Constants for the JsEditor module.
 *
 * @author "Mickaël Leduque"
 */
public interface JsEditorConstants extends Messages {

    @DefaultMessage("Default Editor")
    String defaultEditorDescription();

    @DefaultMessage("Unidentified File")
    String infoPanelUnknownFileType();

    // space is meaningful
    @DefaultMessage("Line ")
    String infoPaneLineLabel();

    // spaces and comma are meaningful
    @DefaultMessage(", Char ")
    String infoPanelCharacterLabel();

    // space is meaningful
    @DefaultMessage("Tab Size: ")
    String infoPaneTabSizeLabel();

    @DefaultMessage("Editor: ")
    String infoPaneEditorLabel();

    @DefaultMessage("Key Bindings: ")
    String infoPaneKeybindingLabel();

    @DefaultMessage("Unknown")
    String infoPanelUnknownEditorType();

    @DefaultMessage("Unknown")
    String infoPanelUnknownKeybindings();

    @DefaultMessage("Close")
    String askWindowCloseTitle();

    @DefaultMessage("{0} has been modified. Save changes?")
    String askWindowSaveChangesMessage(String name);

    @DefaultMessage("An error occured while initializing the editor.\nReloading the page may be necessary.")
    String editorInitErrorMessage();

    @DefaultMessage("An error occured while loading the file.")
    String editorFileErrorMessage();

    @DefaultMessage("Finishing editor initialization")
    String waitEditorInitMessage();

    @DefaultMessage("File changed")
    String fileUpdateTitle();

    @DefaultMessage("The content for the file named {0} has been changed on the server.<br>"
                    + " Do you wish to keep your unsaved changes or do you want to overwrite them with changes from the server?")
    String fileUpdateMessage(String path);

    @DefaultMessage("Overwrite")
    String fileUpdateOvewrite();

    @DefaultMessage("Keep my changes")
    String fileUpdateKeepUnsaved();
}
