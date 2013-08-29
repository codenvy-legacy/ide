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

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class GreetingView extends ViewImpl implements com.codenvy.ide.factory.client.greeting.GreetingUserPresenter.GreetingDisplay {

    public static final String ID = "ide.greetinv_user.view";
    
    public GreetingView(String title, Image image, String contentURL) {
        super(ID, ViewType.INFORMATION, title, image);
        
        Frame frame = new Frame(contentURL);
        frame.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
        frame.setWidth("100%");
        frame.setHeight("100%");
        add(frame);
    }
    
}
