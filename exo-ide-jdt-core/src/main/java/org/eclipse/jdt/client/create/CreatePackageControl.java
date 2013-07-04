/*
 * Copyright (C) 2012 eXo Platform SAS.
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
