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
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public interface GadgetClientBundle extends ClientBundle {

    public static final GadgetClientBundle INSTANCE = GWT.create(GadgetClientBundle.class);

    @Source("bundled/deploy_gadget.png")
    ImageResource deployGadget();

    @Source("bundled/deploy_gadget_Disabled.png")
    ImageResource deployGadgetDisabled();

    @Source("bundled/undeploy_gadget.png")
    ImageResource undeployGadget();

    @Source("bundled/undeploy_gadget_Disabled.png")
    ImageResource undeployGadgetDisabled();

    @Source("bundled/preview.png")
    ImageResource preview();

    @Source("bundled/preview_Disabled.png")
    ImageResource previewDisabled();

}
