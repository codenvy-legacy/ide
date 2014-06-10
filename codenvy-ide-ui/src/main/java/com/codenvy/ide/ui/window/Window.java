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
package com.codenvy.ide.ui.window;

import elemental.js.dom.JsElement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nullable;

/**
 * A popup that automatically centers its content, even if the dimensions of the content change. The
 * centering is done in CSS, so performance is very good. A semi-transparent "glass" panel appears
 * behind the popup. The glass is not optional due to the way {@link Window} is implemented.
 * <p/>
 * <p>
 * {@link Window} animates into and out of view using the shrink in/expand out animation.
 * </p>
 */
public abstract class Window implements IsWidget {

    private static final Resources resources = GWT.create(Resources.class);

    static {
        resources.centerPanelCss().ensureInjected();
    }

    private boolean hideOnEscapeEnabled = false;
    private boolean isShowing;
    private View    view;

    protected Window() {
        this(true);
    }

    protected Window(boolean showBottomPanel) {
        view = new View(resources, showBottomPanel);
    }

    public void setWidget(Widget widget) {
        view.setContent(widget);
        handleViewEvents();
    }

    /**
     * ensureDebugId on the current window container. ensureDebugId id + "-headerLabel" on the window control bar title
     * 
     * @see UIObject#ensureDebugId(String)
     */
    public void ensureDebugId(String id) {
        view.contentContainer.ensureDebugId(id);
        view.headerLabel.ensureDebugId(id + "-headerLabel");
    }


    /**
     * Hides the {@link Window} popup. The popup will animate out of view.
     */
    public void hide() {
        if (!isShowing) {
            return;
        }
        isShowing = false;

        // Animate the popup out of existance.
        view.setShowing(false);

        // Remove the popup when the animation completes.
        new Timer() {
            @Override
            public void run() {
                // The popup may have been shown before this timer executes.
                if (!isShowing) {
                    view.removeFromParent();
                    Style style = view.contentContainer.getElement().getStyle();
                    style.clearPosition();
                    style.clearLeft();
                    style.clearTop();
                }
            }
        }.schedule(view.getAnimationDuration());
    }

    /**
     * Checks if the {@link Window} is showing or animating into view.
     *
     * @return true if showing, false if hidden
     */
    public boolean isShowing() {
        return isShowing;
    }

    /**
     * Sets whether or not the popup should hide when escape is pressed. The
     * default behavior is to ignore the escape key.
     *
     * @param isEnabled
     *         true to close on escape, false not to
     */
    // TODO: This only works if the popup has focus. We need to capture events.
    // TODO: Consider making escaping the default.
    public void setHideOnEscapeEnabled(boolean isEnabled) {
        this.hideOnEscapeEnabled = isEnabled;
    }

    protected Button createButton(String title, String debugId, ClickHandler clickHandler) {
        Button button = new Button();
        button.setText(title);
        button.ensureDebugId(debugId);
        button.getElement().setId(debugId);
        button.addStyleName(resources.centerPanelCss().alignBtn());
        button.addClickHandler(clickHandler);
        return button;
    }

    /**
     * See {@link #show(com.google.gwt.dom.client.InputElement)}.
     */
    public void show() {
        show(null);
    }

    /**
     * Displays the {@link Window} popup. The popup will animate into view.
     *
     * @param selectAndFocusElement
     *         an {@link com.google.gwt.dom.client.InputElement} to select and focus on when the panel is
     *         shown. If null, no element will be given focus
     */
    public void show(@Nullable final InputElement selectAndFocusElement) {
        if (isShowing) {
            return;
        }
        isShowing = true;

        // Attach the popup to the body.
        final JsElement popup = view.popup.getElement().cast();
        if (popup.getParentElement() == null) {
            // Hide the popup so it can enter its initial state without flickering.

            popup.getStyle().setVisibility("hidden");
            RootLayoutPanel.get().add(view);
        }

        // Start the animation after the element is attached.
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                // The popup may have been hidden before this timer executes.
                if (isShowing) {
                    popup.getStyle().removeProperty("visibility");
                    view.setShowing(true);
                    if (selectAndFocusElement != null) {
                        selectAndFocusElement.select();
                        selectAndFocusElement.focus();
                    }
                }
            }
        });
    }

    private void handleViewEvents() {
        view.setDelegate(new ViewEvents() {
            @Override
            public void onEscapeKey() {
                if (hideOnEscapeEnabled) {
                    hide();
                    Window.this.onClose();
                }
            }

            @Override
            public void onClose() {
                hide();
                Window.this.onClose();
            }
        });
    }

    /**
     * this method called when user close Window
     */
    protected abstract void onClose();

    @Override
    public Widget asWidget() {
        return com.google.gwt.user.client.ui.HTML.wrap((Element)view.getElement());
    }

    public void setTitle(String title) {
        view.headerLabel.setText(title);
    }

    public HTMLPanel getFooter() {
        return view.footer;
    }

    /**
     * The resources used by this UI component.
     */
    public interface Resources extends ClientBundle {
        @Source({"com/codenvy/ide/common/constants.css", "Window.css", "com/codenvy/ide/api/ui/style.css"})
        Css centerPanelCss();

        @Source("close-dark-normal.png")
        ImageResource closeDark();

        @Source("close-dark-hover.png")
        ImageResource closeDarkHover();

        @Source("close-white-hover.png")
        ImageResource closeWhiteHover();

        @Source("close-white-normal.png")
        ImageResource closeWhite();

    }

    /**
     * The Css Style names used by this panel.
     */
    public interface Css extends CssResource {
        /**
         * Returns duration of the popup animation in milliseconds.
         */
        int animationDuration();

        String content();

        String contentVisible();

        String glass();

        String glassVisible();

        String popup();

        String positioner();

        String header();

        String headerTitleWrapper();

        String headerTitleLabel();

        String footer();

        String separator();

        String alignBtn();

        String crossButton();
    }

    /**
     * The events sources by the View.
     */
    public interface ViewEvents {
        void onEscapeKey();

        void onClose();
    }

}
