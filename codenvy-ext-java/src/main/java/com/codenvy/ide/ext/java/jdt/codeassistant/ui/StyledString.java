/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.codeassistant.ui;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 5:02:23 PM 34360 2009-07-22 23:58:59Z evgen $
 */
public class StyledString {

    public static abstract class Styler {

        public abstract String applyStyles(String text);

    }

    public static Styler QUALIFIER_STYLER = new DefaultStyler(/*JavaClientBundle.INSTANCE.css().fqnStyle()*/"#FQN#");

    public static Styler COUNTER_STYLER = new DefaultStyler(/*JavaClientBundle.INSTANCE.css().counter()*/"#COUNTER#");

    private StringBuilder builder;

    /** @param string */
    public StyledString(String string) {
        this();
        builder.append(string);
    }

    /**
     *
     */
    public StyledString() {
        builder = new StringBuilder();
    }

    /** @param cs */
    public StyledString(char[] cs) {
        this();
        append(cs);
    }

    /**
     * @param name
     * @param styler
     */
    public StyledString(String name, Styler styler) {
        this();
        append(name, styler);
    }

    /** @return  */
    public String getString() {
        return builder.toString();
    }

    /** @param completion */
    public void append(char[] completion) {
        builder.append(htmlEncode(new String(completion)));
    }

    /** @param c */
    public void append(char c) {
        builder.append(c);
    }

    /** @param returnTypeSeparator */
    public void append(String returnTypeSeparator) {
        builder.append(returnTypeSeparator);
    }

    /** @param qualifier */
    public void append(String qualifier, Styler styler) {
        builder.append(styler.applyStyles(qualifier));
    }

    /** @param c */
    public void append(char c, Styler styler) {
        builder.append(styler.applyStyles(String.valueOf(c)));
    }

    /** @param declaration */
    public void append(char[] declaration, Styler styler) {
        builder.append(styler.applyStyles(new String(declaration)));
    }

    /**
     * HTML-encode a string. This simple method only replaces the five characters &, <, >, ", and '.
     *
     * @param input
     *         the String to convert
     * @return a new String with HTML encoded characters
     */
    public static String htmlEncode(String input) {
        String output = input.replaceAll("&", "&amp;");
        output = output.replaceAll("<", "&lt;");
        output = output.replaceAll(">", "&gt;");
        output = output.replaceAll("\"", "&quot;");
        output = output.replaceAll("'", "&#039;");
        return output;
    }

}
