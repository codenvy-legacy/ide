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
package org.exoplatform.ide.maven;

import java.io.File;
import java.io.IOException;

/**
 * Dependent of maven goals result of running maven task may have different form. To be able get result of build
 * implementation of this interface should be passed to MavenInvoker before calling of {@link
 * MavenInvoker#execute(org.apache.maven.shared.invoker.InvocationRequest)}.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ResultGetter {
    Result getResult(File projectDirectory) throws IOException;
}
