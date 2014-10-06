/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.jseditor.client.texteditor;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/** Resources interface for the editor. */
public interface EditorResources extends ClientBundle {
    /** CssResource for the editor. */
    public interface EditorCss extends CssResource {

        String lineWarning();

        String lineError();

        String annotation();
    }

    @Source({"Editor.css", "com/codenvy/ide/api/ui/style.css"})
    EditorCss editorCss();

    @Source("com/codenvy/ide/texteditor/squiggle.gif")
    ImageResource squiggle();

    @Source("com/codenvy/ide/texteditor/squiggle-warning.png")
    ImageResource squiggleWarning();
}
