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

package com.codenvy.ide.texteditor.selection;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.text.Document;
import com.codenvy.ide.api.texteditor.FocusManager;
import com.codenvy.ide.text.store.DocumentModel;
import com.codenvy.ide.texteditor.Buffer;
import com.codenvy.ide.texteditor.renderer.Renderer;

/*
 * TODO: split SelectionModel into multiple components owned by
 * this class
 */

/** Manages and owns different components related to text selection. */
public class SelectionManager {

    public static SelectionManager create(Document doc, DocumentModel document, Buffer buffer,
                                          FocusManager focusManager,
                                          Resources resources) {
        SelectionModel selectionModel = SelectionModel.create(doc, document, buffer);
        SelectionLineRenderer selectionLineRenderer = new SelectionLineRenderer(selectionModel, focusManager, resources);

        return new SelectionManager(selectionModel, selectionLineRenderer);
    }

    private Renderer renderer;

    private final SelectionLineRenderer selectionLineRenderer;

    private final SelectionModel selectionModel;

    private SelectionManager(SelectionModel selectionModel, SelectionLineRenderer selectionLineRenderer) {
        this.selectionModel = selectionModel;
        this.selectionLineRenderer = selectionLineRenderer;
    }

    public void initialize(Renderer renderer) {
        this.renderer = renderer;
        renderer.addLineRenderer(selectionLineRenderer);
    }

    public void teardown() {
        renderer.removeLineRenderer(selectionLineRenderer);
        selectionModel.teardown();
    }

    public SelectionModel getSelectionModel() {
        return selectionModel;
    }
}
