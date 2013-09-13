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
package org.exoplatform.ide.client.progress;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 20, 2011 evgen $
 */
public class ShowProgressControl extends SimpleControl implements IDEControl {

    /**
     *
     */
    public ShowProgressControl() {
        super(IDE.IDE_LOCALIZATION_CONSTANT.progressControlId());
        setTitle(IDE.IDE_LOCALIZATION_CONSTANT.progressControlTitle());
        setPrompt(IDE.IDE_LOCALIZATION_CONSTANT.progressControlPrompt());
        setImages(IDEImageBundle.INSTANCE.progresImage(), IDEImageBundle.INSTANCE.progresImage());
        setDelimiterBefore(true);
        setEvent(new ShowProgressEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        setEnabled(true);
    }

}
