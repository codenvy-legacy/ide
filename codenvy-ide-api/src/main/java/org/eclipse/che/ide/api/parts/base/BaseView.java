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
package org.eclipse.che.ide.api.parts.base;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.Focusable;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.vectomatic.dom.svg.ui.SVGImage;

import javax.annotation.Nonnull;

/**
 * Base view for part. By default the view has toolbar containing part description and minimize button.
 * Toolbar is represented as dock panel and can be simply expanded.
 *
 * @author Codenvy crowd
 */
public abstract class BaseView<T extends BaseActionDelegate> extends Composite implements View<T>, Focusable {

    /** Root widget */
    private DockLayoutPanel   container;

    protected DockLayoutPanel toolBar;
    protected DockLayoutPanel toolbarHeader;

    protected T               delegate;
    protected ToolButton      minimizeButton;
    protected Label           titleLabel;

    /** Indicates whether this view is focused */
    private boolean focused = false;

    /**
     * Creates an instance of this view.
     *
     * @param resources resources
     */
    public BaseView(PartStackUIResources resources) {
        container = new DockLayoutPanel(Style.Unit.PX);
        container.getElement().setAttribute("role", "part");
        container.setSize("100%", "100%");
        container.getElement().getStyle().setOutlineStyle(Style.OutlineStyle.NONE);
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

    /** {@inheritDoc} */
    @Override
    public final void setDelegate(T delegate) {
        this.delegate = delegate;
    }

    /** Requests delegate to minimize the part */
    protected void minimize() {
        if (delegate != null) {
            delegate.minimize();
        }
    }

    /**
     * Sets content widget.
     *
     * @param widget content widget
     */
    public final void setContentWidget(Widget widget) {
        container.add(widget);
    }

    /**
     * Sets new value of part title.
     *
     * @param title part title
     */
    public void setTitle(@Nonnull String title) {
        titleLabel.setText(title);
    }

    /**
     * Sets new height of the toolbar.
     *
     * @param height new toolbar height
     */
    public final void setToolbarHeight(int height) {
        container.setWidgetSize(toolBar, height);
    }

    /** {@inheritDoc} */
    @Override
    public final void setFocus(boolean focused) {
        this.focused = focused;
        if (focused) {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    focusView();
                }
            });
        }
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isFocused() {
        return focused;
    }

    /**
     * Override this method to set focus to necessary element inside the view.
     * Method is called when focusing the part view.
     */
    protected void focusView() {
        getElement().focus();
    }

}
