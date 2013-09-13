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
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: GwtTestSelectItem.java Mar 9, 2011 5:14:34 PM vereshchaka $
 */
public class GwtTestSelectItem extends GwtComponentTest {

    private final String SELECT_ITEM_NAME = "My Select Item Name";

    public void testCreationSelectItem() {
        SelectItem selectItem = new SelectItem();
        selectItem.setName(SELECT_ITEM_NAME);
        RootPanel.get().add(selectItem);
        String innerhtml = Document.get().getBody().getInnerHTML();

        assertTrue(innerhtml.contains(SELECT_ITEM_NAME));
    }

    /**
     * After click on empty combobox field,
     * suggest display list must appear.
     */
    public void testValueList() {
        final String[] listTypes1 = {"value 1", "value 2", "value 3", "value 4", "value 5"};
        SelectItem selectItem = new SelectItem();
        selectItem.setValueMap(listTypes1);

        RootPanel.get().add(selectItem);
        String innerhtml = Document.get().getBody().getInnerHTML();

        assertTrue(innerhtml.contains("<option value=\"value 1\">value 1</option>"));
        assertTrue(innerhtml.contains("<option value=\"value 2\">value 2</option>"));
        assertTrue(innerhtml.contains("<option value=\"value 3\">value 3</option>"));
        assertTrue(innerhtml.contains("<option value=\"value 4\">value 4</option>"));
        assertTrue(innerhtml.contains("<option value=\"value 5\">value 5</option>"));
    }
}
