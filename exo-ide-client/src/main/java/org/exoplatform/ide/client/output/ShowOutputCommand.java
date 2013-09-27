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
package org.exoplatform.ide.client.output;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.control.IDEControl;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Dec 22, 2011 12:20:39 PM evgen $
 */
public class ShowOutputCommand extends SimpleControl implements IDEControl {
    public static final String ID = "View/Output";

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.outputTitleControl();

    private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.outputPromptControl();

    /**
     *
     */
    public ShowOutputCommand() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(IDEImageBundle.INSTANCE.output(), IDEImageBundle.INSTANCE.outputDisabled());
        setEvent(new ShowOutputEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setEnabled(true);
        setVisible(true);
    }

}
