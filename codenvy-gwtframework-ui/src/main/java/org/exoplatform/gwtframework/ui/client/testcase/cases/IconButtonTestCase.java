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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;

import org.exoplatform.gwtframework.ui.client.component.IconButton;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.testcase.ShowCaseImageBundle;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IconButtonTestCase extends TestCase {

    private IconButton iconButton;

    private ImageButton showHideButton;

    private ImageButton enableDisableButton;

    private ImageButton changeIconButton;

    private ImageButton selectDeselectButton;

    private String enabledIcon1;

    private String disabledIcon1;

    private String enabledIcon2;

    private String disabledIcon2;

    private ClickHandler showHideButtonClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            if (iconButton.isVisible()) {
                iconButton.setVisible(false);
                showHideButton.setText("Show");
            } else {
                iconButton.setVisible(true);
                showHideButton.setText("Hide");
            }
        }
    };

    private ClickHandler enableDisableButtonClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            if (iconButton.isEnabled()) {
                iconButton.setEnabled(false);
                enableDisableButton.setText("Enable");
            } else {
                iconButton.setEnabled(true);
                enableDisableButton.setText("Disable");
            }
        }
    };

    private ClickHandler changeIconButtonClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            if (enabledIcon1.equals(iconButton.getIcon())) {
                iconButton.setIcon(enabledIcon2);
                iconButton.setDisabledIcon(disabledIcon2);
            } else {
                iconButton.setIcon(enabledIcon1);
                iconButton.setDisabledIcon(disabledIcon1);
            }
        }
    };

    private ClickHandler selectDeselectButtonClickHandler = new ClickHandler() {
        public void onClick(ClickEvent event) {
            if (iconButton.isSelected()) {
                iconButton.setSelected(false);
                selectDeselectButton.setText("Select");
            } else {
                iconButton.setSelected(true);
                selectDeselectButton.setText("Remove Selection");
            }
        }
    };

    @Override
    public void draw() {
        FlowPanel panel = new FlowPanel();
        DOM.setStyleAttribute(panel.getElement(), "position", "relative");
        DOM.setStyleAttribute(panel.getElement(), "left", "50px");
        DOM.setStyleAttribute(panel.getElement(), "top", "50px");
        DOM.setStyleAttribute(panel.getElement(), "width", "300px");
        DOM.setStyleAttribute(panel.getElement(), "height", "100px");
        //DOM.setStyleAttribute(panel.getElement(), "background", "#FFAAEE");
        testCasePanel().add(panel);

        enabledIcon1 = ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.ok());
        disabledIcon1 = ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.okDisabled());
        enabledIcon2 = ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.cancel());
        disabledIcon2 = ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.cancelDisabled());

        iconButton = new IconButton(enabledIcon1, disabledIcon1);
        panel.add(iconButton);

        addButtonHeader("Visibility:");
        showHideButton = createButton("Hide", showHideButtonClickHandler);
        addButtonDelimiter("Enabling:");
        enableDisableButton = createButton("Disable", enableDisableButtonClickHandler);
        addButtonDelimiter("Icon:");
        changeIconButton = createButton("Change Icon", changeIconButtonClickHandler);
        addButtonDelimiter("Selection:");
        selectDeselectButton = createButton("Select", selectDeselectButtonClickHandler);
    }

}
