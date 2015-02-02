/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
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
import com.codenvy.ide.api.parts.Focusable;
import com.codenvy.ide.api.parts.PartStackUIResources;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;

import org.vectomatic.dom.svg.ui.SVGImage;

import javax.annotation.Nonnull;

/**
 * Base view for part that must contains toolbar, this class provide one default button: "minimize" or hide part view and
 * label for view title.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 */
public abstract class BaseView<T extends BaseActionDelegate> extends Composite implements View<T>, Focusable {

    protected DockLayoutPanel toolBar;
    protected DockLayoutPanel toolbarHeader;
    protected DockLayoutPanel container;
    protected T               delegate;
    protected ToolButton      minimizeButton;
    protected Label           titleLabel;

    private boolean focused = false;

    public BaseView(PartStackUIResources resources) {
        container = new DockLayoutPanel(Style.Unit.PX);
        container.getElement().setAttribute("role", "part");
        container.setSize("100%", "100%");

        initWidget(container);

        toolBar = new DockLayoutPanel(Style.Unit.PX);
        toolBar.addStyleName(resources.partStackCss().ideBasePartToolbar());
        toolBar.getElement().setAttribute("role", "toolbar");
        container.addNorth(toolBar, 22);

        //this hack used for adding box shadow effect to toolbar
        toolBar.getElement().getParentElement().getStyle().setOverflow(Style.Overflow.VISIBLE);

        toolbarHeader = new DockLayoutPanel(Style.Unit.PX);
        toolbarHeader.getElement().setAttribute("role", "toolbar-header");

        titleLabel = new Label();
        titleLabel.setStyleName(resources.partStackCss().ideBasePartTitleLabel());

        SVGImage minimize = new SVGImage(resources.minimize());
        minimize.getElement().setAttribute("name", "workBenchIconMinimize");
        minimizeButton = new ToolButton(minimize);
        minimizeButton.setTitle("Hide");
        minimizeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                minimize();
            }
        });
        toolbarHeader.addEast(minimizeButton, 29);
        toolbarHeader.add(titleLabel);

        toolBar.addNorth(toolbarHeader, 20);
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
    public void setTitle(@Nonnull String title) {
        titleLabel.setText(title);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(T delegate) {
        this.delegate = delegate;
    }

    @Override
    public final void setFocus(final boolean focused) {
        this.focused = focused;
        updateFocus();
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    /**
     * Override this method to set the focus to a special view element.
     * Method is called just after updating the view focus.
     */
    protected void updateFocus() {
    }

}
