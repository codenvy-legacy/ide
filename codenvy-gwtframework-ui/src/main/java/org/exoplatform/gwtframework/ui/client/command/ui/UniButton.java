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
package org.exoplatform.gwtframework.ui.client.command.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class UniButton extends Composite {
    
    public static enum Type {
        
        DEFAULT, PRIMARY, SUCCESS, INFO, WARNING, DANGER, LINK
        
    }
    
    public static enum Size {
        
        LARGE, DEFAULT, SMALL, EXTRA_SMALL
        
    }

    private static UniButtonUiBinder uiBinder = GWT.create(UniButtonUiBinder.class);

    interface UniButtonUiBinder extends UiBinder<Widget, UniButton> {
    }
    
    interface UniButtonStyles extends CssResource {
        
        String btnDefault();
        
        String btnPrimary();
        
        String btnSuccess();
        
        String btnInfo();
        
        String btnWarning();
        
        String btnDanger();
        
        String btnLink();
        
        String btnLg();
        
        String btnSm();
        
        String btnXs();
        
      }

    @UiField
    UniButtonStyles style;
    
    @UiField
    HTMLPanel panel;
    
    private String text;
    
    private Image icon;
    
    private Type type = Type.DEFAULT;
    
    private Size size = Size.DEFAULT;
    
    public UniButton(String text) {
        this(text, null, Type.DEFAULT, Size.DEFAULT);
    }

    public UniButton(Image icon) {
        this(null, icon, Type.DEFAULT, Size.DEFAULT);
    }
    
    public UniButton(String text, Type type, Size size) {
        this(text, null, type, size);
    }

    public UniButton(String text, Image icon, Type type, Size size) {
        this.text = text;
        this.icon = icon;
        this.type = type;
        this.size = size;
        
        initWidget(uiBinder.createAndBindUi(this));
        render();
    }
    
    public HandlerRegistration addClickHandler(ClickHandler clickHandler) {
        return addDomHandler(clickHandler, ClickEvent.getType());
    }
    
    private void render() {
        panel.clear();
        
        if (icon != null) {
            panel.add(icon);
        }
        
        if (text != null) {
            setText(panel.getElement(), text);
        }

        switch (type) {
            case DEFAULT:
                panel.addStyleName(style.btnDefault());
                break;

            case PRIMARY:
                panel.addStyleName(style.btnPrimary());
                break;
                
            case SUCCESS:
                panel.addStyleName(style.btnSuccess());
                break;

            case INFO:
                panel.addStyleName(style.btnInfo());
                break;
                
            case WARNING:
                panel.addStyleName(style.btnWarning());
                break;
                
            case DANGER:
                panel.addStyleName(style.btnDanger());
                break;

            case LINK:
                panel.addStyleName(style.btnLink());
                break;
        }
        
        switch (size) {
            case LARGE:
                panel.addStyleName(style.btnLg());
                break;

            case DEFAULT:
                break;

            case SMALL:
                panel.addStyleName(style.btnSm());
                break;
                
            case EXTRA_SMALL:
                panel.addStyleName(style.btnXs());
                break;
        }
        
    }
    
    private native void setText(Element element, String text) /*-{
        element.innerHTML += text;
    }-*/;

}
