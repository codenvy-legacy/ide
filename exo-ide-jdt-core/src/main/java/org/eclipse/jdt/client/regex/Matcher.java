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

package org.eclipse.jdt.client.regex;

import com.google.gwt.regexp.shared.MatchResult;

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