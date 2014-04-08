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
package com.codenvy.ide.api.parts.base;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.PartStackUIResources;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import javax.validation.constraints.NotNull;

/**
 * Base view for part that must contains toolbar, this class provide one default button: "minimize" or hide part view and
 * label for view title
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public abstract class BaseView<T extends BaseActionDelegate> extends Composite implements View<T> {

    protected DockLayoutPanel toolBar;
    protected DockLayoutPanel container;
    protected T               delegate;
    protected ToolButton      minimizeButton;
    protected Label           titleLabel;

    public BaseView(PartStackUIResources resources) {
        container = new DockLayoutPanel(Style.Unit.PX);
        initWidget(container);
        container.setSize("100%", "100%");
        toolBar = new DockLayoutPanel(Style.Unit.PX);
        toolBar.addStyleName(resources.partStackCss().ideBasePartToolbar());
        container.addNorth(toolBar, 22);

        //this hack used for adding box shadow effect to toolbar
        toolBar.getElement().getParentElement().getStyle().setOverflow(Style.Overflow.VISIBLE);
        
        
        DockLayoutPanel panel = new DockLayoutPanel(Style.Unit.PX);
        titleLabel = new Label();
        titleLabel.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        titleLabel.getElement().getStyle().setLineHeight(20, Style.Unit.PX);
        titleLabel.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        titleLabel.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        panel.addWest(titleLabel, 100);
        
        Image minimize = new Image(resources.minimize());
        minimize.getElement().setAttribute("name", "workBenchIconMinimize");
        minimizeButton = new ToolButton(minimize);
        minimizeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                minimize();
            }
        });
        panel.addEast(minimizeButton, 20);
        
        toolBar.addNorth(panel, 20);
    }

    /** Call minimize on delegate. */
    protected void minimize() {
        if (delegate != null)
            delegate.minimize();
    }

    /**
     * Set title for this part view.
     *
     * @param title
     */
    public void setTitle(@NotNull String title) {
        titleLabel.setText(title);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(T delegate) {
        this.delegate = delegate;
    }
}