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
package com.codenvy.ide.ext.java.jdt.templates.api;

import com.google.gwt.regexp.shared.MatchResult;

/**
 * GWT adaptation of {@link java.util.regex.Matcher}
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Matcher {

    private Pattern pat = null;

    private String expression = null;

    private int lastPos = 0;

    private MatchResult matches;

    Matcher(Pattern pat, CharSequence cs) {
        this.pat = pat;
        this.expression = cs.toString();
    }

    public boolean find() {
        matches = pat.matches(expression);
        return (matches != null);
    }

    public int start() {
        return expression.indexOf(matches.getGroup(0), lastPos);
    }

    public int end() {
        lastPos = expression.indexOf(matches.getGroup(0), lastPos) + matches.getGroup(0).length();
        return lastPos;
    }

    /** @return  */
    public String group() {
        return matches.getGroup(0);
    }

    /**
     * @param i
     * @return
     */
    public String group(int i) {
        return matches.getGroup(i);
    }

}