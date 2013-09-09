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
package org.exoplatform.gwtframework.ui.client.tab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * Button which can be added to the TabPanel and can be displayed at the upper right corner of the TabPanel.
 * <p/>
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TabButton extends Composite implements HasClickHandlers {

    /** UIBinder for this TabButton. */
    private static TabButtonUiBinder uiBinder = GWT.create(TabButtonUiBinder.class);

    /** UIBinder class for this TabButton. */
    interface TabButtonUiBinder extends UiBinder<Widget, TabButton> {
    }

    /** Style Resource for this TabButton. */
    interface Style extends CssResource {

        String buttonOver();

        String buttonDown();

        String iconPanel();

        String controlTable();

        String button();

        String controlPanel();

        String controlCell();
    }

    interface Resources extends ClientBundle {
        @Source("Tab.css")
        Style css();

        @Source("tab-button-background2.png")
        ImageResource background();
    }

    private static final Resources RESOURCES = GWT.create(Resources.class);

    static {
        RESOURCES.css().ensureInjected();
    }

    /** Instance of Style Resource. */
    @UiField(provided = true)
    Style style;

    @UiField
    HTML controlPanel;

    @UiField
    DivElement buttonPanel;

    @UiField
    DivElement iconPanel;

    private Image image;

    private Image disabledImage;

    boolean enabled = true;

    private List<ClickHandler> clickHandlers = new ArrayList<ClickHandler>();

    public TabButton(Image image, Image disabledImage) {
        this(null, image, disabledImage);
    }

    public TabButton(String id, Image image, Image disabledImage) {
        style = RESOURCES.css();
        this.image = image;
        this.disabledImage = disabledImage;

        initWidget(uiBinder.createAndBindUi(this));
        iconPanel.appendChild(image.getElement());

        if (id != null) {
            getElement().setId(id);
        }
    }

    @UiHandler("controlPanel")
    void onMouseOver(MouseOverEvent e) {
        if (!enabled) {
            return;
        }

        buttonPanel.addClassName(style.buttonOver());
    }

    @UiHandler("controlPanel")
    void onMouseOut(MouseOutEvent e) {
        if (!enabled) {
            return;
        }

        buttonPanel.removeClassName(style.buttonOver());
        buttonPanel.removeClassName(style.buttonDown());
    }

    @UiHandler("controlPanel")
    void onMouseDown(MouseDownEvent e) {
        if (!enabled) {
            return;
        }

        buttonPanel.addClassName(style.buttonDown());
    }

    @UiHandler("controlPanel")
    void onMouseUp(MouseUpEvent e) {
        if (!enabled) {
            return;
        }

        buttonPanel.removeClassName(style.buttonDown());
    }

    @UiHandler("controlPanel")
    void onClick(ClickEvent e) {
        if (!enabled) {
            return;
        }

        for (ClickHandler clickHandler : clickHandlers) {
            clickHandler.onClick(e);
        }
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        clickHandlers.add(handler);
        return new ClickHandlerRegistration(handler);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }

        this.enabled = enabled;

        while (iconPanel.hasChildNodes()) {
            iconPanel.getFirstChild().removeFromParent();
        }

        if (enabled) {
            iconPanel.appendChild(image.getElement());
        } else {
            buttonPanel.removeClassName(style.buttonOver());
            buttonPanel.removeClassName(style.buttonDown());
            iconPanel.appendChild(disabledImage.getElement());
        }
    }

    private class ClickHandlerRegistration implements HandlerRegistration {

        private ClickHandler handler;

        public ClickHandlerRegistration(ClickHandler handler) {
            this.handler = handler;
        }

        @Override
        public void removeHandler() {
            clickHandlers.remove(handler);
        }

    }

}
