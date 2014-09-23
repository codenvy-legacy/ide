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
package com.codenvy.ide.jseditor.client.preference;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;

public interface EditorPreferenceResource extends CellTable.Resources {

    @Source("editor-icon.svg")
    SVGResource editorPrefIcon();

    // use until SVG is supported as pref dialog icon
    @Source("editor-icon-temporary.png")
    ImageResource editorPrefIconTemporary();

    interface CellTableStyle extends CellTable.Style {
    }

    @Source({"CellTable-properties.css", "com/codenvy/ide/api/ui/style.css"})
    CellTableStyle cellTableStyle();

    public interface CellStyle extends CssResource {
        String prefCell();

        String selectWidth();

        String firstColumn();
    }

    @Source("CellStyle.css")
    CellStyle cellStyle();
}
