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

import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.dto.DocOp;
import com.codenvy.ide.dto.DocOpComponent;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.dto.shared.DocOpFactory;

import java.util.ArrayList;

import static com.codenvy.ide.dto.DocOpComponent.Delete;
import static com.codenvy.ide.dto.DocOpComponent.Insert;
import static com.codenvy.ide.dto.DocOpComponent.Retain;
import static com.codenvy.ide.dto.DocOpComponent.RetainLine;
import static com.codenvy.ide.dto.DocOpComponent.Type.DELETE;
import static com.codenvy.ide.dto.DocOpComponent.Type.INSERT;
import static com.codenvy.ide.dto.DocOpComponent.Type.RETAIN;
import static com.codenvy.ide.dto.DocOpComponent.Type.RETAIN_LINE;

/**
 */
//FIXME : XXX
public final class ClientDocOpFactory implements DocOpFactory {

    private static ClientDocOpFactory instance;
    private DtoFactory factory;

    private ClientDocOpFactory(DtoFactory factory) {
        this.factory = factory;
    }

    public static ClientDocOpFactory getInstance(DtoFactory factory) {
        if (instance == null) {
            instance = new ClientDocOpFactory(factory);
        }
        return instance;
    }

    @Override
    public Delete createDelete(String text) {
        Delete del = factory.createDto(Delete.class);
        del.setText(text);
        del.setType(DELETE);
        return del;//(DocOpComponent.Delete)DtoClientImpls.DeleteImpl.make().setText(text).setType(DELETE);
    }

    @Override
    public DocOp createDocOp() {
        DocOp docOp = factory.createDto(DocOp.class);
        docOp.setComponents(new ArrayList<DocOpComponent>());
        return docOp;//DtoClientImpls.DocOpImpl.make().setComponents(JsoArray.<DocOpComponent>create());
    }

    @Override
    public Insert createInsert(String text) {
        Insert insert = factory.createDto(Insert.class);
        insert.setText(text);
        insert.setType(INSERT);
        return insert;//(DocOpComponent.Insert)DtoClientImpls.InsertImpl.make().setText(text).setType(INSERT);
    }

    @Override
    public Retain createRetain(int count, boolean hasTrailingNewline) {
        Retain retain = factory.createDto(Retain.class);
        retain.setCount(count);
        retain.setTrailingNewline(hasTrailingNewline);
        retain.setType(RETAIN);
        return retain;//(DocOpComponent.Retain)DtoClientImpls.RetainImpl.make().setCount(count).setHasTrailingNewline(isTrailingNewline)
        // .setType(RETAIN);
    }

    @Override
    public RetainLine createRetainLine(int lineCount) {
        RetainLine retainLine = factory.createDto(RetainLine.class);
        retainLine.setLineCount(lineCount);
        retainLine.setType(RETAIN_LINE);
        return retainLine;//(DocOpComponent.RetainLine)DtoClientImpls.RetainLineImpl.make().setLineCount(lineCount).setType(RETAIN_LINE);
    }
}
