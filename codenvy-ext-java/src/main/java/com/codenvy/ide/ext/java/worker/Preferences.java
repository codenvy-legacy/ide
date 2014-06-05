/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.worker;

import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 4:30:50 PM Mar 29, 2012 evgen $
 */
public class Preferences {

    /**
     * A named preference that stores the content assist LRU history
     * <p>
     * Value is an JSON encoded version of the history.
     * </p>
     */
    public static final String CODEASSIST_LRU_HISTORY = "content_assist_lru_history_";

    public static final String QUALIFIED_TYPE_NAMEHISTORY = "Qualified_Type_Name_History_";

    private StringMap<String> storage = Collections.createStringMap();

    /**
     *
     */
    public Preferences() {

    }

    /**
     * @param key
     * @param string
     */
    public void setValue(String key, String string) {
         storage.put(key, string);
    }

    /**
     * @param key
     * @return
     */
    public String getString(String key) {
        return storage.get(key);
    }

}
