/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.extension.builder.client.console;

import com.codenvy.ide.api.ui.action.Presentation;
import com.codenvy.ide.api.ui.action.PropertyChangeEvent;
import com.codenvy.ide.api.ui.action.PropertyChangeListener;
import com.codenvy.ide.extension.builder.client.BuilderResources;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * View for {@link InfoAction}.
 * It contains caption and data separated by colon symbol.
 * Data may be text or URL.
 *
 * @author Artem Zatsarynnyy
 */
public class InfoLabel extends Composite {
    private final boolean          isURL;
    private final Presentation     presentation;
    private       Anchor           dataAnchor;
    private       InlineLabel      dataLabel;
    private       PropertyListener propertyListener;

    public InfoLabel(String caption, boolean isURL, Presentation presentation, BuilderResources resources) {
        this.isURL = isURL;
        this.presentation = presentation;

        FlowPanel panel = new FlowPanel();
        InlineLabel captionLabel = new InlineLabel(caption + ':');
        panel.add(captionLabel);

        if (isURL) {
            dataAnchor = new Anchor();
            dataAnchor.setStyleName(resources.builder().dataLabel());
            panel.add(dataAnchor);
        } else {
            dataLabel = new InlineLabel();
            dataLabel.setStyleName(resources.builder().dataLabel());
            panel.add(dataLabel);
        }
        panel.ensureDebugId(caption);
        panel.setStyleName(resources.builder().infoPanel());
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
        if (value == null) {
            value = "";
        }

        if (isURL) {
            if (value.length() > 20) {
                dataAnchor.setText(value.substring(0, 10) + "..." + value.substring(value.length()-10));
            } else {
                dataAnchor.setText(value);
            }
            dataAnchor.setHref(value);
            dataAnchor.setTarget("_blank");
        } else {
            dataLabel.setText(value);
        }
    }

    private class PropertyListener implements PropertyChangeListener {
        @Override
        public void onPropertyChange(PropertyChangeEvent e) {
            if (Properties.DATA_PROPERTY.equals(e.getPropertyName())) {
                setData((String)e.getNewValue());
            }
        }
    }
}
