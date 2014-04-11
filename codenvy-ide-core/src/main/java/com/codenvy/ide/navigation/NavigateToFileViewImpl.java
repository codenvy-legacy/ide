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
import com.codenvy.ide.ui.window.Window;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of {@link NavigateToFileView} view.
 *
 * @author Ann Shumilova
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NavigateToFileViewImpl extends Window implements NavigateToFileView {

    @Override
    protected void onClose() {
        //Do nothing
    }

    interface NavigateToFileViewImplUiBinder extends UiBinder<Widget, NavigateToFileViewImpl> {
    }

    @UiField(provided = true)
    SuggestBox files;

    private ActionDelegate delegate;
    @UiField(provided = true)
    CoreLocalizationConstant locale;

    @Inject
    public NavigateToFileViewImpl(CoreLocalizationConstant locale, NavigateToFileViewImplUiBinder uiBinder) {
        this.locale = locale;
        this.setTitle(locale.navigateToFileViewTitle());
        files = new SuggestBox(new MySuggestOracle());

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
        this.files.setEnabled(true);
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public String getItemPath() {
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

    private class MySuggestOracle extends SuggestOracle {
        /** {@inheritDoc} */
        @Override
        public boolean isDisplayStringHTML() {
            return true;
        }

        @Override
        public void requestSuggestions(final Request request, final Callback callback) {
            delegate.onRequestSuggestions(request.getQuery(), new AsyncCallback<Array<String>>() {
                /** {@inheritDoc} */
                @Override
                public void onSuccess(Array<String> result) {
                    final List<SuggestOracle.Suggestion> suggestions = new ArrayList<>(result.size());
                    for (final String item : result.asIterable()) {
                        suggestions.add(new SuggestOracle.Suggestion() {
                            @Override
                            public String getDisplayString() {
                                return getDisplayName(item);
                            }

                            @Override
                            public String getReplacementString() {
                                return item;
                            }
                        });
                    }

                    callback.onSuggestionsReady(request, new Response(suggestions));
                }

                /** {@inheritDoc} */
                @Override
                public void onFailure(Throwable caught) {
                    Log.error(NavigateToFileViewImpl.class, "Failed to search files.");
                }
            });
        }

        /** Returns the formed display name of the specified path. */
        private String getDisplayName(String path) {
            final String itemName = path.substring(path.lastIndexOf('/') + 1);
            final String itemPath = path.replaceFirst("/", "");
            String displayString = itemName + "   (" + itemPath.substring(0, itemPath.length() - itemName.length() - 1) + ")";

            String[] parts = displayString.split(" ");
            if (parts.length > 1) {
                displayString = parts[0];
                displayString += " <span style=\"color: #989898;\">";
                for (int i = 1; i < parts.length; i++) {
                    displayString += parts[i];
                }
                displayString += "</span>";
            }
            return displayString;
        }
    }
}
