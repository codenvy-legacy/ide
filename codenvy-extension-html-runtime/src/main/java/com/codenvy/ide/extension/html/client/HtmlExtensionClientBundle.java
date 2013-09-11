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
package com.codenvy.ide.extension.html.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: HtmlExtensionClientBundle.java Jun 26, 2013 11:11:11 AM azatsarynnyy $
 */
public interface HtmlExtensionClientBundle extends ClientBundle {
    HtmlExtensionClientBundle INSTANCE = GWT.<HtmlExtensionClientBundle> create(HtmlExtensionClientBundle.class);

    @Source("com/codenvy/ide/extension/html/images/stopApp.png")
    ImageResource stopApp();

    @Source("com/codenvy/ide/extension/html/images/stopApp_Disabled.png")
    ImageResource stopAppDisabled();

    @Source("com/codenvy/ide/extension/html/images/runApp.png")
    ImageResource runApp();

    @Source("com/codenvy/ide/extension/html/images/runApp_Disabled.png")
    ImageResource runAppDisabled();
}
