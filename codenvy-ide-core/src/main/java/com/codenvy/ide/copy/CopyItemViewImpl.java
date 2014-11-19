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
package com.codenvy.ide.copy;

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ui.window.Window;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * {CopyItemView} implementation.
 *
 * @author Ann Shumilova
 */
public class CopyItemViewImpl extends Window implements CopyItemView {
    @UiField
    Label copyItemTitle;
    @UiField
    TextBox newNameField;
    @UiField
    HorizontalPanel newNamePanel;
    @UiField
    VerticalPanel mainPanel;
    @UiField(provided = true)
    SuggestBox directoryField;
    @UiField
    Label errorLabel;
    @UiField
    CheckBox openInEditorField;
    @UiField(provided = true)
    CoreLocalizationConstant locale;
    private ActionDelegate delegate;
    private Button copyButton;

    @Inject
    public CopyItemViewImpl(CopyItemViewImplUiBinder uiBinder, CoreLocalizationConstant locale) {
        this.locale = locale;
        this.directoryField = new SuggestBox(new PathSuggestOracle());
        setWidget(uiBinder.createAndBindUi(this));
        setTitle(locale.copyItemViewTitle());
        createButtons();
    }

    /** Create view's buttons. */
    private void createButtons() {
        copyButton = createButton(locale.copyButton(), "copy-item-copy", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onOkClicked();
            }
        });
        copyButton.getElement().addClassName(resources.centerPanelCss().blueButton());
        copyButton.getElement().getStyle().setMarginRight(12, Style.Unit.PX);

        Button cancelButton = createButton(locale.cancelButton(), "copy-item-copy-cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });

        getFooter().add(copyButton);
        getFooter().add(cancelButton);
    }

    @UiHandler({"directoryField", "newNameField"})
    public void onKeyUp(KeyUpEvent event) {
        delegate.onValueChanged();
    }

    @UiHandler("directoryField")
    public void onDirChanged(ValueChangeEvent<String> event) {
        delegate.onValueChanged();
    }

    @Override
    public void setCopyItemTitle(String title) {
        copyItemTitle.setText(title);
    }

    @Override
    public void setNewNameVisible(boolean isVisible) {
        newNamePanel.setVisible(isVisible);
        mainPanel.setHeight(isVisible ? "120px" : "95px");
    }

    @Override
    public String getDirectory() {
        return directoryField.getValue();
    }

    @Override
    public void setDirectory(String path) {
        directoryField.setValue(path);
    }

    @Override
    public String getNewNameValue() {
        return newNameField.getValue();
    }

    @Override
    public void setNewNameValue(String name) {
        newNameField.setValue(name);

        int selectionLength = name.indexOf('.') >= 0
                ? name.lastIndexOf('.')
                : name.length();
        newNameField.setFocus(true);
        newNameField.setSelectionRange(0, selectionLength);
    }

    @Override
    public boolean getOpenInEditor() {
        return openInEditorField.getValue();
    }

    @Override
    public void setCopyButtonEnabled(boolean isEnabled) {
        copyButton.setEnabled(isEnabled);
    }

    @Override
    public void setErrorMessage(String message) {
        errorLabel.setText(message);
    }

    @Override
    public void showView() {
        this.show();
    }

    @Override
    public void close() {
        this.hide();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onClose() {

    }

    interface CopyItemViewImplUiBinder extends UiBinder<Widget, CopyItemViewImpl> {
    }

    private class PathSuggestOracle extends SuggestOracle {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isDisplayStringHTML() {
            return true;
        }

        @Override
        public void requestSuggestions(final Request request, final Callback callback) {
            delegate.onRequestSuggestions(request.getQuery(), new AsyncCallback<Array<ItemReference>>() {
                /** {@inheritDoc} */
                @Override
                public void onSuccess(Array<ItemReference> result) {
                    final List<Suggestion> suggestions = new ArrayList<>(result.size());
                    for (final ItemReference item : result.asIterable()) {
                        suggestions.add(new SuggestOracle.Suggestion() {
                            @Override
                            public String getDisplayString() {
                                return item.getName();
                            }

                            @Override
                            public String getReplacementString() {
                                return item.getPath();
                            }
                        });
                    }

                    callback.onSuggestionsReady(request, new Response(suggestions));
                }

                /** {@inheritDoc} */
                @Override
                public void onFailure(Throwable caught) {
                    Log.error(CopyItemViewImpl.class, "Failed to search path.");
                }
            });
        }
    }
}