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
package org.exoplatform.ide.extension.aws.client.ec2;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.aws.client.AWSClientBundle;
import org.exoplatform.ide.extension.aws.client.AWSExtension;

/**
 * Control to manage Amazon EC2 virtual sever instances.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EC2ManagerControl.java Sep 21, 2012 9:55:35 AM azatsarynnyy $
 */
@RolesAllowed("developer")
public class EC2ManagerControl extends SimpleControl implements IDEControl {
    private static final String ID = AWSExtension.LOCALIZATION_CONSTANT.ec2ManagementControlId();

    private static final String TITLE = AWSExtension.LOCALIZATION_CONSTANT.ec2ManagementControlTitle();

    private static final String PROMPT = AWSExtension.LOCALIZATION_CONSTANT.ec2ManagementControlPrompt();

    public EC2ManagerControl() {
        super(ID);
        setImages(AWSClientBundle.INSTANCE.ec2(), AWSClientBundle.INSTANCE.ec2Disabled());
        setTitle(TITLE);
        setPrompt(PROMPT);

        setEvent(new ShowEC2ManagerEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        setEnabled(true);
    }

}
