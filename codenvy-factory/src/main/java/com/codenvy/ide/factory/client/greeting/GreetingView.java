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
