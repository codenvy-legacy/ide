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
package com.codenvy.ide.extension.android.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface AndroidExtensionClientBundle extends ClientBundle {
    AndroidExtensionClientBundle INSTANCE = GWT.<AndroidExtensionClientBundle>create(AndroidExtensionClientBundle.class);

    @Source("com/codenvy/ide/extension/android/images/runApp.png")
    ImageResource runApp();

    @Source("com/codenvy/ide/extension/android/images/runApp_Disabled.png")
    ImageResource runAppDisabled();

    @Source("com/codenvy/ide/extension/android/images/manymo48.png")
    ImageResource manymoPaas();

    @Source("com/codenvy/ide/extension/android/images/manymo48_Disabled.png")
    ImageResource manymoPaasDisabled();
}
