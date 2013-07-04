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

import org.eclipse.jdt.client.event.GenerateNewConstructorUsingFieldsEvent;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RolesAllowed("developer")
public class GenerateNewConstructorUsingFieldsControl extends JavaControl {

    /** @param id */
    public GenerateNewConstructorUsingFieldsControl() {
        super("Edit/Source/Generate Constructor using Fields");
        setTitle("Generate Constructor using Fields...");
        setPrompt("Generate Constructor using Fields...");
        setEvent(new GenerateNewConstructorUsingFieldsEvent());
        setImages(JavaClientBundle.INSTANCE.blankImage(), JavaClientBundle.INSTANCE.blankImage());
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     *      .ide.client.framework.editor.event.EditorActiveFileChangedEvent)
     */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        if (event.getEditor() == null) {
            setEnabled(false);
            setShowInContextMenu(false);
        } else {
            if (event.getFile().getMimeType().equals(MimeType.APPLICATION_JAVA)) {
                setEnabled(true);
                setShowInContextMenu(true);
            } else {
                setEnabled(false);
                setShowInContextMenu(false);
            }
        }
    }
    
}
