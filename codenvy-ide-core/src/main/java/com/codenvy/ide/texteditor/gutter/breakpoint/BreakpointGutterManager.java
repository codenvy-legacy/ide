/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.texteditor.gutter.breakpoint;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.resources.model.File;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a> */
@Singleton
public class BreakpointGutterManager {
    private JsonStringMap<JsonArray<Breakpoint>> breakPoints;
    private EditorAgent                          editorAgent;

    @Inject
    protected BreakpointGutterManager(EditorAgent editorAgent) {
        this.editorAgent = editorAgent;
        this.breakPoints = JsonCollections.createStringMap();
    }

    public void changeBreakPoint(int lineNumber) {
        File activeFile = editorAgent.getActiveEditor().getEditorInput().getFile();
        JsonArray<Breakpoint> breakPoints = this.breakPoints.get(activeFile.getId());
        if (breakPoints != null) {
            if (!breakPoints.isEmpty()) {
                for (int i = 0; i < breakPoints.size(); i++) {
                    Breakpoint breakpoint = breakPoints.get(i);
                    if (breakpoint.getLineNumber() == lineNumber) {
                        breakPoints.remove(i);
                        return;
                    }
                }
            } else {
                breakPoints.add(new Breakpoint(Breakpoint.Type.BREAKPOINT, lineNumber));
            }

        } else {
            this.breakPoints
                    .put(activeFile.getId(),
                         JsonCollections.<Breakpoint>createArray(new Breakpoint(Breakpoint.Type.BREAKPOINT, lineNumber)));
        }
    }

    public boolean isBreakPointExist(int lineNumber) {
        File activeFile = editorAgent.getActiveEditor().getEditorInput().getFile();
        JsonArray<Breakpoint> breakPoints = this.breakPoints.get(activeFile.getId());
        if (breakPoints != null) {
            for (int i = 0; i < breakPoints.size(); i++) {
                Breakpoint breakpoint = breakPoints.get(i);
                if (breakpoint.getLineNumber() == lineNumber) {
                    return true;
                }
            }
        }
        return false;
    }
}