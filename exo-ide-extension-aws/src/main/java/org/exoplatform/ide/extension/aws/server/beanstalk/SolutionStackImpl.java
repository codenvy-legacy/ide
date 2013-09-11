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
package org.exoplatform.ide.extension.aws.server.beanstalk;

import org.exoplatform.ide.extension.aws.shared.beanstalk.SolutionStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SolutionStackImpl implements SolutionStack {
    private String       name;
    private List<String> permittedFileTypes;

    public SolutionStackImpl(String name, List<String> permittedFileTypes) {
        this.name = name;
        this.permittedFileTypes = permittedFileTypes;
    }

    public SolutionStackImpl() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<String> getPermittedFileTypes() {
        if (permittedFileTypes == null) {
            permittedFileTypes = new ArrayList<String>();
        }
        return permittedFileTypes;
    }

    @Override
    public void setPermittedFileTypes(List<String> permittedFileTypes) {
        this.permittedFileTypes = permittedFileTypes;
    }

    @Override
    public String toString() {
        return "SolutionStackImpl{" +
               "name='" + name + '\'' +
               ", permittedFileTypes=" + permittedFileTypes +
               '}';
    }
}
