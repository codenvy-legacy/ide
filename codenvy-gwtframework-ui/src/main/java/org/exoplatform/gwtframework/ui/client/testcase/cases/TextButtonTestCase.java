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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextButton;
import org.exoplatform.gwtframework.ui.client.component.TextButton.TextAlignment;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TextButtonTestCase extends TestCase {

    private static final String TEXT1 = "/home/vetal/ui/components";

    private static final String TEXT2 = "/rest/jcr/repository/production";

    private TextButton textButton;

    private ImageButton showHideStatusTextButton;

    private ImageButton changeStatusTextButton;

    private ImageButton setLeftAlignmentButton;

    private ImageButton setCenterAlignmentButton;

    private ImageButton setRightAlignmentButton;

    private ImageButton enableDisableCommandButton;

    private ImageButton setExecuteTypeButton;

    @Override
    public void draw() {
        FlowPanel panel = new FlowPanel();
        DOM.setStyleAttribute(panel.getElement(), "position", "relative");
        DOM.setStyleAttribute(panel.getElement(), "left", "50px");
        DOM.setStyleAttribute(panel.getElement(), "top", "50px");
        DOM.setStyleAttribute(panel.getElement(), "width", "z00px");
        DOM.setStyleAttribute(panel.getElement(), "height", "25px");
        //DOM.setStyleAttribute(panel.getElement(), "background", "#FFAAEE");
        testCasePanel().add(panel);

        textButton = new TextButton(TEXT1, textButtonCommand);
        textButton.setTitle("Title: " + TEXT1);
        textButton.setWidth("300px");
        panel.add(textButton);

        addButtonHeader("Visibility:");
        showHideStatusTextButton = createButton("Hide", showHideStatusTextButtonClickHandler);

        addButtonDelimiter("Text:");
        changeStatusTextButton = createButton("Change text", changeStatusTextButtonClickHandler);

        addButtonDelimiter("Text alignment");
        setLeftAlignmentButton = createButton("Align Text To Left", false, setLeftAlignmentButtonClickHandler);
        setCenterAlignmentButton = createButton("Align Text To Center", setCenterAlignmentButtonClickHandler);
        setRightAlignmentButton = createButton("Align Text To Right", setRightAlignmentButtonClickHandler);

        addButtonDelimiter("Command:");
        enableDisableCommandButton = createButton("Remove Command", enableDisableCommandButtonClickHandler);
        setExecuteTypeButton = createButton("Execute on Double Click", setExecuteTypeButtonClickHandler);
    }

    private ClickHandler showHideStatusTextButtonClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            if (textButton.isVisible()) {
                textButton.setVisible(false);
                showHideStatusTextButton.setText("Show");
            } else {
                textButton.setVisible(true);
                showHideStatusTextButton.setText("Hide");
            }
        }
    };

    private ClickHandler changeStatusTextButtonClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            if (TEXT1.equals(textButton.getText())) {
                textButton.setText(TEXT2);
                textButton.setTitle("Title: " + TEXT2);
            } else {
                textButton.setText(TEXT1);
                textButton.setTitle("Title: " + TEXT1);
            }
        }
    };

    private ClickHandler setLeftAlignmentButtonClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            textButton.setTextAlignment(TextAlignment.LEFT);
            setLeftAlignmentButton.setEnabled(false);
            setCenterAlignmentButton.setEnabled(true);
            setRightAlignmentButton.setEnabled(true);
        }
    };

    private ClickHandler setCenterAlignmentButtonClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            textButton.setTextAlignment(TextAlignment.CENTER);
            setLeftAlignmentButton.setEnabled(true);
            setCenterAlignmentButton.setEnabled(false);
            setRightAlignmentButton.setEnabled(true);
        }
    };

    private ClickHandler setRightAlignmentButtonClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            textButton.setTextAlignment(TextAlignment.RIGHT);
            setLeftAlignmentButton.setEnabled(true);
            setCenterAlignmentButton.setEnabled(true);
            setRightAlignmentButton.setEnabled(false);
        }
    };

    private Command textButtonCommand = new Command() {
        public void execute() {
            Window.alert("Click!");
        }
    };

    private ClickHandler enableDisableCommandButtonClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            if (textButton.getCommand() != null) {
                textButton.setCommand(null);
                enableDisableCommandButton.setText("Set Command");
                setExecuteTypeButton.setVisible(false);
            } else {
                textButton.setCommand(textButtonCommand);
                enableDisableCommandButton.setText("Remove Command");
                setExecuteTypeButton.setVisible(true);
            }
        }
    };

    private ClickHandler setExecuteTypeButtonClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            if (textButton.isExecuteCommandOnSingleClick()) {
                textButton.setExecuteCommandOnSingleClick(false);
                setExecuteTypeButton.setText("Execute on Single Click");
            } else {
                textButton.setExecuteCommandOnSingleClick(true);
                setExecuteTypeButton.setText("Execute on Double Click");
            }

        }
    };

}
