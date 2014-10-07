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
package com.codenvy.ide.jseditor.client.texteditor;

/**
 * Interface for editors that allow line styling.
 */
public interface LineStyler {

    /**
     * Adds the styles to the line.
     * @param lineNumber the line number
     * @param styles the styles to add
     */
    void addLineStyles(int lineNumber, String... styles);

    /**
     * Removes the styles on the line.
     * @param lineNumber the line number
     * @param styles the styles to remove
     */
    void removeLineStyles(int lineNumber, String... styles);

    /**
     * Removes all styles on the line.
     * @param lineNumber the line number
     */
    void clearLineStyles(int lineNumber);
}
