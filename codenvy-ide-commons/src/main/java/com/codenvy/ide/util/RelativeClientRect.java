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

package com.codenvy.ide.util;

import elemental.html.ClientRect;

/** A {@link ClientRect} which is relative to a given point. */
public class RelativeClientRect implements ClientRect {

    public static ClientRect relativeToRect(ClientRect relativeParent, ClientRect rect) {
        return new RelativeClientRect((int)relativeParent.getLeft(), (int)relativeParent.getTop(), rect);
    }

    private final ClientRect rect;

    private final int offsetLeft;

    private final int offsetTop;

    public RelativeClientRect(int offsetLeft, int offsetTop, ClientRect rect) {
        this.offsetLeft = offsetLeft;
        this.offsetTop = offsetTop;
        this.rect = rect;
    }

    @Override
    public double getBottom() {
        return rect.getBottom() - offsetTop;
    }

    @Override
    public double getHeight() {
        return rect.getHeight();
    }

    @Override
    public double getLeft() {
        return rect.getLeft() - offsetLeft;
    }

    @Override
    public double getRight() {
        return rect.getRight() - offsetLeft;
    }

    @Override
    public double getTop() {
        return rect.getTop() - offsetTop;
    }

    @Override
    public double getWidth() {
        return rect.getWidth();
    }
}
