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
package org.exoplatform.ide.extension.aws.client.beanstalk;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.aws.client.AWSClientBundle;
import org.exoplatform.ide.extension.aws.client.AWSExtension;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 17, 2012 10:15:42 AM anya $
 */
@RolesAllowed({"workspace/developer"})
public class BeanstalkControl extends SimpleControl implements IDEControl {
    private static final String ID = AWSExtension.LOCALIZATION_CONSTANT.beanstalkControlId();

    private static final String TITLE = AWSExtension.LOCALIZATION_CONSTANT.beanstalkControlTitle();

    private static final String PROMPT = AWSExtension.LOCALIZATION_CONSTANT.beanstalkControlPrompt();

    public BeanstalkControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(AWSClientBundle.INSTANCE.elasticBeanstalk(), AWSClientBundle.INSTANCE.elasticBeanstalkDisabled());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        setEnabled(true);
    }
}
