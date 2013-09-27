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
package org.exoplatform.ide.extension.nodejs.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: NodeJsExtensionClientBundle.java Apr 18, 2013 4:42:33 PM vsvydenko $
 *
 */
public interface NodeJsExtensionClientBundle extends ClientBundle {
    NodeJsExtensionClientBundle INSTANCE = GWT.<NodeJsExtensionClientBundle>create(NodeJsExtensionClientBundle.class);

    @Source("org/exoplatform/ide/extension/nodejs/images/stopApp.png")
    ImageResource stopApp();

    @Source("org/exoplatform/ide/extension/nodejs/images/stopApp_Disabled.png")
    ImageResource stopAppDisabled();

    @Source("org/exoplatform/ide/extension/nodejs/images/runApp.png")
    ImageResource runApp();

    @Source("org/exoplatform/ide/extension/nodejs/images/runApp_Disabled.png")
    ImageResource runAppDisabled();

    @Source("org/exoplatform/ide/extension/nodejs/images/logs.png")
    ImageResource logs();

    @Source("org/exoplatform/ide/extension/nodejs/images/logs_Disabled.png")
    ImageResource logsDisabled();
}
