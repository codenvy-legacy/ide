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

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.KeyboardEvent;
import elemental.js.events.JsKeyboardEvent;
import elemental.js.html.JsElement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
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
        view = new View(resources);
    }

    public void setWidget(Widget widget) {
        view.setContent(widget);
        handleViewEvents();
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

        String crossButton();
    }

    /**
     * The events sources by the View.
     */
    private interface ViewEvents {
        void onEscapeKey();

        void onClose();
    }

    /**
     * The view that renders the {@link Window}. The View consists of a glass
     * panel that fades out the background, and a DOM structure that positions the
     * contents in the exact center of the screen.
     */
    public static class View extends Composite {

        private static MyBinder uiBinder = GWT.create(MyBinder.class);
        final Resources res;
        @UiField(provided = true)
        final Css       css;
        @UiField
        HTMLPanel contentContainer;
        @UiField
        HTMLPanel glass;
        @UiField
        HTMLPanel popup;
        @UiField
        HTMLPanel header;
        @UiField
        HTMLPanel content;
        @UiField
        Label     headerLabel;
        @UiField
        HTMLPanel crossButton;

        // the left style attribute in pixels
        private int leftPosition = -1;
        // The top style attribute in pixels
        private int topPosition = -1;
        private int windowWidth;
        private int clientLeft;
        private int clientTop;
        private ViewEvents delegate;
        private boolean    dragging;
        private int        dragStartX;
        private int        dragStartY;

        View(Resources res) {
            this.res = res;
            this.css = res.centerPanelCss();
            windowWidth = com.google.gwt.user.client.Window.getClientWidth();
            clientLeft = Document.get().getBodyOffsetLeft();
            clientTop = Document.get().getBodyOffsetTop();
            initWidget(uiBinder.createAndBindUi(this));
            handleEvents();
        }

        /**
         * Returns the duration of the popup animation in milliseconds. The return
         * value should equal the value of {@link Css#animationDuration()}.
         */
        protected int getAnimationDuration() {
            return css.animationDuration();
        }

        /**
         * Updates the View to reflect the showing state of the popup.
         *
         * @param showing
         *         true if showing, false if not.
         */
        protected void setShowing(boolean showing) {
            if (showing) {
                glass.addStyleName(css.glassVisible());
                contentContainer.addStyleName(css.contentVisible());
            } else {
                glass.removeStyleName(css.glassVisible());
                contentContainer.removeStyleName(css.contentVisible());
            }
        }

        private void handleEvents() {
            ((elemental.html.Element)getElement()).addEventListener(Event.KEYDOWN, new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    JsKeyboardEvent keyEvt = (JsKeyboardEvent)evt;
                    int keyCode = keyEvt.getKeyCode();
                    if (KeyboardEvent.KeyCode.ESC == keyCode) {
                        if (delegate != null) {
                            delegate.onEscapeKey();
                        }
                    }
                }
            }, true);
            crossButton.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (delegate != null) {
                        delegate.onClose();
                    }
                }
            }, ClickEvent.getType());
            MouseHandler mouseHandler = new MouseHandler();
            header.addDomHandler(mouseHandler, MouseDownEvent.getType());
            header.addDomHandler(mouseHandler, MouseUpEvent.getType());
            header.addDomHandler(mouseHandler, MouseMoveEvent.getType());
        }

        public void setDelegate(ViewEvents delegate) {
            this.delegate = delegate;
        }

        public void setContent(Widget content) {
            this.content.add(content);
        }

        private void endDragging(MouseUpEvent event) {
            dragging = false;
            DOM.releaseCapture(header.getElement());
        }

        private void continueDragging(MouseMoveEvent event) {
            if (dragging) {
                int absX = event.getX() + contentContainer.getAbsoluteLeft();
                int absY = event.getY() + contentContainer.getAbsoluteTop();

                // if the mouse is off the screen to the left, right, or top, don't
                // move the dialog box. This would let users lose dialog boxes, which
                // would be bad for modal popups.
                if (absX < clientLeft || absX >= windowWidth || absY < clientTop) {
                    return;
                }

                setPopupPosition(absX - dragStartX, absY - dragStartY);
            }
        }

        private void beginDragging(MouseDownEvent event) {
            if (DOM.getCaptureElement() == null) {
              /*
               * Need to check to make sure that we aren't already capturing an element
               * otherwise events will not fire as expected. If this check isn't here,
               * any class which extends custom button will not fire its click event for
               * example.
               */
                dragging = true;
                DOM.setCapture(header.getElement());
                if("".equals(contentContainer.getElement().getStyle().getPosition())){
                  contentContainer.getElement().getStyle().setTop(contentContainer.getAbsoluteTop()+1, Style.Unit.PX);
                  contentContainer.getElement().getStyle().setLeft(contentContainer.getAbsoluteLeft(), Style.Unit.PX);
                }
                else{
                  contentContainer.getElement().getStyle().setTop(contentContainer.getAbsoluteTop(), Style.Unit.PX);
                  contentContainer.getElement().getStyle().setLeft(contentContainer.getAbsoluteLeft(), Style.Unit.PX);

                }

                contentContainer.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);

                dragStartX = event.getX();
                dragStartY = event.getY();
            }

        }
        /**
         * Sets the popup's position relative to the browser's client area. The
         * popup's position may be set before calling {@link #show()}.
         *
         * @param left the left position, in pixels
         * @param top the top position, in pixels
         */
        public void setPopupPosition(int left, int top) {
            // Save the position of the popup
            leftPosition = left;
            topPosition = top;

            // Account for the difference between absolute position and the
            // body's positioning context.
            left -= Document.get().getBodyOffsetLeft();
            top -= Document.get().getBodyOffsetTop();

            // Set the popup's position manually, allowing setPopupPosition() to be
            // called before show() is called (so a popup can be positioned without it
            // 'jumping' on the screen).
            Element elem = contentContainer.getElement();
            elem.getStyle().setPropertyPx("left", left);
            elem.getStyle().setPropertyPx("top", top);
        }
        @UiTemplate("Window.ui.xml")
        interface MyBinder extends UiBinder<HTMLPanel, View> {
        }

        private class MouseHandler implements MouseDownHandler, MouseUpHandler,
                                              MouseMoveHandler {

            public void onMouseDown(MouseDownEvent event) {
                beginDragging(event);
            }

            public void onMouseMove(MouseMoveEvent event) {
                continueDragging(event);
            }

            public void onMouseUp(MouseUpEvent event) {
                endDragging(event);
            }
        }
    }

}
