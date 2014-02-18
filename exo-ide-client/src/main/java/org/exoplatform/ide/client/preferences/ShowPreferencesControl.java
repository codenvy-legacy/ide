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
package org.exoplatform.ide.client.preferences;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;

/**
 * Control for showing and editing IDE preferences.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 18, 2012 12:47:10 PM anya $
 */
@RolesAllowed({"workspace/developer"})
public class ShowPreferencesControl extends SimpleControl implements IDEControl {
    public ShowPreferencesControl() {
        super(IDE.PREFERENCES_CONSTANT.showPreferencesControlId());
        setTitle(IDE.PREFERENCES_CONSTANT.showPreferencesControlTitle());
        setPrompt(IDE.PREFERENCES_CONSTANT.showPreferencesControlPrompt());
        setEvent(new ShowPreferencesEvent());
        setImages(IDEImageBundle.INSTANCE.preferences(), IDEImageBundle.INSTANCE.preferencesDisabled());
        setDelimiterBefore(true);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        setEnabled(true);
    }

}
