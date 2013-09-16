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
package org.exoplatform.ide.extension.python.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public interface PythonExtensionClientBundle extends ClientBundle {
    PythonExtensionClientBundle INSTANCE = GWT.<PythonExtensionClientBundle>create(PythonExtensionClientBundle.class);

    @Source("org/exoplatform/ide/extension/python/images/stopApp.png")
    ImageResource stopApp();

    @Source("org/exoplatform/ide/extension/python/images/stopApp_Disabled.png")
    ImageResource stopAppDisabled();

    @Source("org/exoplatform/ide/extension/python/images/runApp.png")
    ImageResource runApp();

    @Source("org/exoplatform/ide/extension/python/images/runApp_Disabled.png")
    ImageResource runAppDisabled();

    @Source("org/exoplatform/ide/extension/python/images/logs.png")
    ImageResource logs();

    @Source("org/exoplatform/ide/extension/python/images/logs_Disabled.png")
    ImageResource logsDisabled();
}
