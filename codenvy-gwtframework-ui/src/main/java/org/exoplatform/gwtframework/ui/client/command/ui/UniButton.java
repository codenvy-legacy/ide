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
