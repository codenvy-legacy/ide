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
package org.exoplatform.ide.extension.java.jdi.client.ui;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.editor.java.client.JavaClientBundle;
import org.exoplatform.ide.extension.java.jdi.shared.Field;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class VariableCell extends AbstractCell<Variable> {

    @Override
    public void render(Context context, Variable value, SafeHtmlBuilder sb) {

        if (value instanceof Field) {
            Field f = (Field)value;
            Image image = getMainImage(JavaClientBundle.INSTANCE.privateField());
            sb.appendHtmlConstant(image.toString());
            // Add all modifiers container:
            sb.appendHtmlConstant(getModifiersContainer(f));
            sb.appendHtmlConstant(getName(f.getName()).getString());
            sb.appendHtmlConstant(getType(f.getValue()).toString());
        } else {
            Image image = getMainImage(JavaClientBundle.INSTANCE.variable());
            sb.appendHtmlConstant(image.toString());
            // Add all modifiers container:
            sb.appendHtmlConstant(getName(value.getName()).getString());
            sb.appendHtmlConstant(getType(value.getValue()).toString());

        }
    }

    /**
     * Returns the HTML element with the title of the node.
     *
     * @param title
     * @return {@link Element}
     */
    protected Element getName(String title) {
        Element span = DOM.createSpan();
        span.setInnerHTML(title);
        return span;
    }

    /**
     * Returns HTML code of the element with node's type.
     *
     * @param type
     *         type's string representation
     * @return {@link String} HTML code of the element with node's type
     */
    protected String getType(String type) {
        Element span = DOM.createSpan();
        span.setInnerText(" : " + type);
        span.getStyle().setColor("#644A17");
        return span.getString();
    }

    /**
     * Returns mail image of the node.
     *
     * @param imageResource
     * @return {@link Image}
     */
    protected Image getMainImage(ImageResource imageResource) {
        Image image = new Image(imageResource);
        DOM.setStyleAttribute(image.getElement(), "cssFloat", "left");
        DOM.setStyleAttribute(image.getElement(), "marginRight", "5px");
        return image;
    }

    protected String getModifiersContainer(Field f) {
        if (f.isTransient() || f.isVolatile() || f.isStatic() || f.isFinal()) {
            String span =
                    "<span style = \"position: absolute; top: -4px; margin-left: -14px; font-family: Verdana,Bitstream Vera Sans," +
                    "sans-serif; font-size: 9px; text-align: right;' \">";
            span += (f.isTransient()) ? "<span class='item-modifier' style='color:#6d0000;'>t</span>" : "";
            span += (f.isVolatile()) ? "<span class='item-modifier' style='color:#6d0000'>v</span>" : "";
            span += (f.isStatic()) ? "<span class='item-modifier' style='color:#6d0000'>s</span>" : "";
            span += (f.isFinal()) ? "<span class='item-modifier' style='color:#174c83'>f</span>" : "";
            span += "</span>";
            return span;
        }
        return "";
    }

}
