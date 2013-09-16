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
package org.exoplatform.ide.vfs.shared;

import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PropertyFilter.java 79579 2012-02-17 13:27:25Z andrew00x $
 */
public class PropertyFilter {
    /** Property filter for all properties. */
    public static final String ALL = "*";

    /** Property filter for skip all properties. */
    public static final String NONE = "none";

    /** Property filter for all properties. */
    public static final PropertyFilter ALL_FILTER;

    /** Property filter for skip all properties. */
    public static final PropertyFilter NONE_FILTER;

    static {
        ALL_FILTER = new PropertyFilter();
        ALL_FILTER.retrievalAllProperties = true;
        NONE_FILTER = new PropertyFilter();
        NONE_FILTER.propertyNames = Collections.emptySet();
    }

    /**
     * Construct new Property Filter.
     *
     * @param filterString
     *         the string that contains either '*' or comma-separated list of properties names. An arbitrary
     *         number of space allowed before and after each comma. If filterString is 'none' it minds all properties should be
     *         rejected by filter.
     * @return PropertyFilter instance
     * @throws InvalidArgumentException
     *         if <code>filterString</code> is invalid
     */
    public static PropertyFilter valueOf(String filterString) throws InvalidArgumentException {
        if (filterString == null || filterString.length() == 0 || ALL.equals(filterString = filterString.trim())) {
            return ALL_FILTER;
        } else if (filterString.equalsIgnoreCase(NONE)) {
            return NONE_FILTER;
        }
        return new PropertyFilter(filterString);
    }

    /** Characters that split. */
    private static final Pattern SPLITTER = Pattern.compile("\\s*,\\s*");

    /** Characters that not allowed in property name. */
    private static final String ILLEGAL_CHARACTERS = ",\"'\\.()";

    /** Property names. */
    private Set<String> propertyNames;

    /** Is all properties requested. */
    private boolean retrievalAllProperties = false;

    /**
     * Construct new Property Filter.
     *
     * @param filterString
     *         the string that contains either '*' or comma-separated list of properties names. An arbitrary
     *         number of
     *         space allowed before and after each comma.
     * @throws InvalidArgumentException
     *         if <code>filterString</code> is invalid
     */
    private PropertyFilter(String filterString) throws InvalidArgumentException {
        this.propertyNames = new HashSet<String>();
        for (String token : SPLITTER.split(filterString)) {
            if (token.length() > 0 && !token.equals(ALL)) {
                for (char ch : token.toCharArray()) {
                    if (Character.isWhitespace(ch) || ILLEGAL_CHARACTERS.indexOf(ch) != -1) {
                        throw new InvalidArgumentException("Invalid filter '" + filterString
                                                           + "' contains illegal characters.");
                    }
                }
                this.propertyNames.add(token);
            } else {
                throw new InvalidArgumentException("Invalid filter '" + filterString
                                                   + "'. Filter must contains either '*' OR comma-separated list of properties.");
            }
        }
    }

    private PropertyFilter() {
    }

    public boolean accept(String name) {
        return retrievalAllProperties || propertyNames.contains(name);
    }
}
