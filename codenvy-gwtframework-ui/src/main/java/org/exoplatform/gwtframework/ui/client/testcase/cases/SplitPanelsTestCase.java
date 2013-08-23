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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import org.exoplatform.gwtframework.ui.client.component.IconButton;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.gwtframework.ui.client.menu.MenuBar;
import org.exoplatform.gwtframework.ui.client.menu.MenuItem;
import org.exoplatform.gwtframework.ui.client.tab.TabPanel;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;
import org.exoplatform.gwtframework.ui.client.wrapper.Wrapper;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SplitPanelsTestCase extends TestCase {

    @Override
    public void draw() {
        hideControlsPanel();

        VerticalPanel topPanel = new VerticalPanel();
        topPanel.setWidth("100%");
        topPanel.setHeight("100%");
        testCasePanel().add(topPanel);

        SplitLayoutPanel p = new SplitLayoutPanel();
        //DOM.setStyleAttribute(p.getElement(), "background", "#EEFF66");
        DOM.setStyleAttribute(p.getElement(), "background", "#EEAA99");
        p.setWidth("100%");
        p.setHeight("100%");

        topPanel.add(getMenu());
        topPanel.add(getToolbar());
        topPanel.add(p);
        topPanel.add(getStatusbar());

        topPanel.setCellHeight(p, "100%");

        p.addWest(getLeftPanel(), 300);
        p.addEast(getRightPanel(), 200);
        p.addSouth(wrap(getOperationPanel()), 150);
        p.add(wrap(getEditorPanel()));

    }

    private Widget getMenu() {
        FlowPanel menu = new FlowPanel();
        menu.setWidth("100%");
        menu.setHeight("20px");
        DOM.setStyleAttribute(menu.getElement(), "background", "#555555");

        MenuBar menuBar = new MenuBar();
        menu.add(menuBar);

        MenuItem fileItem = menuBar.addItem("File");
        {
            MenuItem newItem = fileItem.addItem("New");
            newItem.addItem("Text File");
            newItem.addItem("XML File");
            newItem.addItem("HTML File");
            newItem.addItem(null);
            newItem.addItem("Folder...");
        }

        MenuItem editItem = menuBar.addItem("Edit");
        {
            editItem.addItem("Cut");
            editItem.addItem("Copy");
            editItem.addItem("Paste");
        }

        MenuItem windowItem = menuBar.addItem("Window");
        {
            windowItem.addItem("Select Workspace...");
            windowItem.addItem(null);

            MenuItem viewItem = windowItem.addItem("Show View");
            viewItem.addItem("Properties");
            viewItem.addItem("Outline");
            viewItem.addItem("Preview");
            viewItem.addItem("Output");

        }

        MenuItem helpItem = menuBar.addItem("Help");
        {

            helpItem.addItem("About...");

        }

        return menu;
    }

    Image icon1 = new Image("showcase-images/properties.png");

    Image icon1Disabled = new Image("showcase-images/properties_Disabled.png");

    Image icon2 = new Image("showcase-images/refresh.png");

    Image icon2Disabled = new Image("showcase-images/refresh_Disabled.png");

    Image icon3 = new Image("showcase-images/no1.png");

    Image icon3Disabled = new Image("showcase-images/no1_Disabled.png");

    IconButton testButton = new IconButton(icon1, icon1Disabled);

    IconButton enableDisable = new IconButton(icon2, icon2Disabled);

    IconButton changeIcon = new IconButton(icon3, icon3Disabled);

    private Widget getToolbar() {
        Toolbar toolbar = new Toolbar();

        toolbar.addItem(testButton);
        toolbar.addItem(enableDisable);
        toolbar.addItem(changeIcon);

        testButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.alert("Click!");
            }
        });

        enableDisable.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (testButton.isEnabled()) {
                    testButton.setEnabled(false);
                } else {
                    testButton.setEnabled(true);
                }
            }
        });

        changeIcon.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (imageChanged) {
//               Image icon1 = new Image("showcase-images/properties.png");
//               Image icon1Disabled = new Image("showcase-images/properties_Disabled.png");
//
//               testButton.setImage(icon1, icon1Disabled);
                    imageChanged = false;
                } else {
//               Image icon2 = new Image("showcase-images/refresh.png");
//               Image icon2Disabled = new Image("showcase-images/refresh_Disabled.png");
//
//               testButton.setImages(icon2, icon2Disabled);
                    imageChanged = true;
                }
            }
        });

        return toolbar;
    }

    boolean imageChanged = false;

    private Widget getStatusbar() {
        FlowPanel statusBar = new FlowPanel();
        statusBar.setWidth("100%");
        statusBar.setHeight("28px");
        DOM.setStyleAttribute(statusBar.getElement(), "background", "#9999AA");
        return statusBar;
    }

    private Widget wrap(Widget w) {
        Wrapper wrapper = new Wrapper();
        wrapper.add(w);
        return wrapper;
    }

    private Widget getLeftPanel() {
        TabPanel tabPanel = new TabPanel();
        tabPanel.setWidth("100%");
        tabPanel.setHeight("100%");

        Image ws = new Image("showcase-images/workspace.png");
        Image search = new Image("showcase-images/search.png");

        FlowPanel pp = new FlowPanel();
        pp.setWidth("500px");
        pp.setHeight("600px");
        DOM.setStyleAttribute(pp.getElement(), "background", "#EE88FF");

        tabPanel.addTab("Workspace", ws, "Workspace", pp, true);
        tabPanel.addTab("Search", search, "Search", new HTML("Search"), true);

        FlowPanel control1 = new FlowPanel();
        control1.setWidth("16px");
        control1.setHeight("16px");
        DOM.setStyleAttribute(control1.getElement(), "background", "#FFEEAA");

        FlowPanel control2 = new FlowPanel();
        control2.setWidth("16px");
        control2.setHeight("16px");
        DOM.setStyleAttribute(control2.getElement(), "background", "#FFEEAA");

        tabPanel.addTabButton(control1);
        tabPanel.addTabButton(control2);

        return wrap(tabPanel);
    }

    private Widget getMigglePanel() {
        VerticalSplitPanel vSplitter = new VerticalSplitPanel();
        vSplitter.setWidth("100%");
        vSplitter.setHeight("100%");

        Widget editorPanel = getEditorPanel();
        Widget operatonPanel = getOperationPanel();
        vSplitter.setTopWidget(wrap(editorPanel));
        vSplitter.setBottomWidget(wrap(operatonPanel));
        vSplitter.setSplitPosition("75%");

        return vSplitter;
    }

    private Widget getRightPanel() {
        TabPanel tabPanel = new TabPanel();

        tabPanel.setWidth("100%");
        tabPanel.setHeight("100%");

        Image workspaceIcon = new Image("showcase-images/outline.png");
        tabPanel.addTab("Outline", workspaceIcon, "Outline", new HTML("Outline content"), true);

        return wrap(tabPanel);
    }

    private Widget getEditorPanel() {
        TabPanel tabPanel = new TabPanel();

        tabPanel.setWidth("100%");
        tabPanel.setHeight("100%");

        Image htmlIcon = new Image("showcase-images/mime-type/html.png");
        Image gadgetIcon = new Image("showcase-images/mime-type/gadget.png");

        tabPanel.addTab("html", htmlIcon, "My HTML file.html", new HTML("HTML file content"), true);
        tabPanel.addTab("gadget", gadgetIcon, "My First Gadget.xml", new HTML("Gadget content"), true);

        return tabPanel;
    }

    private Widget getOperationPanel() {
        TabPanel tabPanel = new TabPanel();
        tabPanel.setWidth("100%");
        tabPanel.setHeight("100%");

        Image iconOutput = new Image("showcase-images/output.png");
        Image iconProperties = new Image("showcase-images/properties.png");
        Image iconPreview = new Image("showcase-images/preview.png");

        tabPanel.addTab("Output", iconOutput, "Output", new HTML("Output content"), true);
        tabPanel.addTab("Properties", iconProperties, "Properties", new HTML("Properties content"), true);
        tabPanel.addTab("Preview", iconPreview, "Preview", new HTML("Preview content"), true);

        return tabPanel;
    }

}
