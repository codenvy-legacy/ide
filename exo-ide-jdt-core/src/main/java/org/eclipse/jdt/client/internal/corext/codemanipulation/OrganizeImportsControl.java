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
package org.eclipse.jdt.client.internal.corext.codemanipulation;

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.event.OrganizeImportsEvent;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RolesAllowed("developer")
public class OrganizeImportsControl extends JavaControl {

    /** @param id */
    public OrganizeImportsControl() {
        super("Edit/Organize Imports");
        setTitle("Organize Imports");
        setPrompt("Organize Imports");
        setEvent(new OrganizeImportsEvent());
        setHotKey("Ctrl+Shift+O");
        setImages(JdtClientBundle.INSTANCE.organizeImports(), JdtClientBundle.INSTANCE.organizeImportsDisabled());
    }

}
