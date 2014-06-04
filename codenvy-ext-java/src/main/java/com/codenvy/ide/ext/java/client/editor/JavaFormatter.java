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
package com.codenvy.ide.ext.java.client.editor;

import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.edits.TextEdit;
import com.codenvy.ide.texteditor.api.ContentFormatter;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;

/**
 * ContentFormatter implementation
 *
 * @author Roman Nikitenko
 */
public class JavaFormatter implements ContentFormatter, JavaParserWorker.FormatResultCallback {

    private JavaParserWorker javaParserWorker;
    private Document document;

    @Inject
    public JavaFormatter(JavaParserWorker javaParserWorker){
        this.javaParserWorker = javaParserWorker;
    }

    @Override
    public void format(Document doc, Region region) {
        document = doc;
        int offset = region.getOffset();
        int length = region.getLength();
        try {
            javaParserWorker.format(offset, length, doc.get(0, doc.getLength()), this);
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
    }

    @Override
    public void onApplyFormat(TextEdit edit) {
        try {
            edit.apply(document);
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
    }
}
