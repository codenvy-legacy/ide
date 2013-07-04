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

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.event.CreateJavaClassEvent;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.GroupNames;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jan 10, 2012 2:05:22 PM anya $
 */
@RolesAllowed({"developer"})
public class CreateJavaClassControl extends JavaControl {

    public CreateJavaClassControl() {
        super("File/New/New Java Class");
        setTitle("Java Class");
        setPrompt("Create Java Class");
        setNormalImage(JdtClientBundle.INSTANCE.newClassWizz());
        setDisabledImage(JdtClientBundle.INSTANCE.newClassWizzDisabled());
        setEvent(new CreateJavaClassEvent());
        setGroupName(GroupNames.NEW_SCRIPT);
    }

}
