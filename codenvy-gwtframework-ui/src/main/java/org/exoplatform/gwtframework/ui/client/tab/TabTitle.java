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
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

import org.exoplatform.gwtframework.ui.client.tab.event.CloseTabEvent;
import org.exoplatform.gwtframework.ui.client.tab.event.CloseTabHandler;

/**
 * Tab's title with icon, text and close button.
 * <p/>
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TabTitle extends Composite implements HasText, HasClickHandlers {

    private static final String TABTITLE_CLOSE_BUTTON_OVER = "tabTitleCloseButtonOver";

    /** TabTitle UI Binder */
    interface TabTitleUiBinder extends UiBinder<Widget, TabTitle> {
    }

    /** TabTitle UI Binder instance */
    private static TabTitleUiBinder uiBinder = GWT.create(TabTitleUiBinder.class);

    /** Element for placing title's icon */
    @UiField
    TableCellElement iconPanel;

    /** Element for placing title's text */
    @UiField
    TableCellElement textElement;

    /** Close tab button */
    @UiField
    HTML closeButton;

    /** Id of this Tab Title */
    private String tabId;

    /**
     * Handler to handle the closing of this tab.
     * Closing can be canceled by calling CloseTabEvent.cancelClosing() method.
     */
    private CloseTabHandler closeTabHandler;

    /** Icon of this tab */
    private Image icon;

    /** Tab's text */
    private String text;

    /**
     * Creates new instance of this TabTitle
     *
     * @param tabId
     *         id of this tab
     * @param icon
     *         icon
     * @param text
     *         tab text
     * @param closeTabHandler
     *         handler for handling closing this tab
     */
    public TabTitle(String tabId, Image icon, String text, CloseTabHandler closeTabHandler) {
        this.tabId = tabId;
        this.text = text;
        this.closeTabHandler = closeTabHandler;

        initWidget(uiBinder.createAndBindUi(this));
        textElement.setInnerHTML(text);

        setIcon(icon);

        closeButton.getElement().setAttribute("button-name", "close-tab");
        closeButton.getElement().setAttribute("tab-title", text);
    }

    /**
     * Handle click on close tab button.
     *
     * @param e
     *         Click Event
     */
    @UiHandler("closeButton")
    void onClick(ClickEvent e) {
        e.stopPropagation();

        if (closeTabHandler != null) {
            CloseTabEvent event = new CloseTabEvent(tabId);
            closeTabHandler.onCloseTab(event);
        }
    }

    /**
     * Handler of MouseOut Event on close tab button.
     *
     * @param e
     *         MouseOutEvent
     */
    @UiHandler("closeButton")
    void onMouseOut(MouseOutEvent e) {
        closeButton.removeStyleName(TABTITLE_CLOSE_BUTTON_OVER);
    }

    /**
     * Handler of MouseOver Event on close tab button.
     *
     * @param e
     */
    @UiHandler("closeButton")
    void onMouseOver(MouseOverEvent e) {
        closeButton.addStyleName(TABTITLE_CLOSE_BUTTON_OVER);
    }

    /**
     * Sets new icon
     *
     * @param icon
     */
    public void setIcon(Image icon) {
        if (this.icon != null) {
            while (iconPanel.getChildCount() > 0) {
                Node child = iconPanel.getFirstChild();
                child.removeFromParent();
            }

        }

        this.icon = icon;

        if (icon != null) {
            iconPanel.appendChild(icon.getElement());
        }
    }

    /**
     * Sets new text
     *
     * @param text
     */
    public void setText(String text) {
        this.text = text;
        textElement.setInnerHTML(text);
        closeButton.getElement().setAttribute("tab-title", text);
    }

    /**
     * Sets visibility of close button.
     *
     * @param canClose
     *         <b>true</b> shows close button, <b>false</b> hides close button
     */
    public void setCanClose(boolean canClose) {
        if (canClose) {
            closeButton.setVisible(true);
        } else {
            closeButton.setVisible(false);
        }
    }

    /** @see com.google.gwt.user.client.ui.HasText#getText() */
    @Override
    public String getText() {
        return text;
    }

    /** @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler) */
    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }
}
