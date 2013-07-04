/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
