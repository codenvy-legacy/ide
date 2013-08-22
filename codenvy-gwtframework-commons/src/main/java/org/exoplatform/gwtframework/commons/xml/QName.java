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

package org.exoplatform.gwtframework.commons.xml;

/**
 * Created by The eXo Platform SAS        .
 *
 * @version $Id: $
 */

public class QName {

    private String localName;

    private String namespaceURI;

    private String prefix;

    public QName(String fullname, String namespaceURI) {
        this.namespaceURI = namespaceURI;
        String[] tmp = fullname.split(":");
        if (tmp.length > 1) {
            this.localName = tmp[1];
            this.prefix = tmp[0];
        } else {
            this.localName = tmp[0];
        }
    }

    public final String getNamespaceURI() {
        return namespaceURI;
    }

    public final String getPrefix() {
        return prefix;
    }

    public final String getLocalName() {
        return localName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((localName == null) ? 0 : localName.hashCode());
        result = prime * result + ((namespaceURI == null) ? 0 : namespaceURI.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final QName other = (QName)obj;
        if (localName == null) {
            if (other.localName != null)
                return false;
        } else if (!localName.equals(other.localName))
            return false;
        if (namespaceURI == null) {
            if (other.namespaceURI != null)
                return false;
        } else if (!namespaceURI.equals(other.namespaceURI))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "{\"prefix\":\"" + prefix + "\",\"localName\": \"" + localName + "\",\"namespaceURI\":\"" + namespaceURI
               + "\"}";
    }

}
