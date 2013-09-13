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
package org.exoplatform.ide.editor.ruby.client.codeassistant.ui;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperty;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.ruby.client.RubyClientBundle;
import org.exoplatform.ide.editor.ruby.client.codeassistant.model.Modifiers;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JSBaseWidget Feb 24, 2011 11:56:26 AM evgen $
 */
public abstract class RubyBaseWidget extends TokenWidget {

    protected Grid grid;

    protected int modifieres;

    /** @param token */
    public RubyBaseWidget(Token token) {
        super(token);
        if (token.hasProperty(TokenProperties.MODIFIERS)) {
            TokenProperty mod = token.getProperty(TokenProperties.MODIFIERS);
            if (mod.isNumericProperty() != null)
                modifieres = mod.isNumericProperty().numericValue().intValue();
            else {
                modifieres = 0;
            }
        } else
            modifieres = 0;
    }

    protected String getModifiers() {

        String span =
                "<span style = \"position: absolute; margin-top: -5px; margin-left: -25px; width: 22px; "
                + "height: 10px; font-family:  font-family: Verdana,Bitstream Vera Sans,sans-serif; font-size: 10px; \">";
        // span += (ModifierHelper.isAbstract(modifieres)) ? "<font color ='#004e00' style='float: right;'>A</font>" : "";
        // span += (ModifierHelper.isFinal(modifieres)) ? "<font color ='#174c83' style='float: right;'>F</font>" : "";
        span += (modifieres == Modifiers.AccStatic) ? "<font color ='#6d0000' style='float: right;'>S</font>" : "";
        span += "</span>";
        return span;
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget#getTokenDecription() */
    @Override
    public Widget getTokenDecription() {
        if (token.hasProperty(TokenProperties.FULL_TEXT)) {
            Widget w = new SimplePanel();
            w.getElement().setInnerHTML(token.getProperty(TokenProperties.FULL_TEXT).isStringProperty().stringValue());
            return w;
        }
        return null;
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenName() */
    @Override
    public String getTokenName() {
        return getToken().getName();
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setSelectedStyle() */
    @Override
    public void setSelectedStyle() {
        setStyleName(RubyClientBundle.INSTANCE.css().selectedItem());
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setDefaultStyle() */
    @Override
    public void setDefaultStyle() {
        setStyleName(RubyClientBundle.INSTANCE.css().item());
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget#getTokenValue() */
    @Override
    public String getTokenValue() {
        if (token.hasProperty(TokenProperties.CODE))
            return token.getProperty(TokenProperties.CODE).isStringProperty().stringValue();
        else
            return token.getName();
    }

}
