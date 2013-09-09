/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.TextInputResource;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;

/**
 * Represents the base complex component, which consists of component for displaying text input
 * and title(label) near it.
 * This is super class for such text inputs as text field, text area and password field.
 * It consists of {@link TextInputBase} - field for editing text and {@link SpanElement} - title of the field.<br>
 * User can change the title's position (on top, on the right or left side of the text field):<br>
 * <code>textField.setTitleOrientation(TitleOrientation.RIGHT);</code>
 * The title's align can be set (left, right, center):<br>
 * <code>textField.setTitleAlign(Align.CENTER);<code>
 * <p/>
 * When component is disabled - the style of it is changed to show the state.
 * For not to show disabled state, use <code>setShowDisabled(false)</code>.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Feb 22, 2011 10:27:20 AM anya $
 */
public class TextItemBase extends ComplexPanel implements TextFieldItem {
    interface TextItemBaseUiBinder extends UiBinder<Widget, TextItemBase> {
    }

    /** CSS and image resources. */
    public static final TextInputResource resource = GWT.create(TextInputResource.class);

    private static TextItemBaseUiBinder uiBinder = GWT.create(TextItemBaseUiBinder.class);

    /** HTML element for title. */
    @UiField
    SpanElement titleElement;

    /** Text input element. */
    private TextInputBase textElement;

    /** Text of the title. */
    private String title;

    /** Enabled state of the component. */
    private boolean enabled = true;

    /** Display title or not. */
    private boolean isShowTitle;

    /** Show disabled state or not. */
    private boolean isShowDisabled;

    /** The align of the title. */
    private Align titleAlign;

    /** Title's element width. */
    private int titleWidth;

    /** Input component's width. */
    private int width;

    /** Input component's height. */
    private int heigth;

    /** The title's position near input. */
    private TitleOrientation titleOrientation;

    /**
     * @param textElement
     *         the text input element
     */
    public TextItemBase(TextInputBase textElement) {
        setElement(uiBinder.createAndBindUi(this).getElement());
        resource.css().ensureInjected();
        this.textElement = (textElement == null) ? new TextInputBase(new TextBox().getElement()) : textElement;
        add(textElement, getElement());
        setEnabled(true);
        setShowTitle(true);
        //setShowDisabled(true);
        setTitleOrientation(TitleOrientation.LEFT);
        setTitleAlign(Align.LEFT);
    }

    /** @return the textItem */
    protected TextBoxBase getTextElement() {
        return textElement;
    }

    /** @return the width */
    public int getWidth() {
        int w = textElement.getElement().getAbsoluteRight() - textElement.getElement().getAbsoluteLeft();
        return (w <= 0) ? width : w;
    }

    /**
     * Sets new width of this item
     *
     * @param width
     *         the width to set
     */
    public void setWidth(int width) {
        this.width = width;
        textElement.setWidth(width + "px");
        checkTitleWith();
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);

        if (width.endsWith("px")) {
            int widthValue = Integer.parseInt(width.substring(0, width.length() - 2));
            setWidth(widthValue);
        } else {
            textElement.setWidth(width);
            checkTitleWith();
        }
    }

    /** Check the title's element proper width. */
    private void checkTitleWith() {
        if (TitleOrientation.TOP.equals(titleOrientation)) {
            titleElement.setAttribute("style", "width:" + getWidth() + "px");
        } else {
            String w = (titleWidth <= 0) ? "auto" : titleWidth + "px";
            titleElement.setAttribute("style", "width:" + w);
        }
    }

    /** @return the height */
    public int getHeight() {
        int h = textElement.getElement().getAbsoluteBottom() - textElement.getElement().getAbsoluteTop();
        return (h <= 0) ? heigth : h;
    }

    /**
     * @param height
     *         the height to set
     */
    public void setHeight(int height) {
        this.heigth = height;
        textElement.setHeight(height + "px");
    }

    /**
     * Sets new height of text field.
     *
     * @param height
     */
    public void setTextItemHeight(int height) {
        this.heigth = height;
        textElement.setHeight(height + "px");
    }

    /** @return the id */
    public String getId() {
        return getElement().getId();
    }

    /**
     * @param id
     *         the id to set
     */
    public void setId(String id) {
        getElement().setAttribute("id", id);
    }

    /** @return the title */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the text of the title to display near text field.
     *
     * @param title
     *         title to display
     */
    public void setTitle(String title) {
        this.title = (title == null) ? "" : title;
        titleElement.setInnerHTML(title);
        redraw();
    }

    /** @return the enabled */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Change the enabled state of the component.
     *
     * @param enabled
     *         enabled state
     */
    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled)
            return;
        this.enabled = enabled;
        textElement.setEnabled(enabled, isShowDisabled);
        if (enabled || !isShowDisabled) {
            getElement().removeClassName(resource.css().textInputDisabled());
            DOM.setStyleAttribute(textElement.getElement(), "color", "#000000");
            DOM.setStyleAttribute(textElement.getElement(), "backgroundColor", "#FFFFFF");
        } else {
            getElement().addClassName(resource.css().textInputDisabled());
        }
    }

    /** Enable the element (can change text in input). */
    public void enable() {
        setEnabled(true);
    }

    /** Disable the element (cannot change text in input). */
    public void disable() {
        setEnabled(false);
    }

    /**
     * Set the disabled state of the component.
     *
     * @param isDisabled
     */
    public void setDisabled(boolean isDisabled) {
        setEnabled(!isDisabled);
    }

    /** Show component. */
    public void show() {
        setVisible(true);
    }

    /** Hide component on HTML page. */
    public void hide() {
        setVisible(false);
    }

    /** Give browser focus to text input. */
    public void focusInItem() {
        textElement.setFocus(true);
    }

    /** Clear value of the text input. */
    public void clearValue() {
        textElement.setValue("", true);
    }

    /** @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared
     * .ValueChangeHandler) */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return textElement.addValueChangeHandler(handler);
    }

    /** @param name */
    public void setName(String name) {
        textElement.setName(name);
    }

    /** @return the isShowTitle if <code>false</code> title is not displayed */
    public boolean isShowTitle() {
        return isShowTitle;
    }

    /**
     * @param isShowTitle
     *         if <code>false</code> title is not displayed
     */
    public void setShowTitle(boolean isShowTitle) {
        this.isShowTitle = isShowTitle;
        redraw();
    }

    private boolean hasBorder;

    public void setHasBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;

        if (hasBorder) {
            DOM.setStyleAttribute(textElement.getElement(), "border", (String)null);
        } else {
            DOM.setStyleAttribute(textElement.getElement(), "border", "none");
        }
    }

    public boolean hasBorder() {
        return hasBorder;
    }

    /** @return the isShowDisabled */
    public boolean isShowDisabled() {
        return isShowDisabled;
    }

    /**
     * if <code>false</code> the disabled state of the component is not shown.
     *
     * @param isShowDisabled
     */
    public void setShowDisabled(boolean isShowDisabled) {
        this.isShowDisabled = isShowDisabled;
        if (!isEnabled()) {
            redraw();
        }
    }

    /** @return the titleAlign */
    public Align getTitleAlign() {
        return titleAlign;
    }

    /**
     * @param titleAlign
     *         the titleAlign to set
     */
    public void setTitleAlign(Align titleAlign) {
        if (this.titleAlign == titleAlign)
            return;
        this.titleAlign = titleAlign;
        if (Align.CENTER.equals(titleAlign)) {
            titleElement.addClassName(resource.css().textAlignCenter());
            titleElement.removeClassName(resource.css().textAlignRight());
            titleElement.removeClassName(resource.css().textAlignLeft());
        } else if (Align.RIGHT.equals(titleAlign)) {
            titleElement.addClassName(resource.css().textAlignRight());
            titleElement.removeClassName(resource.css().textAlignCenter());
            titleElement.removeClassName(resource.css().textAlignLeft());
        } else if (Align.LEFT.equals(titleAlign)) {
            titleElement.addClassName(resource.css().textAlignLeft());
            titleElement.removeClassName(resource.css().textAlignRight());
            titleElement.removeClassName(resource.css().textAlignCenter());
        }
    }

    /** @return the titleOrientation */
    public TitleOrientation getTitleOrientation() {
        return titleOrientation;
    }

    /**
     * Set the title's position near text field.
     *
     * @param titleOrientation
     *         title's orientation
     */
    public void setTitleOrientation(TitleOrientation titleOrientation) {
        if (this.titleOrientation == titleOrientation)
            return;

        this.titleOrientation = titleOrientation;
        if (TitleOrientation.LEFT.equals(getTitleOrientation())) {
            titleElement.addClassName(resource.css().textInputTitleLeft());
            titleElement.removeClassName(resource.css().textInputTitleRight());
            titleElement.removeClassName(resource.css().textInputTitleTop());
            textElement.getElement().removeClassName(resource.css().textInputWithTopTitle());
        } else if (TitleOrientation.RIGHT.equals(getTitleOrientation())) {
            titleElement.addClassName(resource.css().textInputTitleRight());
            titleElement.removeClassName(resource.css().textInputTitleLeft());
            titleElement.removeClassName(resource.css().textInputTitleTop());
            textElement.getElement().removeClassName(resource.css().textInputWithTopTitle());
        } else if (TitleOrientation.TOP.equals(getTitleOrientation())) {
            titleElement.addClassName(resource.css().textInputTitleTop());
            textElement.getElement().addClassName(resource.css().textInputWithTopTitle());
            titleElement.removeClassName(resource.css().textInputTitleLeft());
            titleElement.removeClassName(resource.css().textInputTitleRight());
        }
        checkTitleWith();
    }

    /**
     * Sets position of item's Title
     *
     * @param orientation
     *         title's orientation. Applies only "left", "right" and "top" values
     */
    public void setTitleOrientation(String orientation) {
        if (TitleOrientation.LEFT.name().equalsIgnoreCase(orientation)) {
            setTitleOrientation(TitleOrientation.LEFT);
        } else if (TitleOrientation.RIGHT.name().equalsIgnoreCase(orientation)) {
            setTitleOrientation(TitleOrientation.RIGHT);
        } else if (TitleOrientation.TOP.name().equalsIgnoreCase(orientation)) {
            setTitleOrientation(TitleOrientation.TOP);
        }
    }

    /** @return the titleElement */
    public SpanElement getTitleElement() {
        return titleElement;
    }

    @UiField
    Element titlePanel;

    public void redraw() {
        if (isShowTitle) {
            titleElement.removeClassName(resource.css().textInputTitleHidden());
            titlePanel.getStyle().setProperty("display", "");
        } else {
            titleElement.addClassName(resource.css().textInputTitleHidden());
            titlePanel.getStyle().setProperty("display", "none");
        }
    }

    /** Select the text in text input. */
    public void selectValue() {
        textElement.selectAll();
    }

    /** @see com.google.gwt.event.dom.client.HasKeyPressHandlers#addKeyPressHandler(com.google.gwt.event.dom.client.KeyPressHandler) */
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return textElement.addKeyPressHandler(handler);
    }

    public String getName() {
        return textElement.getName();
    }

    /** @see com.google.gwt.event.dom.client.HasKeyUpHandlers#addKeyUpHandler(com.google.gwt.event.dom.client.KeyUpHandler) */
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return textElement.addKeyUpHandler(handler);
    }

    /** @see com.google.gwt.event.dom.client.HasKeyDownHandlers#addKeyDownHandler(com.google.gwt.event.dom.client.KeyDownHandler) */
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return textElement.addKeyDownHandler(handler);
    }

    /** @see com.google.gwt.user.client.ui.HasValue#getValue() */
    public String getValue() {
        return textElement.getValue();
    }

    /** @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object) */
    public void setValue(String value) {
        textElement.setValue(value);
    }

    /** @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean) */
    public void setValue(String value, boolean fireEvents) {
        textElement.setValue(value, true);
    }

    /** @return the titleWidth */
    public int getTitleWidth() {
        return titleWidth;
    }

    /**
     * @param titleWidth
     *         the titleWidth to set
     */
    public void setTitleWidth(int titleWidth) {
        this.titleWidth = titleWidth;
        checkTitleWith();
    }

    /**
     * Set text input read only
     *
     * @param b
     *         is read only
     */
    public void setReadOnly(boolean b) {
        textElement.getElement().setAttribute("readOnly", String.valueOf(b));
    }

//   @Override
//   public void resize(int width, int height)
//   {
//      this.width = width;
//      this.heigth = height;
//
//      if (isShowTitle)
//      {
//         int titleHeight = titlePanel.getOffsetHeight();
//         titlePanel.getStyle().setProperty("width", width + "px");
//         titleElement.getStyle().setProperty("width", width + "px");
//
//         height = height - titleHeight;
//      }
//
//      textElement.setSize(width + "px", height + "px");
//   }

}
