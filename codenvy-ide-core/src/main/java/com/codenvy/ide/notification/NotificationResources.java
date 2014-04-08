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
package com.codenvy.ide.notification;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
public interface NotificationResources extends ClientBundle {
    public interface NotificationCss extends CssResource {
        String notificationPanel();
        
        String notificationPopup();
        
        String notificationItem();

        String floatLeft();

        String margin2px();

        String error();

        String unread();

        String warning();
        
        String success();

        String right25px();

        String invertColor();
        
        String countLabel();
        
        String center();
        
        String close();
        
        String closePopupIcon();
    }

    @Source({"notification.css", "com/codenvy/ide/api/ui/style.css"})
    NotificationCss notificationCss();

    @Source("message.png")
    ImageResource message();

    @Source("progress.gif")
    ImageResource progress();

    @Source("success.svg")
    SVGResource success();

    @Source("error.svg")
    SVGResource error();

    @Source("warning.svg")
    SVGResource warning();
    
    @Source("close-popup.svg")
    SVGResource closePopup();
}