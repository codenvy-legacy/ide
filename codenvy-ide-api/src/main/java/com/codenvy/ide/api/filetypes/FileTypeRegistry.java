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
package com.codenvy.ide.api.filetypes;

import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.collections.Array;

/**
 * Registry allows to register new {@link FileType} and get the registered one.
 *
 * @author Artem Zatsarynnyy
 */
public interface FileTypeRegistry {
    /**
     * Register the specified file type.
     *
     * @param fileType
     *         file type to register
     */
    void registerFileType(FileType fileType);

    /**
     * Returns the {@link Array} of all registered file types.
     *
     * @return {@link Array} of all registered file types
     */
    Array<FileType> getRegisteredFileTypes();

    /**
     * Returns the file type of the specified file.
     *
     * @param file
     *         file for which type need to find
     * @return file type or default file type if no file type found
     */
    FileType getFileTypeByFile(File file);

    /**
     * Returns the file type for the specified file extension.
     *
     * @param extension
     *         extension for which file type need to find
     * @return file type or default file type if no file type found
     */
    FileType getFileTypeByExtension(String extension);

    /**
     * Returns the file type for the specified MIME-type.
     *
     * @param mimeType
     *         MIME-type for which file type need to find
     * @return file type or default file type if no file type found
     */
    FileType getFileTypeByMimeType(String mimeType);

    /**
     * Returns the file type which pattern matches the specified file name.
     *
     * @param name
     *         file name
     * @return file type or default file type if no file type's name pattern matches the given file name
     */
    FileType getFileTypeByNamePattern(String name);
}
