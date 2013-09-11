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


import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.Type;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class SignatureCreator {

    public static final String ObjectSignature = "Ljava/lang/Object;"; //$NON-NLS-1$

    public static final int BASE_TYPE_SIGNATURE = 2;

    private static final char[] BOOLEAN = "boolean".toCharArray(); //$NON-NLS-1$

    private static final char[] BYTE = "byte".toCharArray(); //$NON-NLS-1$

    /** Character constant indicating an array type in a signature. Value is <code>'['</code>. */
    public static final char C_ARRAY = '[';

    /** Character constant indicating the primitive type boolean in a signature. Value is <code>'Z'</code>. */
    public static final char C_BOOLEAN = 'Z';

    /** Character constant indicating the primitive type byte in a signature. Value is <code>'B'</code>. */
    public static final char C_BYTE = 'B';

    /**
     * Character constant indicating a capture of a wildcard type in a signature. Value is <code>'!'</code>.
     *
     * @since 3.1
     */
    public static final char C_CAPTURE = '!';

    /** Character constant indicating the primitive type char in a signature. Value is <code>'C'</code>. */
    public static final char C_CHAR = 'C';

    /**
     * Character constant indicating the colon in a signature. Value is <code>':'</code>.
     *
     * @since 3.0
     */
    public static final char C_COLON = ':';

    /** Character constant indicating the dollar in a signature. Value is <code>'$'</code>. */
    public static final char C_DOLLAR = '$';

    /** Character constant indicating the dot in a signature. Value is <code>'.'</code>. */
    public static final char C_DOT = '.';

    /** Character constant indicating the primitive type double in a signature. Value is <code>'D'</code>. */
    public static final char C_DOUBLE = 'D';

    /**
     * Character constant indicating an exception in a signature. Value is <code>'^'</code>.
     *
     * @since 3.1
     */
    public static final char C_EXCEPTION_START = '^';

    /**
     * Character constant indicating a bound wildcard type argument in a signature with extends clause. Value is <code>'+'</code>.
     *
     * @since 3.1
     */
    public static final char C_EXTENDS = '+';

    /** Character constant indicating the primitive type float in a signature. Value is <code>'F'</code>. */
    public static final char C_FLOAT = 'F';

    /**
     * Character constant indicating the end of a generic type list in a signature. Value is <code>'&gt;'</code>.
     *
     * @since 3.0
     */
    public static final char C_GENERIC_END = '>';

    /**
     * Character constant indicating the start of a formal type parameter (or type argument) list in a signature. Value is
     * <code>'&lt;'</code>.
     *
     * @since 3.0
     */
    public static final char C_GENERIC_START = '<';

    /** Character constant indicating the primitive type int in a signature. Value is <code>'I'</code>. */
    public static final char C_INT = 'I';

    /**
     * Character constant indicating an intersection type in a signature. Value is <code>'|'</code>.
     *
     * @since 3.7.1
     */
    public static final char C_INTERSECTION = '|';

    /** Character constant indicating the primitive type long in a signature. Value is <code>'J'</code>. */
    public static final char C_LONG = 'J';

    /** Character constant indicating the end of a named type in a signature. Value is <code>';'</code>. */
    public static final char C_NAME_END = ';';

    /** Character constant indicating the end of a parameter type list in a signature. Value is <code>')'</code>. */
    public static final char C_PARAM_END = ')';

    /** Character constant indicating the start of a parameter type list in a signature. Value is <code>'('</code>. */
    public static final char C_PARAM_START = '(';

    /** Character constant indicating the start of a resolved, named type in a signature. Value is <code>'L'</code>. */
    public static final char C_RESOLVED = 'L';

    /** Character constant indicating the semicolon in a signature. Value is <code>';'</code>. */
    public static final char C_SEMICOLON = ';';

    /** Character constant indicating the primitive type short in a signature. Value is <code>'S'</code>. */
    public static final char C_SHORT = 'S';

    /**
     * Character constant indicating an unbound wildcard type argument in a signature. Value is <code>'*'</code>.
     *
     * @since 3.0
     */
    public static final char C_STAR = '*';

    /**
     * Character constant indicating a bound wildcard type argument in a signature with super clause. Value is <code>'-'</code>.
     *
     * @since 3.1
     */
    public static final char C_SUPER = '-';

    /**
     * Character constant indicating the start of a resolved type variable in a signature. Value is <code>'T'</code>.
     *
     * @since 3.0
     */
    public static final char C_TYPE_VARIABLE = 'T';

    /** Character constant indicating the start of an unresolved, named type in a signature. Value is <code>'Q'</code>. */
    public static final char C_UNRESOLVED = 'Q';

    /** Character constant indicating result type void in a signature. Value is <code>'V'</code>. */
    public static final char C_VOID = 'V';

    private static final char[] CAPTURE = "capture-of".toCharArray(); //$NON-NLS-1$

    /**
     * Kind constant for the capture of a wildcard type signature.
     *
     * @see #getTypeSignatureKind(String)
     * @since 3.1
     */
    public static final int CAPTURE_TYPE_SIGNATURE = 6;

    private static final char[] CHAR = "char".toCharArray(); //$NON-NLS-1$

    /**
     * Kind constant for a class type signature.
     *
     * @see #getTypeSignatureKind(String)
     * @since 3.0
     */
    public static final int CLASS_TYPE_SIGNATURE = 1;

    private static final char[] DOUBLE = "double".toCharArray(); //$NON-NLS-1$

    private static final char[] EXTENDS = "extends".toCharArray(); //$NON-NLS-1$

    private static final char[] FLOAT = "float".toCharArray(); //$NON-NLS-1$

    private static final char[] INT = "int".toCharArray(); //$NON-NLS-1$

    /**
     * Kind constant for the intersection type signature.
     *
     * @see #getTypeSignatureKind(String)
     * @since 3.7.1
     */
    public static final int INTERSECTION_TYPE_SIGNATURE = 7;

    private static final char[] LONG = "long".toCharArray(); //$NON-NLS-1$

    private static final char[] SHORT = "short".toCharArray(); //$NON-NLS-1$

    private static final char[] VOID = "void".toCharArray(); //$NON-NLS-1$

    private static final char[] SUPER = "super".toCharArray(); //$NON-NLS-1$

    public static String createMethodSignature(JavaMethod method) {
        Type[] parameters = method.getParameterTypes(true);
        String[] signatures = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getValue().length() == 1)
                signatures[i] = ObjectSignature;
            else
                signatures[i] = createTypeSignature(parameters[i].getValue(), true);

        }
        String genericValue;
        if (method.isConstructor()) {
            genericValue = Type.VOID.getValue();
        } else {
//         method.getReturnType().getValue());
            genericValue = method.getReturnType().getValue().length() == 1 ? "java.lang.Object" : method.getReturnType().getValue();
        }

        return createMethodSignature(signatures, createTypeSignature(genericValue, true)).replace('.', '/');
    }

    public static String createTypeSignature(JavaField field) {
        return createTypeSignature(field.getType().getGenericValue(), true);
    }


    /**
     * Creates a new type parameter signature with the given name and bounds.
     *
     * @param typeParameterName
     *         the type parameter name
     * @param boundSignatures
     *         the signatures of associated bounds or empty array if none
     * @return the encoded type parameter signature
     * @since 3.1
     */
    public static String createTypeParameterSignature(String typeParameterName, String[] boundSignatures) {
        int length = boundSignatures.length;
        char[][] boundSignatureChars = new char[length][];
        for (int i = 0; i < length; i++) {
            boundSignatureChars[i] = boundSignatures[i].toCharArray();
        }
        return new String(createTypeParameterSignature(typeParameterName.toCharArray(), boundSignatureChars));
    }

    /**
     * Creates a new type parameter signature with the given name and bounds.
     *
     * @param typeParameterName
     *         the type parameter name
     * @param boundSignatures
     *         the signatures of associated bounds or empty array if none
     * @return the encoded type parameter signature
     * @since 3.1
     */
    public static char[] createTypeParameterSignature(char[] typeParameterName, char[][] boundSignatures) {
        int length = boundSignatures.length;
        if (length == 0) {
            return append(typeParameterName, C_COLON); // param signature with no bounds still gets trailing colon
        }
        int boundsSize = 0;
        for (int i = 0; i < length; i++) {
            boundsSize += boundSignatures[i].length + 1;
        }
        int nameLength = typeParameterName.length;
        char[] result = new char[nameLength + boundsSize];
        System.arraycopy(typeParameterName, 0, result, 0, nameLength);
        int index = nameLength;
        for (int i = 0; i < length; i++) {
            result[index++] = C_COLON;
            int boundLength = boundSignatures[i].length;
            System.arraycopy(boundSignatures[i], 0, result, index, boundLength);
            index += boundLength;
        }
        return result;
    }

    /**
     * Creates a new type signature from the given type name. If the type name is qualified, then it is expected to be dot-based.
     * The type name may contain primitive types or array types. However, parameterized types are not supported.
     * <p>
     * For example:
     * <p/>
     * <pre>
     * <code>
     * createTypeSignature("int", hucairz) -> "I"
     * createTypeSignature("java.lang.String", true) -> "Ljava.lang.String;"
     * createTypeSignature("String", false) -> "QString;"
     * createTypeSignature("java.lang.String", false) -> "Qjava.lang.String;"
     * createTypeSignature("int []", false) -> "[I"
     * </code>
     * </pre>
     * <p/>
     * </p>
     *
     * @param typeName
     *         the possibly qualified type name
     * @param isResolved
     *         <code>true</code> if the type name is to be considered resolved (for example, a type name from a binary
     *         class file), and <code>false</code> if the type name is to be considered unresolved (for example, a type name
     *         found in source code)
     * @return the encoded type signature
     */
    public static String createTypeSignature(String typeName, boolean isResolved) {
        return createTypeSignature(typeName == null ? null : typeName.toCharArray(), isResolved);
    }

    /**
     * Creates a new type signature from the given type name encoded as a character array. The type name may contain primitive
     * types, array types or parameterized types. This method is equivalent to
     * <code>createTypeSignature(new String(typeName),isResolved)</code>, although more efficient for callers with character arrays
     * rather than strings. If the type name is qualified, then it is expected to be dot-based.
     *
     * @param typeName
     *         the possibly qualified type name
     * @param isResolved
     *         <code>true</code> if the type name is to be considered resolved (for example, a type name from a binary
     *         class file), and <code>false</code> if the type name is to be considered unresolved (for example, a type name
     *         found in source code)
     * @return the encoded type signature
     * @see #createTypeSignature(java.lang.String, boolean)
     */
    public static String createTypeSignature(char[] typeName, boolean isResolved) {
        return new String(createCharArrayTypeSignature(typeName, isResolved));
    }

    /**
     * Creates a new type signature from the given type name encoded as a character array. The type name may contain primitive
     * types or array types or parameterized types. This method is equivalent to
     * <code>createTypeSignature(new String(typeName),isResolved).toCharArray()</code> , although more efficient for callers with
     * character arrays rather than strings. If the type name is qualified, then it is expected to be dot-based.
     *
     * @param typeName
     *         the possibly qualified type name
     * @param isResolved
     *         <code>true</code> if the type name is to be considered resolved (for example, a type name from a binary
     *         class file), and <code>false</code> if the type name is to be considered unresolved (for example, a type name
     *         found in source code)
     * @return the encoded type signature
     * @see #createTypeSignature(java.lang.String, boolean)
     * @since 2.0
     */
    public static char[] createCharArrayTypeSignature(char[] typeName, boolean isResolved) {
        if (typeName == null)
            throw new IllegalArgumentException("null"); //$NON-NLS-1$
        int length = typeName.length;
        if (length == 0)
            throw new IllegalArgumentException(new String(typeName));
        StringBuffer buffer = new StringBuffer(5);
        int pos = encodeTypeSignature(typeName, 0, isResolved, length, buffer);
        pos = consumeWhitespace(typeName, pos, length);
        if (pos < length)
            throw new IllegalArgumentException(new String(typeName));
        char[] result = new char[length = buffer.length()];
        buffer.getChars(0, length, result, 0);
        return result;
    }

    private static int encodeTypeSignature(char[] typeName, int start, boolean isResolved, int length,
                                           StringBuffer buffer) {
        int pos = start;
        pos = consumeWhitespace(typeName, pos, length);
        if (pos >= length)
            throw new IllegalArgumentException(new String(typeName));
        int checkPos;
        char currentChar = typeName[pos];
        switch (currentChar) {
            // primitive type?
            case 'b':
                checkPos = checkName(BOOLEAN, typeName, pos, length);
                if (checkPos > 0) {
                    pos = encodeArrayDimension(typeName, checkPos, length, buffer);
                    buffer.append(C_BOOLEAN);
                    return pos;
                }
                checkPos = checkName(BYTE, typeName, pos, length);
                if (checkPos > 0) {
                    pos = encodeArrayDimension(typeName, checkPos, length, buffer);
                    buffer.append(C_BYTE);
                    return pos;
                }
                break;
            case 'd':
                checkPos = checkName(DOUBLE, typeName, pos, length);
                if (checkPos > 0) {
                    pos = encodeArrayDimension(typeName, checkPos, length, buffer);
                    buffer.append(C_DOUBLE);
                    return pos;
                }
                break;
            case 'f':
                checkPos = checkName(FLOAT, typeName, pos, length);
                if (checkPos > 0) {
                    pos = encodeArrayDimension(typeName, checkPos, length, buffer);
                    buffer.append(C_FLOAT);
                    return pos;
                }
                break;
            case 'i':
                checkPos = checkName(INT, typeName, pos, length);
                if (checkPos > 0) {
                    pos = encodeArrayDimension(typeName, checkPos, length, buffer);
                    buffer.append(C_INT);
                    return pos;
                }
                break;
            case 'l':
                checkPos = checkName(LONG, typeName, pos, length);
                if (checkPos > 0) {
                    pos = encodeArrayDimension(typeName, checkPos, length, buffer);
                    buffer.append(C_LONG);
                    return pos;
                }
                break;
            case 's':
                checkPos = checkName(SHORT, typeName, pos, length);
                if (checkPos > 0) {
                    pos = encodeArrayDimension(typeName, checkPos, length, buffer);
                    buffer.append(C_SHORT);
                    return pos;
                }
                break;
            case 'v':
                checkPos = checkName(VOID, typeName, pos, length);
                if (checkPos > 0) {
                    pos = encodeArrayDimension(typeName, checkPos, length, buffer);
                    buffer.append(C_VOID);
                    return pos;
                }
                break;
            case 'c':
                checkPos = checkName(CHAR, typeName, pos, length);
                if (checkPos > 0) {
                    pos = encodeArrayDimension(typeName, checkPos, length, buffer);
                    buffer.append(C_CHAR);
                    return pos;
                } else {
                    checkPos = checkName(CAPTURE, typeName, pos, length);
                    if (checkPos > 0) {
                        pos = consumeWhitespace(typeName, checkPos, length);
                        if (typeName[pos] != '?') {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                buffer.append(C_CAPTURE);
                //$FALL-THROUGH$ for wildcard part of capture typecheckPos
            case '?':
                // wildcard
                pos = consumeWhitespace(typeName, pos + 1, length);
                checkPos = checkName(EXTENDS, typeName, pos, length);
                if (checkPos > 0) {
                    buffer.append(C_EXTENDS);
                    pos = encodeTypeSignature(typeName, checkPos, isResolved, length, buffer);
                    return pos;
                }
                checkPos = checkName(SUPER, typeName, pos, length);
                if (checkPos > 0) {
                    buffer.append(C_SUPER);
                    pos = encodeTypeSignature(typeName, checkPos, isResolved, length, buffer);
                    return pos;
                }
                buffer.append(C_STAR);
                return pos;
        }
        // non primitive type
        checkPos = checkArrayDimension(typeName, pos, length);
        int end;
        if (checkPos > 0) {
            end = encodeArrayDimension(typeName, checkPos, length, buffer);
        } else {
            end = -1;
        }
        buffer.append(isResolved ? C_RESOLVED : C_UNRESOLVED);
        while (true) { // loop on qualifiedName[<args>][.qualifiedName[<args>]*
            pos = encodeQualifiedName(typeName, pos, length, buffer);
            checkPos = checkNextChar(typeName, '<', pos, length, true);
            if (checkPos > 0) {
                buffer.append(C_GENERIC_START);
                // Stop gap fix for <>.
                if ((pos = checkNextChar(typeName, '>', checkPos, length, true)) > 0) {
                    buffer.append(C_GENERIC_END);
                } else {
                    pos = encodeTypeSignature(typeName, checkPos, isResolved, length, buffer);
                    while ((checkPos = checkNextChar(typeName, ',', pos, length, true)) > 0) {
                        pos = encodeTypeSignature(typeName, checkPos, isResolved, length, buffer);
                    }
                    pos = checkNextChar(typeName, '>', pos, length, false);
                    buffer.append(C_GENERIC_END);
                }
            }
            checkPos = checkNextChar(typeName, '.', pos, length, true);
            if (checkPos > 0) {
                buffer.append(C_DOT);
                pos = checkPos;
            } else {
                break;
            }
        }
        buffer.append(C_NAME_END);
        if (end > 0)
            pos = end; // skip array dimension which were preprocessed
        return pos;
    }

    private static int consumeWhitespace(char[] typeName, int pos, int length) {
        while (pos < length) {
            char currentChar = typeName[pos];
            if (currentChar != ' ' && !ScannerHelper.isWhitespace(currentChar)) {
                break;
            }
            pos++;
        }
        return pos;
    }

    private static int encodeArrayDimension(char[] typeName, int pos, int length, StringBuffer buffer) {
        int checkPos;
        while (pos < length && (checkPos = checkNextChar(typeName, '[', pos, length, true)) > 0) {
            pos = checkNextChar(typeName, ']', checkPos, length, false);
            buffer.append(C_ARRAY);
        }
        return pos;
    }

    private static int checkArrayDimension(char[] typeName, int pos, int length) {
        int genericBalance = 0;
        while (pos < length) {
            switch (typeName[pos]) {
                case '<':
                    genericBalance++;
                    break;
                case ',':
                    if (genericBalance == 0)
                        return -1;
                    break;
                case '>':
                    if (genericBalance == 0)
                        return -1;
                    genericBalance--;
                    break;
                case '[':
                    if (genericBalance == 0) {
                        return pos;
                    }
            }
            pos++;
        }
        return -1;
    }

    private static int encodeQualifiedName(char[] typeName, int pos, int length, StringBuffer buffer) {
        int count = 0;
        char lastAppendedChar = 0;
        nameLoop:
        while (pos < length) {
            char currentChar = typeName[pos];
            switch (currentChar) {
                case '<':
                case '>':
                case '[':
                case ',':
                    break nameLoop;
                case '.':
                    buffer.append(C_DOT);
                    lastAppendedChar = C_DOT;
                    count++;
                    break;
                default:
                    if (currentChar == ' ' || ScannerHelper.isWhitespace(currentChar)) {
                        if (lastAppendedChar == C_DOT) { // allow spaces after a dot
                            pos = consumeWhitespace(typeName, pos, length) - 1; // will be incremented
                            break;
                        }
                        // allow spaces before a dot
                        int checkPos = checkNextChar(typeName, '.', pos, length, true);
                        if (checkPos > 0) {
                            buffer.append(C_DOT); // process dot immediately to avoid one iteration
                            lastAppendedChar = C_DOT;
                            count++;
                            pos = checkPos;
                            break;
                        }
                        break nameLoop;
                    }
                    buffer.append(currentChar);
                    lastAppendedChar = currentChar;
                    count++;
                    break;
            }
            pos++;
        }
        if (count == 0)
            throw new IllegalArgumentException(new String(typeName));
        return pos;
    }

    private static int checkNextChar(char[] typeName, char expectedChar, int pos, int length, boolean isOptional) {
        pos = consumeWhitespace(typeName, pos, length);
        if (pos < length && typeName[pos] == expectedChar)
            return pos + 1;
        if (!isOptional)
            throw new IllegalArgumentException(new String(typeName));
        return -1;
    }

    private static int checkName(char[] name, char[] typeName, int pos, int length) {
        if (fragmentEquals(name, typeName, pos, true)) {
            pos += name.length;
            if (pos == length)
                return pos;
            char currentChar = typeName[pos];
            switch (currentChar) {
                case ' ':
                case '.':
                case '<':
                case '>':
                case '[':
                case ',':
                    return pos;
                default:
                    if (ScannerHelper.isWhitespace(currentChar))
                        return pos;

            }
        }
        return -1;
    }

    /**
     * If isCaseSensite is true, the equality is case sensitive, otherwise it is case insensitive.
     * <p/>
     * Answers true if the name contains the fragment at the starting index startIndex, otherwise false. <br>
     * <br>
     * For example:
     * <ol>
     * <li>
     * <p/>
     * <pre>
     *    fragment = { 'b', 'c' , 'd' }
     *    name = { 'a', 'b', 'c' , 'd' }
     *    startIndex = 1
     *    isCaseSensitive = true
     *    result => true
     * </pre>
     * <p/>
     * </li>
     * <li>
     * <p/>
     * <pre>
     *    fragment = { 'b', 'c' , 'd' }
     *    name = { 'a', 'b', 'C' , 'd' }
     *    startIndex = 1
     *    isCaseSensitive = true
     *    result => false
     * </pre>
     * <p/>
     * </li>
     * <li>
     * <p/>
     * <pre>
     *    fragment = { 'b', 'c' , 'd' }
     *    name = { 'a', 'b', 'C' , 'd' }
     *    startIndex = 0
     *    isCaseSensitive = false
     *    result => false
     * </pre>
     * <p/>
     * </li>
     * <li>
     * <p/>
     * <pre>
     *    fragment = { 'b', 'c' , 'd' }
     *    name = { 'a', 'b'}
     *    startIndex = 0
     *    isCaseSensitive = true
     *    result => false
     * </pre>
     * <p/>
     * </li>
     * </ol>
     *
     * @param fragment
     *         the fragment to check
     * @param name
     *         the array to check
     * @param startIndex
     *         the starting index
     * @param isCaseSensitive
     *         check whether or not the equality should be case sensitive
     * @return true if the name contains the fragment at the starting index startIndex according to the value of isCaseSensitive,
     *         otherwise false.
     * @throws NullPointerException
     *         if fragment or name is null.
     */
    public static final boolean fragmentEquals(char[] fragment, char[] name, int startIndex, boolean isCaseSensitive) {

        int max = fragment.length;
        if (name.length < max + startIndex)
            return false;
        if (isCaseSensitive) {
            for (int i = max; --i >= 0; )
                // assumes the prefix is not larger than the name
                if (fragment[i] != name[i + startIndex])
                    return false;
            return true;
        }
        for (int i = max; --i >= 0; )
            // assumes the prefix is not larger than the name
            if (ScannerHelper.toLowerCase(fragment[i]) != ScannerHelper.toLowerCase(name[i + startIndex]))
                return false;
        return true;
    }

    /**
     * Creates a method signature from the given parameter and return type signatures. The encoded method signature is dot-based.
     * This method is equivalent to <code>createMethodSignature(parameterTypes, returnType)</code>.
     *
     * @param parameterTypes
     *         the list of parameter type signatures
     * @param returnType
     *         the return type signature
     * @return the encoded method signature
     * @see Signature#createMethodSignature(char[][], char[])
     */
    public static String createMethodSignature(String[] parameterTypes, String returnType) {
        int parameterTypesLenth = parameterTypes.length;
        char[][] parameters = new char[parameterTypesLenth][];
        for (int i = 0; i < parameterTypesLenth; i++) {
            parameters[i] = parameterTypes[i].toCharArray();
        }
        return new String(createMethodSignature(parameters, returnType.toCharArray()));
    }

    /**
     * Creates a method signature from the given parameter and return type signatures. The encoded method signature is dot-based.
     *
     * @param parameterTypes
     *         the list of parameter type signatures
     * @param returnType
     *         the return type signature
     * @return the encoded method signature
     * @since 2.0
     */
    public static char[] createMethodSignature(char[][] parameterTypes, char[] returnType) {
        int parameterTypesLength = parameterTypes.length;
        int parameterLength = 0;
        for (int i = 0; i < parameterTypesLength; i++) {
            parameterLength += parameterTypes[i].length;

        }
        int returnTypeLength = returnType.length;
        char[] result = new char[1 + parameterLength + 1 + returnTypeLength];
        result[0] = C_PARAM_START;
        int index = 1;
        for (int i = 0; i < parameterTypesLength; i++) {
            char[] parameterType = parameterTypes[i];
            int length = parameterType.length;
            System.arraycopy(parameterType, 0, result, index, length);
            index += length;
        }
        result[index] = C_PARAM_END;
        System.arraycopy(returnType, 0, result, index + 1, returnTypeLength);
        return result;
    }

    /**
     * Returns the type signature without any array nesting.
     * <p>
     * For example:
     * <p/>
     * <pre>
     * <code>
     * getElementType("[[I") --> "I".
     * </code>
     * </pre>
     * <p/>
     * </p>
     *
     * @param typeSignature
     *         the type signature
     * @return the type signature without arrays
     * @throws IllegalArgumentException
     *         if the signature is not syntactically correct
     */
    public static String getElementType(String typeSignature) throws IllegalArgumentException {
        char[] signature = typeSignature.toCharArray();
        char[] elementType = getElementType(signature);
        return signature == elementType ? typeSignature : new String(elementType);
    }

    /**
     * Returns the type signature without any array nesting.
     * <p>
     * For example:
     * <p/>
     * <pre>
     * <code>
     * getElementType({'[', '[', 'I'}) --> {'I'}.
     * </code>
     * </pre>
     * <p/>
     * </p>
     *
     * @param typeSignature
     *         the type signature
     * @return the type signature without arrays
     * @throws IllegalArgumentException
     *         if the signature is not syntactically correct
     * @since 2.0
     */
    public static char[] getElementType(char[] typeSignature) throws IllegalArgumentException {
        int count = getArrayCount(typeSignature);
        if (count == 0)
            return typeSignature;
        int length = typeSignature.length;
        char[] result = new char[length - count];
        System.arraycopy(typeSignature, count, result, 0, length - count);
        return result;
    }

    /**
     * Returns the array count (array nesting depth) of the given type signature.
     *
     * @param typeSignature
     *         the type signature
     * @return the array nesting depth, or 0 if not an array
     * @throws IllegalArgumentException
     *         if the signature is not syntactically correct
     * @since 2.0
     */
    public static int getArrayCount(char[] typeSignature) throws IllegalArgumentException {
        try {
            int count = 0;
            while (typeSignature[count] == C_ARRAY) {
                ++count;
            }
            return count;
        } catch (ArrayIndexOutOfBoundsException e) { // signature is syntactically incorrect if last character is C_ARRAY
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns the array count (array nesting depth) of the given type signature.
     *
     * @param typeSignature
     *         the type signature
     * @return the array nesting depth, or 0 if not an array
     * @throws IllegalArgumentException
     *         if the signature is not syntactically correct
     */
    public static int getArrayCount(String typeSignature) throws IllegalArgumentException {
        return getArrayCount(typeSignature.toCharArray());
    }

    /**
     * Answers a new array with appending the suffix character at the end of the array. <br>
     * <br>
     * For example:<br>
     * <ol>
     * <li>
     * <p/>
     * <pre>
     *    array = { 'a', 'b' }
     *    suffix = 'c'
     *    => result = { 'a', 'b' , 'c' }
     * </pre>
     * <p/>
     * </li>
     * <li>
     * <p/>
     * <pre>
     *    array = null
     *    suffix = 'c'
     *    => result = { 'c' }
     * </pre>
     * <p/>
     * </li>
     * </ol>
     *
     * @param array
     *         the array that is concatenated with the suffix character
     * @param suffix
     *         the suffix character
     * @return the new array
     */
    public static final char[] append(char[] array, char suffix) {
        if (array == null)
            return new char[]{suffix};
        int length = array.length;
        System.arraycopy(array, 0, array = new char[length + 1], 0, length);
        array[length] = suffix;
        return array;
    }

    // ---------------------------------------------------------------------//
    public static class ScannerHelper {
        public final static int MAX_OBVIOUS = 128;

        public final static int C_UPPER_LETTER = 0x20;

        public final static int C_LOWER_LETTER = 0x10;

        public final static int C_IDENT_PART = 0x8;

        public final static int C_DIGIT = 0x4;

        public final static int C_IDENT_START = 0x40;

        public final static int C_SPECIAL = 0x80;

        public final static int C_SEPARATOR = 0x2;

        public final static int C_SPACE = 0x1;

        public final static int C_JLS_SPACE = 0x100;

        public final static int[] OBVIOUS_IDENT_CHAR_NATURES = new int[MAX_OBVIOUS];

        static {
            OBVIOUS_IDENT_CHAR_NATURES[0] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[1] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[2] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[3] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[4] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[5] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[6] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[7] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[8] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[14] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[15] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[16] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[17] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[18] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[19] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[20] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[21] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[22] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[23] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[24] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[25] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[26] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[27] = C_IDENT_PART;
            OBVIOUS_IDENT_CHAR_NATURES[127] = C_IDENT_PART;

            for (int i = '0'; i <= '9'; i++)
                OBVIOUS_IDENT_CHAR_NATURES[i] = C_DIGIT | C_IDENT_PART;

            for (int i = 'a'; i <= 'z'; i++)
                OBVIOUS_IDENT_CHAR_NATURES[i] = C_LOWER_LETTER | C_IDENT_PART | C_IDENT_START;
            for (int i = 'A'; i <= 'Z'; i++)
                OBVIOUS_IDENT_CHAR_NATURES[i] = C_UPPER_LETTER | C_IDENT_PART | C_IDENT_START;

            OBVIOUS_IDENT_CHAR_NATURES['_'] = C_SPECIAL | C_IDENT_PART | C_IDENT_START;
            OBVIOUS_IDENT_CHAR_NATURES['$'] = C_SPECIAL | C_IDENT_PART | C_IDENT_START;

            OBVIOUS_IDENT_CHAR_NATURES[9] = C_SPACE | C_JLS_SPACE; // \ u0009: HORIZONTAL TABULATION
            OBVIOUS_IDENT_CHAR_NATURES[10] = C_SPACE | C_JLS_SPACE; // \ u000a: LINE FEED
            OBVIOUS_IDENT_CHAR_NATURES[11] = C_SPACE;
            OBVIOUS_IDENT_CHAR_NATURES[12] = C_SPACE | C_JLS_SPACE; // \ u000c: FORM FEED
            OBVIOUS_IDENT_CHAR_NATURES[13] = C_SPACE | C_JLS_SPACE; // \ u000d: CARRIAGE RETURN
            OBVIOUS_IDENT_CHAR_NATURES[28] = C_SPACE;
            OBVIOUS_IDENT_CHAR_NATURES[29] = C_SPACE;
            OBVIOUS_IDENT_CHAR_NATURES[30] = C_SPACE;
            OBVIOUS_IDENT_CHAR_NATURES[31] = C_SPACE;
            OBVIOUS_IDENT_CHAR_NATURES[32] = C_SPACE | C_JLS_SPACE; // \ u0020: SPACE

            OBVIOUS_IDENT_CHAR_NATURES['.'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES[':'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES[';'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES[','] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['['] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES[']'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['('] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES[')'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['{'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['}'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['+'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['-'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['*'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['/'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['='] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['&'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['|'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['?'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['<'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['>'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['!'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['%'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['^'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['~'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['"'] = C_SEPARATOR;
            OBVIOUS_IDENT_CHAR_NATURES['\''] = C_SEPARATOR;
        }

        /**
         * Include also non JLS whitespaces.
         * <p/>
         * return true if Character.isWhitespace(c) would return true
         */
        public static boolean isWhitespace(char c) {
            if (c < MAX_OBVIOUS) {
                return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_SPACE) != 0;
            }
            return isWhitespace(c);
        }

        public static boolean isWhitespace(int cp) {
            switch (cp) {
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 0x1C:
                case 0x1D:
                case 0x1E:
                case 0x1F:
                    return true;
                case 0x00A0:
                case 0x2007:
                case 0x202f:
                    return false;
                default:
                    return isSpaceChar(cp);
            }
        }

        @SuppressWarnings("deprecation")
        public static boolean isSpaceChar(int cp) {
            return Character.isSpace((char)cp);
        }

        public static char toLowerCase(char c) {
            if (c < MAX_OBVIOUS) {
                if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_LOWER_LETTER) != 0) {
                    return c;
                } else if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_UPPER_LETTER) != 0) {
                    return (char)(32 + c);
                }
            }
            return Character.toLowerCase(c);
        }
    }

}
