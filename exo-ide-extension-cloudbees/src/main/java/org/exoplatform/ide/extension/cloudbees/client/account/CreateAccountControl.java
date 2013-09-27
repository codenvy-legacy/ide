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
package org.exoplatform.ide.extension.cloudbees.client.account;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientBundle;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;

/**
 * Control for creating new CloudBees account.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 10, 2012 4:32:40 PM anya $
 */
@RolesAllowed({"developer"})
public class CreateAccountControl extends SimpleControl implements IDEControl {
    private static final String ID = CloudBeesExtension.LOCALIZATION_CONSTANT.controlCreateAccountId();

    private static final String TITLE = CloudBeesExtension.LOCALIZATION_CONSTANT.controlCreateAccountTitle();

    private static final String PROMPT = CloudBeesExtension.LOCALIZATION_CONSTANT.controlCreateAccountPrompt();

    public CreateAccountControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(CloudBeesClientBundle.INSTANCE.createAccount(), CloudBeesClientBundle.INSTANCE.createAccountDisabled());
        setEvent(new CreateAccountEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        setEnabled(true);
    }
}
