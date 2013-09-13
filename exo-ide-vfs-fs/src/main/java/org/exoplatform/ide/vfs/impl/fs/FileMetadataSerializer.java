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
package org.exoplatform.ide.vfs.impl.fs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class FileMetadataSerializer implements DataSerializer<Map<String, String[]>> {
    @Override
    public void write(DataOutput output, Map<String, String[]> props) throws IOException {
        output.writeInt(props.size());
        for (Map.Entry<String, String[]> entry : props.entrySet()) {
            String[] values = entry.getValue();
            if (values != null) {
                output.writeUTF(entry.getKey());
                output.writeInt(values.length);
                for (String v : values) {
                    output.writeUTF(v);
                }
            }
        }
    }

    @Override
    public Map<String, String[]> read(DataInput input) throws IOException {
        final int recordsNum = input.readInt();
        final Map<String, String[]> props = new HashMap<String, String[]>(recordsNum);
        int readRecords = 0;
        while (readRecords < recordsNum) {
            String name = input.readUTF();
            String[] values = new String[input.readInt()];
            for (int i = 0; i < values.length; i++) {
                values[i] = input.readUTF();
            }
            props.put(name, values);
            ++readRecords;
        }
        return props;
    }
}
