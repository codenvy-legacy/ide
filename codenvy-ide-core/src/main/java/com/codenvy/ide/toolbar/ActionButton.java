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
package com.codenvy.ide.toolbar;

import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ActionGroup;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.CustomComponentAction;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.api.action.PropertyChangeEvent;
import com.codenvy.ide.api.action.PropertyChangeListener;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

import org.vectomatic.dom.svg.ui.SVGImage;

/**
 * Toolbar image button.
 *
 * @author Evgen Vidolob
 */
public class ActionButton extends Composite implements MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseUpHandler,
                                                       ClickHandler {

    protected static final ToolbarResources.Css css = Toolbar.RESOURCES.toolbar();
    private final Presentation presentation;
    private final String       place;
    /** Command which will be executed when button was pressed. */
    protected     Action       action;
    private       FlowPanel    panel;
    /** Is enabled. */
    private boolean enabled  = true;
    /** Is button selected. */
    private boolean selected = false;
    private ActionManager            actionManager;
    private ActionButtonSynchronizer actionButtonSynchronizer;

    public ActionButton(final Action action, ActionManager actionManager, final Presentation presentation, String place) {
        this.actionManager = actionManager;
        panel = new FlowPanel();
        initWidget(panel);
        panel.setStyleName(css.iconButtonPanel());
        this.action = action;
        this.presentation = presentation;
        this.place = place;
        addDomHandlers();
        renderImage();
        setEnabled(presentation.isEnabled());
        setVisible(presentation.isVisible());
        if (presentation.getDescription() != null)
            panel.getElement().setAttribute("title", presentation.getDescription());
    }

    /** {@inheritDoc} */
    @Override
    protected void onLoad() {
        super.onLoad();
        if (actionButtonSynchronizer == null) {
            actionButtonSynchronizer = new ActionButtonSynchronizer();
            presentation.addPropertyChangeListener(actionButtonSynchronizer);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void onUnload() {
        super.onUnload();
        if (actionButtonSynchronizer != null) {
            presentation.removePropertyChangeListener(actionButtonSynchronizer);
            actionButtonSynchronizer = null;
        }
    }

    private void addDomHandlers() {
        panel.addDomHandler(this, MouseOverEvent.getType());
        panel.addDomHandler(this, MouseOutEvent.getType());
        panel.addDomHandler(this, MouseDownEvent.getType());
        panel.addDomHandler(this, MouseUpEvent.getType());
        panel.addDomHandler(this, ClickEvent.getType());
    }

    /** Redraw icon. */
    private void renderImage() {
        panel.clear();
        if (presentation.getSVGIcon() != null) {
            SVGImage image = new SVGImage(presentation.getSVGIcon());
            image.getElement().setAttribute("class", css.iconButtonIcon());
            panel.add(image);
        } else if (presentation.getIcon() != null) {
            Image img = new Image(presentation.getIcon());
            img.setStyleName(css.iconButtonIcon());
            panel.add(img);
        }
    }

    /** {@inheritDoc} */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            removeStyleName(css.disabled());
        } else {
            addStyleName(css.disabled());
        }
        this.ensureDebugId(place + "/" + actionManager.getId(action) + "-" + enabled);
    }

    /** {@inheritDoc} */
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            panel.setStyleName(css.iconButtonPanelSelected());
        } else {
            panel.setStyleName(css.iconButtonPanelOver());
        }

    }

    /** Mouse Over handler. */
    @Override
    public void onMouseOver(MouseOverEvent event) {
        if (!enabled) {
            return;
        }
        if (selected) {
            panel.setStyleName(css.iconButtonPanelSelectedOver());
        } else {
            panel.setStyleName(css.iconButtonPanelOver());
        }
    }

    /** Mouse Out handler. */
    @Override
    public void onMouseOut(MouseOutEvent event) {
        if (!enabled) {
            return;
        }
        if (selected) {
            panel.setStyleName(css.iconButtonPanelSelected());
        } else {
            panel.setStyleName(css.iconButtonPanel());
        }
    }

    /** Mouse Down handler. */
    @Override
    public void onMouseDown(MouseDownEvent event) {
        if (!enabled) {
            return;
        }
        if (selected) {
            panel.setStyleName(css.iconButtonPanelSelectedDown());
        } else {
            panel.setStyleName(css.iconButtonPanelDown());
        }
    }

    /** Mouse Up handler. */
    @Override
    public void onMouseUp(MouseUpEvent event) {
        if (!enabled) {
            return;
        }
        if (selected) {
            panel.setStyleName(css.iconButtonPanelSelectedOver());
        } else {
            panel.setStyleName(css.iconButtonPanelOver());
        }
    }

    /** Mouse Click handler. */
    @Override
    public void onClick(ClickEvent event) {
        if (!enabled) {
            return;
        }

        //todo handle popup group
        ActionEvent e = new ActionEvent(
                place,
                presentation,
                actionManager,
                0
        );
        if (action instanceof ActionGroup && !(action instanceof CustomComponentAction) && ((ActionGroup)action).isPopup()) {

        } else {
            action.actionPerformed(e);
        }
    }

    private class ActionButtonSynchronizer implements PropertyChangeListener {
        protected static final String SELECTED_PROPERTY_NAME = "selected";

        @Override
        public void onPropertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if (Presentation.PROP_TEXT.equals(propertyName)) {
                //TODO
//                updateToolTipText();
            } else if (Presentation.PROP_ENABLED.equals(propertyName)) {
                setEnabled((Boolean)e.getNewValue());
            } else if (Presentation.PROP_ICON.equals(propertyName)) {
                renderImage();
            } else if (Presentation.PROP_VISIBLE.equals(propertyName)) {
                setVisible((Boolean)e.getNewValue());
            } else if (SELECTED_PROPERTY_NAME.equals(propertyName)) {
                setSelected((Boolean)e.getNewValue());
            }
        }
    }
}
