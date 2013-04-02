/**
 * Copyright (C) 2010 eXo Platform SAS.
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
 *
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
