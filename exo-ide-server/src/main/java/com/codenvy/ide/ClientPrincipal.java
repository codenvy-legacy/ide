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
package com.codenvy.ide;

import java.security.Principal;
import java.util.*;

/** Represent Principal on SSO client side. */
public class ClientPrincipal implements Principal {
    private final String                   name;
    private final Set<String>              registeredClients;
    private       Map<String, Set<String>> roles;

    public ClientPrincipal(String name, Map<String, Set<String>> roles) {
        this.name = name;
        this.roles = roles;
        this.registeredClients = new HashSet<>();
    }

    @Override
    public String toString() {
        return "ClientPrincipal{" +
               "name='" + name + '\'' +
               ", registeredClients=" + registeredClients +
               ", roles=" + roles +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientPrincipal that = (ClientPrincipal)o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    /**
     * @param clientUrl
     *         - given client url.
     * @return - true if SSO server knows about registration of the current user in given client url.
     */
    public boolean isClientRegistered(String clientUrl) {
        return registeredClients.contains(clientUrl);
    }

    /**
     * @param clientUrl
     *         - Indicate that SSO server knows about registration of the current user in given client url.
     */
    void registerClientUrl(String clientUrl) {
        registeredClients.add(clientUrl);
    }

    @Override

    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * @param workspaceId
     *         - given workspace.
     * @return Set of roles in given workspace.
     */
    public Set<String> getRoles(String workspaceId) {
        Set<String> result = roles.get(workspaceId);
        if (result != null) {
            return Collections.unmodifiableSet(result);
        }
        return Collections.EMPTY_SET;
    }

    Map<String, Set<String>> getRoles() {
        return roles;
    }

    void setRoles(Map<String, Set<String>> roles) {
        if (roles == null) {
            throw new IllegalArgumentException("Null value is not allowed for roles parameter");
        }
        Map<String, Set<String>> newRoles = new HashMap<>(roles.size());
        for (Map.Entry<String, Set<String>> entry : roles.entrySet()) {
            newRoles.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        this.roles = newRoles;
    }

}
