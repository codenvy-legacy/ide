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
package org.eclipse.jdt.client.create;

import org.eclipse.jdt.client.event.CreatePackageEvent;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;
import org.exoplatform.ide.editor.java.client.JavaEditorExtension;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RolesAllowed({"developer"})
public class CreatePackageControl extends JavaControl {

    private static final String ID = "File/New/New Package";

    /** @param id */
    public CreatePackageControl() {
        super(ID);
        setTitle(JavaEditorExtension.MESSAGES.controlPackageTitle());
        setPrompt(JavaEditorExtension.MESSAGES.controlPackagePrompt());
        setImages(JavaClientBundle.INSTANCE.packageItem(), JavaClientBundle.INSTANCE.packageDisabled());
        setEvent(new CreatePackageEvent());
        setGroupName(GroupNames.NEW_COLLECTION);
    }

}
