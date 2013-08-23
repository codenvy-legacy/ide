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

package org.exoplatform.ide.client.editor;

import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.editor.ckeditor.CKEditor;
import org.exoplatform.ide.editor.codemirror.CodeMirror;

/**
 * @author <a href="mailto:dnochevnov@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public enum EditorType {

    SOURCE(org.exoplatform.ide.client.IDE.EDITOR_CONSTANT.editorControllerFileTabSourceView(), Images.Editor.SOURCE_BUTTON_ICON, 0),

    DESIGN(org.exoplatform.ide.client.IDE.EDITOR_CONSTANT.editorControllerFileTabDesignView(), Images.Editor.DESIGN_BUTTON_ICON, 1),

    DEFAULT(org.exoplatform.ide.client.IDE.EDITOR_CONSTANT.editorControllerFileTabSourceView(), Images.Editor.SOURCE_BUTTON_ICON, 0);

    private String label;

    private String iconUrl;

    /** Editor position within editor area started from 0. */
    private int position;

    EditorType(String label, String icon, int position) {
        this.label = label;
        this.iconUrl = icon;
        this.position = position;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return iconUrl;
    }

    /**
     * Get editor position within editor area started from 0.
     *
     * @return
     */
    public int getPosition() {
        return position;
    }

    public static EditorType getType(String editorClassName) {
        if (CodeMirror.class.getName().equals(editorClassName)) {
            return SOURCE;
        } else if (CKEditor.class.getName().equals(editorClassName)) {
            return DESIGN;
        }

        return DEFAULT;
    }
}
