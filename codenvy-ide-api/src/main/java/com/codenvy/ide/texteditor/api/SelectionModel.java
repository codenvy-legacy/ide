// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.codenvy.ide.texteditor.api;

import com.codenvy.ide.text.Position;

/**
 * A interface that models the user's selection.
 * 
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface SelectionModel extends CursorModel {
    /** Clear selection */
    void deselect();

    /** @return true if editor has selection */
    boolean hasSelection();

    /** Select all test in editor. */
    void selectAll();

    /**
     * Get selected range
     * 
     * @return the selected range
     */
    Position getSelectedRange();

    /**
     * Select and reveal text in editor
     * 
     * @param offset the offset, start selection
     * @param length the length of the selection
     */
    void selectAndReveal(int offset, int length);

}
