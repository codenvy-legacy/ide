/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
