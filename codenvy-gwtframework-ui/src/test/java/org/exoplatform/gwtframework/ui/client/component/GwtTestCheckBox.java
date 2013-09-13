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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

import org.exoplatform.gwtframework.ui.client.GwtResources;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 10, 2011 3:14:59 PM anya $
 */
public class GwtTestCheckBox extends GwtComponentTest {
    private final String CHECKBOX_TITLE = "Test Title";

    private final String CHECKBOX_NAME = "testName";

    private final int CHECKBOX_WIDTH = 100;

    private final int CHECKBOX_HEIGHT = 22;

    /** Test the creation of the check box. */
    public void testCreateCheckBox() {
        CheckboxItem checkbox = new CheckboxItem(CHECKBOX_NAME, CHECKBOX_TITLE);
        checkbox.setWidth(CHECKBOX_WIDTH + "px");
        checkbox.setHeight(CHECKBOX_HEIGHT + "px");
        RootPanel.get().add(checkbox);
        assertEquals(1, checkbox.getElement().getElementsByTagName("input").getLength());
        Element checkElement = checkbox.getElement().getElementsByTagName("input").getItem(0);
        assertEquals("checkbox", checkElement.getAttribute("type"));
        assertEquals(CHECKBOX_NAME, checkElement.getAttribute("name"));
        assertEquals(CHECKBOX_NAME, checkbox.getName());

        assertTrue(DOM.getInnerHTML(checkbox.getElement()).contains(CHECKBOX_TITLE));
    }

    /** Test the title checkbox title's usage. */
    public void testCheckBoxTitle() {
        CheckboxItem checkbox = new CheckboxItem(CHECKBOX_NAME, CHECKBOX_TITLE);
        checkbox.setWidth(CHECKBOX_WIDTH + "px");
        checkbox.setHeight(CHECKBOX_HEIGHT + "px");
        RootPanel.get().add(checkbox);
        assertEquals(CHECKBOX_TITLE, checkbox.getTitle());

        //Show title at the left side of the checkbox:
        checkbox.setLabelAsTitle(true);
        assertEquals(1, checkbox.getElement().getElementsByTagName("label").getLength());
        Element labelElement = checkbox.getElement().getElementsByTagName("label").getItem(0);
        assertTrue(labelElement.getClassName().contains(GwtResources.INSTANCE.css().checkBoxTitleLeft()));
        assertTrue(labelElement.getInnerHTML().contains(CHECKBOX_TITLE));

        //Change title:
        checkbox.setTitle("new" + CHECKBOX_TITLE);
        assertTrue(labelElement.getInnerHTML().contains("new" + CHECKBOX_TITLE));
        assertEquals("new" + CHECKBOX_TITLE, checkbox.getTitle());
    }
}
