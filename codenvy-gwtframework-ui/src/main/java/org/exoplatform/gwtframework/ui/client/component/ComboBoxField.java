/**
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */

package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionCallback;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

import org.exoplatform.gwtframework.ui.client.SelectItemResource;

import java.util.ArrayList;
import java.util.Collection;

/**
 * {@link ComboBoxField} represents the select list with input field.
 * After typing text to input field, items in popup list are filtered.
 * <p/>
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ComboBoxField extends Composite implements HasValue<String> {
    private static ComboBoxUiBinder uiBinder = GWT.create(ComboBoxUiBinder.class);

    interface ComboBoxUiBinder extends UiBinder<Widget, ComboBoxField> {
    }

    private static final String SUGGEST_PANEL_ID = "exoSuggestPanel";

    /** Suggest box. */
    @UiField(provided = true)
    SuggestBox suggestBox;

    /** Oracle, that find suggestions. */
    private MultiWordSuggestOracle oracle;

    private ComboboxSuggestDisplay suggestDisplay;

    public static final SelectItemResource resource = GWT.create(SelectItemResource.class);

    private boolean enabled = true;

    private boolean showDefaultSuggestions = false;

    private boolean suggestionShown = false;

    @UiField
    Image image;

    public ComboBoxField() {
        super();
        resource.css().ensureInjected();

        oracle = new MultiWordSuggestOracleExt();
        suggestDisplay = new ComboboxSuggestDisplay();
        suggestBox = new SuggestBox(oracle, new TextBox(), suggestDisplay);

        initWidget(uiBinder.createAndBindUi(this));

        suggestBox.addSelectionHandler(new SelectionHandler<Suggestion>() {
            @Override
            public void onSelection(SelectionEvent<Suggestion> event) {
                suggestionShown = false;
            }
        });

        suggestBox.getValueBox().setStyleName(resource.css().comboBoxInput(), true);
        image.getElement().setAttribute("image-id", "suggest-image");
        image.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                GWT.log("click event: " + suggestionShown);
                if (!suggestionShown) {
                    showDefaultSuggestions = true;
                    suggestBox.showSuggestionList();
                    showDefaultSuggestions = false;
                    suggestBox.setFocus(true);
                    suggestionShown = !suggestionShown;
                } else {
                    suggestionShown = !suggestionShown;
                }
            }
        });
        adjustSuggestingPopupSize();
        Window.addResizeHandler(new ListWindowResizeHandler());
    }

   /*
    * -------- HasValue methods --------------
    */

    /** @see com.google.gwt.user.client.ui.HasValue#getValue() */
    public String getValue() {
        return suggestBox.getText();
    }

    /** @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared
     * .ValueChangeHandler) */
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> valueChangeHandler) {
        suggestBox.addValueChangeHandler(valueChangeHandler);

        // Add SelectionHandler to fire ValueChangeEvent when value selected from popup list
        suggestBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
            @Override
            public void onSelection(SelectionEvent<Suggestion> event) {
                valueChangeHandler.onValueChange(new ValueChangeEventImpl(event.getSelectedItem().getReplacementString()));
            }
        });

        return suggestBox.getValueBox().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                valueChangeHandler.onValueChange(new ValueChangeEventImpl(suggestBox.getText()));
            }
        });

    }

    /** @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object) */
    public void setValue(String value) {
        suggestBox.setText(value);
    }

    /** @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean) */
    @Override
    public void setValue(String value, boolean fireEvents) {
        suggestBox.setValue(value, fireEvents);
    }

   /*
    * -------- Combobox API --------------
    */

    /**
     * Set is element enabled.
     *
     * @param enabled
     *         the enabled to set
     */
    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        suggestBox.getValueBox().setEnabled(enabled);
        if (enabled) {
            getElement().removeClassName(resource.css().selectItemDisabled());
        } else {
            getElement().addClassName(resource.css().selectItemDisabled());
        }
    }

    /**
     * Set width of text box and popup suggest list.
     * <p/>
     * Use this method, is you know the width of element in pixels.
     * <p/>
     * In this case the width will be set to text input and pick up list
     * correctly.
     *
     * @param width
     *         - the width of element
     */
    public void setWidth(int width) {
        setWidth(width + "px");
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        adjustSuggestingPopupSize();
    }

    /**
     * Set the name of text box of element.
     *
     * @param name
     *         - the name
     */
    public void setName(String name) {
        suggestBox.getValueBox().setName(name);
    }

    /**
     * Set height of popup suggest list.
     *
     * @param height
     *         - the height of popup list
     */
    public void setPickListHeight(int height) {
        suggestDisplay.setHeight(height);
    }

    /**
     * Set value map:
     * first argument -the text of the item to be added,
     * second argument - the item's value, to be submitted if it is part of a FormPanel; cannot be null,
     *
     * @param values
     */
    public void setValueMap(String[] values) {
        oracle.clear();
        final Collection<Suggestion> defaultSuggestions = new ArrayList<Suggestion>();
        for (String value : values) {
            oracle.add(value);
            defaultSuggestions.add(new Word(value, value));
        }
        oracle.setDefaultSuggestions(defaultSuggestions);
    }

    /**
     * @param index
     *         the widget's tab index
     * @see com.google.gwt.user.client.ui.SuggestBox#setTabIndex(int)
     */
    public void setTabIndex(int index) {
        suggestBox.setTabIndex(index);
    }

    /**
     * Set height of element in pixels.
     *
     * @param height
     */
    public void setHeight(int height) {
        super.setHeight(height + "px");
        suggestBox.setHeight(height + "px");
        //need to keep input field inside the box
        suggestBox.getValueBox().setHeight("100%");
    }

    /** @see com.google.gwt.user.client.ui.UIObject#setHeight(java.lang.String) */
    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        suggestBox.setHeight(height);
        //need to keep input field inside the box
        suggestBox.getValueBox().setHeight("100%");
    }

   /*
    * -------- Inner classes and implementation --------------
    */

    /** Implements Suggestion interface. */
    private class Word implements Suggestion {
        private String value;

        private String display;

        public Word(String value, String display) {
            this.value = value;
            this.display = display;
        }

        /** @see com.google.gwt.user.client.ui.SuggestOracle.Suggestion#getDisplayString() */
        public String getDisplayString() {
            return display;
        }

        /** @see com.google.gwt.user.client.ui.SuggestOracle.Suggestion#getReplacementString() */
        public String getReplacementString() {
            return value;
        }
    }

    /**
     * Represents class for suggest display for combobox field.
     * <p/>
     * Use custom css style, can set width and heidght of picklist in combobox.
     */
    protected class ComboboxSuggestDisplay extends DefaultSuggestionDisplay {
        private static final String DEFAULT_HEIGHT = "200px";

        /**
         *
         */
        public ComboboxSuggestDisplay() {
            setSuggestionListHiddenWhenEmpty(true);
        }

        @Override
        protected PopupPanel createPopup() {
            PopupPanel popupPanel = new PopupPanel(true, false);
            popupPanel.setStyleName(resource.css().comboboxSelectPanel(), true);
            popupPanel.setStyleName("comboboxSelectPanel", true);
            popupPanel.setHeight(DEFAULT_HEIGHT);
            popupPanel.getElement().setId(SUGGEST_PANEL_ID);
            return popupPanel;
        }

        @Override
        protected void showSuggestions(SuggestBox suggestBox, Collection<? extends Suggestion> suggestions,
                                       boolean isDisplayStringHTML, boolean isAutoSelectEnabled, final SuggestionCallback callback) {
            super.showSuggestions(suggestBox, suggestions, isDisplayStringHTML, isAutoSelectEnabled, callback);
            adjustSuggestingPopupSize();
        }

        public void setWidth(String width) {
            super.getPopupPanel().setWidth(width);
        }

        /**
         * Set width in pixels.
         *
         * @param width
         */
        public void setWidth(int width) {
            super.getPopupPanel().setWidth(width + "px");
        }

        /**
         * Set height in pixels.
         *
         * @param height
         */
        public void setHeight(int height) {
            super.getPopupPanel().setHeight(height + "px");
        }

        public void setHeight(String height) {
            super.getPopupPanel().setHeight(height);
        }

    }

    private class MultiWordSuggestOracleExt extends MultiWordSuggestOracle {
        /**
         * @see com.google.gwt.user.client.ui.MultiWordSuggestOracle#requestSuggestions(com.google.gwt.user.client.ui.SuggestOracle.Request,
         *      com.google.gwt.user.client.ui.SuggestOracle.Callback)
         */
        @Override
        public void requestSuggestions(Request request, Callback callback) {
            if (showDefaultSuggestions) {
                super.requestDefaultSuggestions(request, callback);
            } else {
                super.requestSuggestions(request, callback);
            }
        }
    }

    private class ValueChangeEventImpl extends ValueChangeEvent<String> {

        /** @param value */
        protected ValueChangeEventImpl(String value) {
            super(value);
        }

    }

    private void adjustSuggestingPopupSize() {
        int width = getOffsetWidth() - 2;
        if (width >= 0) {
            suggestDisplay.setWidth(width + "px");
        }
    }

    /** This handler is invoked on window resize and changes suggesting popup panel width. */
    protected class ListWindowResizeHandler implements ResizeHandler {
        /** See class docs */
        @Override
        public void onResize(ResizeEvent resizeEvent) {
            adjustSuggestingPopupSize();
        }
    }
}
