/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.jseditor.client.minimap;

/**
 * Interface for editor minimaps.
 */
public interface Minimap {

    /**
     * Add a mark on the minimap.<br>
     * @param line the line where the mark is set
     * @param style the style of the mark
     */
    void addMark(int line, String style);

    /**
     * Add a mark on the minimap.
     * @param line the line where the mark is set
     * @param style the style of the mark
     * @param level the mark priority level
     */
    void addMark(int line, String style, int level);

    /**
     * Remove the marks on the lines between the two given lines (included).
     * @param lineStart the beginning of the range
     * @param lineEnd the end of the range
     */
    void removeMarks(int lineStart, int lineEnd);

    /**
     * Clear all marks on the minimap.
     */
    void clearMarks();
}
