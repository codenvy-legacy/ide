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
package org.exoplatform.ide.extension.gadget.client;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.gadget.client.controls.ShowGadgetPreviewControl;
import org.exoplatform.ide.extension.gadget.client.service.GadgetService;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class GadgetExtension extends Extension implements InitializeServicesHandler {
    /** The generator of an {@link AutoBean}. */
    public static final GadgetAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(GadgetAutoBeanFactory.class);

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize(com.google.gwt.event.shared.HandlerManager) */
    @Override
    public void initialize() {
        IDE.getInstance().addControl(new ShowGadgetPreviewControl(), Docking.TOOLBAR_RIGHT);
        new GadgetPluginEventHandler();
        IDE.addHandler(InitializeServicesEvent.TYPE, this);
    }

    public void onInitializeServices(InitializeServicesEvent event) {
      
    }

}
