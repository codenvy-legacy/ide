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
package com.codenvy.ide.jseditor.client.preference.editorselection;

import java.util.List;

import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeRegistry;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.cell.client.AbstractInputCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/** {@link Cell} for a select that contains all editor types. */
public class EditorSelectionCell extends AbstractInputCell<EditorType, String> {

    /** The safe html template for the cell contents. */
    interface Template extends SafeHtmlTemplates {

        /** The options element for unselected items. */
        @SafeHtmlTemplates.Template("<option value=\"{0}\">{1}</option>")
        SafeHtml deselected(String key, String display);

        /** The options element for selected items. */
        @SafeHtmlTemplates.Template("<option value=\"{0}\" selected=\"selected\">{1}</option>")
        SafeHtml selected(String key, String display);

        /** The select element. */
        @SafeHtmlTemplates.Template("<select class=\"{0}\" style=\"{1}\">")
        SafeHtml select(String classname, SafeStyles selectWidthStyle);
    }

    /** The template instance. */
    private static Template          template;

    private final String             stylename;
    private final List<EditorType>   editorTypes;

    private final Double             selectWidthValue;
    private final Unit               selectWidthUnit;

    private final EditorTypeRegistry editorTypeRegistry;
    private final EditorType         defaultEditor;

    /**
     * Construct a new {@link SelectionCell} with the specified options.
     *
     * @param defaultEditor
     * @param options the options in the cell
     */
    public EditorSelectionCell(final List<EditorType> editorTypes, final String stylename,
                                   final Double selectWidthValue, final Unit selectWidthUnit,
                                   final EditorTypeRegistry editorTypeRegistry, final EditorType defaultEditor) {
        super(BrowserEvents.CHANGE);

        initTemplate();

        this.stylename = stylename;
        this.selectWidthValue = selectWidthValue;
        this.selectWidthUnit = selectWidthUnit;
        this.editorTypes = editorTypes;
        this.editorTypeRegistry = editorTypeRegistry;
        this.defaultEditor = defaultEditor;
    }

    @Override
    public void onBrowserEvent(final Context context, final Element parent, final EditorType value,
                               final NativeEvent event, final ValueUpdater<EditorType> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        final String type = event.getType();
        if (BrowserEvents.CHANGE.equals(type)) {
            final FileType key = (FileType)context.getKey();
            final SelectElement select = parent.getFirstChild().cast();

            final EditorType newValue = this.editorTypes.get(select.getSelectedIndex());
            setViewData(key, newValue.getEditorTypeKey());
            finishEditing(parent, newValue, key, valueUpdater);
            if (valueUpdater != null) {
                valueUpdater.update(newValue);
            }
        }
    }

    @Override
    public void render(final Context context, final EditorType editorType, final SafeHtmlBuilder sb) {
        // Get the view data.
        final FileType filetype = (FileType)context.getKey();
        String viewData = getViewData(filetype);
        if (viewData != null && viewData.equals(editorType.getEditorTypeKey())) {
            clearViewData(filetype);
            viewData = null;
        }

        int selectedIndex = getIndex(this.editorTypes, viewData, editorType);
        Log.debug(EditorSelectionCell.class, "File type " + filetype + " - found selection " + selectedIndex);
        if (selectedIndex == -1) {
            selectedIndex = getDefaultIndex(this.editorTypes);
            Log.debug(EditorSelectionCell.class, "... using default value " + selectedIndex + " instead.");
        }

        final SafeStyles widthStyle = SafeStylesUtils.forWidth(this.selectWidthValue, this.selectWidthUnit);
        sb.append(template.select(this.stylename, widthStyle));
        int index = 0;
        for (final EditorType option : this.editorTypes) {
            if (index++ == selectedIndex) {
                sb.append(template.selected(option.getEditorTypeKey(), this.editorTypeRegistry.getName(option)));
            } else {
                sb.append(template.deselected(option.getEditorTypeKey(), this.editorTypeRegistry.getName(option)));
            }
        }
        sb.appendHtmlConstant("</select>");
    }

    private int getIndex(final List<EditorType> editorTypes, final String viewData, final EditorType editorType) {
        String value = viewData;
        if (value == null) {
            if (editorType != null) {
                value = editorType.getEditorTypeKey();
            } else {
                return -1;
            }
        }
        for (int i = 0; i < editorTypes.size(); i++) {
            final EditorType item = editorTypes.get(i);
            if (item != null && item.getEditorTypeKey().equals(value)) {
                return i;
            }
        }
        return -1;
    }

    private int getDefaultIndex(final List<EditorType> editorTypes) {
        for (int i = 0; i < editorTypes.size(); i++) {
            final EditorType item = editorTypes.get(i);
            if (item != null && item.equals(this.defaultEditor)) {
                return i;
            }
        }
        return -1;
    }

    private static void initTemplate() {
        if (template == null) {
            template = GWT.create(Template.class);
        }
    }
}
