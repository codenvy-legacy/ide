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
package com.codenvy.ide.ext.java.worker.env;

import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryElementValuePair;
import com.codenvy.ide.ext.java.worker.env.json.ElementValuePairJso;

/**
 * @author Evgen Vidolob
 */
public class ElementValuePair implements IBinaryElementValuePair {

    private ElementValuePairJso jso;

    public ElementValuePair(ElementValuePairJso jso) {
        this.jso = jso;
    }

    @Override
    public char[] getName() {
        if(jso.getName() == null) return null;
        return jso.getName().toCharArray();
    }

    @Override
    public Object getValue() {
        return Util.getDefaultValue(jso.getValue());
    }
}
