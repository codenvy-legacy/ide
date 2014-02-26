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

//FIXME : XXX
public final class ClientDocOpFactory implements DocOpFactory {

    private static ClientDocOpFactory instance;
    private        DtoFactory         dtoFactory;

    private ClientDocOpFactory(DtoFactory dtoFactory) {
        this.dtoFactory = dtoFactory;
    }

    public static ClientDocOpFactory getInstance(DtoFactory factory) {
        if (instance == null) {
            instance = new ClientDocOpFactory(factory);
        }
        return instance;
    }

    @Override
    public Delete createDelete(String text) {
        Delete del = dtoFactory.createDto(Delete.class);
        del.setText(text);
        del.setType(DELETE);
        return del;
    }

    @Override
    public DocOp createDocOp() {
        DocOp docOp = dtoFactory.createDto(DocOp.class);
        docOp.setComponents(new ArrayList<DocOpComponent>());
        return docOp;
    }

    @Override
    public Insert createInsert(String text) {
        Insert insert = dtoFactory.createDto(Insert.class);
        insert.setText(text);
        insert.setType(INSERT);
        return insert;
    }

    @Override
    public Retain createRetain(int count, boolean hasTrailingNewline) {
        Retain retain = dtoFactory.createDto(Retain.class);
        retain.setCount(count);
        retain.setTrailingNewline(hasTrailingNewline);
        retain.setType(RETAIN);
        return retain;
    }

    @Override
    public RetainLine createRetainLine(int lineCount) {
        RetainLine retainLine = dtoFactory.createDto(RetainLine.class);
        retainLine.setLineCount(lineCount);
        retainLine.setType(RETAIN_LINE);
        return retainLine;
    }
}
