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
package org.exoplatform.ide.extension.gadget.server.opensocial.model;

/**
 * The components of the person's real name.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 */
public class Name {
    /** The family name of this Person, or "Last Name". */
    private String familyName;

    /**
     * The full name, including all middle names, titles, and suffixes as appropriate, formatted for display (e.g. Mr. Joseph
     * Robert Smarr, Esq.).
     */
    private String formatted;

    /** The given name of this Person, or "First Name". */
    private String givenName;

    /** The honorific prefix(es) of this Person. */
    private String honorificPrefix;

    /** The honorifix suffix(es) of this Person. */
    private String honorificSuffix;

    /** The middle name(s) of this Person. */
    private String middleName;

    /** @return the familyName */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * @param familyName
     *         the familyName to set
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    /** @return the formatted */
    public String getFormatted() {
        return formatted;
    }

    /**
     * @param formatted
     *         the formatted to set
     */
    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    /** @return the givenName */
    public String getGivenName() {
        return givenName;
    }

    /**
     * @param givenName
     *         the givenName to set
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    /** @return the honorificPrefix */
    public String getHonorificPrefix() {
        return honorificPrefix;
    }

    /**
     * @param honorificPrefix
     *         the honorificPrefix to set
     */
    public void setHonorificPrefix(String honorificPrefix) {
        this.honorificPrefix = honorificPrefix;
    }

    /** @return the honorificSuffix */
    public String getHonorificSuffix() {
        return honorificSuffix;
    }

    /**
     * @param honorificSuffix
     *         the honorificSuffix to set
     */
    public void setHonorificSuffix(String honorificSuffix) {
        this.honorificSuffix = honorificSuffix;
    }

    /** @return the middleName */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * @param middleName
     *         the middleName to set
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
}
