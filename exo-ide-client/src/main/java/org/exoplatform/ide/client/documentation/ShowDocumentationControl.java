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
package org.exoplatform.ide.client.documentation;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: ShowDocumentationControl Jan 21, 2011 11:10:08 AM evgen $
 */
public class ShowDocumentationControl extends SimpleControl implements IDEControl {

    public static final String ID = "View/Show \\ Hide Documentation";

    public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.documentationTitle();

    public static final String PROMPT_SHOW = IDE.IDE_LOCALIZATION_CONSTANT.showDocumentationControl();

    public static final String PROMPT_HIDE = IDE.IDE_LOCALIZATION_CONSTANT.hideDocumentationControl();

    /**
     *
     */
    public ShowDocumentationControl() {
        super(ID);
        setTitle(TITLE);
        setImages(IDEImageBundle.INSTANCE.documentation(), IDEImageBundle.INSTANCE.documentationDisabled());
        setEvent(new ShowDocumentationEvent(true));
        setEnabled(true);
        setDelimiterBefore(true);
        setCanBeSelected(true);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
    }

}
