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
package org.exoplatform.ide.extension.cloudbees.client.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientBundle;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;

/**
 * Control for submenu for CloudBees.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: InitializeApplicationControl.java Jun 23, 2011 11:01:19 AM vereshchaka $
 */
@RolesAllowed({"workspace/developer"})
public class CloudBeesControl extends SimpleControl implements IDEControl {

    private static final String ID = CloudBeesExtension.LOCALIZATION_CONSTANT.cloudBeesControlId();

    private static final String TITLE = CloudBeesExtension.LOCALIZATION_CONSTANT.cloudBeesControlTitle();

    private static final String PROMPT = CloudBeesExtension.LOCALIZATION_CONSTANT.cloudBeesControlPrompt();

    /** @param id */
    public CloudBeesControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(CloudBeesClientBundle.INSTANCE.cloudBees(), CloudBeesClientBundle.INSTANCE.cloudBeesDisabled());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        setEnabled(true);
    }

}
