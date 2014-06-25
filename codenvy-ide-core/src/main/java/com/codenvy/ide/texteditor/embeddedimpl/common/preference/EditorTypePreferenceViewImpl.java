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
package com.codenvy.ide.texteditor.embeddedimpl.common.preference;


import com.codenvy.ide.core.editor.EditorType;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implementation of the {@link EditorTypePreferenceView}.
 * 
 * @author "Mickaël Leduque"
 */
public class EditorTypePreferenceViewImpl implements EditorTypePreferenceView {

    private static EditorTypePreferenceViewImplUiBinder ourUiBinder = GWT.create(EditorTypePreferenceViewImplUiBinder.class);
    private final FlowPanel                             rootElement;

    @UiField
    ListBox                                             editorTypeList;

    private ActionDelegate                              delegate;

    public EditorTypePreferenceViewImpl() {
        rootElement = ourUiBinder.createAndBindUi(this);
        for (final EditorType type : EditorType.values()) {
            this.editorTypeList.addItem(type.getName());
        }
    }

    @Override
    public void setDelegate(final ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

    @Override
    public void setEditorType(final EditorType newType) {
        if (newType == null) {
            Log.warn(EditorTypePreferenceViewImpl.class, "Attempt to set null editor type in preference view.");
            return;
        }
        for (final EditorType type : EditorType.values()) {
            boolean selected = newType.equals(type);
            this.editorTypeList.setItemSelected(type.getIndex(), selected);
        }
    }

    @UiHandler("editorTypeList")
    void handleSelectionChanged(final ChangeEvent event) {
        final int selectedIndex = this.editorTypeList.getSelectedIndex();
        final EditorType newType = EditorType.fromIndex(selectedIndex);
        Log.warn(EditorTypePreferenceViewImpl.class, "Selection of editor type " + newType);
        this.delegate.editorTypeSelected(newType);
    }

    interface EditorTypePreferenceViewImplUiBinder extends UiBinder<FlowPanel, EditorTypePreferenceViewImpl> {
    }
}
