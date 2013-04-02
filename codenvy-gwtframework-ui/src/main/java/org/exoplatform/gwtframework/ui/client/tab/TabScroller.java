/**
 * Copyright (C) 2010 eXo Platform SAS.
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
 *
 */

package org.exoplatform.gwtframework.ui.client.tab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * This component combines two buttons for scrolling TabBar at the left and at the right.
 * <p/>
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TabScroller extends Composite {

    private static final String SCROLLER_LEFT_OVER = "tabPanelScrollerLeftOver";

    private static final String SCROLLER_RIGHT_OVER = "tabPanelScrollerRightOver";

    /** TabScroller UI Binder */
    interface ScrollButtonUiBinder extends UiBinder<Widget, TabScroller> {
    }

    /** TabScroller UI Binder instance */
    private static ScrollButtonUiBinder uiBinder = GWT.create(ScrollButtonUiBinder.class);

    /** Scroll Left Button */
    @UiField
    HTML scrollLeft;

    /** Scroll Right Button */
    @UiField
    HTML scrollRight;

    /** Scrollable panel */
    private Scrollable scrollable;

    /**
     * Creates new instance of TabScroller.
     *
     * @param scrollable
     *         Scrollable panel
     */
    public TabScroller(Scrollable scrollable) {
        this.scrollable = scrollable;

        initWidget(uiBinder.createAndBindUi(this));
    }

    /**
     * Mouse Click handler on ScrollLeft button.
     *
     * @param e
     *         event
     */
    @UiHandler("scrollLeft")
    void onClickLeft(ClickEvent e) {
        if (scrollable != null) {
            scrollable.scrollLeft();
        }
    }

    /**
     * Mouse Click handler on ScrollRight button.
     *
     * @param e
     *         event
     */
    @UiHandler("scrollRight")
    void onClickRight(ClickEvent e) {
        if (scrollable != null) {
            scrollable.scrollRight();
        }
    }

    /**
     * Mouse Out handler on ScrollLeft button.
     *
     * @param e
     *         event
     */
    @UiHandler("scrollLeft")
    void onMouseOutLeft(MouseOutEvent e) {
        scrollLeft.removeStyleName(SCROLLER_LEFT_OVER);
    }

    /**
     * Mouse Out handler on ScrollRight button.
     *
     * @param e
     *         event
     */
    @UiHandler("scrollRight")
    void onMouseOutRight(MouseOutEvent e) {
        scrollRight.removeStyleName(SCROLLER_RIGHT_OVER);
    }

    /**
     * Mouse Over handler on ScrollLeft button.
     *
     * @param e
     */
    @UiHandler("scrollLeft")
    void onMouseOverLeft(MouseOverEvent e) {
        scrollLeft.addStyleName(SCROLLER_LEFT_OVER);
    }

    /**
     * Mouse Over handler on ScrollRight button.
     *
     * @param e
     */
    @UiHandler("scrollRight")
    void onMouseOverRight(MouseOverEvent e) {
        scrollRight.addStyleName(SCROLLER_RIGHT_OVER);
    }

}
