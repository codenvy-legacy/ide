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

/*
 * FIXME: this 'controller' is an 'AutoHideComponent', which is
 * weird, but less code than creating a bunch of delegates to an encapsulated
 * AutoHideComponent. We can fix this if it starts getting ugly.
 */

/** A controller that wraps the given element in a {@link AutoHideComponent}. */
public class AutoHideController extends AutoHideComponent<AutoHideView<Void>, AutoHideComponent.AutoHideModel> {

    public static AutoHideController create(Element element) {
        AutoHideView<Void> view = new AutoHideView<Void>(element);
        AutoHideModel model = new AutoHideModel();
        return new AutoHideController(view, model);
    }

    private AutoHideController(AutoHideView<Void> view, AutoHideComponent.AutoHideModel model) {
        super(view, model);
    }
}
