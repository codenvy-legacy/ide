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
package com.codenvy.ide.api.parts;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/** @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> */
public interface PartStackUIResources extends ClientBundle {
    public interface PartStackCss extends CssResource {

        @ClassName("ide-PartStack")
        String idePartStack();

        @ClassName("ide-PartStack-Tab")
        String idePartStackTab();

        @ClassName("ide-PartStack-Tabs")
        String idePartStackTabs();

        @ClassName("CloseButton")
        String idePartStackTabCloseButton();

        @ClassName("ide-PartStack-Tab-selected")
        String idePartStackTabSelected();

        @ClassName("ide-PartStack-Editor-Content")
        String idePartStackEditorContent();

        @ClassName("ide-PartStack-focused")
        String idePartStackFocused();

        @ClassName("ide-PartStack-Tab-Left")
        String idePartStackTabLeft();

        @ClassName("ide-PartStack-Tab-Right")
        String idePartStackTabRight();

        @ClassName("ide-PartStack-Tab-Below")
        String idePartStackTabBelow();

        @ClassName("ide-Base-Part-Toolbar")
        String ideBasePartToolbar();

        @ClassName("ide-PartStack-Tab-Label")
        String idePartStackTabLabel();

        @ClassName("ide-PartStack-Content")
        String idePartStackContent();

        @ClassName("ide-PartStack-Tool-Tab")
        String idePartStackToolTab();

        @ClassName("ide-Base-Part-Toolbar-Bottom")
        String idePartStackToolbarBottom();

        @ClassName("ide-Base-Part-Toolbar-Separator")
        String idePartStackToolbarSeparator();

        @ClassName("ide-Base-Part-Toolbar-Bottom-Icon")
        String idePartStackToolbarBottomIcon();

        @ClassName("ide-PartStack-Tool-Tab-selected")
        String idePartStackToolTabSelected();

        @ClassName("ide-PartStack-Tab-Right-Button")
        String idePartStackTabRightButton();

        @ClassName("ide-PartStack-Tab-Button")
        String idePartStackTabButton();

        @ClassName("ide-PartStack-Tab-Button-selected")
        String idePartStackTabButtonSelected();

        @ClassName("ide-PartStack-Multiple-Tabs-Container")
        String idePartStackMultipleTabsContainer();

        @ClassName("ide-PartStack-Multiple-Tabs-Item")
        String idePartStackMultipleTabsItem();

        @ClassName("ide-Base-Part-Title-Label")
        String ideBasePartTitleLabel();

        @ClassName("ide-PartStack-Button-Left")
        String idePartStackButtonLeft();
    }

    @Source({"partstack.css", "com/codenvy/ide/api/ui/style.css"})
    PartStackCss partStackCss();

    ImageResource close();

    @Source("minimize.svg")
    SVGResource minimize();

    @Source("arrow.svg")
    SVGResource arrow();
}
