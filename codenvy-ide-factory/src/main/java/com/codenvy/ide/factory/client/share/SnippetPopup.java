/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
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
package com.codenvy.ide.factory.client.share;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.factory.client.FactoryResources;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Popup for displaying snippets with content selected for easy copy operation.
 * 
 * @author Ann Shumilova
 */
public class SnippetPopup extends Window {

    public SnippetPopup(String title, String content, CoreLocalizationConstant coreLocale, FactoryResources factoryResources) {
        final TextArea textArea = new TextArea();
        textArea.setStyleName(factoryResources.factoryCSS().input());
        textArea.setWidth("330px");
        textArea.setHeight("120px");
        textArea.getElement().getStyle().setMargin(10, Unit.PX);
        textArea.getElement().getStyle().setPadding(5, Unit.PX);
        textArea.setValue(content);

        textArea.addKeyDownHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.isControlKeyDown() && event.getNativeKeyCode() == 67) {
                    hide();
                }
            }
        });

        Button btnOk = createButton(coreLocale.ok(), "snippet-popup-ok", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        setWidget(textArea);
        getFooter().add(btnOk);
        show();

        new Timer() {

            @Override
            public void run() {
                textArea.setFocus(true);
                textArea.selectAll();
            }
        }.schedule(200);
        ;
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }
}
