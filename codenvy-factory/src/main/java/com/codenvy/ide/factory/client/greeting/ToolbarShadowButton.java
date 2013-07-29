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

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class ToolbarShadowButton extends FlowPanel {

    // image size 115 * 20
    private Image image;
    
    private ImageResource imageResource;
    
    private ImageResource imageResourceHover;
    
    private ClickHandler clickHandler;
    
    public ToolbarShadowButton(final ImageResource imageResource, final ImageResource imageResourceHover, ClickHandler clickHandler) {
        this.imageResource = imageResource;
        this.imageResourceHover = imageResourceHover;
        this.clickHandler = clickHandler;
        
        setSize((imageResource.getWidth() + 4) + "px", "24px");

        image = new Image(imageResource);
        image.setSize(imageResource.getWidth() + "px", "20px");
        add(image);

        image.getElement().getStyle().setPosition(Position.RELATIVE);

        image.getElement().getStyle().setLeft(1, Unit.PX);
        image.getElement().getStyle().setTop(1, Unit.PX);
        image.getElement().getStyle().setProperty("boxShadow", "2px 2px 2px #888888");
        
        sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONCLICK);        
    }
    
    /** Handle browser's events. */
    @Override
    public void onBrowserEvent(Event event) {
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEOVER:
                mouseOver();
                break;

            case Event.ONMOUSEOUT:
                mouseOut();
                break;

            case Event.ONMOUSEDOWN:
                if (event.getButton() != Event.BUTTON_LEFT) {
                    return;
                }

                mouseDown();
                break;

            case Event.ONMOUSEUP:
                mouseUp();
                break;

            case Event.ONCLICK:
                click();
                break;
        }
    }
    
    private void mouseOver() {
        image.setResource(imageResourceHover);
    }
    
    private void mouseOut() {
        image.setResource(imageResource);
    }
    
    private void mouseDown() {
        image.getElement().getStyle().setLeft(3, Unit.PX);
        image.getElement().getStyle().setTop(3, Unit.PX);
        image.getElement().getStyle().setProperty("boxShadow", "0px 0px 2px #000000");
    }
    
    private void mouseUp() {
        image.getElement().getStyle().setLeft(1, Unit.PX);
        image.getElement().getStyle().setTop(1, Unit.PX);
        image.getElement().getStyle().setProperty("boxShadow", "1px 1px 2px #888888");        
    }
    
    private void click() {
        clickHandler.onClick(new ClickEvent() {});
    }
    
}
