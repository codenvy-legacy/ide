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
package com.codenvy.ide.extension.runner.client.console.indicators;

import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.api.action.PropertyChangeEvent;
import com.codenvy.ide.api.action.PropertyChangeListener;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * View for {@link IndicatorAction}.
 * It contains caption and data separated by colon symbol.
 * Data may be text or URL.
 *
 * @author Artem Zatsarynnyy
 */
public class IndicatorView extends Composite {
    private final boolean          isURL;
    private final Presentation     presentation;
    private       Anchor           dataAnchor;
    private       InlineLabel      dataLabel;
    private       PropertyListener propertyListener;
    private       FlowPanel        panel;

    public IndicatorView(String caption, boolean isURL, int width, Presentation presentation, RunnerResources resources) {
        this.isURL = isURL;
        this.presentation = presentation;
        panel = new FlowPanel();

        InlineLabel captionLabel = new InlineLabel(caption + ':');
        panel.add(captionLabel);

        if (isURL) {
            dataAnchor = new Anchor();
            dataAnchor.setStyleName(resources.runner().dataLabel());
        } else {
            dataLabel = new InlineLabel();
            dataLabel.setStyleName(resources.runner().dataLabel());
            panel.add(dataLabel);
        }
        panel.ensureDebugId(caption);
        panel.setWidth(width + "px");
        panel.setStyleName(resources.runner().infoPanel());
        initWidget(panel);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (propertyListener == null) {
            propertyListener = new PropertyListener();
            presentation.addPropertyChangeListener(propertyListener);
        }
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        if (propertyListener != null) {
            presentation.removePropertyChangeListener(propertyListener);
            propertyListener = null;
        }
    }

    private void setData(String value) {
        if (isURL) {
            if (value != null) {
                if (value.length() > 20) {
                    dataAnchor.setText(value.substring(0, 10) + "..." + value.substring(value.length() - 10));
                } else {
                    dataAnchor.setText(value);
                }
                dataAnchor.setHref(value);
                dataAnchor.setTarget("_blank");
                if (!dataAnchor.isAttached())
                    panel.add(dataAnchor);
            } else {
                if (dataAnchor.isAttached())
                    dataAnchor.removeFromParent();
            }

        } else {
            dataLabel.setText(value == null ? "" : value);
        }
    }

    private void setHint(String value) {
        if (value == null) {
            value = "";
        }

        if (isURL) {
            dataAnchor.setTitle(value);
        } else {
            dataLabel.setTitle(value);
        }
    }

    private class PropertyListener implements PropertyChangeListener {
        @Override
        public void onPropertyChange(PropertyChangeEvent e) {
            switch (e.getPropertyName()) {
                case Properties.DATA_PROPERTY:
                    setData((String)e.getNewValue());
                case Properties.HINT_PROPERTY:
                    setHint((String)e.getNewValue());
            }
        }
    }
}
