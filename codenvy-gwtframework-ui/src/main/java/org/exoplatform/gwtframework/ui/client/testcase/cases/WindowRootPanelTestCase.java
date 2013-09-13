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
package org.exoplatform.gwtframework.ui.client.testcase.cases;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;
import org.exoplatform.gwtframework.ui.client.window.ResizeableWindow;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class WindowRootPanelTestCase extends TestCase {

    private AbsolutePanel region;

    @Override
    public void draw() {
        ImageButton showWindowButton = new ImageButton("Show Window Attached To Region");
        testCasePanel().add(showWindowButton);

        showWindowButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showWindowInRegion();
            }
        });

        region = new AbsolutePanel();
        region.setPixelSize(500, 300);
        region.getElement().getStyle().setBackgroundColor("#99AAFF");
        region.getElement().getStyle().setLeft(150, Unit.PX);
        region.getElement().getStyle().setTop(50, Unit.PX);
        region.getElement().getStyle().setOverflow(Overflow.HIDDEN);
        testCasePanel().add(region);
    }

    private void showWindowInRegion() {
        final ResizeableWindow window = new ResizeableWindow("Window in region");
        window.setCanClose(true);
        window.setCanMaximize(true);
        window.setModal(false);
        window.setWidth(300);
        window.setHeight(150);

        FlowPanel contentWidget = new FlowPanel();
        contentWidget.setSize("100%", "100%");
        contentWidget.getElement().getStyle().setBackgroundColor("#CCFFCC");
        window.add(contentWidget);

        ImageButton button = new ImageButton("Close this window");
        contentWidget.add(button);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                window.destroy();
            }
        });

        window.showCentered(region);
    }

}
