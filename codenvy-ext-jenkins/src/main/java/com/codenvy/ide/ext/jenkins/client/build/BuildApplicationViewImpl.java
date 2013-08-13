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
package com.codenvy.ide.ext.jenkins.client.build;

import com.codenvy.ide.part.PartStackUIResources;
import com.codenvy.ide.part.base.BaseView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link BuildApplicationView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class BuildApplicationViewImpl extends BaseView<BuildApplicationView.ActionDelegate> implements BuildApplicationView {
    interface BuildApplicationViewImplUiBinder extends UiBinder<Widget, BuildApplicationViewImpl> {
    }

    private static BuildApplicationViewImplUiBinder ourUiBinder = GWT.create(BuildApplicationViewImplUiBinder.class);

    @UiField
    FlowPanel output;
    private int animationCharIndex = 1;
    private Label progress;

    /**
     * Create view.
     *
     * @param partStackUIResources
     */
    @Inject
    protected BuildApplicationViewImpl(PartStackUIResources partStackUIResources) {
        super(partStackUIResources);
        container.add(ourUiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void showMessageInOutput(String text) {
        if (progress != null) {
            output.remove(progress);
            output.add(new HTML(text));
            output.add(progress);
        } else {
            output.add(new HTML(text));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void startAnimation() {
        animationCharIndex = 1;

        progress = new Label();
        output.add(progress);

        animationTimer.scheduleRepeating(150);
    }

    /** {@inheritDoc} */
    @Override
    public void stopAnimation() {
        animationTimer.cancel();
        output.remove(progress);
        progress = null;
    }

    /** Animate of build progress. */
    private Timer animationTimer = new Timer() {
        @Override
        public void run() {
            String c = "";
            switch (animationCharIndex) {
                case 1:
                    c = "/";
                    break;

                case 2:
                    c = "-";
                    break;

                case 3:
                    c = "\\";
                    break;

                case 4:
                    c = "|";
                    break;
            }

            progress.setText(c);

            animationCharIndex++;
            if (animationCharIndex > 4) {
                animationCharIndex = 1;
            }
        }
    };

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }
}