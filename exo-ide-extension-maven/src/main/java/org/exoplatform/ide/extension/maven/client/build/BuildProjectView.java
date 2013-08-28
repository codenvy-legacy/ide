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
package org.exoplatform.ide.extension.maven.client.build;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.IconButton;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.gwtframework.ui.client.component.ToolbarItem;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.maven.client.BuilderClientBundle;
import org.exoplatform.ide.extension.maven.client.BuilderExtension;

/**
 * View for build project by maven builder.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildProjectView.java Feb 17, 2012 6:20:16 PM azatsarynnyy $
 */
public class BuildProjectView extends ViewImpl implements BuildProjectPresenter.Display {
    /** Identifier of view. */
    private static final String ID = BuilderExtension.LOCALIZATION_CONSTANT.buildProjectId();

    /** Title of view. */
    private static final String TITLE = BuilderExtension.LOCALIZATION_CONSTANT.buildProjectTitle();

    private static final String CLEAR_OUTPUT = BuilderExtension.LOCALIZATION_CONSTANT.outputClear();

    private static final int HEIGHT = 450;

    private static final int WIDTH = 250;

    /** Animation of build progress is enabled. */
    private boolean animationEnabled = false;

    private int animationCharIndex = 1;

    /** Button for clear output panel. */
    private IconButton clearOutputButton;

    @UiField
    Toolbar toolbar;

    /** Panel for output messages. */
    @UiField
    HTMLPanel buildOutputPanel;

    interface BuildProjectViewUiBinder extends UiBinder<Widget, BuildProjectView> {
    }

    /** UIBinder instance. */
    private static BuildProjectViewUiBinder uiBinder = GWT.create(BuildProjectViewUiBinder.class);

    public BuildProjectView() {
        super(ID, ViewType.OPERATION, TITLE, new Image(BuilderClientBundle.INSTANCE.build()), WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        Image normalIcon = new Image(BuilderClientBundle.INSTANCE.clearOutput());
        Image disabledIcon = new Image(BuilderClientBundle.INSTANCE.clearOutputDisabled());

        clearOutputButton = new IconButton(normalIcon, disabledIcon);
        clearOutputButton.setTitle(CLEAR_OUTPUT);
        clearOutputButton.setEnabled(true);

        ToolbarItem toolbarItem = toolbar.addItem(clearOutputButton, true);
        toolbarItem.getElement().getStyle().setPaddingTop(2, Unit.PX);
        toolbarItem.getElement().getStyle().setPaddingRight(2, Unit.PX);
    }

    /** @see org.exoplatform.ide.extension.maven.client.build.BuildProjectPresenter.Display#showMessageInOutput(java.lang.String) */
    @Override
    public final native void showMessageInOutput(String text) /*-{
        var pre = $doc.getElementById('ide.builder.buildOutput');
        if (pre == null || pre == undefined) {
            return;
        }

        var curText = pre.textContent;
        if (curText != null && curText != undefined && curText != "") {
            pre.innerHTML += "\r\n";
        }
        pre.innerHTML += text;

        this.@org.exoplatform.ide.extension.maven.client.build.BuildProjectView::scrollToBottom()();
    }-*/;

    /** @see org.exoplatform.ide.extension.maven.client.build.BuildProjectPresenter.Display#clearOutput() */
    @Override
    public final native void clearOutput() /*-{
        var pre = $doc.getElementById('ide.builder.buildOutput');
        if (pre == null || pre == undefined) {
            return;
        }
        pre.textContent = "";
    }-*/;

    /** Scrolling to bottom of buildOutputPanel. */
    private void scrollToBottom() {
        int scrollHeight = DOM.getElementPropertyInt(buildOutputPanel.getElement(), "scrollHeight");
        DOM.setElementPropertyInt(buildOutputPanel.getElement(), "scrollTop", scrollHeight);
    }

    /** @see org.exoplatform.ide.extension.maven.client.build.BuildProjectPresenter.Display#startAnimation() */
    @Override
    public void startAnimation() {
        animationCharIndex = 1;
        Element animationElement = DOM.getElementById("ide.builder.buildingAnimation");
        if (animationElement == null) {
            return;
        }
        animationElement.getStyle().setDisplay(Display.BLOCK);
        animationTimer.scheduleRepeating(150);
    }

    /** @see org.exoplatform.ide.extension.maven.client.build.BuildProjectPresenter.Display#stopAnimation() */
    @Override
    public void stopAnimation() {
        animationTimer.cancel();

        Element animationElement = DOM.getElementById("ide.builder.buildingAnimation");
        if (animationElement == null) {
            return;
        }

        animationElement.getStyle().setDisplay(Display.NONE);
        animationElement.setInnerHTML("");
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

            Element animationElement = DOM.getElementById("ide.builder.buildingAnimation");
            if (animationElement != null) {
                animationElement.setInnerHTML(c);
            }

            animationCharIndex++;
            if (animationCharIndex > 4) {
                animationCharIndex = 1;
            }
        }
    };

    /** @see org.exoplatform.ide.extension.maven.client.build.BuildProjectPresenter.Display#getClearOutputButton() */
    @Override
    public HasClickHandlers getClearOutputButton() {
        return clearOutputButton;
    }

    /** @see org.exoplatform.ide.extension.maven.client.build.BuildProjectPresenter.Display#setClearOutputButtonEnabled(boolean) */
    @Override
    public void setClearOutputButtonEnabled(boolean isEnabled) {
        clearOutputButton.setEnabled(isEnabled);
    }

}
