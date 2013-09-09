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
package org.exoplatform.ide.extension.heroku.client.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.heroku.client.HerokuClientBundle;

/**
 * Control for grouping all Heroku controls.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 31, 2011 3:32:33 PM anya $
 */
@RolesAllowed({"developer"})
public class HerokuControlsGroup extends SimpleControl implements IDEControl {
    /** Control ID. */
    public static final String ID = "PaaS/Heroku";

    /** Control's title. */
    public static final String TITLE = "Heroku";

    /** Control's prompt, when user hovers the mouse on it. */
    public static final String PROMPT = "Heroku";

    public HerokuControlsGroup() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(HerokuClientBundle.INSTANCE.heroku(), HerokuClientBundle.INSTANCE.herokuDisabled());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        setEnabled(true);
    }
}
