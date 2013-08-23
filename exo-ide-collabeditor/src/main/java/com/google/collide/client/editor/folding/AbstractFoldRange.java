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
package com.google.collide.client.editor.folding;

import org.exoplatform.ide.editor.shared.text.Position;
import org.exoplatform.ide.editor.shared.text.projection.IProjectionPosition;

/**
 * A base implementation of a text range that may be collapsible.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: AbstractFoldRange.java Mar 18, 2013 12:18:43 PM azatsarynnyy $
 */
public abstract class AbstractFoldRange extends Position implements IProjectionPosition {
    public AbstractFoldRange(int offset, int length) {
        super(offset, length);
    }
}
