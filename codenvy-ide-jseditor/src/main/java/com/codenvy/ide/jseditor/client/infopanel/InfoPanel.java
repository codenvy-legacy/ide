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
package com.codenvy.ide.jseditor.client.infopanel;

import com.codenvy.ide.jseditor.client.JsEditorConstants;
import com.codenvy.ide.jseditor.client.document.EmbeddedDocument.TextPosition;
import com.codenvy.ide.jseditor.client.events.CursorActivityEvent;
import com.codenvy.ide.jseditor.client.events.CursorActivityHandler;
import com.codenvy.ide.jseditor.client.texteditor.EmbeddedTextEditorPartView;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * The presenter for the editor info panel.<br>
 * Info panel shows the following things: cursor position, number of lines, tab settings and file type.
 * 
 * @author "Mickaël Leduque"
 */
public class InfoPanel extends Composite implements CursorActivityHandler, FocusHandler, BlurHandler {

    /** The UI binder instance. */
    private static final InfoPanelUiBinder UIBINDER = GWT.create(InfoPanelUiBinder.class);

    /** The related editor view. */
    private EmbeddedTextEditorPartView     editor;
    /** The i18n constants. */

    @UiField(provided = true)
    JsEditorConstants                      constants;

    @UiField
    SpanElement                            charPosLabel;
    @UiField
    Label                                  charPosition;
    @UiField
    Label                                  lineNumber;
    @UiField
    Label                                  fileType;
    @UiField
    Label                                  tabSize;

    public InfoPanel(final EmbeddedTextEditorPartView editor, final JsEditorConstants constants) {
        this.editor = editor;
        this.constants = constants;
        initWidget(UIBINDER.createAndBindUi(this));
    }

    /**
     * Creates an initial state, before actual data is available.
     * 
     * @param fileContentDescription the file type
     * @param numberOfLines the file number of lines
     * @param tabSize the space-equivalent width of a tabulation character
     */
    public void createDefaultState(final String fileContentDescription, final int numberOfLines, final int tabSize) {
        setCharPosition(null);
        setLineNumber(numberOfLines);
        setFileType(fileContentDescription);
        setTabSize(tabSize);
    }

    @Override
    public void onBlur(final BlurEvent event) {
        setCharPosition(null);
        setLineNumber(editor.getEmbeddedDocument().getLineCount());
    }

    @Override
    public void onFocus(final FocusEvent event) {
        updateCursorPosition();
    }

    /** Update the line and char display. */
    public void updateCursorPosition() {
        final TextPosition position = this.editor.getEmbeddedDocument().getCursorPosition();
        setCharPosition(position.getCharacter() + 1);
        setLineNumber(position.getLine() + 1);
    }

    @Override
    public void onCursorActivity(final CursorActivityEvent event) {
        updateCursorPosition();
    }

    /**
     * Changes the displayed value of the cusor line number.
     * 
     * @param lineNum the new value
     */
    private void setLineNumber(final Integer lineNum) {
        String lineString = "";
        if (lineNum != null) {
            lineString = String.valueOf(lineNum);
        }
        this.lineNumber.setText(lineString);
    }

    /**
     * Changes the displayed value of the cursor character position.
     * 
     * @param charPos the new value
     */
    private void setCharPosition(final Integer charPos) {
        if (charPos != null) {
            final String charPosString = String.valueOf(charPos);
            this.charPosition.setText(charPosString);
            this.charPosition.setVisible(true);
            this.charPosLabel.getStyle().setVisibility(Visibility.VISIBLE);
        } else {
            this.charPosition.setText("");
            this.charPosition.setVisible(false);
            this.charPosLabel.getStyle().setVisibility(Visibility.HIDDEN);
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
     * UI binder interface for this component.
     * 
     * @author "Mickaël Leduque"
     */
    interface InfoPanelUiBinder extends UiBinder<HTMLPanel, InfoPanel> {
    }
}
