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
package org.exoplatform.ide.extension.cloudfoundry.client.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientBundle;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;

/**
 * Control for Tier3 Web Fabric submenu.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: Tier3WebFabricControlGroup.java Apr 24, 2013 11:35:14 AM azatsarynnyy $
 *
 */
@RolesAllowed({"workspace/developer"})
public class Tier3WebFabricControlGroup extends SimpleControl implements IDEControl {

    private static final String ID = CloudFoundryExtension.LOCALIZATION_CONSTANT.tier3WebFabricControlId();

    private static final String TITLE = CloudFoundryExtension.LOCALIZATION_CONSTANT.tier3WebFabricControlTitle();

    private static final String PROMPT = CloudFoundryExtension.LOCALIZATION_CONSTANT.tier3WebFabricControlPrompt();

    public Tier3WebFabricControlGroup() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(CloudFoundryClientBundle.INSTANCE.tier3WebFabric16(),
                  CloudFoundryClientBundle.INSTANCE.tier3WebFabric16Disabled());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        setEnabled(true);
    }

}
