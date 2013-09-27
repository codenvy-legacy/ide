/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.client.application;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.module.EditorCreator;
import org.exoplatform.ide.client.framework.module.EditorNotFoundException;
import org.exoplatform.ide.client.framework.module.FileType;
import org.exoplatform.ide.client.framework.module.FileTypeRegistry;
import org.exoplatform.ide.client.framework.util.ImageUtil;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.codemirror.CodeMirror;

import java.util.HashMap;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class IDEFileTypeRegistry implements FileTypeRegistry {

    /**
     *
     */
    private HashMap<String, FileType> fileTypes = new HashMap<String, FileType>();

    /**
     *
     */
    private HashMap<String, EditorCreator[]> editors = new HashMap<String, EditorCreator[]>();

    public IDEFileTypeRegistry() {
        addFileType(new FileType(MimeType.TEXT_PLAIN, "txt", IDEImageBundle.INSTANCE.textFile()),
                    new EditorCreator() {
                        @Override
                        public Editor createEditor() {
                            return new CodeMirror(MimeType.TEXT_PLAIN);
                        }
                    });
    }

    /**
     * @see org.exoplatform.ide.client.framework.module.FileTypeRegistry#addFileType(org.exoplatform.ide.client.framework.module.FileType,
     *      org.exoplatform.ide.client.framework.module.EditorCreator[])
     */
    @Override
    public void addFileType(FileType fileType, EditorCreator... editors) {
        fileTypes.put(fileType.getMimeType(), fileType);
        this.editors.put(fileType.getMimeType(), editors);

        ImageUtil.putIcon(fileType.getMimeType(), fileType.getIcon());
    }

    /** @see org.exoplatform.ide.client.framework.module.FileTypeRegistry#getFileType(java.lang.String) */
    @Override
    public FileType getFileType(String mimeType) {
        return fileTypes.get(mimeType);
    }

    /** @see org.exoplatform.ide.client.framework.module.FileTypeRegistry#getEditors(java.lang.String) */
    @Override
    public Editor[] getEditors(String mimeType) throws EditorNotFoundException {
        EditorCreator[] creatorList = editors.containsKey(mimeType) ? editors.get(mimeType) :
                                      editors.get(MimeType.TEXT_PLAIN);

        Editor[] editors = new Editor[creatorList.length];
        for (int i = 0; i < creatorList.length; i++) {
            EditorCreator creator = creatorList[i];
            editors[i] = creator.createEditor();
        }

        return editors;
    }

    /** @see org.exoplatform.ide.client.framework.module.FileTypeRegistry#getEditor(java.lang.String) */
    @Override
    public Editor getEditor(String mimeType) throws EditorNotFoundException {
        if (!editors.containsKey(mimeType)) {
            throw new EditorNotFoundException("Editor for " + mimeType + " not found");
        }

        EditorCreator[] creatorList = editors.get(mimeType);
        if (creatorList.length == 0) {
            throw new EditorNotFoundException("Editor for " + mimeType + " not found");
        }

        return creatorList[0].createEditor();
    }

    @Override
    public FileType[] getSupportedFileTypes() {
        return fileTypes.values().toArray(new FileType[fileTypes.size()]);
    }

}
