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

import org.exoplatform.ide.codeassistant.jvm.bean.*;
import org.exoplatform.ide.codeassistant.jvm.shared.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class contains set of operations for efficient object Externalization.
 * For example, String objects will be saved as array of bytes in UTF-8 format.
 * For serialization and deserialization of objects where it's possible will be
 * used they own Externalization mechanism.
 */
public class ExternalizationTools {

    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * Creates ObjectInputStream from array of bytes. Use this method for
     * initialization of deserialization.
     *
     * @param data
     *         - serialized object in array of bytes representation
     * @return - ObjectInputStream with received sequence of bytes
     * @throws IOException
     */
    public static ObjectInputStream createObjectInputStream(byte[] data) throws IOException {
        ByteArrayInputStream io = new ByteArrayInputStream(data);
        return new ObjectInputStream(io);
    }

    /**
     * Read list of serialized FieldInfo from ObjectInput. First int in the
     * stream expected as a length of the List.
     *
     * @param in
     *         - input stream with serialized FieldInfo objects
     * @return - List of deserialized FieldInfo objects
     * @throws IOException
     * @see ExternalizationTools#readStringUTF(ObjectInput)
     */
    public static List<FieldInfo> readFields(ObjectInput in) throws IOException {
        int size = in.readInt();
        List<FieldInfo> result = null;
        if (size == 0) {
            result = Collections.emptyList();
        } else {
            result = new ArrayList<FieldInfo>(size);
            for (int i = 0; i < size; i++) {
                FieldInfo field = new FieldInfoBean();
                // Member
                field.setModifiers(in.readInt());
                field.setName(readStringUTF(in));
                //Field
                field.setType(readStringUTF(in));
                field.setDeclaringClass(readStringUTF(in));
                field.setDescriptor(readStringUTF(in));
                field.setSignature(readStringUTF(in));
                field.setValue(readStringUTF(in));
                result.add(field);
            }
        }
        return result;
    }

    /**
     * Read list of serialized MethodInfo from ObjectInput. First int in the
     * stream expected as a length of the List.
     *
     * @param in
     *         - input stream with serialized objects
     * @return - List of deserialized MethodInfo objects
     * @throws IOException
     * @see ExternalizationTools#readStringUTF(ObjectInput)
     */
    public static List<MethodInfo> readMethods(ObjectInput in) throws IOException {
        int size = in.readInt();
        List<MethodInfo> result = null;
        if (size == 0) {
            result = Collections.emptyList();
        } else {
            result = new ArrayList<MethodInfo>(size);
            for (int i = 0; i < size; i++) {
                MethodInfo method = new MethodInfoBean();
                // Member
                method.setModifiers(in.readInt());
                method.setName(readStringUTF(in));

                //Field
                method.setDeclaringClass(readStringUTF(in));
                method.setExceptionTypes(readStringUTFList(in));
                method.setParameterTypes(readStringUTFList(in));
                method.setParameterNames(readStringUTFList(in));
                method.setReturnType(readStringUTF(in));
                method.setConstructor(in.readBoolean());
                method.setDescriptor(readStringUTF(in));
                method.setSignature(readStringUTF(in));
                method.setAnnotationDefault(readAnnotationValue(in));
                result.add(method);

            }
        }
        return result;
    }

    /**
     * @param in
     * @return
     * @throws IOException
     */
    public static AnnotationValue readAnnotationValue(ObjectInput in) throws IOException {
        if (in.readBoolean()) {
            AnnotationValue ann = new AnnotationValueBean();
            ann.setPrimitiveType(readArrayStringUTF(in));
            ann.setArrayType(readArrayStringUTF(in));
            ann.setClassSignature(readStringUTF(in));
            ann.setEnumConstant(readArrayStringUTF(in));
            ann.setAnnotation(readAnnotation(in));
            ann.setAnnotations(readAnnotations(in));
            return ann;
        }
        return null;
    }

    /**
     * @param in
     * @return
     * @throws IOException
     */
    public static Annotation[] readAnnotations(ObjectInput in) throws IOException {
        if (in.readBoolean()) {
            int length = in.readInt();
            Annotation[] ann = new Annotation[length];
            for (int i = 0; i < length; i++) {
                ann[i] = readAnnotation(in);
            }
            return ann;
        }
        return null;
    }

    /**
     * @param in
     * @return
     * @throws IOException
     */
    public static Annotation readAnnotation(ObjectInput in) throws IOException {
        if (in.readBoolean()) {
            Annotation ann = new AnnotationBean();
            ann.setTypeName(readStringUTF(in));
            ann.setAnnotationParameters(readAnnotationParameters(in));
            return ann;
        }
        return null;
    }

    /**
     * @param in
     * @return
     * @throws IOException
     */
    public static AnnotationParameter[] readAnnotationParameters(ObjectInput in) throws IOException {
        int len = in.readInt();
        if (len == 0)
            return null;
        AnnotationParameter[] parameters = new AnnotationParameter[len];
        for (int i = 0; i < len; i++) {
            AnnotationParameter p = new AnnotationParamerBean();
            p.setName(readStringUTF(in));
            p.setValue(readAnnotationValue(in));
            parameters[i] = p;
        }
        return parameters;
    }

    /**
     * @param in
     * @return
     * @throws IOException
     */
    public static String[] readArrayStringUTF(ObjectInput in) throws IOException {
        int leng = in.readInt();
        if (leng == 0)
            return null;
        String[] str = new String[leng];
        for (int i = 0; i < leng; i++) {
            str[i] = readStringUTF(in);
        }
        return str;
    }

    /**
     * Read UTF-8 string from ObjectInput. Reading have to be made in the same
     * order as writing. At the first time - length of the string, then array of
     * bytes (content of the string). The most important to keep the same
     * encoding during serialization and deserialization.
     *
     * @param in
     *         - input stream with serialized String object
     * @return - deserealized String object
     * @throws IOException
     */
    public static String readStringUTF(ObjectInput in) throws IOException {
        int length = in.readInt();
        String result = null;
        if (length == 0) {
            result = "";
        } else {
            byte[] bytes = new byte[length];
            in.readFully(bytes);

            result = new String(bytes, DEFAULT_ENCODING);
        }
        return result;
    }

    /**
     * Read list of strings in UTF encoding from ObjectInput
     * <p/>
     * First int in the stream expected as a length of the array, then string
     * will be read one by one with help if readStringUTF method
     *
     * @param in
     *         - input stream with serialized array of String objects
     * @return - List of deserialized strings
     * @throws IOException
     * @see ExternalizationTools#readStringUTF(ObjectInput)
     */
    public static List<String> readStringUTFList(ObjectInput in) throws IOException {
        int size = in.readInt();
        List<String> result = null;
        if (size == 0) {
            result = Collections.emptyList();
        } else {
            result = new ArrayList<String>(size);

            for (int i = 0; i < size; i++) {
                result.add(readStringUTF(in));
            }

        }
        return result;
    }

    /**
     * Write list of Member to the ObjectOutput.
     * <p/>
     * At first the length of the list will be written to the output.
     *
     * @param list
     *         - list of Member objects for serialization
     * @param out
     *         - output stream for objects content writing.
     * @throws IOException
     * @see ExternalizationTools#writeStringUTF(String, ObjectOutput)
     */
    public static void writeObjectList(List<? extends Member> list, ObjectOutput out) throws IOException {
        if (list == null) {
            out.writeInt(0);
        } else {
            out.writeInt(list.size());

            for (Member element : list) {

                // Member
                out.writeInt(element.getModifiers());
                writeStringUTF(element.getName(), out);

                if (element instanceof FieldInfo) {
                    //FieldInfo
                    writeStringUTF(((FieldInfo)element).getType(), out);
                    writeStringUTF(((FieldInfo)element).getDeclaringClass(), out);
                    writeStringUTF(((FieldInfo)element).getDescriptor(), out);
                    writeStringUTF(((FieldInfo)element).getSignature(), out);
                    writeStringUTF(((FieldInfo)element).getValue(), out);

                } else if (element instanceof MethodInfo) {
                    //MethodInfe
                    writeStringUTF(((MethodInfo)element).getDeclaringClass(), out);
                    writeStringUTFList(((MethodInfo)element).getExceptionTypes(), out);
                    writeStringUTFList(((MethodInfo)element).getParameterTypes(), out);
                    writeStringUTFList(((MethodInfo)element).getParameterNames(), out);
                    writeStringUTF(((MethodInfo)element).getReturnType(), out);
                    out.writeBoolean(((MethodInfo)element).isConstructor());
                    writeStringUTF(((MethodInfo)element).getDescriptor(), out);
                    writeStringUTF(((MethodInfo)element).getSignature(), out);
                    writeAnnotationValue(((MethodInfo)element).getAnnotationDefault(), out);
                }

            }
        }
    }

    /**
     * @param annotationDefault
     * @param out
     * @throws IOException
     */
    public static void writeAnnotationValue(AnnotationValue annotationDefault, ObjectOutput out) throws IOException {
        if (annotationDefault == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            writeArrayStringUTF(annotationDefault.getPrimitiveType(), out);
            writeArrayStringUTF(annotationDefault.getArrayType(), out);
            writeStringUTF(annotationDefault.getClassSignature(), out);
            writeArrayStringUTF(annotationDefault.getEnumConstant(), out);
            writeAnnotation(annotationDefault.getAnnotation(), out);
            writeAnnotaions(annotationDefault.getAnnotations(), out);
        }
    }

    /**
     * @param annotations
     * @param out
     * @throws IOException
     */
    public static void writeAnnotaions(Annotation[] annotations, ObjectOutput out) throws IOException {
        if (annotations == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeInt(annotations.length);
            for (Annotation a : annotations) {
                writeAnnotation(a, out);
            }
        }
    }

    /**
     * @param annotation
     * @param out
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public static void writeAnnotation(Annotation annotation, ObjectOutput out) throws UnsupportedEncodingException,
                                                                                       IOException {
        if (annotation == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            writeStringUTF(annotation.getTypeName(), out);
            writeAnnotationParameters(annotation.getAnnotationParameters(), out);
        }
    }

    /**
     * @param annotationParameters
     * @param out
     * @throws IOException
     */
    public static void writeAnnotationParameters(AnnotationParameter[] annotationParameters, ObjectOutput out)
            throws IOException {
        if (annotationParameters == null) {
            out.writeInt(0);
        } else {
            out.writeInt(annotationParameters.length);
            for (AnnotationParameter p : annotationParameters) {
                writeStringUTF(p.getName(), out);
                writeAnnotationValue(p.getValue(), out);
            }
        }
    }

    /**
     * Write array if UTF string to ObjectOutput. At first - length of the array, than call
     * {@link #writeStringUTF(String, ObjectOutput)} for each string.
     *
     * @param strings
     * @param out
     * @throws IOException
     */
    public static void writeArrayStringUTF(String[] strings, ObjectOutput out) throws IOException {
        if (strings == null) {
            out.writeInt(0);
        } else {
            out.writeInt(strings.length);
            for (String s : strings)
                writeStringUTF(s, out);
        }
    }

    /**
     * Write UTF string to ObjectOutput. At first - length of the string, then
     * array of bytes (content of the string) will be written to output. The most
     * important to keep the same encoding during serialization and
     * deserialization.
     *
     * @param string
     *         - object for serialization
     * @param out
     *         - output stream for objects content writing.
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static void writeStringUTF(String string, ObjectOutput out) throws UnsupportedEncodingException, IOException {
        if (string == null) {
            out.writeInt(0);
        } else {
            byte[] bytes = string.getBytes(DEFAULT_ENCODING);
            out.writeInt(bytes.length);
            out.write(bytes);
        }

    }

    /**
     * Write list of UTF strings to object output.
     *
     * @param list
     *         - list of strings for serialization
     * @param out
     *         - output stream for objects content writing.
     * @throws IOException
     */
    public static void writeStringUTFList(List<String> list, ObjectOutput out) throws IOException {

        if (list == null) {
            out.writeInt(0);
        } else {
            out.writeInt(list.size());

            for (String element : list) {
                writeStringUTF(element, out);
            }

        }
    }

    /**
     * Read TypeInfo from serialized state.
     *
     * @param content
     *         - serialized TypeInfo
     * @return - deserialized TypeInfo object.
     * @throws IOException
     */
    public static TypeInfo readExternal(InputStream content) throws IOException {
        TypeInfoBean result = new TypeInfoBean();
        ObjectInputStream in = new ObjectInputStream(content);
        // Member
        result.setModifiers(in.readInt());
        result.setName(readStringUTF(in));
        // ShortType
        result.setType(readStringUTF(in));
        result.setSignature(readStringUTF(in));
        // TypeInfo
        result.setSuperClass(readStringUTF(in));
        result.setInterfaces(readStringUTFList(in));
        result.setFields(readFields(in));
        result.setMethods(readMethods(in));
        result.setNestedTypes(readNestedTypes(in));
        return result;
    }

    /**
     * @param in
     * @return
     * @throws IOException
     */
    public static List<Member> readNestedTypes(ObjectInputStream in) throws IOException {
        int size = in.readInt();
        List<Member> result = null;
        if (size == 0) {
            result = Collections.emptyList();
        } else {
            result = new ArrayList<Member>(size);
            for (int i = 0; i < size; i++) {
                Member member = new MemberBean();
                member.setModifiers(in.readInt());
                member.setName(readStringUTF(in));
                result.add(member);
            }
        }
        return result;
    }

    /**
     * Serialize TypeInfo to the array of bytes.
     *
     * @param typeInfo
     *         - initial object
     * @return - serialized object
     * @throws IOException
     */
    public static byte[] externalize(TypeInfo typeInfo) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        // Member
        out.writeInt(typeInfo.getModifiers());
        writeStringUTF(typeInfo.getName(), out);
        // ShortType
        writeStringUTF(typeInfo.getType(), out);
        writeStringUTF(typeInfo.getSignature(), out);
        // TypeInfo
        writeStringUTF(typeInfo.getSuperClass(), out);
        writeStringUTFList(typeInfo.getInterfaces(), out);
        writeObjectList(typeInfo.getFields(), out);
        writeObjectList(typeInfo.getMethods(), out);
        writeObjectList(typeInfo.getNestedTypes(), out);
        out.close();
        return bos.toByteArray();
    }

    /** Protect utill class from creation. */
    private ExternalizationTools() {
    }
}
