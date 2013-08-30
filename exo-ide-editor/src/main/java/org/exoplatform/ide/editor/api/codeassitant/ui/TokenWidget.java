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
package org.exoplatform.ide.editor.api.codeassitant.ui;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.api.codeassitant.Token;

/**
 * Base class for UI representation of token.<br>
 * <p/>
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 19, 2010 4:13:18 PM evgen $
 */
public abstract class TokenWidget extends Composite implements HasClickHandlers, HasMouseOverHandlers,
                                                               HasDoubleClickHandlers {

    protected Token token;

    public TokenWidget(Token token) {
        this.token = token;
    }

    /** @return the token */
    public Token getToken() {
        return token;
    }

    /** @return name of token */
    public abstract String getTokenName();

    /** @return String that will inserted in editor */
    public abstract String getTokenValue();

    /**
     * Get token description. It's may be javadoc, template content etc.
     *
     * @return {@link Widget} with description
     */
    public abstract Widget getTokenDecription();

    /** Calls when user select this {@link Widget} */
    public abstract void setSelectedStyle();

    /** Calls when clear selection or mouse blur this {@link Widget} */
    public abstract void setDefaultStyle();

    /** @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler) */
    public HandlerRegistration addClickHandler(ClickHandler handler) {

        return addDomHandler(handler, ClickEvent.getType());
    }

    /** @see com.google.gwt.event.dom.client.HasMouseOverHandlers#addMouseOverHandler(com.google.gwt.event.dom.client.MouseOverHandler) */
    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return addDomHandler(handler, MouseOverEvent.getType());
    }

    /**
     * @see com.google.gwt.event.dom.client.HasDoubleClickHandlers#addDoubleClickHandler(com.google.gwt.event.dom.client
     *      .DoubleClickHandler)
     */
    public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
        return addDomHandler(handler, DoubleClickEvent.getType());
    }

}
