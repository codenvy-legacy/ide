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
package org.exoplatform.ide.extension.java.server.parser;

import com.thoughtworks.qdox.JavaDocBuilder.ErrorHandler;
import com.thoughtworks.qdox.parser.ParseException;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Dec 1, 2011 11:48:25 AM evgen $
 */
public class JavaDocBuilderErrorHandler implements ErrorHandler {

    /** @see com.thoughtworks.qdox.JavaDocBuilder.ErrorHandler#handle(com.thoughtworks.qdox.parser.ParseException) */
    @Override
    public void handle(ParseException parseException) {
        // TODO collect parser errors
    }

}
