/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.navigation;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.collections.Array;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link NavigateToFileView} view.
 * 
 * @author Ann Shumilova
 */
@Singleton
public class NavigateToFileViewImpl extends DialogBox implements NavigateToFileView {

    interface NavigateToFileViewImplUiBinder extends UiBinder<Widget, NavigateToFileViewImpl> {
    }

    @UiField(provided = true)
    SuggestBox               files;

    private ActionDelegate   delegate;
    @UiField(provided = true)
    CoreLocalizationConstant locale;

    @Inject
    public NavigateToFileViewImpl(CoreLocalizationConstant locale, NavigateToFileViewImplUiBinder uiBinder) {
        this.setText(locale.navigateToFileViewTitle());
        this.locale = locale;
        files = new SuggestBox(new FilesSuggestOracle());

        files.getValueBox().addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (KeyCodes.KEY_ESCAPE == event.getNativeKeyCode()) {
                    close();
                }

            }
        });

        files.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {

            @Override
            public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
                delegate.onFileSelected();
            }

        });

        Widget widget = uiBinder.createAndBindUi(this);
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        files.setEnabled(false);
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.center();
        this.files.setEnabled(true);
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setFiles(Array<String> files) {
        FilesSuggestOracle oracle = ((FilesSuggestOracle)this.files.getSuggestOracle());
        oracle.clear();
        for (String file : files.asIterable()) {
            oracle.add(file);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getFile() {
        return files.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void focusInput() {
        files.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public void clearInput() {
        files.getValueBox().setValue("");
    }

    private class FilesSuggestOracle extends MultiWordSuggestOracle {
        /** {@inheritDoc} */
        @Override
        public boolean isDisplayStringHTML() {
            return true;
        }

        /** {@inheritDoc} */
        @Override
        protected MultiWordSuggestion createSuggestion(String replacementString, String displayString) {
            String[] parts = displayString.split(" ");
            if (parts.length > 1) {
                displayString = parts[0];
                displayString += " <span style=\"color: #989898;\">";
                for (int i = 1; i < parts.length; i++) {
                    displayString += parts[i];
                }
                displayString += "</span>";
            }
            return super.createSuggestion(replacementString, displayString);
        }
    }
}
