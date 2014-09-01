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
package com.codenvy.ide.texteditor.infopanel;

import elemental.dom.Element;

import com.codenvy.ide.api.text.Document;
import com.codenvy.ide.mvp.CompositeView;
import com.codenvy.ide.mvp.UiComponent;
import com.codenvy.ide.text.store.LineInfo;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.api.texteditor.FocusManager;
import com.codenvy.ide.texteditor.linedimensions.LineDimensionsUtils;
import com.codenvy.ide.texteditor.selection.SelectionModel;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;


/**
 * The presenter for the editor info panel. Info panel shows the following things:
 * cursor position,  number of lines, tab settings and file type.
 *
 * @author Oleksii Orel
 */

public class InfoPanel extends UiComponent<InfoPanel.View> {
    protected TextEditorViewImpl editor;

    public InfoPanel(Resources res, TextEditorViewImpl editor) {
        this.editor = editor;
        setView(new View(res));
    }

    public void createDefaultState(String fileContentDescription, int numberOfLines) {
        getView().setCharPosition(null);
        getView().setLineNumber(numberOfLines);
        getView().setFileType(fileContentDescription);
    }

    public void addCursorListener(SelectionModel selectionModel) {
        selectionModel.getCursorListenerRegistrar().add(new SelectionModel.CursorListener() {
            @Override
            public void onCursorChange(LineInfo lineInfo, int column, boolean isExplicitChange) {
                getView().setLineNumber(lineInfo.number() + 1);
                getView().setCharPosition(column + 1);
            }
        });
    }

    public void addFocusListener(FocusManager focusManager) {

        focusManager.getFocusListenerRegistrar().add(new FocusManager.FocusListener() {
            @Override
            public void onFocusChange(boolean hasFocus) {
                getView().setCharPosition(null);
                Document document = editor.getDocument();
                if (editor.getDocument() != null) {
                    getView().setLineNumber(document.getNumberOfLines());
                }
            }
        });

    }

    /** CssResource for the text editor info panel. */
    public interface InfoPanelCss extends CssResource {

        String editorInfo();

        String topBorder();

        String panelInfo();

        String labelPosition();

        String labelTabSize();

        String labelFileType();
    }

    public interface Resources extends ClientBundle {
        @Source({"InfoPanel.css", "com/codenvy/ide/api/ui/style.css"})
        InfoPanelCss infoPanelCss();
    }


    /**
     * The view for the editor info panel.
     */
    public static class View extends CompositeView<Void> {
        private Resources    res;
        private InfoPanelCss css;
        private Integer      lineNumber;
        private Integer      charPosition;
        private Element      divElementInfoPanel;
        private Element      spanElementLabelPosition;
        private Element      spanElementLabelTabSize;
        private Element      spanElementLabelFileType;

        private View(Resources res) {
            this.res = res;
            this.css = res.infoPanelCss();
            divElementInfoPanel = Elements.createDivElement(css.editorInfo());
            Element divElementBorder = Elements.createDivElement(css.topBorder());
            divElementInfoPanel.appendChild(divElementBorder);
            Element divElementInfoPart = Elements.createDivElement(css.panelInfo());
            divElementInfoPanel.appendChild(divElementInfoPart);
            spanElementLabelPosition = Elements.createSpanElement(css.labelPosition());
            divElementInfoPart.appendChild(spanElementLabelPosition);
            spanElementLabelTabSize = Elements.createSpanElement(css.labelTabSize());
            spanElementLabelTabSize.setInnerText("Tab Size:" + LineDimensionsUtils.getTabWidth());
            divElementInfoPart.appendChild(spanElementLabelTabSize);
            spanElementLabelFileType = Elements.createSpanElement(css.labelFileType());
            divElementInfoPart.appendChild(spanElementLabelFileType);

            setElement(divElementInfoPanel);
        }

        public void setLineNumber(Integer lineNumber) {
            this.lineNumber = lineNumber;
            if (lineNumber != null) {
                String outerText = "Line " + lineNumber.toString();
                if (charPosition != null) {
                    outerText += ", Char " + charPosition.toString();
                }
                spanElementLabelPosition.setInnerText(outerText);
            }
        }

        public void setCharPosition(Integer charPosition) {
            this.charPosition = charPosition;
            if (lineNumber != null) {
                String outerText = "Line " + lineNumber.toString();
                if (charPosition != null) {
                    outerText += ", Char " + charPosition.toString();
                }
                spanElementLabelPosition.setInnerText(outerText);
            }
        }

        public void setTabSize(Integer tabSize) {
            if (tabSize != null) {
                spanElementLabelTabSize.setInnerText("Tab Size:" + tabSize.toString());
            }
        }

        public void setFileType(String fileType) {
            if (fileType != null) {
                spanElementLabelFileType.setInnerText(fileType);
            } else {
                spanElementLabelFileType.setInnerText("Type is not defined");
            }
        }

        public Resources getResources() {
            return res;
        }
    }
}
