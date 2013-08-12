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
package com.codenvy.ide.ui.menu;

import elemental.html.Element;

import com.codenvy.ide.mvp.CompositeView;
import com.codenvy.ide.util.AnimationController;


/**
 * The View for AutoHideComponent.
 *
 * @param <D>
 *         event delegate class
 */
public class AutoHideView<D> extends CompositeView<D> {

    private AnimationController animationController = AnimationController.NO_ANIMATION_CONTROLLER;

    public AutoHideView(final Element elem) {
        super(elem);
    }

    /** Constructor to allow subclasses to use UiBinder. */
    protected AutoHideView() {
    }

    /** Hides the view, using the animation controller. */
    public void hide() {
        animationController.hide(getElement());
    }

    /** Shows the view, using the animation controller. */
    public void show() {
        animationController.show(getElement());
    }

    public void setAnimationController(AnimationController controller) {
        this.animationController = controller;
    }

    @Override
    protected void setElement(Element element) {
      /*
       * Start in the hidden state. animationController may not be initialized if
       * this method is called from the constructor, so use the default animation
       * controller.
       */
        AnimationController.NO_ANIMATION_CONTROLLER.hideWithoutAnimating(element);
        super.setElement(element);
    }
}
