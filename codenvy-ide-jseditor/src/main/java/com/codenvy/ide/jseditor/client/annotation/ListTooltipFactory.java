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
package com.codenvy.ide.jseditor.client.annotation;

import elemental.dom.Element;
import elemental.html.LIElement;

import com.codenvy.ide.ui.Tooltip;
import com.codenvy.ide.ui.Tooltip.Builder;
import com.codenvy.ide.ui.Tooltip.TooltipPositionerBuilder;
import com.codenvy.ide.ui.Tooltip.TooltipRenderer;
import com.codenvy.ide.ui.menu.PositionController;
import com.codenvy.ide.ui.menu.PositionController.Positioner;
import com.codenvy.ide.ui.menu.PositionController.PositionerBuilder;
import com.codenvy.ide.util.dom.Elements;

/**
 * Factory for a tooltip that shows list of messages.
 */
public final class ListTooltipFactory {

    private ListTooltipFactory() {
    }

    /** Static factory method for creating a list tooltip. */
    public static Tooltip create(final Element targetElement,
                                 final String header,
                                 final PositionController.VerticalAlign vAlign,
                                 final PositionController.HorizontalAlign hAlign,
                                 final String... tooltipText) {
        final PositionerBuilder positionrBuilder = new TooltipPositionerBuilder()
                                                                                 .setVerticalAlign(vAlign)
                                                                                 .setHorizontalAlign(hAlign);
        final Positioner positioner = positionrBuilder.buildAnchorPositioner(targetElement);
        final Builder builder = new Builder(targetElement, positioner);
        builder.setTooltipRenderer(new ListRenderer(header, tooltipText));

        return builder.build();
    }

    private static class ListRenderer implements TooltipRenderer {
        private final String header;
        private final String[] tooltipText;

        ListRenderer(final String header, final String... tooltipText) {
            this.tooltipText = tooltipText;
            this.header = header;
        }

        @Override
        public Element renderDom() {
            final Element content = Elements.createSpanElement();
            content.setInnerText(header);
            final Element list = Elements.createUListElement();
            for (final String tooltip : tooltipText) {
                final LIElement item = Elements.createLiElement();
                item.appendChild(Elements.createTextNode(tooltip));
                list.appendChild(item);
            }
            content.appendChild(list);
            return content;
        }
    }
}
