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
package com.codenvy.ide.texteditor.renderer;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.text.store.Line;
import com.codenvy.ide.text.store.LineNumberAndColumn;
import com.codenvy.ide.texteditor.ot.PositionMigrator;
import com.codenvy.ide.util.SortedList;
import com.codenvy.ide.util.loging.Log;


/** Renders code errors in the editor. */
public class ErrorRenderer implements LineRenderer {

    private static final SortedList.Comparator<AnnotationCode> ERROR_COMPARATOR = new SortedList.Comparator<AnnotationCode>() {
        @Override
        public int compare(AnnotationCode annotation1, AnnotationCode annotation2) {
            int startLineDiff = annotation1.getStart().getLineNumber() - annotation2.getStart().getLineNumber();
            if (startLineDiff != 0) {
                return startLineDiff;
            }
            int startColumnDiff = annotation1.getStart().getColumn() - annotation2.getStart().getColumn();
            if (startColumnDiff != 0) {
                return startColumnDiff;
            }
            int endLineDiff = annotation1.getEnd().getLineNumber() - annotation2.getEnd().getLineNumber();
            if (endLineDiff != 0) {
                return endLineDiff;
            } else {
                return annotation1.getEnd().getColumn() - annotation2.getEnd().getColumn();
            }
        }
    };

    public Array<AnnotationCode> getCodeErrors() {
        return codeErrors;
    }

    private int currentLineNumber;

    private int currentLineLength;

    // Current render start position.
    private int linePosition;

    // Errors that are visible at current line. They may start on the previous line
    // (or even earlier) or end in one of the next lines.
    private SortedList<AnnotationCode> lineErrors;

    // Index of next error to render in lineErrors array.
    private int nextErrorIndex;

    // List of errors for a file.
    private Array<AnnotationCode> codeErrors;

    private PositionMigrator positionMigrator;

    public ErrorRenderer() {
        codeErrors = Collections.createArray();
    }

    @Override
    public void renderNextChunk(Target target) {
        AnnotationCode nextError = getNextErrorToRender();
        if (nextError == null) {
            // No errors to render. So render the rest of the line with null.
            renderNothingAndProceed(target, currentLineLength - linePosition);
        } else if (nextError.getStart().getLineNumber() < currentLineNumber
                   || nextError.getStart().getColumn() == linePosition) {
            int errorLength;
            if (nextError.getEnd().getLineNumber() > currentLineNumber) {
                errorLength = currentLineLength - linePosition;
            } else {
                // Error ends at current line.
                errorLength = nextError.getEnd().getColumn() + 1 - linePosition;
            }
            renderErrorAndProceed(target, errorLength, nextError.getDecoration());
        } else {
            // Wait until we get to the next error.
            renderNothingAndProceed(target, nextError.getStart().getColumn() - linePosition);
        }
    }

    @Override
    public boolean shouldLastChunkFillToRight() {
        return false;
    }

    private void renderErrorAndProceed(Target target, int characterCount, String decoration) {
        Log.debug(getClass(), "Rendering " + characterCount + " characters with error style at position " + linePosition
                              + ", next line position: " + (linePosition + characterCount));
        target.render(characterCount, decoration);
        linePosition += characterCount;
        nextErrorIndex++;
    }

    private void renderNothingAndProceed(Target target, int characterCount) {
        target.render(characterCount, null);
        linePosition += characterCount;
    }

    private AnnotationCode getNextErrorToRender() {
        while (nextErrorIndex < lineErrors.size()) {
            AnnotationCode nextError = lineErrors.get(nextErrorIndex);
            if (nextError.getEnd().getLineNumber() == currentLineNumber
                && nextError.getEnd().getColumn() < linePosition) {
                // This may happen if errors overlap.
                nextErrorIndex++;
                continue;
            } else {
                return nextError;
            }
        }
        return null;
    }

    @Override
    public boolean resetToBeginningOfLine(Line line, int lineNumber) {
        // TODO: Convert to anchors so that error positions are updated when text edits happen.
        this.lineErrors = getErrorsAtLine(lineNumber);
        if (lineErrors.size() > 0) {
            Log.debug(getClass(), "Rendering line: " + lineNumber, ", errors size: " + lineErrors.size());
        } else {
            return false;
        }
        this.currentLineNumber = lineNumber;
        this.currentLineLength = line.getText().length();
        this.nextErrorIndex = 0;
        this.linePosition = 0;
        return true;
    }

    private SortedList<AnnotationCode> getErrorsAtLine(int lineNumber) {
        int oldLineNumber = migrateLineNumber(lineNumber);
        SortedList<AnnotationCode> result = new SortedList<AnnotationCode>(ERROR_COMPARATOR);
        for (int i = 0; i < codeErrors.size(); i++) {
            AnnotationCode error = codeErrors.get(i);
            if (error.getStart().getLineNumber() <= oldLineNumber
                && error.getEnd().getLineNumber() >= oldLineNumber) {
                result.add(migrateError(error));
            }
        }
        return result;
    }

    private int migrateLineNumber(int lineNumber) {
        if (positionMigrator == null) {
            return lineNumber;
        } else {
            return positionMigrator.migrateFromNow(lineNumber, 0).lineNumber;
        }
    }

    private AnnotationCode migrateError(AnnotationCode oldError) {
        DocumentPosition newErrorStart = migrateFilePositionToNow(oldError.getStart());
        DocumentPosition newErrorEnd = migrateFilePositionToNow(oldError.getEnd());
        if (newErrorStart == oldError.getStart() && newErrorEnd == oldError.getEnd()) {
            return oldError;
        }
        AnnotationCode newError = new AnnotationCode(newErrorStart, newErrorEnd, oldError.getDecoration());
        Log.debug(getClass(), "Migrated error [" + codeErrorToString(oldError) + "] to [" + codeErrorToString(newError)
                              + "]");
        return newError;
    }

    private DocumentPosition migrateFilePositionToNow(DocumentPosition filePosition) {
        if (!positionMigrator.haveChanges()) {
            return filePosition;
        }
        LineNumberAndColumn newPosition =
                positionMigrator.migrateToNow(filePosition.getLineNumber(), filePosition.getColumn());
        return new DocumentPosition(newPosition.lineNumber, newPosition.column);
    }

    public void setCodeErrors(Array<AnnotationCode> codeErrors, PositionMigrator positionMigrator) {
        this.codeErrors = codeErrors;
        this.positionMigrator = positionMigrator;
    }

    private static String filePositionToString(DocumentPosition position) {
        return "(" + position.getLineNumber() + "," + position.getColumn() + ")";
    }

    private static String codeErrorToString(AnnotationCode codeError) {
        if (codeError == null) {
            return "null";
        } else {
            return filePositionToString(codeError.getStart()) + "-" + filePositionToString(codeError.getEnd());
        }
    }
}
