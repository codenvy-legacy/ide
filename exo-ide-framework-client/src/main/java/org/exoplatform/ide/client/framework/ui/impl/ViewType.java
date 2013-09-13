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
package org.exoplatform.ide.client.framework.ui.impl;

/**
 * Types of view.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 8, 2010 $
 */
public interface ViewType {

    /** View type "navigation". All views of this type will be opened in the left part of IDE. */
    String NAVIGATION = "navigation";

    /** View type "editor". All views of this type will be opened in the editor panel. */
    String EDITOR = "editor";

    /** View type "information". All views of this type will be opened in the right part of IDE. */
    String INFORMATION = "information";

    /** View type "operation". All views of this type will be opened in the bottom part of IDE. */
    String OPERATION = "operation";

    /** View type "popup". All views of this type will be opened in window. */
    String POPUP = "popup";

    /** View type "modal". All views of this type will be opened in modal window. */
    String MODAL = "modal";

}
