/**
 * Copyright (C) 2010 eXo Platform SAS.
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
