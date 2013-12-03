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

package com.codenvy.ide.dto.client;


// TODO: These should be moved to an Editor2-specific package

import com.codenvy.ide.dto.DocOp;
import com.codenvy.ide.dto.DocOpComponent;
import com.codenvy.ide.dto.shared.DocOpFactory;
import com.codenvy.ide.collections.js.JsoArray;

import static com.codenvy.ide.dto.DocOpComponent.Type.DELETE;
import static com.codenvy.ide.dto.DocOpComponent.Type.INSERT;
import static com.codenvy.ide.dto.DocOpComponent.Type.RETAIN;
import static com.codenvy.ide.dto.DocOpComponent.Type.RETAIN_LINE;

/**
 */
public final class ClientDocOpFactory implements DocOpFactory {

    public static final ClientDocOpFactory INSTANCE = new ClientDocOpFactory();

    private ClientDocOpFactory() {
    }

    @Override
    public DocOpComponent.Delete createDelete(String text) {
        return (DocOpComponent.Delete)DtoClientImpls.DeleteImpl.make().setText(text).setType(DELETE);
    }

    @Override
    public DocOp createDocOp() {
        return DtoClientImpls.DocOpImpl.make().setComponents(JsoArray.<DocOpComponent>create());
    }

    @Override
    public DocOpComponent.Insert createInsert(String text) {
        return (DocOpComponent.Insert)DtoClientImpls.InsertImpl.make().setText(text).setType(INSERT);
    }

    @Override
    public DocOpComponent.Retain createRetain(int count, boolean hasTrailingNewline) {
        return (DocOpComponent.Retain)DtoClientImpls.RetainImpl.make().setCount(count).setHasTrailingNewline(hasTrailingNewline)
                                                    .setType(RETAIN);
    }

    @Override
    public DocOpComponent.RetainLine createRetainLine(int lineCount) {
        return (DocOpComponent.RetainLine)DtoClientImpls.RetainLineImpl.make().setLineCount(lineCount).setType(RETAIN_LINE);
    }
}
