/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.jseditor.client.infopanel;

import javax.inject.Inject;

import org.eclipse.che.ide.jseditor.client.JsEditorConstants;
import org.eclipse.che.ide.jseditor.client.editortype.EditorType;
import org.eclipse.che.ide.jseditor.client.editortype.EditorTypeRegistry;
import org.eclipse.che.ide.jseditor.client.keymap.Keymap;
import org.eclipse.che.ide.jseditor.client.keymap.KeymapChangeEvent;
import org.eclipse.che.ide.jseditor.client.keymap.KeymapChangeHandler;
import org.eclipse.che.ide.jseditor.client.text.TextPosition;
import org.eclipse.che.ide.util.loging.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The presenter for the editor info panel.<br>
 * Info panel shows the following things: cursor position, number of lines, tab settings and file type.
 * 
 * @author "Mickaël Leduque"
 */
public class InfoPanel extends Composite implements KeymapChangeHandler {

    private static final char NO_BREAK_SPACE = '\u00A0';

    /** The UI binder instance. */
    private static final InfoPanelUiBinder UIBINDER = GWT.create(InfoPanelUiBinder.class);

    /**
     * Inital budget of characters for line/char info display.
     */
    private static final int CHAR_INITIAL_BUDGET = 6;

    private final EditorTypeRegistry editorTypeRegistry;

    /** The i18n constants. */
    @UiField(provided = true)
    JsEditorConstants constants;

    @UiField
    SpanElement charPosLabel;
    @UiField
    Element     charPosition;
    @UiField
    Element     lineNumber;
    @UiField
    Label       fileType;
    @UiField
    Label       editorTypeValue;
    @UiField
    Label       keybindingsValue;
    @UiField
    Label       tabSize;

    private EditorType editorType;

    @Inject
    public InfoPanel(final JsEditorConstants constants,
                     final EditorTypeRegistry editorTypeRegistry,
                     final EventBus eventBus) {
        this.constants = constants;
        this.editorTypeRegistry = editorTypeRegistry;
        initWidget(UIBINDER.createAndBindUi(this));

        eventBus.addHandler(KeymapChangeEvent.TYPE, this);
    }

    /**
     * Creates an initial state, before actual data is available.
     *
     * @param fileContentDescription the file type
     * @param numberOfLines the file number of lines
     * @param tabSize the space-equivalent width of a tabulation character
     */
    public void createDefaultState(final String fileContentDescription,
                                   final EditorType editorType,
                                   final Keymap keymap,
                                   final int numberOfLines, final int tabSize) {
        setCharPosition(null);
        setLineNumber(numberOfLines);
        setFileType(fileContentDescription);
        setEditorTypeFromInstance(editorType);
        setKeybindingsFromInstance(keymap);
        setTabSize(tabSize);
    }

    /**
     *  Update the line and char display to show a position in the text.
     *  @param position the position in the text
     */
    public void updateCursorPosition(final TextPosition position) {
        setCharPosition(position.getCharacter() + 1);
        setLineNumber(position.getLine() + 1);
    }

    /**
     *  Update the line and char display to show the line count.
     */
    public void displayLineCount(final int linecount) {
        setCharPosition(null);
        setLineNumber(linecount + 1);
    }

    /**
     * Changes the displayed value of the cusor line number.
     *
     * @param lineNum the new value
     * @return the character count of the line number
     */
    private int setLineNumber(final Integer lineNum) {
        String lineString = "";
        if (lineNum != null) {
            lineString = String.valueOf(lineNum);
        }
        this.lineNumber.setInnerText(lineString); // safe by construction
        return lineString.length();
    }

    /**
     * Changes the displayed value of the cursor character position.
     * 
     * @param charPos the new value
     * @return the character count of the character number
     */
    private void setCharPosition(final Integer charPos) {
        if (charPos != null) {
            final String charPosString = String.valueOf(charPos);
            this.charPosition.setInnerHTML(charPosString); //safe by definition
            this.charPosition.getStyle().setOpacity(1D);
            this.charPosLabel.getStyle().setOpacity(1D);
        } else {
            this.charPosition.setInnerHTML(NO_BREAK_SPACE + "-"); //safe by definition
            this.charPosition.getStyle().setOpacity(0.35);
            this.charPosLabel.getStyle().setOpacity(0.35);
        }
    }

    /**
     * Changes the displayed value of tab size.
     * 
     * @param tabSize the new value
     */
    private void setTabSize(final Integer tabSize) {
        String tabSizeString = "";
        if (tabSize != null) {
            tabSizeString = String.valueOf(tabSize);
        }
        this.tabSize.setText(tabSizeString);
    }

    /**
     * Changes the displayed value of the file type.
     * 
     * @param type the new value
     */
    private void setFileType(final String type) {
        if (type == null || type.isEmpty()) {
            this.fileType.setText(constants.infoPanelUnknownFileType());
        } else {
            this.fileType.setText(type);
        }
    }

    /**
     * Changes the displayed value of the editor type.
     * 
     * @param type the new value
     */
    private void setEditorType(final String type) {
        if (type == null || type.isEmpty()) {
            this.editorTypeValue.setText(constants.infoPanelUnknownEditorType());
        } else {
            this.editorTypeValue.setText(type);
        }
    }

    private void setEditorTypeFromInstance(final EditorType type) {
        this.editorType = type;
        if (type != null) {
            Log.debug(InfoPanel.class, "Editor type is " + type);
            final String name = this.editorTypeRegistry.getName(type);
            Log.debug(InfoPanel.class, "... got name " + name);
            setEditorType(name);
        } else {
            Log.debug(InfoPanel.class, "Editor type: null");
            setEditorType(null);
        }
    }

    /**
     * Changes the displayed value of the editor type.
     * 
     * @param type the new value
     */
    private void setKeybindings(final String bindings) {
        if (bindings == null || bindings.isEmpty()) {
            this.keybindingsValue.setText(constants.infoPanelUnknownKeybindings());
        } else {
            this.keybindingsValue.setText(bindings);
        }
    }

    private void setKeybindingsFromKey(final String keymapKey) {
        final Keymap keymap = Keymap.fromKey(keymapKey);
        setKeybindingsFromInstance(keymap);
    }

    private void setKeybindingsFromInstance(final Keymap keymap) {
        if (keymap != null) {
            setKeybindings(keymap.getDisplay());
        } else {
            setKeybindings(null);
        }
    }

    @Override
    public void onKeymapChanged(final KeymapChangeEvent event) {
        final String editorTypeKey = event.getEditorTypeKey();
        if (editorTypeKey == null || editorTypeKey.isEmpty()) {
            return;
        }
        final EditorType editorType = EditorType.getInstance(editorTypeKey);
        if (editorType == null) {
            return;
        }
        if (editorType.equals(this.editorType)) {
            final String keymapKey = event.getKeymapKey();
            setKeybindingsFromKey(keymapKey);
        }
        // else ignore, we're not in the same editor type
    }

    /**
     * UI binder interface for this component.
     * 
     * @author "Mickaël Leduque"
     */
    interface InfoPanelUiBinder extends UiBinder<HTMLPanel, InfoPanel> {
    }
}
