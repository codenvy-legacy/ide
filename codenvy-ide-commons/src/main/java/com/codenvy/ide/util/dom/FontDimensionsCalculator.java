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

package com.codenvy.ide.util.dom;

import elemental.css.CSSStyleDeclaration;
import elemental.html.Element;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.util.CssUtils;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;


/** A class that computes the height and width of a character. */
public class FontDimensionsCalculator {

    public interface FontDimensions {
        float getBaseWidth();

        float getBaseHeight();

        float getWidthZoomFactor();

        float getHeightZoomFactor();

        float getCharacterWidth();

        float getCharacterHeight();
    }

    private class FontDimensionsImpl implements FontDimensions {

        private float baseWidth;

        private float baseHeight;

        private float widthFactor = 1;

        private float heightFactor = 1;

        /**
         * Updates the width and height internally. If this has never been set
         * before this will set the baseWidth and baseHeight. Otherwise it will
         * adjust the zoom factor and the current character width and height.
         *
         * @return false if no update was required.
         */
        private boolean update(float width, float height) {
            if (baseWidth == 0 && baseHeight == 0) {
                baseWidth = width;
                baseHeight = height;
                return true;
            } else if (baseWidth * widthFactor != width || baseHeight * heightFactor != height) {
                widthFactor = width / baseWidth;
                heightFactor = height / baseHeight;
                return true;
            }

            return false;
        }

        @Override
        public float getBaseWidth() {
            return baseWidth;
        }

        @Override
        public float getBaseHeight() {
            return baseHeight;
        }

        @Override
        public float getWidthZoomFactor() {
            return widthFactor;
        }

        @Override
        public float getHeightZoomFactor() {
            return heightFactor;
        }

        @Override
        public float getCharacterWidth() {
            return widthFactor * baseWidth;
        }

        @Override
        public float getCharacterHeight() {
            return heightFactor * baseHeight;
        }
    }

    private static final int POLLING_CUTOFF = 10000;

    private static final String SAMPLE_TEXT = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    private static final int SAMPLE_ROWS = 30;

    public interface Callback {
        void onFontDimensionsChanged(FontDimensions fontDimensions);
    }

    private static FontDimensionsCalculator INSTANCE;

    public static FontDimensionsCalculator get(String fontClassName) {
        if (INSTANCE == null) {
            INSTANCE = new FontDimensionsCalculator(fontClassName);
        }

        return INSTANCE;
    }

    private final Element dummyElement;

    /*
     * This timer is called during the first 12s. When using a web font it will be
     * loaded asynchronously and our initial measurements will be incorrect. By
     * polling we remeasure after this font has been loaded and updated our size.
     */
    private final Timer repeater = new Timer() {
        @Override
        public void run() {
            if (pollingDelay >= POLLING_CUTOFF) {
                // Terminate polling rescheduling once we cross the cutoff.
                return;
            }

            measureAndDispatch();

            schedule(pollingDelay);
            pollingDelay *= 2;
        }
    };

    private int pollingDelay = 500;

    private JsonArray<Callback> callbacks = JsonCollections.createArray();

    private final FontDimensionsImpl fontDimensions;

    private final String fontClassName;

    private FontDimensionsCalculator(String fontClassName) {
        this.fontClassName = fontClassName;
        // This handler will be called when the browser window zooms
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent arg0) {
                measureAndDispatch();
            }
        });

        // Build a multirow text block so we can measure it
        StringBuilder htmlContent = new StringBuilder(SAMPLE_TEXT);
        for (int i = 1; i < SAMPLE_ROWS; i++) {
            htmlContent.append("<br/>");
            htmlContent.append(SAMPLE_TEXT);
        }

        dummyElement = Elements.createSpanElement(fontClassName);
        dummyElement.setInnerHTML(htmlContent.toString());
        dummyElement.getStyle().setVisibility(CSSStyleDeclaration.Visibility.HIDDEN);
        dummyElement.getStyle().setPosition(CSSStyleDeclaration.Position.ABSOLUTE);
        Elements.getBody().appendChild(dummyElement);

        fontDimensions = new FontDimensionsImpl();

        repeater.schedule(pollingDelay);

      /*
       * Force an initial measure (the dispatch won't happen since no one is
       * attached)
       */
        measureAndDispatch();
    }

    public void addCallback(final Callback callback) {
        callbacks.add(callback);
    }

    public void removeCallback(final Callback callback) {
        callbacks.remove(callback);
    }

    /**
     * Returns a font dimensions that will be updated as the font dimensions
     * change.
     */
    public FontDimensions getFontDimensions() {
        return fontDimensions;
    }

    public String getFontClassName() {
        return fontClassName;
    }

    public String getFont() {
        return CssUtils.getComputedStyle(dummyElement).getPropertyValue("font");
    }

    private void measureAndDispatch() {
        float curWidth = dummyElement.getOffsetWidth() / ((float)SAMPLE_TEXT.length());
        float curHeight = dummyElement.getOffsetHeight() / ((float)SAMPLE_ROWS);

        if (fontDimensions.update(curWidth, curHeight)) {
            dispatchToCallbacks();
        }
    }

    private void dispatchToCallbacks() {
        for (int i = 0, n = callbacks.size(); i < n; i++) {
            callbacks.get(i).onFontDimensionsChanged(fontDimensions);
        }
    }
}
