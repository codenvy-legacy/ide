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
package org.exoplatform.ide.extension.aws.client.s3;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.aws.client.AWSClientBundle;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.s3.events.ShowS3ManagerEvent;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: S3ManagerControl.java Sep 19, 2012 vetal $
 */
@RolesAllowed("developer")
public class S3ManagerControl extends SimpleControl implements IDEControl

{
    private static final String ID = AWSExtension.LOCALIZATION_CONSTANT.s3ManagementControlId();

    private static final String TITLE = AWSExtension.LOCALIZATION_CONSTANT.s3ManagementControlTitle();

    private static final String PROMPT = AWSExtension.LOCALIZATION_CONSTANT.s3ManagementControlPrompt();

    public S3ManagerControl() {
        super(ID);
        setImages(AWSClientBundle.INSTANCE.s3(), AWSClientBundle.INSTANCE.s3());
        setTitle(TITLE);
        setPrompt(PROMPT);

        setEvent(new ShowS3ManagerEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        setEnabled(true);
    }
}
