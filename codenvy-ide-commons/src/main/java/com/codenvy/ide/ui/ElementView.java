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

package com.codenvy.ide.ui;

import elemental.html.Element;
import elemental.js.html.JsElement;

import com.codenvy.ide.mvp.UiComponent;
import com.codenvy.ide.mvp.View;


/**
 * A single DOM Element.
 * <p/>
 * This is a View (V) in our use of MVP.
 * <p/>
 * Use this when you want to give some brains to a single DOM element by making this the View for
 * some {@link UiComponent} that will contain business logic.
 */
// TODO: move this to mvp package when ray fixes the
// JsoRestrictionChecker bug in the gwt compiler.
public class ElementView<D> extends JsElement implements View<D> {
    protected ElementView() {
    }

    @Override
    public final native D getDelegate() /*-{
        return this["delegate"];
    }-*/;

    @Override
    public final native void setDelegate(D delegate) /*-{
        this["delegate"] = delegate;
    }-*/;

    @Override
    public final native Element getElement() /*-{
        return this;
    }-*/;
}
