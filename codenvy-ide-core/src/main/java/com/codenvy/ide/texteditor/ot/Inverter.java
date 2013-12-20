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

package com.codenvy.ide.texteditor.ot;


import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.dto.DocOp;
import com.codenvy.ide.dto.DocOpComponent;
import com.codenvy.ide.dto.shared.DocOpFactory;

/**
 * Inverts document operations such that A composed with the inverse of A is an
 * identity document operation.
 */
public class Inverter {

    /** Inverts the given document operation. */
    public static DocOp invert(DocOpFactory factory, DocOp docOp) {
        DocOp invertedDocOp = factory.createDocOp();
        Array<DocOpComponent> invertedDocOpComponents = Collections.createArray(invertedDocOp.getComponents());

        Array<DocOpComponent> components = Collections.createArray(docOp.getComponents());
        for (int i = 0, n = components.size(); i < n; i++) {
            invertedDocOpComponents.add(invertComponent(factory, components.get(i)));
        }

        return invertedDocOp;
    }

    private static DocOpComponent invertComponent(DocOpFactory factory, DocOpComponent component) {
        switch (component.getType()) {
            case DocOpComponent.Type.INSERT:
                return factory.createDelete(((DocOpComponent.Insert)component).getText());

            case DocOpComponent.Type.DELETE:
                return factory.createInsert(((DocOpComponent.Insert)component).getText());

            default:
                return component;
        }
    }
}
