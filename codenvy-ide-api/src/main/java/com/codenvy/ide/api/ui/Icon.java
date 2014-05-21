/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.api.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nullable;

/**
 * Icon.
 *
 * @author Artem Zatsarynnyy
 */
public class Icon {
    private final String      id;
    private final String      sourcePath;
    private final SVGResource svgResource;

    /**
     * Create a new icon based on the specified image path.
     *
     * @param id
     *         icon id
     * @param sourcePath
     *         relative path to image within the GWT module's public folder, e.g. my-extension/icon.png
     */
    public Icon(String id, String sourcePath) {
        this.id = id;
        this.sourcePath = sourcePath;
        this.svgResource = null;
    }

    /**
     * Create a new icon based on the specified {@link SVGResource}.
     *
     * @param id
     *         icon id
     * @param svgResource
     *         resource that contains SVG
     */
    public Icon(String id, SVGResource svgResource) {
        this.id = id;
        this.sourcePath = null;
        this.svgResource = svgResource;
    }

    /**
     * Creates new icon.
     *
     * @param id
     *         icon id
     * @param sourcePath
     *         relative path to image within the GWT module's public folder, e.g. my-extension/icon.png
     * @param svgResource
     *         resource that contains SVG
     */
    public Icon(String id, String sourcePath, SVGResource svgResource) {
        this.id = id;
        this.sourcePath = sourcePath;
        this.svgResource = svgResource;
    }

    /**
     * Icon id.
     *
     * @return icon id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns {@link Image} widget.
     *
     * @return {@link Image} widget
     */
    @Nullable
    public Image getImage() {
        if (sourcePath == null) {
            return null;
        }
        return new Image(GWT.getModuleBaseForStaticFiles() + sourcePath);
    }

    /**
     * Returns {@link SVGImage} widget.
     *
     * @return {@link SVGImage} widget
     */
    @Nullable
    public SVGImage getSVGImage() {
        if (svgResource == null) {
            return null;
        }
        return new SVGImage(svgResource);
    }
}
