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
package org.exoplatform.ide.extension.logreader.client.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.logreader.client.LogReaderClientBundle;
import org.exoplatform.ide.extension.logreader.client.event.ShowLogReaderEvent;

/**
 * Control to open LogReader View
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
@RolesAllowed("developer")
public class LogReaderControl extends SimpleControl implements IDEControl {
    /** Control ID. */
    public static final String ID = "View/Log";

    /** Control's title. */
    public static final String TITLE = "Log";

    /** Control's prompt, when user hovers the mouse on it. */
    public static final String PROMPT = "Show Log form";

    public LogReaderControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(LogReaderClientBundle.INSTANCE.logReader(), LogReaderClientBundle.INSTANCE.logReaderDisabled());
        setEvent(new ShowLogReaderEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        setEnabled(true);
    }

}
