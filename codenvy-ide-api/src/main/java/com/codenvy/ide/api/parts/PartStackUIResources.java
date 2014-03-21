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
package com.codenvy.ide.api.parts;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

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

//        @ClassName("ide-PartStack-Tab-selected-Below")
//        String idePartStackTabSelectedBelow();

        @ClassName("ide-Base-Part-Toolbar")
        String ideBasePartToolbar();

        @ClassName("ide-PartStack-Tab-Label")
        String idePartStackTabLabel();

        @ClassName("ide-PartStack-Content")
        String idePartStackContent();

        @ClassName("ide-PartStack-Tool-Tab")
        String idePartStackToolTab();

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
        
        @ClassName("ide-button-micro")
        String ideButtonMicro();

        @ClassName("ide-image-icon-minimize")
        String ideImageIconMinimize();
    }

    @Source({"partstack.css", "com/codenvy/ide/api/ui/style.css"})
    PartStackCss partStackCss();

    ImageResource close();

    @Source("thin_min_view.png")
    ImageResource minimize();
}
