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
package org.eclipse.jdt.client.internal.corext.codemanipulation;

import org.eclipse.jdt.client.event.AddGetterSetterEvent;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RolesAllowed("developer")
public class AddGetterSetterControl extends JavaControl {

    /** @param id */
    public AddGetterSetterControl() {
        super("Edit/Source/Generate Getters and Setters");
        setTitle("Generate Getters and Setters");
        setPrompt("Generate Getters and Setters");
        setEvent(new AddGetterSetterEvent());
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
