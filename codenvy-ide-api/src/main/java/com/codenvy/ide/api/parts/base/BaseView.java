/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.parts.base;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.PartStackUIResources;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;

import org.vectomatic.dom.svg.ui.SVGImage;

import javax.validation.constraints.NotNull;

/**
 * Base view for part that must contains toolbar, this class provide one default button: "minimize" or hide part view and
 * label for view title.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
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
        titleLabel.setStyleName(resources.partStackCss().ideBasePartTitleLabel());

        SVGImage minimize = new SVGImage(resources.minimize());
        minimize.getElement().setAttribute("name", "workBenchIconMinimize");
        minimizeButton = new ToolButton(minimize);
        minimizeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                minimize();
            }
        });
        panel.addEast(minimizeButton, 29);
        panel.add(titleLabel);
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