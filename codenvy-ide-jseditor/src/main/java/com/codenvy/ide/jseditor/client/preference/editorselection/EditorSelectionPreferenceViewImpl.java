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


import javax.inject.Inject;
import javax.inject.Named;

import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.jseditor.client.JsEditorExtension;
import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeRegistry;
import com.codenvy.ide.jseditor.client.preference.EditorPrefLocalizationConstant;
import com.codenvy.ide.jseditor.client.preference.EditorPreferenceResource;
import com.codenvy.ide.jseditor.client.preference.EditorPreferenceResource.CellStyle;
import com.codenvy.ide.jseditor.client.preference.EditorPreferenceResource.CellTableStyle;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.view.client.ProvidesKey;

/** Implementation of the {@link EditorSelectionPreferenceView}. */
public class EditorSelectionPreferenceViewImpl extends Composite implements EditorSelectionPreferenceView {

    /** The UI binder instance. */
    private static final EditorTypePreferenceViewImplUiBinder UIBINDER    = GWT.create(EditorTypePreferenceViewImplUiBinder.class);

    private final EditorTypeRegistry   editorTypeRegistry;
    private final EditorType           defaultEditorType;

    private ActionDelegate             delegate;

    @UiField(provided = true)
    ValueListBox<EditorType>           defaultEditorSelection;

    @UiField(provided = true)
    EditorPrefLocalizationConstant     constants;

    @UiField(provided = true)
    CellStyle cellStyle;

    @UiField(provided = true)
    CellTableStyle                     tableStyle;

    @Inject
    public EditorSelectionPreferenceViewImpl(final EditorRegistry editorRegistry,
                                        final EditorTypeRegistry editorTypeRegistry,
                                        final EditorPreferenceResource resources,
                                        final @Named(JsEditorExtension.DEFAULT_EDITOR_TYPE_INSTANCE) EditorType defaultEditorType,
                                        final EditorPrefLocalizationConstant constants) {
        this.defaultEditorSelection = new ValueListBox<EditorType>(new AbstractRenderer<EditorType>() {

            @Override
            public String render(final EditorType editorType) {
                return editorTypeRegistry.getName(editorType);
            }
        }, new ProvidesKey<EditorType>() {
            @Override
            public Object getKey(final EditorType editorType) {
                return editorType;
            }
        });
        this.cellStyle = resources.cellStyle();
        this.tableStyle = resources.cellTableStyle();

        this.constants = constants;

        initWidget(UIBINDER.createAndBindUi(this));

        this.editorTypeRegistry = editorTypeRegistry;
        this.defaultEditorType = defaultEditorType;
        Log.debug(EditorSelectionPreferenceViewImpl.class, "Default editor type is " + this.defaultEditorType);
    }

    @Override
    public void setDelegate(final ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onLoad() {
        // delayed until the view is displayed

        // seValue before setAcceptableValues - see https://code.google.com/p/google-web-toolkit/issues/detail?id=5477
        this.defaultEditorSelection.setValue(this.delegate.getConfiguredDefaultEditor());
        this.defaultEditorSelection.setAcceptableValues(this.editorTypeRegistry.getEditorTypes());
    }

    @UiHandler("defaultEditorSelection")
    public void onValueChange(final ValueChangeEvent<EditorType> event) {
        final EditorType newValue = event.getValue();
        if (newValue == null) {
            // reset the listbox to the previous value
            this.defaultEditorSelection.setValue(this.delegate.getConfiguredDefaultEditor());
            return;
        }
        this.delegate.defaultEditorChanged(newValue);
    }

    /** UI binder interface for the {@link EditorSelectionPreferenceViewImpl} component. */
    interface EditorTypePreferenceViewImplUiBinder extends UiBinder<HTMLPanel, EditorSelectionPreferenceViewImpl> {
    }
}
