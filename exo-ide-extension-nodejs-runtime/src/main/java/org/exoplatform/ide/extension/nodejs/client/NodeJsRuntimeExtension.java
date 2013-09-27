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

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.nodejs.client.logs.LogsHandler;
import org.exoplatform.ide.extension.nodejs.client.run.RunApplicationManager;

/**
 * Node.js runtime extension.
 * 
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: NodeJsRuntimeExtension.java Apr 18, 2013 4:19:43 PM vsvydenko $
 *
 */
public class NodeJsRuntimeExtension extends Extension implements InitializeServicesHandler {
    public static final NodeJsExtensionAutoBeanFactory AUTO_BEAN_FACTORY = GWT
            .create(NodeJsExtensionAutoBeanFactory.class);

    public static final NodeJsExtensionLocalizationConstant NODEJS_LOCALIZATION = GWT.create(NodeJsExtensionLocalizationConstant.class);

    /** @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide
     * .client.framework.application.event.InitializeServicesEvent) */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new NodeJsRuntimeServiceImpl(Utils.getRestContext(), Utils.getWorkspaceName(), IDE.messageBus());
    }

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        new RunApplicationManager();
        new LogsHandler();
    }
}
