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
package com.codenvy.ide.ext.java.jdi.server.model;


import com.codenvy.ide.ext.java.jdi.shared.VariablePath;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class VariablePathImpl implements VariablePath {
    private List<String> path;

    public VariablePathImpl(List<String> path) {
        this.path = path;
    }

    public VariablePathImpl() {
    }

    @Override
    public List<String> getPath() {
        if (path == null) {
            path = new ArrayList<String>();
        }
        return path;
    }

    @Override
    public void setPath(List<String> path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "VariablePathImpl{" +
               "path=" + path +
               '}';
    }
}
