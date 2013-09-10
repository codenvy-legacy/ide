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
package com.codenvy.ide.ext.aws.shared.beanstalk;

import com.codenvy.ide.dto.DTO;

/**
 * A regular expression representing a restriction on a string configuration option value.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@DTO
public interface ConfigurationOptionRestriction {
    /**
     * A unique name representing this regular expression.
     *
     * @return A unique name representing this regular expression.
     */
    String getLabel();

    /**
     * The regular expression pattern that a string configuration option
     * value with this restriction must match.
     *
     * @return The regular expression pattern that a string configuration option
     *         value with this restriction must match.
     */
    String getPattern();
}
