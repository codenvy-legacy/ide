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
package com.codenvy.vfs.impl.fs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Writes object to stream and restores object from stream. Implementation has full control over format of serialization.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
public interface DataSerializer<T> {
    /**
     * Writes <code>value</code> to <code>output</code>.
     *
     * @param output
     *         serialization stream
     * @param value
     *         instance for serialization
     * @throws IOException
     *         if an i/o error occurs
     */
    void write(DataOutput output, T value) throws IOException;

    /**
     * Restores object from <code>input</code>.
     *
     * @param input
     *         stream which contains serialized object
     * @return restored instance
     * @throws IOException
     *         if an i/o error occurs
     */
    T read(DataInput input) throws IOException;
}
