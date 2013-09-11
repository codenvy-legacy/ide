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
package org.exoplatform.ide.codeassistant.storage;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/** Test read and write operations from ExternalizationTool */
public class TestExternalizationTools {
    @Test
    public void testName() throws Exception {

    }

    @Test
    public void shouldSerializeCyrillicString() throws IOException {
        shouldSerializeString("Кириллическая строка");
    }

    @Test
    public void shouldSerializeLatinString() throws IOException {
        shouldSerializeString("Latin String");
    }

    private void shouldSerializeString(String string) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);

        ExternalizationTools.writeStringUTF(string, oos);
        oos.flush();

        ObjectInputStream in = ExternalizationTools.createObjectInputStream(out.toByteArray());
        String deserializedString = readString(in);

        assertEquals(string, deserializedString);
    }

    @Test
    public void shouldSerializeStringArray() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        String[] strings = new String[]{"one", "two", "tree"};

        ExternalizationTools.writeStringUTFList(Arrays.asList(strings), oos);
        oos.flush();

        ObjectInputStream in = ExternalizationTools.createObjectInputStream(out.toByteArray());
        int arrayLength = in.readInt();
        String[] deserializedArray = new String[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            deserializedArray[i] = readString(in);
        }

        assertArrayEquals(strings, deserializedArray);
    }

    @Test
    public void shouldNotInvokeWriteStringObjectOnStringWritting() throws IOException {
        ObjectOutput out = mock(ObjectOutput.class);

        ExternalizationTools.writeStringUTF("String", out);

        verify(out, atLeastOnce()).writeInt(anyInt());
        verify(out, atLeastOnce()).write((byte[])any());
        verify(out, never()).writeObject(anyString());
    }

    @Test
    public void shouldNotInvokeWriteStringObjectOnStringArrayWritting() throws IOException {
        ObjectOutput out = mock(ObjectOutput.class);

        ExternalizationTools.writeStringUTFList(Arrays.asList(new String[]{"one", "two", "three"}), out);

        verify(out, atLeastOnce()).writeInt(anyInt());
        verify(out, atLeastOnce()).write((byte[])any());
        verify(out, never()).writeObject(anyString());
    }

    @Test
    public void shouldDeserializeCyrillicString() throws IOException {
        shouldDeserializeString("Кириллическая строка");
    }

    @Test
    public void shouldDeserializeLatinString() throws IOException {
        shouldDeserializeString("Latin string");
    }

    private void shouldDeserializeString(String serializedString) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);

        byte[] bytes = serializedString.getBytes("UTF-8");
        oos.writeInt(bytes.length);
        oos.write(bytes);
        oos.flush();

        ObjectInputStream io = ExternalizationTools.createObjectInputStream(out.toByteArray());

        String deserializedString = ExternalizationTools.readStringUTF(io);

        assertEquals(serializedString, deserializedString);
    }

    @Test
    public void shouldDeserializeStringArray() throws IOException {
        String[] serializedArray = new String[]{"one", "two", "three"};

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);

        oos.writeInt(serializedArray.length);

        for (String element : serializedArray) {
            oos.writeInt(element.length());
            oos.write(element.getBytes("UTF-8"));
        }
        oos.flush();

        ObjectInputStream io = ExternalizationTools.createObjectInputStream(out.toByteArray());
        List<String> deserializedArray = ExternalizationTools.readStringUTFList(io);

        assertArrayEquals(serializedArray, deserializedArray.toArray());
    }

    @Test
    public void shouldNotInvokeReadObjectOnStringReading() throws IOException, ClassNotFoundException {
        ObjectInput in = mock(ObjectInput.class, Mockito.RETURNS_SMART_NULLS);
        when(in.readInt()).thenReturn(1);

        ExternalizationTools.readStringUTF(in);

        verify(in, times(1)).readInt();
        verify(in, times(1)).readFully((byte[])any());
        verify(in, never()).readObject();
    }

    @Test
    public void shouldNotInvokeReadObjectOnStringArrayReading() throws IOException, ClassNotFoundException {
        ObjectInput in = mock(ObjectInput.class, Mockito.RETURNS_SMART_NULLS);
        when(in.readInt()).thenReturn(1);

        ExternalizationTools.readStringUTFList(in);

        verify(in, atLeastOnce()).readInt();
        verify(in, atLeastOnce()).readFully((byte[])any());
        verify(in, never()).readObject();
    }

    private String readString(ObjectInputStream in) throws IOException, UnsupportedEncodingException {
        int stringLength = in.readInt();
        byte[] stringBytes = new byte[stringLength];
        in.read(stringBytes);
        String deserializedString = new String(stringBytes, "UTF-8");
        return deserializedString;
    }

}
