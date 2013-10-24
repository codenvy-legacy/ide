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
 * Serializer for AccessControlList.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
public class AccessControlListSerializer implements DataSerializer<AccessControlList> {
    @Override
    public void write(DataOutput output, AccessControlList value) throws IOException {
        value.write(output);
    }

    @Override
    public AccessControlList read(DataInput input) throws IOException {
        return AccessControlList.read(input);
    }
}
