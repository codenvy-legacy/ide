/*
 * Copyright (C) 2012 eXo Platform SAS.
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
 */
package com.codenvy.ide.factory.client.greeting;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class TooltipHint extends Composite implements ClickHandler {

    private static TooltipHintUiBinder uiBinder = GWT.create(TooltipHintUiBinder.class);

    interface TooltipHintUiBinder extends UiBinder<Widget, TooltipHint> {
    }

    @UiField
    TableCellElement messageElement;
    
    @UiField
    Label closeButton;
    
    private int opacity = 0;
    
    private int top = 2;
    
    public TooltipHint(String text) {
        initWidget(uiBinder.createAndBindUi(this));
        messageElement.setInnerHTML(text);
        closeButton.addClickHandler(this);
        
        getElement().getStyle().setProperty("opacity", "0");
        getElement().getStyle().setTop(top, Unit.PX);
        RootPanel.get().add(this);
        
        new Timer() {
            @Override
            public void run() {
                opacity += 1;
                top += 2;
                getElement().getStyle().setTop(top, Unit.PX);
                
                if (opacity >= 10) {
                    getElement().getStyle().setProperty("opacity", "1");
                    cancel();
                } else {                    
                    getElement().getStyle().setProperty("opacity", "0." + opacity);
                }
            }
        }.scheduleRepeating(50);
    }

    @Override
    public void onClick(ClickEvent event) {
        opacity = 10;
        
        // Hide animation
        new Timer() {
            @Override
            public void run() {
                opacity--;
                if (opacity <= 0) {
                    cancel();
                    removeFromParent();
                } else {
                    getElement().getStyle().setProperty("opacity", "0." + opacity);
                }
            }
        }.scheduleRepeating(50);
    }        

}
