/*
 * Copyright (C) 2011 eXo Platform SAS.
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
