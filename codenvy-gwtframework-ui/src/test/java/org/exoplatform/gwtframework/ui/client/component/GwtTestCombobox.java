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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class GwtTestCombobox extends GwtComponentTest {

    private final String COMBOBOX_NAME = "My Test Combobox";

    private String innerHtml;

    public void testCreationButton() {
        ComboBoxField combobox = new ComboBoxField();
        combobox.setName(COMBOBOX_NAME);
        RootPanel.get().add(combobox);
        String innerhtml = Document.get().getBody().getInnerHTML();
        assertTrue(innerhtml.contains(COMBOBOX_NAME));
    }

    /**
     * After click on combobox,
     * select list must appear.
     */
    public void testClickEvent() {
        final String[] listTypes1 = {"value 1", "value 2", "value 3", "value 4", "value 5"};

        ComboBoxField combobox = new ComboBoxField();
        combobox.setName(COMBOBOX_NAME);
        combobox.setValueMap(listTypes1);

        RootPanel.get().add(combobox);
        combobox.image.getElement()
                .dispatchEvent(Document.get().createClickEvent(1, 0, 0, 0, 0, false, false, false, false));

        Timer timer = new Timer() {
            public void run() {
                innerHtml = Document.get().getBody().getInnerHTML();
                assertTrue(innerHtml.contains("value 1"));
                assertTrue(innerHtml.contains("value 2"));
                assertTrue(innerHtml.contains("value 3"));
                assertTrue(innerHtml.contains("value 4"));
                assertTrue(innerHtml.contains("value 5"));
                finishTest();
            }
        };
        // Set a delay period significantly longer than the
        // event is expected to take.
        delayTestFinish(500);
        // Schedule the event and return control to the test system.
        timer.schedule(100);
    }
}
