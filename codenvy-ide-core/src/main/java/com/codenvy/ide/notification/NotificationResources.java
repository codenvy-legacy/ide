/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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

        String margin4px();

        String error();

        String unread();

        String warning();

        String success();

        String right25px();

        String notificationMessage();

        String progress();

        String countLabel();

        String center();

        String close();

        String closePopupIcon();
    }

    @Source({"notification.css", "com/codenvy/ide/api/ui/style.css"})
    NotificationCss notificationCss();

    @Source("message.png")
    ImageResource message();

    @Source("progress.svg")
    SVGResource progress();

    @Source("success.svg")
    SVGResource success();

    @Source("error.svg")
    SVGResource error();

    @Source("warning.svg")
    SVGResource warning();

    @Source("close-popup.svg")
    SVGResource closePopup();
}