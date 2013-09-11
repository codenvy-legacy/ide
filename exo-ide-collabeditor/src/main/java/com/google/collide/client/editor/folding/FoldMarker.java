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
package com.google.collide.client.editor.folding;

import com.google.collide.client.Resources;
import com.google.gwt.user.client.ui.Image;

/**
 * Marker used to represent the projection of a master document onto a {@link ProjectionDocument}.
 * This marker can be either expanded or collapsed. If expanded it corresponds to
 * a segment of the projection document. If collapsed, it represents a region of
 * the master document that does not have a corresponding segment in the
 * projection document.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FoldMarker.java Mar 12, 2013 12:59:31 AM azatsarynnyy $
 */
public class FoldMarker {
    /** Image for collapsed fold marker. */
    private Image collapsedImage;

    /** Image for expanded fold marker. */
    private Image expandedImage;

    /** The state of this fold marker. */
    private boolean isCollapsed;

    /**
     * Creates a new expanded fold marker.
     *
     * @param resources
     */
    public FoldMarker(Resources resources) {
        this(false, resources);
    }

    /**
     * Creates a new fold marker. When <code>isCollapsed</code>
     * is <code>true</code> the fold marker is initially collapsed.
     *
     * @param isCollapsed
     *         <code>true</code> if the fold marker should initially be collapsed, <code>false</code> otherwise
     * @param resources
     */
    public FoldMarker(boolean isCollapsed, Resources resources) {
        this.isCollapsed = isCollapsed;
        this.collapsedImage = new Image(resources.collapsed());
        this.expandedImage = new Image(resources.expanded());
    }

    /**
     * Returns the state of this fold point.
     *
     * @return <code>true</code> if collapsed, <code>false</code> otherwise
     */
    public boolean isCollapsed() {
        return isCollapsed;
    }

    /** Marks this fold point as being collapsed. */
    void markCollapsed() {
        isCollapsed = true;
    }

    /** Marks this fold point as being unfolded. */
    void markExpanded() {
        isCollapsed = false;
    }

    /**
     * Get marker's image.
     *
     * @return image for marker
     */
    public Image getImage() {
        return isCollapsed() ? collapsedImage : expandedImage;
    }

}
