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
package org.exoplatform.ide.editor.api.codeassitant;

import java.util.Collection;
import java.util.Set;

/**
 * Token interface represent abstract token.
 * <p/>
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public interface Token {

    /**
     * Get name of token
     *
     * @return name
     */
    String getName();

    /**
     * Get token type
     *
     * @return type
     */
    TokenType getType();

    /**
     * Get names of all token properties
     *
     * @return {@link Set} of {@link String} properties names
     */
    Set<String> getPropertiesNames();

    /**
     * Get all token properties
     *
     * @return {@link Collection} of {@link TokenProperty}
     */
    Collection<TokenProperty> getProperties();

    /**
     * Get specific property
     *
     * @param name
     *         of property
     * @return {@link TokenProperty} property
     */
    TokenProperty getProperty(String name);

    /**
     * Set property
     *
     * @param name
     *         of property
     * @param property
     *         value
     */
    void setProperty(String name, TokenProperty property);

    /**
     * Check is token has property
     *
     * @param name
     *         of property
     * @return <code>true</code> if token has property, otherwise <code>false</code>
     */
    boolean hasProperty(String name);

}
