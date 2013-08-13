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

package com.codenvy.ide.util.dom;

import elemental.css.CSSStyleDeclaration;
import elemental.html.Element;


/**
 * A class that computes and caches the width of a vertical scrollbar and height
 * of a horizontal scrollbar.
 */
public class ScrollbarSizeCalculator {

    public static final ScrollbarSizeCalculator INSTANCE = new ScrollbarSizeCalculator();

    private int heightOfHorizontalScrollbar = -1;

    private int widthOfVerticalScrollbar = -1;

    /** Calculates (or recalculates) the sizes of the scrollbars. */
    public void calculateSize() {
        Element container = createContainer();

        // No scrollbars
        container.getStyle().setOverflow(CSSStyleDeclaration.Overflow.HIDDEN);
        int noScrollbarClientHeight = container.getClientHeight();
        int noScrollbarClientWidth = container.getClientWidth();

        // Force scrollbars
        container.getStyle().setOverflow(CSSStyleDeclaration.Overflow.SCROLL);
        heightOfHorizontalScrollbar = noScrollbarClientHeight - container.getClientHeight();
        widthOfVerticalScrollbar = noScrollbarClientWidth - container.getClientWidth();

        container.removeFromParent();
    }

    private Element createContainer() {
        Element container = Elements.createDivElement();

        final int containerSize = 500;
        CSSStyleDeclaration containerStyle = container.getStyle();
        containerStyle.setWidth(containerSize, CSSStyleDeclaration.Unit.PX);
        containerStyle.setHeight(containerSize, CSSStyleDeclaration.Unit.PX);
        containerStyle.setPosition(CSSStyleDeclaration.Position.ABSOLUTE);
        containerStyle.setLeft(-containerSize, CSSStyleDeclaration.Unit.PX);
        containerStyle.setTop(-containerSize, CSSStyleDeclaration.Unit.PX);

        Elements.getBody().appendChild(container);

        return container;
    }

    /**
     * Gets the height of a horizontal scrollbar. This will calculate the size if
     * it has not already been calculated.
     */
    public int getHeightOfHorizontalScrollbar() {
        ensureSizeCalculated();
        return heightOfHorizontalScrollbar;
    }

    /**
     * Gets the width of a vertical scrollbar. This will calculate the size if it
     * has not already been calculated.
     */
    public int getWidthOfVerticalScrollbar() {
        ensureSizeCalculated();
        return widthOfVerticalScrollbar;
    }

    private void ensureSizeCalculated() {
        if (heightOfHorizontalScrollbar < 0 || widthOfVerticalScrollbar < 0) {
            calculateSize();
        }
    }
}
