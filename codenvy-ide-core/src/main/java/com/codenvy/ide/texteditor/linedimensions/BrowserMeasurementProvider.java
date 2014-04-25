// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.codenvy.ide.texteditor.linedimensions;

import elemental.css.CSSStyleDeclaration;
import elemental.dom.Element;

import com.codenvy.ide.util.StringUtils;
import com.codenvy.ide.util.dom.Elements;
import com.codenvy.ide.util.dom.FontDimensionsCalculator;
import com.codenvy.ide.util.dom.FontDimensionsCalculator.FontDimensions;


/** A {@link MeasurementProvider} which utilizes the DOM to measure a string. */
class BrowserMeasurementProvider implements MeasurementProvider {

    /**
     * The minimum number of characters we require to measure.
     * #getRenderedStringWidth(String) will use this to duplicate the string when
     * measuring short strings.
     */
   /*
    * this is not completely arbitrary, too low of a number and WebKit
    * mis-calculates the width due to rounding errors and minor variations in
    * zoom level character widths. As a reference, 10.0 was too small.
    */
    private static final double MINIMUM_TEXT_LENGTH = 30.0;

    private final FontDimensions fontDimensions;

    private final Element element;

    public BrowserMeasurementProvider(FontDimensionsCalculator calculator) {
        fontDimensions = calculator.getFontDimensions();

        element = Elements.createSpanElement(calculator.getFontClassName());
        element.getStyle().setVisibility(CSSStyleDeclaration.Visibility.HIDDEN);
        element.getStyle().setPosition(CSSStyleDeclaration.Position.ABSOLUTE);
        Elements.getBody().appendChild(element);
    }

    @Override
    public double getCharacterWidth() {
        return fontDimensions.getCharacterWidth();
    }

    @Override
    public double measureStringWidth(String text) {
        int instances = (int)Math.ceil(MINIMUM_TEXT_LENGTH / text.length());
      /*
       * We add a hardspace since this prevents bi-directional combining marks
       * from screwing with our measurement (these spaces must be removed from our
       * result at the end).
       */
        String repeatString = StringUtils.repeatString(text + "\u00A0", instances);
        String content = repeatString.replaceAll(" ", "\u00A0");
        element.setTextContent(content);
        double elementWidht = element.getBoundingClientRect().getWidth();
        double width = (elementWidht - (getCharacterWidth() * instances)) / instances;
        return width;
    }
}
