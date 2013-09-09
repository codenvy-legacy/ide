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
package org.eclipse.jdt.client.refactoring.rename;

import org.eclipse.jdt.client.JdtExtension;
import org.eclipse.jdt.client.internal.corext.codemanipulation.JavaControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;

/**
 * Control for rename refactoring.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RefactoringRenameControl.java Jan 18, 2013 11:32:03 AM azatsarynnyy $
 */
@RolesAllowed("developer")
public class RefactoringRenameControl extends JavaControl {

    public RefactoringRenameControl() {
        super(JdtExtension.LOCALIZATION_CONSTANT.refactoringRenameControlId());
        setTitle(JdtExtension.LOCALIZATION_CONSTANT.refactoringRenameControlTitle());
        setPrompt(JdtExtension.LOCALIZATION_CONSTANT.refactoringRenameControlPrompt());
        setEvent(new RefactoringRenameEvent());
        setHotKey("Alt+Shift+R");
        setImages(JavaClientBundle.INSTANCE.blankImage(), JavaClientBundle.INSTANCE.blankImage());

        // TODO remove from context menu while bug with group name will fixed
        //setShowInContextMenu(true);
    }

}
