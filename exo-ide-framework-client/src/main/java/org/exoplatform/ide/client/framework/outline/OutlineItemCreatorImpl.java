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
package org.exoplatform.ide.client.framework.outline;

import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.commons.util.StringEscapeUtils;
import org.exoplatform.ide.editor.api.codeassitant.Modifier;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;

import java.util.List;

/**
 * This implementation of interface OutlineItemCreatorImpl which is used to create code outline item widget from OutlineTreeGrid
 * class of exo.ide.client library. Also consists of some utility functions to select outline item icon and define its display
 * label. Function getOutlineItemWidget(Token token) is extended in the specific {FileType}OutlineItemCreator classes of
 * exo-ide-editor-{FileType} libraries.
 *
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $
 */

public abstract class OutlineItemCreatorImpl implements OutlineItemCreator {

    @Override
    public Widget getOutlineItemWidget(Token token) {
        FlowPanel flowPanel = new FlowPanel();

        ImageResource imageResource = getTokenIcon((TokenBeenImpl)token);
        if (imageResource != null) {
            Image i = new Image(imageResource);
            DOM.setStyleAttribute(i.getElement(), "cssFloat", "left");
            DOM.setStyleAttribute(i.getElement(), "marginRight", "5px");
            flowPanel.add(i);
        }
        Element span = DOM.createSpan();
        span.setInnerHTML(getTokenDisplayTitle((TokenBeenImpl)token));
        flowPanel.getElement().appendChild(span);

        return flowPanel;
    }

    /**
     * Get icon for token.
     *
     * @param token
     *         token
     * @return icon
     */
    public abstract ImageResource getTokenIcon(TokenBeenImpl token);

    /**
     * Get the string to display token.
     *
     * @param token
     *         to display
     * @return {@link String} display string of the token
     */
    public abstract String getTokenDisplayTitle(TokenBeenImpl token);

    /**
     * Return parameters list from token.getParameters()
     *
     * @param token
     * @return parameters list like '(String, int)', or '()' if there are no parameters
     */
    protected String getParametersList(TokenBeenImpl token) {
        StringBuffer parametersDescription = new StringBuffer("(");

        if (token.getParameters() != null && token.getParameters().size() > 0) {

            List<TokenBeenImpl> parameters = token.getParameters();

            for (int i = 0; i < parameters.size(); i++) {
                TokenBeenImpl parameter = parameters.get(i);
                if (i > 0) {
                    parametersDescription.append(", ");
                }

                parametersDescription.append("<span class='item-parameter'>")
                                     .append(StringEscapeUtils.htmlEncode(parameter.getElementType())).append("</span>");
            }
        }
        parametersDescription.append(")");
        return parametersDescription.toString();
    }

    /**
     * get formatted string with java type from token.getElementType() like " : java.lang.String"
     *
     * @param token
     * @return string like " : java.lang.String", or "".
     */
    protected String getElementType(TokenBeenImpl token) {
        if (token.getElementType() != null) {
            // encode "<>" in HTML entities
            return "<span style='color:#644a17;' class='item-type' > : "
                   + StringEscapeUtils.htmlEncode(token.getElementType()) + "</span>";
        }

        return "";
    }

    protected boolean isFinal(TokenBeenImpl token) {
        return token.getModifiers() != null && token.getModifiers().contains(Modifier.FINAL);
    }

    protected boolean isAbstract(TokenBeenImpl token) {
        return token.getModifiers() != null && token.getModifiers().contains(Modifier.ABSTRACT);
    }

    protected boolean isTransient(TokenBeenImpl token) {
        return token.getModifiers() != null && token.getModifiers().contains(Modifier.TRANSIENT);
    }

    protected boolean isVolative(TokenBeenImpl token) {
        return token.getModifiers() != null && token.getModifiers().contains(Modifier.VOLATILE);
    }

    protected boolean isStatic(TokenBeenImpl token) {
        return token.getModifiers() != null && token.getModifiers().contains(Modifier.STATIC);
    }

    protected boolean isProtected(TokenBeenImpl token) {
        return token.getModifiers() != null && token.getModifiers().contains(Modifier.PROTECTED);
    }

    protected boolean isPrivate(TokenBeenImpl token) {
        return token.getModifiers() != null && token.getModifiers().contains(Modifier.PRIVATE);
    }

    protected boolean isPublic(TokenBeenImpl token) {
        return token.getModifiers() != null && token.getModifiers().contains(Modifier.PUBLIC);
    }

    protected boolean isSynchronized(TokenBeenImpl token) {
        return token.getModifiers() != null && token.getModifiers().contains(Modifier.SYNCHRONIZED);
    }

    /**
     * Checks, whether method has deprecated annotation.
     *
     * @param token
     *         method
     * @return boolean whether method is deprecated
     */
    protected boolean isDeprecated(TokenBeenImpl token) {
        if (token.getAnnotations() == null)
            return false;

        for (TokenBeenImpl annotation : token.getAnnotations()) {
            if ("@deprecated".equalsIgnoreCase(annotation.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return formatted annotation list from token.getAnnotations()
     *
     * @param token
     * @return annotations like '@Path; @PathParam(&#34;name&#34;)' or "", if there are no annotations in the token
     */
    protected String getAnnotationList(TokenBeenImpl token) {
        if (token.getAnnotations() != null && token.getAnnotations().size() > 0) {
            StringBuffer title = new StringBuffer();

            for (TokenBeenImpl annotation : token.getAnnotations()) {
                title.append(annotation.getName()).append("; ");
            }
            // replace all '"' on HTML Entity "&#34;"
            return StringEscapeUtils.htmlEncode(title.toString());
        }

        return "";
    }

    /**
     * @param token
     *         {@link TokenBeenImpl}
     * @return html element with modifiers and annotation sign
     */
    protected String getModifiersContainer(TokenBeenImpl token) {
        if (isTransient(token) || isVolative(token) || isStatic(token) || isFinal(token) || isAbstract(token)
            || getAnnotationList(token).length() > 0) {

            String span =
                    "<span style = \"position: relative; top: -5px; margin-left: -3px; font-family: Verdana,Bitstream Vera Sans," +
                    "sans-serif; font-size: 9px; text-align: right;' \">";
            span += (isTransient(token)) ? "<span class='item-modifier' color ='#6d0000'>t</span>" : "";
            span += (isVolative(token)) ? "<span class='item-modifier' color ='#6d0000'>v</span>" : "";
            span += (isStatic(token)) ? "<span class='item-modifier' color ='#6d0000'>s</span>" : "";
            span += (isFinal(token)) ? "<span class='item-modifier' color ='#174c83'>f</span>" : "";
            span += (isAbstract(token)) ? "<span class='item-modifier' color ='#004e00'>a</span>" : "";
            span += (getAnnotationList(token).length() > 0) ? "<span color ='#000000'>@</span>" : "";
            span += "</span>";

            return span;
        }

        return "";
    }
}
