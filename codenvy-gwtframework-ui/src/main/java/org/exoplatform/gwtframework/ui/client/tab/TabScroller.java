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
