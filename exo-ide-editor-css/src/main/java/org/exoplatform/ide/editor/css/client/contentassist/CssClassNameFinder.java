/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.editor.css.client.contentassist;

import com.google.collide.client.code.autocomplete.AutocompleteProposal;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import org.exoplatform.ide.json.client.JsoArray;
import org.exoplatform.ide.json.shared.JsonArray;

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
