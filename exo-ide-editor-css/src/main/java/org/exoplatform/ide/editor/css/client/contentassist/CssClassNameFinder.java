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
package org.exoplatform.ide.editor.css.client.contentassist;

import com.codenvy.ide.json.client.JsoArray;
import com.codenvy.ide.json.shared.JsonArray;
import com.google.collide.client.code.autocomplete.AutocompleteProposal;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;


/**
 * Based on the class name list that is already defined
 * in the current style sheet, this class proposes
 * autocompletions for the slot where the cursor currently is.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CssClassNameFinder.java Apr 5, 2013 11:38:05 AM azatsarynnyy $
 */
public class CssClassNameFinder {

    private static final RegExp REGEXP_CSS_CLASS_NAMES = RegExp.compile("\\.(-?[_a-zA-Z]+[_a-zA-Z0-9-]*)(?![^\\{]*\\})", "g");

    /**
     * Finds all autocompletions and filters them based on the prefix that the user has already typed.
     * 
     * @param prefix the prefix of the class name that the user has already typed
     * @param text the text to find CSS class names
     * @return an array of autocompletions, or an empty array if there are no autocompletion proposals
     */
    public static JsonArray<AutocompleteProposal> findAutocompletions(String prefix, String text) {
        JsoArray<AutocompleteProposal> proposals = JsoArray.create();
        for (MatchResult result = REGEXP_CSS_CLASS_NAMES.exec(text); result != null; result = REGEXP_CSS_CLASS_NAMES.exec(text)) {
            String proposalLabel = result.getGroup(0);
            if (proposalLabel.startsWith(prefix) && !proposalLabel.equals(prefix)) {
                AutocompleteProposal value = new AutocompleteProposal(proposalLabel);
                if (!proposals.contains(value)) {
                    proposals.add(value);
                }
            }
        }
        return proposals;
    }
}
