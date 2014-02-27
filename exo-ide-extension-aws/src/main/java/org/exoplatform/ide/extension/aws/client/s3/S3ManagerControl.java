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
@RolesAllowed({"workspace/developer"})
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
