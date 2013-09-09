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

package org.exoplatform.gwtframework.ui.client.testcase;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class TestCase extends Composite {

    interface TestCaseUiBinder extends UiBinder<Widget, TestCase> {
    }

    private static TestCaseUiBinder uiBinder = GWT.create(TestCaseUiBinder.class);

    protected FlowPanel buttonGroup;

    @UiField
    FlowPanel controlsPanel;

    @UiField
    FlowPanel testCasePanel;

    @UiField
    TableCellElement controlsElement;

    public abstract void draw();

    @Override
    protected void onAttach() {
        super.onAttach();
        draw();
    }

    @Override
    protected void onDetach() {
        super.onDetach();

        while (DOM.getChildCount(testCasePanel.getElement()) > 0) {
            Element e = DOM.getChild(testCasePanel.getElement(), 0);
            DOM.removeChild(testCasePanel.getElement(), e);
        }
    }

    public TestCase() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    protected void addButtonDelimiter() {
        HTML html = new HTML("<hr>");
        controlsPanel.add(html);
    }

    protected void addButtonDelimiter(String title) {
        HTML html = new HTML("<hr><font color=\"#008800\"><b>" + title + "</b></font><br>");
        controlsPanel.add(html);
    }

    protected void addButtonHeader(String title) {
        HTML html = new HTML("<font color=\"#008800\"><b>" + title + "</b></font><br>");
        controlsPanel.add(html);
    }

    public FlowPanel buttonsPanel() {
        return controlsPanel;
    }

    protected ImageButton createButton(String text, boolean enabled, ClickHandler clickHandler) {
        ImageButton button = new ImageButton(text);
        button.setEnabled(enabled);
        button.addClickHandler(clickHandler);

        if (buttonGroup != null) {
            buttonGroup.add(button);
            DOM.setStyleAttribute(button.getElement(), "float", "left");
            button.removeStyleName("gwt-Label");
        } else {
            controlsPanel.add(button);

            HTML html = new HTML("<br>");
            DOM.setStyleAttribute(html.getElement(), "height", "5px");
            controlsPanel.add(html);
        }

        return button;
    }

    protected ImageButton createButton(String text, ClickHandler clickHandler) {
        return createButton(text, true, clickHandler);
    }

    protected Widget createButton(String text, final Command command) {
        return createButton(text, new ClickHandler() {
            public void onClick(ClickEvent event) {
                command.execute();
            }
        });
    }

    protected void createButtonGroup() {
        buttonGroup = new FlowPanel();
        DOM.setStyleAttribute(buttonGroup.getElement(), "display", "inline");
        DOM.setStyleAttribute(buttonGroup.getElement(), "height", "30px");
        controlsPanel.add(buttonGroup);
    }

    protected void endButtonGroup() {
        buttonGroup = null;
    }

    public FlowPanel testCasePanel() {
        return testCasePanel;
    }

    public FlowPanel controlsPanel() {
        return controlsPanel;
    }

    public void hideControlsPanel() {
        controlsElement.getStyle().setProperty("display", "none");
    }

}
