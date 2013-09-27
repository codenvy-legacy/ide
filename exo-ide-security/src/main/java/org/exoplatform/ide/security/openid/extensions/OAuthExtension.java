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
package org.exoplatform.ide.security.openid.extensions;

import org.openid4java.message.*;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OAuthExtension implements MessageExtension, MessageExtensionFactory {
    public static final String TYPE_URI = "http://specs.openid.net/extensions/oauth/1.0";

    private ParameterList params;

    public OAuthExtension(String consumer, List<String> scopes) {
        if (consumer == null || consumer.isEmpty()) {
            throw new IllegalArgumentException("Consumer key is required. ");
        }
        if (scopes == null || scopes.isEmpty()) {
            throw new IllegalArgumentException("List of scopes is required. ");
        }
        this.params = new ParameterList();
        this.params.set(new Parameter("consumer", consumer));
        this.params.set(new Parameter("scope", flattenScopes(scopes)));
    }

    private static String flattenScopes(List<String> scopes) {
        StringBuilder flatten = new StringBuilder();
        Iterator<String> iterator = scopes.iterator();
        while (true) {
            String s = iterator.next();
            flatten.append(s);
            if (!iterator.hasNext()) {
                return flatten.toString();
            }
            flatten.append(' ');
        }
    }

    private OAuthExtension(ParameterList params) {
        validate(params);
        this.params = params;
    }

    // MessageExtensionFactory
    @Override
    public MessageExtension getExtension(ParameterList parameterList, boolean isRequest) throws MessageException {
        return new OAuthExtension(parameterList);
    }
    // -----

    @Override
    public final String getTypeUri() {
        return TYPE_URI;
    }

    @Override
    public ParameterList getParameters() {
        return this.params;
    }

    @Override
    public void setParameters(ParameterList params) {
        validate(params);
        this.params = params;
    }

    @Override
    public final boolean providesIdentifier() {
        return false;
    }

    @Override
    public final boolean signRequired() {
        return true;
    }

    //

    /**
     * Check is required parameters presents in parameters list.
     *
     * @param params
     *         parameters list for validation
     * @throws IllegalArgumentException
     *         if parameters list does not contains required parameter
     */

    private void validate(ParameterList params) {
        if (!params.hasParameter("consumer")) {
            throw new IllegalArgumentException("Consumer key is required. ");
        }
        if (!params.hasParameter("scope")) {
            throw new IllegalArgumentException("List of scopes is required. ");
        }
    }
}
