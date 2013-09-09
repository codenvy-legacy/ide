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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.SelectItemResource;
import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SelectItemsTestCase extends TestCase {

    public static final SelectItemResource resource = GWT.create(SelectItemResource.class);

    @Override
    public void draw() {
        final FlowPanel panel = new FlowPanel();
        DOM.setStyleAttribute(panel.getElement(), "position", "relative");
        DOM.setStyleAttribute(panel.getElement(), "width", "100%");
        DOM.setStyleAttribute(panel.getElement(), "height", "100px");

        final SelectItem dropBox1 = new SelectItem();
        String[] listTypes1 =
                {"value 1", "value 2", "value 3", "value 4", "value 5", "value 6", "value 7", "value 8", "value 9",
                 "value 10", "value 11", "value 12", "value 13", "value 14", "value 15", "value 16", "value 17", "value 18",
                 "value 19", "value 20", "value 21", "value 22", "value 23", "value 24", "value 25", "value 26", "value 27",
                 "value 28", "value 29", "value 30", "value 31", "value 32", "value 33", "value 34", "value 35", "value 36",
                 "value 37", "value 38", "value 39"};
        dropBox1.setValueMap(listTypes1);
        dropBox1.setWidth(150);

        panel.add(dropBox1);

        final SelectItem dropBox = new SelectItem();
        String[] listTypes = {"value 1", "value 2", "value 3"};
        dropBox.setValueMap(listTypes);
        dropBox.setTitle("abc");
        dropBox.setWidth(120);
        dropBox.setEnabled(false);

        panel.add(dropBox);

        //---Multi select-------------
        String[] words =
                {"aord 1", "aword 2", "aword 3", "word 4", "word 5", "word 11", "word 12", "word 13", "word 14",
                 "word 15", "word 21", "word 22", "word 23", "word 24",
                 "word 25", "word 31", "word 32", "word 33", "word 34", "word 35", "word 41", "word 42", "word 43",
                 "word 44", "word 45"};

        final ComboBoxField combobox = new ComboBoxField();
        combobox.setValueMap(words);
        VerticalPanel vp = new VerticalPanel();
        Label label = new Label("sample label");
        vp.add(label);
        vp.add(combobox);
        combobox.setWidth(350);
        combobox.setHeight("20px");

        ImageButton cc = new ImageButton("Size");
        cc.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                combobox.setWidth(300);
            }
        });

        panel.add(cc);
        panel.add(vp);
        testCasePanel().add(panel);
    }

}
