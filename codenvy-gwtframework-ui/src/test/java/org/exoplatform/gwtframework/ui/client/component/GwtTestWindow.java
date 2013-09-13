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
package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;

import org.exoplatform.gwtframework.ui.client.window.CloseClickHandler;
import org.exoplatform.gwtframework.ui.client.window.MaximizeHandler;
import org.exoplatform.gwtframework.ui.client.window.Window;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 10, 2011 3:55:48 PM anya $
 */
public class GwtTestWindow extends GwtComponentTest {
    private final String WINDOW_TITLE = "Test Title";

    private final int WINDOW_WIDTH = 200;

    private final int WINDOW_HEIGHT = 200;

    private boolean closeClick;

    private boolean maximizeClick;

    /** Test simple window's creation. */
    public void testCreateWindow() {
        Window window = new Window(WINDOW_TITLE);
        window.setWidth(WINDOW_WIDTH);
        window.setHeight(WINDOW_HEIGHT);
        window.center();
        window.show();

        assertEquals(WINDOW_TITLE, window.getTitle());
        assertTrue(DOM.getInnerHTML(window.getElement()).contains(WINDOW_TITLE));
    }

    /** Test close window button and close click handler. */
    public void testCloseWindow() {
        closeClick = false;
        final Window window = new Window(WINDOW_TITLE);
        window.setWidth(WINDOW_WIDTH);
        window.setHeight(WINDOW_HEIGHT);
        window.setCanClose(true);

        window.show();

        assertTrue(window.isCanClose());
        Element closeButton = getWindowButtonByPrompt("Close", window);
        assertNotNull(closeButton);

        window.addCloseClickHandler(new CloseClickHandler() {
            public void onCloseClick() {
                closeClick = true;
            }
        });

        closeButton.dispatchEvent(Document.get().createClickEvent(1, 0, 0, 0, 0, false, false, false, false));
        Timer timer = new Timer() {
            public void run() {
                assertTrue(closeClick);
                finishTest();
            }
        };
        delayTestFinish(500);
        timer.schedule(100);
    }

    /** Test maximizing window and it's handler. */
    public void testMaximizeWindow() {
        maximizeClick = false;
        final Window window = new Window(WINDOW_TITLE);
        window.setWidth(WINDOW_WIDTH);
        window.setHeight(WINDOW_HEIGHT);
        window.setCanMaximize(true);

        window.show();

        assertTrue(window.isCanMaximize());
        Element maximizeButton = getWindowButtonByPrompt("Maximize", window);
        assertNotNull(maximizeButton);

        window.addMaximizeHandler(new MaximizeHandler() {
            public void onMaximize() {
                maximizeClick = true;
            }
        });

        maximizeButton.dispatchEvent(Document.get().createClickEvent(1, 0, 0, 0, 0, false, false, false, false));
        Timer timer = new Timer() {
            public void run() {
                assertTrue(maximizeClick);
                finishTest();
            }
        };
        delayTestFinish(500);
        timer.schedule(100);
    }

    /**
     * Get button's element by title.
     *
     * @param prompt
     *         button's prompt
     * @param window
     *         window
     * @return
     */
    private Element getWindowButtonByPrompt(String prompt, Window window) {
        NodeList<Element> imgs = window.getElement().getElementsByTagName("img");
        for (int i = 0; i < imgs.getLength(); i++) {
            if (prompt.equals(imgs.getItem(i).getAttribute("title"))) {
                return imgs.getItem(i);
            }
        }
        return null;
    }
}
