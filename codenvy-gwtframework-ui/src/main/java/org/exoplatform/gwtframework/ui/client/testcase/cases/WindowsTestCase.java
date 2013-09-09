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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.testcase.ShowCaseImageBundle;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;
import org.exoplatform.gwtframework.ui.client.window.Window;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class WindowsTestCase extends TestCase {

    @Override
    public void draw() {
        ImageButton showWindowButton = new ImageButton("Open Popup Window");
        testCasePanel().add(showWindowButton);
        showWindowButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openPopupWindow();
            }
        });

        testCasePanel().add(new HTML("<br>"));

        ImageButton showModalWindowButton = new ImageButton("Open Modal Window");
        testCasePanel().add(showModalWindowButton);
        showModalWindowButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openModalWindow();
            }
        });

    }

    private void openPopupWindow() {
        Window window = new Window("Popup Window");
        window.setAnimationEnabled(true);
        window.setCanClose(true);
        window.setModal(false);
        window.setWidth(500);
        window.setHeight(300);

        Widget contentWidget = new FlowPanel();
        contentWidget.setSize("100%", "100%");
        contentWidget.getElement().getStyle().setBackgroundColor("#CCCCFF");
        window.add(contentWidget);

        window.center();
        window.show();
    }

    private void openModalWindow() {
        Window window = new Window("Modal Window", new Image(ShowCaseImageBundle.INSTANCE.search()));
        window.setAnimationEnabled(true);
        window.setCanClose(true);
        window.setModal(true);
        window.setWidth(550);
        window.setHeight(250);
        window.setGlassEnabled(true);

        Widget contentWidget = new FlowPanel();
        contentWidget.setSize("100%", "100%");
        contentWidget.getElement().getStyle().setBackgroundColor("#AACC00");
        window.add(contentWidget);

        window.center();
        window.show();
    }

}
