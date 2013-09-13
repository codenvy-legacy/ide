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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Basic implementation of {@link Token} interface
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 5:22:15 PM evgen $
 */
public class TokenImpl implements Token {
    private Map<String, TokenProperty> properties = new LinkedHashMap<String, TokenProperty>();

    private String name;

    private TokenType type;

    public TokenImpl() {
    }

    /**
     * @param name
     * @param type
     */
    public TokenImpl(String name, TokenType type) {
        this.name = name;
        this.type = type;
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.Token#getName() */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.Token#getType() */
    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.Token#getPropertiesKeys() */
    public Set<String> getPropertiesNames() {
        return properties.keySet();
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.Token#getProperties() */

    public Collection<TokenProperty> getProperties() {
        return properties.values();
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.Token#getProperty(java.lang.String) */

    public TokenProperty getProperty(String key) {
        return properties.get(key);
    }

    /**
     * @see org.exoplatform.ide.editor.api.codeassitant.Token#setProperty(java.lang.String,
     *      org.exoplatform.ide.editor.api.codeassitant.TokenProperty)
     */

    public void setProperty(String key, TokenProperty property) {
        properties.put(key, property);
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.Token#hasProperty(java.lang.String) */

    public boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

}
