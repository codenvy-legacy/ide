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
package org.exoplatform.ide.client.framework.control;

import org.exoplatform.gwtframework.ui.client.command.Control;

import java.util.List;

/**
 * Impement it to create any formatter for controls order.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 19, 2011 5:05:29 PM anya $
 */
public interface ControlsFormatter {
    /**
     * Performs formatting controls.
     *
     * @param controls
     *         controls to format
     */
    void format(List<Control> controls);

}
