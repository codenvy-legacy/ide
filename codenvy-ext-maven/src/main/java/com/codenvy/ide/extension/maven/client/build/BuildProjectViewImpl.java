/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.extension.maven.client.build;

import com.codenvy.ide.extension.maven.client.BuilderResources;
import com.codenvy.ide.part.PartStackUIResources;
import com.codenvy.ide.part.base.BaseView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link BuildProjectView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class BuildProjectViewImpl extends BaseView<BuildProjectView.ActionDelegate> implements BuildProjectView {
    private static BuildProjectViewImplUiBinder uiBinder = GWT.create(BuildProjectViewImplUiBinder.class);

    interface BuildProjectViewImplUiBinder extends UiBinder<Widget, BuildProjectViewImpl> {
    }

    @UiField
    Button    btnClearOutput;
    @UiField
    FlowPanel output;
    private int animationCharIndex = 1;
    private Label progress;

    /**
     * Create view.
     *
     * @param resources
     * @param partStackUIResources
     */
    @Inject
    protected BuildProjectViewImpl(BuilderResources resources, PartStackUIResources partStackUIResources) {
        super(partStackUIResources);
        container.add(uiBinder.createAndBindUi(this));

        btnClearOutput.setHTML(new Image(resources.clearOutput()).toString());
    }

    /** {@inheritDoc} */
    @Override
    public void showMessageInOutput(String text) {
        output.add(new HTML(text));
    }

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
    public void clearOutput() {
        output.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void setClearOutputButtonEnabled(boolean isEnabled) {
        btnClearOutput.setEnabled(isEnabled);
    }

    @UiHandler("btnClearOutput")
    void onBtnCancelClick(ClickEvent event) {
        delegate.onClearOutputClicked();
    }
}