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
package org.exoplatform.ide.client.edit.control;

import org.exoplatform.gwtframework.ui.client.command.TextInputControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@RolesAllowed({"workspace/developer"})
public class QuickTextSearchControl extends TextInputControl implements IDEControl {

    public static final String ID = "Quick search";

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.quickTextSearchControl();

    /**
     *
     */
    public QuickTextSearchControl() {
        super(ID);
        setPrompt(TITLE);
        setNormalImage(IDEImageBundle.INSTANCE.findText());
        setDisabledImage(IDEImageBundle.INSTANCE.findTextDisabled());
        setSize(150);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setEnabled(true);
        setVisible(true);
    }

}
