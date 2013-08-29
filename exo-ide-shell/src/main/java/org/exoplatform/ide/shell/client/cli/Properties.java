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
package org.exoplatform.ide.shell.client.cli;

import java.util.*;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */

/**
 * Minimal emulation of {@link java.util.Properties}.
 *
 * @author Hayward Chan
 */
public class Properties extends AbstractMap<Object, Object> {
    /*
     * Q: Why use a backing map instead of having the class extends from HashMap directly? A: This will make the class
     * serializable. While it works in web mode, it doesn't work in hosted mode because this implementation conflicts with the JDK
     * version, and cause VerifyError. It can be worked around by providing custom field serializer, but it probably isn't worth
     * the effort.
     */
    private final Map<Object, Object> values = new HashMap<Object, Object>();

    public Properties() {
    }

    public Enumeration<?> propertyNames() {
        return Collections.enumeration(keySet());
    }

    public String getProperty(String name) {
        return (String)get(name);
    }

    @Override
    public Object put(Object key, Object value) {
        return values.put(key, value);
    }

    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return values.entrySet();
    }
}
