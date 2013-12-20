/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.core.util;

import com.codenvy.ide.ext.java.jdt.core.Signature;
import com.codenvy.ide.ext.java.jdt.core.compiler.CharOperation;
import com.codenvy.ide.ext.java.jdt.core.dom.*;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ast.Argument;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ast.TypeReference;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ast.UnionTypeReference;
import com.codenvy.ide.ext.java.jdt.internal.compiler.parser.ScannerHelper;
import com.codenvy.ide.ext.java.jdt.internal.compiler.util.SuffixConstants;

import com.codenvy.ide.runtime.Assert;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/** Provides convenient utility methods to other types in this package. */
public class Util {

    public interface Comparable {
        /** Returns 0 if this and c are equal, >0 if this is greater than c, or <0 if this is less than c. */
        int compareTo(Comparable c);
    }

    public interface Comparer {
        /** Returns 0 if a and b are equal, >0 if a is greater than b, or <0 if a is less than b. */
        int compare(Object a, Object b);
    }

    private static final char ARGUMENTS_DELIMITER = '#';

    private static final String EMPTY_ARGUMENT = "   "; //$NON-NLS-1$

    private static char[][] JAVA_LIKE_EXTENSIONS;

    private static final char[] BOOLEAN = "boolean".toCharArray(); //$NON-NLS-1$

    private static final char[] BYTE = "byte".toCharArray(); //$NON-NLS-1$

    private static final char[] CHAR = "char".toCharArray(); //$NON-NLS-1$

    private static final char[] DOUBLE = "double".toCharArray(); //$NON-NLS-1$

    private static final char[] FLOAT = "float".toCharArray(); //$NON-NLS-1$

    private static final char[] INT = "int".toCharArray(); //$NON-NLS-1$

    private static final char[] LONG = "long".toCharArray(); //$NON-NLS-1$

    private static final char[] SHORT = "short".toCharArray(); //$NON-NLS-1$

    private static final char[] VOID = "void".toCharArray(); //$NON-NLS-1$

    private Util() {
        // cannot be instantiated
    }

    /**
     * Returns a new array adding the second array at the end of first array. It answers null if the first and second are null. If
     * the first array is null or if it is empty, then a new array is created with second. If the second array is null, then the
     * first array is returned. <br>
     * <br>
     * For example:
     * <ol>
     * <li>
     * <p/>
     * <pre>
     *    first = null
     *    second = "a"
     *    => result = {"a"}
     * </pre>
     * <p/>
     * <li>
     * <p/>
     * <pre>
     *    first = {"a"}
     *    second = null
     *    => result = {"a"}
     * </pre>
     * <p/>
     * </li>
     * <li>
     * <p/>
     * <pre>
     *    first = {"a"}
     *    second = {"b"}
     *    => result = {"a", "b"}
     * </pre>
     * <p/>
     * </li>
     * </ol>
     *
     * @param first
     *         the first array to concatenate
     * @param second
     *         the array to add at the end of the first array
     * @return a new array adding the second array at the end of first array, or null if the two arrays are null.
     */
    public static final String[] arrayConcat(String[] first, String second) {
        if (second == null)
            return first;
        if (first == null)
            return new String[]{second};

        int length = first.length;
        if (first.length == 0) {
            return new String[]{second};
        }

        String[] result = new String[length + 1];
        System.arraycopy(first, 0, result, 0, length);
        result[length] = second;
        return result;
    }

    /**
     * Checks the type signature in String sig, starting at start and ending before end (end is not included). Returns the index of
     * the character immediately after the signature if valid, or -1 if not valid.
     */
    private static int checkTypeSignature(String sig, int start, int end, boolean allowVoid) {
        if (start >= end)
            return -1;
        int i = start;
        char c = sig.charAt(i++);
        int nestingDepth = 0;
        while (c == '[') {
            ++nestingDepth;
            if (i >= end)
                return -1;
            c = sig.charAt(i++);
        }
        switch (c) {
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'Z':
                break;
            case 'V':
                if (!allowVoid)
                    return -1;
                // array of void is not allowed
                if (nestingDepth != 0)
                    return -1;
                break;
            case 'L':
                int semicolon = sig.indexOf(';', i);
                // Must have at least one character between L and ;
                if (semicolon <= i || semicolon >= end)
                    return -1;
                i = semicolon + 1;
                break;
            default:
                return -1;
        }
        return i;
    }

    /**
     * Simple replacement for clone() method for GWT
     *
     * @param array
     * @return cloned array
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] clone(T[] array) {
        return (T[])Arrays.asList(array).toArray();
    }

    /** Combines two hash codes to make a new one. */
    public static int combineHashCodes(int hashCode1, int hashCode2) {
        return hashCode1 * 17 + hashCode2;
    }

    /**
     * Compares two byte arrays. Returns <0 if a byte in a is less than the corresponding byte in b, or if a is shorter, or if a is
     * null. Returns >0 if a byte in a is greater than the corresponding byte in b, or if a is longer, or if b is null. Returns 0
     * if they are equal or both null.
     */
    public static int compare(byte[] a, byte[] b) {
        if (a == b)
            return 0;
        if (a == null)
            return -1;
        if (b == null)
            return 1;
        int len = Math.min(a.length, b.length);
        for (int i = 0; i < len; ++i) {
            int diff = a[i] - b[i];
            if (diff != 0)
                return diff;
        }
        if (a.length > len)
            return 1;
        if (b.length > len)
            return -1;
        return 0;
    }

    /**
     * Compares two strings lexicographically. The comparison is based on the Unicode value of each character in the strings.
     *
     * @return the value <code>0</code> if the str1 is equal to str2; a value less than <code>0</code> if str1 is lexicographically
     *         less than str2; and a value greater than <code>0</code> if str1 is lexicographically greater than str2.
     */
    public static int compare(char[] str1, char[] str2) {
        int len1 = str1.length;
        int len2 = str2.length;
        int n = Math.min(len1, len2);
        int i = 0;
        while (n-- != 0) {
            char c1 = str1[i];
            char c2 = str2[i++];
            if (c1 != c2) {
                return c1 - c2;
            }
        }
        return len1 - len2;
    }

    /** Concatenate a String[] compound name to a continuous char[]. */
    public static char[] concatCompoundNameToCharArray(String[] compoundName) {
        if (compoundName == null)
            return null;
        int length = compoundName.length;
        if (length == 0)
            return new char[0];
        int size = 0;
        for (int i = 0; i < length; i++) {
            size += compoundName[i].length();
        }
        char[] compoundChars = new char[size + length - 1];
        int pos = 0;
        for (int i = 0; i < length; i++) {
            String name = compoundName[i];
            if (i > 0)
                compoundChars[pos++] = '.';
            int nameLength = name.length();
            name.getChars(0, nameLength, compoundChars, pos);
            pos += nameLength;
        }
        return compoundChars;
    }

    public static String concatenateName(String name1, String name2, char separator) {
        StringBuffer buf = new StringBuffer();
        if (name1 != null && name1.length() > 0) {
            buf.append(name1);
        }
        if (name2 != null && name2.length() > 0) {
            if (buf.length() > 0) {
                buf.append(separator);
            }
            buf.append(name2);
        }
        return buf.toString();
    }

    /**
     * Returns the concatenation of the given array parts using the given separator between each part. <br>
     * <br>
     * For example:<br>
     * <ol>
     * <li>
     * <p/>
     * <pre>
     *    array = {"a", "b"}
     *    separator = '.'
     *    => result = "a.b"
     * </pre>
     * <p/>
     * </li>
     * <li>
     * <p/>
     * <pre>
     *    array = {}
     *    separator = '.'
     *    => result = ""
     * </pre>
     * <p/>
     * </li>
     * </ol>
     *
     * @param array
     *         the given array
     * @param separator
     *         the given separator
     * @return the concatenation of the given array parts using the given separator between each part
     */
    public static final String concatWith(String[] array, char separator) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0, length = array.length; i < length; i++) {
            buffer.append(array[i]);
            if (i < length - 1)
                buffer.append(separator);
        }
        return buffer.toString();
    }

    /**
     * Returns the concatenation of the given array parts using the given separator between each part and appending the given name
     * at the end. <br>
     * <br>
     * For example:<br>
     * <ol>
     * <li>
     * <p/>
     * <pre>
     *    name = "c"
     *    array = { "a", "b" }
     *    separator = '.'
     *    => result = "a.b.c"
     * </pre>
     * <p/>
     * </li>
     * <li>
     * <p/>
     * <pre>
     *    name = null
     *    array = { "a", "b" }
     *    separator = '.'
     *    => result = "a.b"
     * </pre>
     * <p/>
     * </li>
     * <li>
     * <p/>
     * <pre>
     *    name = " c"
     *    array = null
     *    separator = '.'
     *    => result = "c"
     * </pre>
     * <p/>
     * </li>
     * </ol>
     *
     * @param array
     *         the given array
     * @param name
     *         the given name
     * @param separator
     *         the given separator
     * @return the concatenation of the given array parts using the given separator between each part and appending the given name
     *         at the end
     */
    public static final String concatWith(String[] array, String name, char separator) {

        if (array == null || array.length == 0)
            return name;
        if (name == null || name.length() == 0)
            return concatWith(array, separator);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0, length = array.length; i < length; i++) {
            buffer.append(array[i]);
            buffer.append(separator);
        }
        buffer.append(name);
        return buffer.toString();

    }

    /** Converts a type signature from the IBinaryType representation to the DC representation. */
    public static String convertTypeSignature(char[] sig, int start, int length) {
        return new String(sig, start, length).replace('/', '.');
    }

    /*
     * Returns the default java extension (".java"). To be used when the extension is not known.
     */
    public static String defaultJavaExtension() {
        return SuffixConstants.SUFFIX_STRING_java;
    }

    /** Returns true iff str.toLowerCase().endsWith(end.toLowerCase()) implementation is not creating extra strings. */
    public final static boolean endsWithIgnoreCase(String str, String end) {

        int strLength = str == null ? 0 : str.length();
        int endLength = end == null ? 0 : end.length();

        // return false if the string is smaller than the end.
        if (endLength > strLength)
            return false;

        // return false if any character of the end are
        // not the same in lower case.
        for (int i = 1; i <= endLength; i++) {
            if (ScannerHelper.toLowerCase(end.charAt(endLength - i)) != ScannerHelper.toLowerCase(str
                                                                                                          .charAt(strLength - i)))
                return false;
        }

        return true;
    }

    /**
     * Compares two arrays using equals() on the elements. Neither can be null. Only the first len elements are compared. Return
     * false if either array is shorter than len.
     */
    public static boolean equalArrays(Object[] a, Object[] b, int len) {
        if (a == b)
            return true;
        if (a.length < len || b.length < len)
            return false;
        for (int i = 0; i < len; ++i) {
            if (a[i] == null) {
                if (b[i] != null)
                    return false;
            } else {
                if (!a[i].equals(b[i]))
                    return false;
            }
        }
        return true;
    }

    /**
     * Compares two arrays using equals() on the elements. Either or both arrays may be null. Returns true if both are null.
     * Returns false if only one is null. If both are arrays, returns true iff they have the same length and all elements are
     * equal.
     */
    public static boolean equalArraysOrNull(int[] a, int[] b) {
        if (a == b)
            return true;
        if (a == null || b == null)
            return false;
        int len = a.length;
        if (len != b.length)
            return false;
        for (int i = 0; i < len; ++i) {
            if (a[i] != b[i])
                return false;
        }
        return true;
    }

    /**
     * Compares two arrays using equals() on the elements. Either or both arrays may be null. Returns true if both are null.
     * Returns false if only one is null. If both are arrays, returns true iff they have the same length and all elements compare
     * true with equals.
     */
    public static boolean equalArraysOrNull(Object[] a, Object[] b) {
        if (a == b)
            return true;
        if (a == null || b == null)
            return false;

        int len = a.length;
        if (len != b.length)
            return false;
        // walk array from end to beginning as this optimizes package name cases
        // where the first part is always the same (e.g. org.eclipse.jdt)
        for (int i = len - 1; i >= 0; i--) {
            if (a[i] == null) {
                if (b[i] != null)
                    return false;
            } else {
                if (!a[i].equals(b[i]))
                    return false;
            }
        }
        return true;
    }

    /**
     * Compares two String arrays using equals() on the elements. The arrays are first sorted. Either or both arrays may be null.
     * Returns true if both are null. Returns false if only one is null. If both are arrays, returns true iff they have the same
     * length and iff, after sorting both arrays, all elements compare true with equals. The original arrays are left untouched.
     */
    public static boolean equalArraysOrNullSortFirst(String[] a, String[] b) {
        if (a == b)
            return true;
        if (a == null || b == null)
            return false;
        int len = a.length;
        if (len != b.length)
            return false;
        if (len >= 2) { // only need to sort if more than two items
            a = sortCopy(a);
            b = sortCopy(b);
        }
        for (int i = 0; i < len; ++i) {
            if (!a[i].equals(b[i]))
                return false;
        }
        return true;
    }

    /**
     * Compares two objects using equals(). Either or both array may be null. Returns true if both are null. Returns false if only
     * one is null. Otherwise, return the result of comparing with equals().
     */
    public static boolean equalOrNull(Object a, Object b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }

    /*
     * Returns whether the given file name equals to the given string ignoring the java like extension of the file name. Returns
     * false if it is not a java like file name.
     */
    public static boolean equalsIgnoreJavaLikeExtension(String fileName, String string) {
        int fileNameLength = fileName.length();
        int stringLength = string.length();
        if (fileNameLength < stringLength)
            return false;
        for (int i = 0; i < stringLength; i++) {
            if (fileName.charAt(i) != string.charAt(i)) {
                return false;
            }
        }
        char[][] javaLikeExtensions = getJavaLikeExtensions();
        suffixes:
        for (int i = 0, length = javaLikeExtensions.length; i < length; i++) {
            char[] suffix = javaLikeExtensions[i];
            int extensionStart = stringLength + 1;
            if (extensionStart + suffix.length != fileNameLength)
                continue;
            if (fileName.charAt(stringLength) != '.')
                continue;
            for (int j = extensionStart; j < fileNameLength; j++) {
                if (fileName.charAt(j) != suffix[j - extensionStart])
                    continue suffixes;
            }
            return true;
        }
        return false;
    }

    /** Given a qualified name, extract the last component. If the input is not qualified, the same string is answered. */
    public static String extractLastName(String qualifiedName) {
        int i = qualifiedName.lastIndexOf('.');
        if (i == -1)
            return qualifiedName;
        return qualifiedName.substring(i + 1);
    }

    /** Extracts the parameter types from a method signature. */
    public static String[] extractParameterTypes(char[] sig) {
        int count = getParameterCount(sig);
        String[] result = new String[count];
        if (count == 0)
            return result;
        int i = CharOperation.indexOf('(', sig) + 1;
        count = 0;
        int len = sig.length;
        int start = i;
        for (; ; ) {
            if (i == len)
                break;
            char c = sig[i];
            if (c == ')')
                break;
            if (c == '[') {
                ++i;
            } else if (c == 'L') {
                i = CharOperation.indexOf(';', sig, i + 1) + 1;
                Assert.isTrue(i != 0);
                result[count++] = convertTypeSignature(sig, start, i - start);
                start = i;
            } else {
                ++i;
                result[count++] = convertTypeSignature(sig, start, i - start);
                start = i;
            }
        }
        return result;
    }

    /** Extracts the return type from a method signature. */
    public static String extractReturnType(String sig) {
        int i = sig.lastIndexOf(')');
        Assert.isTrue(i != -1);
        return sig.substring(i + 1);
    }

    /**
     * Finds the first line separator used by the given text.
     *
     * @return </code>"\n"</code> or </code>"\r"</code> or </code>"\r\n"</code>, or <code>null</code> if none found
     */
    public static String findLineSeparator(char[] text) {
        // find the first line separator
        int length = text.length;
        if (length > 0) {
            char nextChar = text[0];
            for (int i = 0; i < length; i++) {
                char currentChar = nextChar;
                nextChar = i < length - 1 ? text[i + 1] : ' ';
                switch (currentChar) {
                    case '\n':
                        return "\n"; //$NON-NLS-1$
                    case '\r':
                        return nextChar == '\n' ? "\r\n" : "\r"; //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
        // not found
        return null;
    }

    /** Returns the registered Java like extensions. */
    public static char[][] getJavaLikeExtensions() {
        if (JAVA_LIKE_EXTENSIONS == null) {
            HashSet<String> fileExtensions = new HashSet<String>();
            fileExtensions.add(SuffixConstants.EXTENSION_java);

            int length = fileExtensions.size();
            // note that file extensions contains "java" as it is defined in JDT Core's plugin.xml
            char[][] extensions = new char[length][];
            extensions[0] = SuffixConstants.EXTENSION_java.toCharArray(); // ensure that "java" is first
            int index = 1;
            Iterator<String> iterator = fileExtensions.iterator();
            while (iterator.hasNext()) {
                String fileExtension = iterator.next();
                if (SuffixConstants.EXTENSION_java.equals(fileExtension))
                    continue;
                extensions[index++] = fileExtension.toCharArray();
            }
            JAVA_LIKE_EXTENSIONS = extensions;
        }
        return JAVA_LIKE_EXTENSIONS;
    }

    /**
     * Returns the substring of the given file name, ending at the start of a Java like extension. The entire file name is returned
     * if it doesn't end with a Java like extension.
     */
    public static String getNameWithoutJavaLikeExtension(String fileName) {
        int index = indexOfJavaLikeExtension(fileName);
        if (index == -1)
            return fileName;
        return fileName.substring(0, index);
    }

    /** Returns the number of parameter types in a method signature. */
    public static int getParameterCount(char[] sig) {
        int i = CharOperation.indexOf('(', sig) + 1;
        Assert.isTrue(i != 0);
        int count = 0;
        int len = sig.length;
        for (; ; ) {
            if (i == len)
                break;
            char c = sig[i];
            if (c == ')')
                break;
            if (c == '[') {
                ++i;
            } else if (c == 'L') {
                ++count;
                i = CharOperation.indexOf(';', sig, i + 1) + 1;
                Assert.isTrue(i != 0);
            } else {
                ++count;
                ++i;
            }
        }
        return count;
    }

    /** Put all the arguments in one String. */
    public static String getProblemArgumentsForMarker(String[] arguments) {
        StringBuffer args = new StringBuffer(10);

        args.append(arguments.length);
        args.append(':');

        for (int j = 0; j < arguments.length; j++) {
            if (j != 0)
                args.append(ARGUMENTS_DELIMITER);

            if (arguments[j].length() == 0) {
                args.append(EMPTY_ARGUMENT);
            } else {
                encodeArgument(arguments[j], args);
            }
        }

        return args.toString();
    }

    /**
     * Encode the argument by doubling the '#' if present into the argument value.
     * <p/>
     * <p>
     * This stores the encoded argument into the given buffer.
     * </p>
     *
     * @param argument
     *         the given argument
     * @param buffer
     *         the buffer in which the encoded argument is stored
     */
    private static void encodeArgument(String argument, StringBuffer buffer) {
        for (int i = 0, max = argument.length(); i < max; i++) {
            char charAt = argument.charAt(i);
            switch (charAt) {
                case ARGUMENTS_DELIMITER:
                    buffer.append(ARGUMENTS_DELIMITER).append(ARGUMENTS_DELIMITER);
                    break;
                default:
                    buffer.append(charAt);
            }
        }
    }

    /** Separate all the arguments of a String made by getProblemArgumentsForMarker */
    public static String[] getProblemArgumentsFromMarker(String argumentsString) {
        if (argumentsString == null) {
            return null;
        }
        int index = argumentsString.indexOf(':');
        if (index == -1)
            return null;

        int length = argumentsString.length();
        int numberOfArg = 0;
        try {
            numberOfArg = Integer.parseInt(argumentsString.substring(0, index));
        } catch (NumberFormatException e) {
            return null;
        }
        argumentsString = argumentsString.substring(index + 1, length);

        return decodeArgumentString(numberOfArg, argumentsString);
    }

    private static String[] decodeArgumentString(int length, String argumentsString) {
        // decode the argumentString knowing that '#' is doubled if part of the argument value
        if (length == 0) {
            if (argumentsString.length() != 0) {
                return null;
            }
            return CharOperation.NO_STRINGS;
        }
        String[] result = new String[length];
        int count = 0;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0, max = argumentsString.length(); i < max; i++) {
            char current = argumentsString.charAt(i);
            switch (current) {
                case ARGUMENTS_DELIMITER:
               /*
                * check the next character. If this is also ARGUMENTS_DELIMITER then only put one into the decoded argument and
                * proceed with the next character
                */
                    if ((i + 1) == max) {
                        return null;
                    }
                    char next = argumentsString.charAt(i + 1);
                    if (next == ARGUMENTS_DELIMITER) {
                        buffer.append(ARGUMENTS_DELIMITER);
                        i++; // proceed with the next character
                    } else {
                        // this means the current argument is over
                        String currentArgumentContents = String.valueOf(buffer);
                        if (EMPTY_ARGUMENT.equals(currentArgumentContents)) {
                            currentArgumentContents = com.codenvy.ide.ext.java.jdt.internal.compiler.util.Util.EMPTY_STRING;
                        }
                        result[count++] = currentArgumentContents;
                        if (count > length) {
                            // too many elements - ill-formed
                            return null;
                        }
                        buffer.delete(0, buffer.length());
                    }
                    break;
                default:
                    buffer.append(current);
            }
        }
        // process last argument
        String currentArgumentContents = String.valueOf(buffer);
        if (EMPTY_ARGUMENT.equals(currentArgumentContents)) {
            currentArgumentContents = com.codenvy.ide.ext.java.jdt.internal.compiler.util.Util.EMPTY_STRING;
        }
        result[count++] = currentArgumentContents;
        if (count > length) {
            // too many elements - ill-formed
            return null;
        }
        buffer.delete(0, buffer.length());
        return result;
    }

    /* Returns the signature of the given type. */
    public static String getSignature(Type type) {
        StringBuffer buffer = new StringBuffer();
        getFullyQualifiedName(type, buffer);
        return Signature.createTypeSignature(buffer.toString(), false/*
                                                                    * not resolved in source
                                                                    */);
    }

    /*
     * Returns the declaring type signature of the element represented by the given binding key. Returns the signature of the
     * element if it is a type.
     * @return the declaring type signature
     */
    public static String getDeclaringTypeSignature(String key) {
        KeyToSignature keyToSignature = new KeyToSignature(key, KeyToSignature.DECLARING_TYPE);
        keyToSignature.parse();
        return keyToSignature.signature.toString();
    }

    /*
     * Appends to the given buffer the fully qualified name (as it appears in the source) of the given type
     */
    private static void getFullyQualifiedName(Type type, StringBuffer buffer) {
        switch (type.getNodeType()) {
            case ASTNode.ARRAY_TYPE:
                ArrayType arrayType = (ArrayType)type;
                getFullyQualifiedName(arrayType.getElementType(), buffer);
                for (int i = 0, length = arrayType.getDimensions(); i < length; i++) {
                    buffer.append('[');
                    buffer.append(']');
                }
                break;
            case ASTNode.PARAMETERIZED_TYPE:
                ParameterizedType parameterizedType = (ParameterizedType)type;
                getFullyQualifiedName(parameterizedType.getType(), buffer);
                buffer.append('<');
                Iterator<Type> iterator = parameterizedType.typeArguments().iterator();
                boolean isFirst = true;
                while (iterator.hasNext()) {
                    if (!isFirst)
                        buffer.append(',');
                    else
                        isFirst = false;
                    Type typeArgument = iterator.next();
                    getFullyQualifiedName(typeArgument, buffer);
                }
                buffer.append('>');
                break;
            case ASTNode.PRIMITIVE_TYPE:
                buffer.append(((PrimitiveType)type).getPrimitiveTypeCode().toString());
                break;
            case ASTNode.QUALIFIED_TYPE:
                buffer.append(((QualifiedType)type).getName().getFullyQualifiedName());
                break;
            case ASTNode.SIMPLE_TYPE:
                buffer.append(((SimpleType)type).getName().getFullyQualifiedName());
                break;
            case ASTNode.WILDCARD_TYPE:
                buffer.append('?');
                WildcardType wildcardType = (WildcardType)type;
                Type bound = wildcardType.getBound();
                if (bound == null)
                    return;
                if (wildcardType.isUpperBound()) {
                    buffer.append(" extends "); //$NON-NLS-1$
                } else {
                    buffer.append(" super "); //$NON-NLS-1$
                }
                getFullyQualifiedName(bound, buffer);
                break;
        }
    }

    /** Returns a trimmed version the simples names returned by Signature. */
    public static String[] getTrimmedSimpleNames(String name) {
        String[] result = Signature.getSimpleNames(name);
        for (int i = 0, length = result.length; i < length; i++) {
            result[i] = result[i].trim();
        }
        return result;
    }

    /**
     * Returns true if the given name ends with one of the known java like extension.
     * (implementation is not creating extra strings)
     */
    public final static boolean isJavaLikeFileName(String name) {
        if (name == null)
            return false;
        return indexOfJavaLikeExtension(name) != -1;
    }

    /**
     * Returns the index of the Java like extension of the given file name or -1 if it doesn't end with a known Java like
     * extension. Note this is the index of the '.' even if it is not considered part of the extension.
     */
    public static int indexOfJavaLikeExtension(String fileName) {
        int fileNameLength = fileName.length();
        char[][] javaLikeExtensions = getJavaLikeExtensions();
        extensions:
        for (int i = 0, length = javaLikeExtensions.length; i < length; i++) {
            char[] extension = javaLikeExtensions[i];
            int extensionLength = extension.length;
            int extensionStart = fileNameLength - extensionLength;
            int dotIndex = extensionStart - 1;
            if (dotIndex < 0)
                continue;
            if (fileName.charAt(dotIndex) != '.')
                continue;
            for (int j = 0; j < extensionLength; j++) {
                if (fileName.charAt(extensionStart + j) != extension[j])
                    continue extensions;
            }
            return dotIndex;
        }
        return -1;
    }

    /** Returns true if the given method signature is valid, false if it is not. */
    public static boolean isValidMethodSignature(String sig) {
        int len = sig.length();
        if (len == 0)
            return false;
        int i = 0;
        char c = sig.charAt(i++);
        if (c != '(')
            return false;
        if (i >= len)
            return false;
        while (sig.charAt(i) != ')') {
            // Void is not allowed as a parameter type.
            i = checkTypeSignature(sig, i, len, false);
            if (i == -1)
                return false;
            if (i >= len)
                return false;
        }
        ++i;
        i = checkTypeSignature(sig, i, len, true);
        return i == len;
    }

    /** Returns true if the given type signature is valid, false if it is not. */
    public static boolean isValidTypeSignature(String sig, boolean allowVoid) {
        int len = sig.length();
        return checkTypeSignature(sig, 0, len, allowVoid) == len;
    }

    /*
     * Returns the simple name of a local type from the given binary type name. The last '$' is at lastDollar. The last character
     * of the type name is at end-1.
     */
    public static String localTypeName(String binaryTypeName, int lastDollar, int end) {
        if (lastDollar > 0 && binaryTypeName.charAt(lastDollar - 1) == '$')
            // local name starts with a dollar sign
            // (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=103466)
            return binaryTypeName;
        int nameStart = lastDollar + 1;
        while (nameStart < end && Character.isDigit(binaryTypeName.charAt(nameStart)))
            nameStart++;
        return binaryTypeName.substring(nameStart, end);
    }

    /* Add a log entry */
    public static void log(Throwable e, String message) {
        //TODO log error
//        Log.error(Util.class, message, e);

    }

    /** Returns the length of the common prefix between s1 and s2. */
    public static int prefixLength(char[] s1, char[] s2) {
        int len = 0;
        int max = Math.min(s1.length, s2.length);
        for (int i = 0; i < max && s1[i] == s2[i]; ++i)
            ++len;
        return len;
    }

    /** Returns the length of the common prefix between s1 and s2. */
    public static int prefixLength(String s1, String s2) {
        int len = 0;
        int max = Math.min(s1.length(), s2.length());
        for (int i = 0; i < max && s1.charAt(i) == s2.charAt(i); ++i)
            ++len;
        return len;
    }

    private static void quickSort(char[][] list, int left, int right) {
        int original_left = left;
        int original_right = right;
        char[] mid = list[left + (right - left) / 2];
        do {
            while (compare(list[left], mid) < 0) {
                left++;
            }
            while (compare(mid, list[right]) < 0) {
                right--;
            }
            if (left <= right) {
                char[] tmp = list[left];
                list[left] = list[right];
                list[right] = tmp;
                left++;
                right--;
            }
        }
        while (left <= right);
        if (original_left < right) {
            quickSort(list, original_left, right);
        }
        if (left < original_right) {
            quickSort(list, left, original_right);
        }
    }

    /** Sort the comparable objects in the given collection. */
    private static void quickSort(Comparable[] sortedCollection, int left, int right) {
        int original_left = left;
        int original_right = right;
        Comparable mid = sortedCollection[left + (right - left) / 2];
        do {
            while (sortedCollection[left].compareTo(mid) < 0) {
                left++;
            }
            while (mid.compareTo(sortedCollection[right]) < 0) {
                right--;
            }
            if (left <= right) {
                Comparable tmp = sortedCollection[left];
                sortedCollection[left] = sortedCollection[right];
                sortedCollection[right] = tmp;
                left++;
                right--;
            }
        }
        while (left <= right);
        if (original_left < right) {
            quickSort(sortedCollection, original_left, right);
        }
        if (left < original_right) {
            quickSort(sortedCollection, left, original_right);
        }
    }

    private static void quickSort(int[] list, int left, int right) {
        int original_left = left;
        int original_right = right;
        int mid = list[left + (right - left) / 2];
        do {
            while (list[left] < mid) {
                left++;
            }
            while (mid < list[right]) {
                right--;
            }
            if (left <= right) {
                int tmp = list[left];
                list[left] = list[right];
                list[right] = tmp;
                left++;
                right--;
            }
        }
        while (left <= right);
        if (original_left < right) {
            quickSort(list, original_left, right);
        }
        if (left < original_right) {
            quickSort(list, left, original_right);
        }
    }

    /** Sort the objects in the given collection using the given comparer. */
    private static void quickSort(Object[] sortedCollection, int left, int right, Comparer comparer) {
        int original_left = left;
        int original_right = right;
        Object mid = sortedCollection[left + (right - left) / 2];
        do {
            while (comparer.compare(sortedCollection[left], mid) < 0) {
                left++;
            }
            while (comparer.compare(mid, sortedCollection[right]) < 0) {
                right--;
            }
            if (left <= right) {
                Object tmp = sortedCollection[left];
                sortedCollection[left] = sortedCollection[right];
                sortedCollection[right] = tmp;
                left++;
                right--;
            }
        }
        while (left <= right);
        if (original_left < right) {
            quickSort(sortedCollection, original_left, right, comparer);
        }
        if (left < original_right) {
            quickSort(sortedCollection, left, original_right, comparer);
        }
    }

    /** Sort the strings in the given collection. */
    private static void quickSort(String[] sortedCollection, int left, int right) {
        int original_left = left;
        int original_right = right;
        String mid = sortedCollection[left + (right - left) / 2];
        do {
            while (sortedCollection[left].compareTo(mid) < 0) {
                left++;
            }
            while (mid.compareTo(sortedCollection[right]) < 0) {
                right--;
            }
            if (left <= right) {
                String tmp = sortedCollection[left];
                sortedCollection[left] = sortedCollection[right];
                sortedCollection[right] = tmp;
                left++;
                right--;
            }
        }
        while (left <= right);
        if (original_left < right) {
            quickSort(sortedCollection, original_left, right);
        }
        if (left < original_right) {
            quickSort(sortedCollection, left, original_right);
        }
    }

    /* Resets the list of Java-like extensions after a change in content-type. */
    public static void resetJavaLikeExtensions() {
        JAVA_LIKE_EXTENSIONS = null;
    }

    /**
     * Scans the given string for a type signature starting at the given index and returns the index of the last character.
     * <p/>
     * <pre>
     * TypeSignature:
     *  |  BaseTypeSignature
     *  |  ArrayTypeSignature
     *  |  ClassTypeSignature
     *  |  TypeVariableSignature
     * </pre>
     *
     * @param string
     *         the signature string
     * @param start
     *         the 0-based character index of the first character
     * @return the 0-based character index of the last character
     * @throws IllegalArgumentException
     *         if this is not a type signature
     */
    public static int scanTypeSignature(char[] string, int start) {
        // this method is used in jdt.debug
        return com.codenvy.ide.ext.java.jdt.internal.compiler.util.Util.scanTypeSignature(string, start);
    }

    /**
     * Return a new array which is the split of the given string using the given divider. The given end is exclusive and the given
     * start is inclusive. <br>
     * <br>
     * For example:
     * <ol>
     * <li>
     * <p/>
     * <pre>
     *    divider = 'b'
     *    string = "abbaba"
     *    start = 2
     *    end = 5
     *    result => { "", "a", "" }
     * </pre>
     * <p/>
     * </li>
     * </ol>
     *
     * @param divider
     *         the given divider
     * @param string
     *         the given string
     * @param start
     *         the given starting index
     * @param end
     *         the given ending index
     * @return a new array which is the split of the given string using the given divider
     * @throws ArrayIndexOutOfBoundsException
     *         if start is lower than 0 or end is greater than the array length
     */
    public static final String[] splitOn(char divider, String string, int start, int end) {
        int length = string == null ? 0 : string.length();
        if (length == 0 || start > end)
            return CharOperation.NO_STRINGS;

        int wordCount = 1;
        for (int i = start; i < end; i++)
            if (string.charAt(i) == divider)
                wordCount++;
        String[] split = new String[wordCount];
        int last = start, currentWord = 0;
        for (int i = start; i < end; i++) {
            if (string.charAt(i) == divider) {
                split[currentWord++] = string.substring(last, i);
                last = i + 1;
            }
        }
        split[currentWord] = string.substring(last, end);
        return split;
    }

    public static void sort(char[][] list) {
        if (list.length > 1)
            quickSort(list, 0, list.length - 1);
    }

    /** Sorts an array of Comparable objects in place. */
    public static void sort(Comparable[] objects) {
        if (objects.length > 1)
            quickSort(objects, 0, objects.length - 1);
    }

    public static void sort(int[] list) {
        if (list.length > 1)
            quickSort(list, 0, list.length - 1);
    }

    /** Sorts an array of objects in place. The given comparer compares pairs of items. */
    public static void sort(Object[] objects, Comparer comparer) {
        if (objects.length > 1)
            quickSort(objects, 0, objects.length - 1, comparer);
    }

    /** Sorts an array of strings in place using quicksort. */
    public static void sort(String[] strings) {
        if (strings.length > 1)
            quickSort(strings, 0, strings.length - 1);
    }

    /** Sorts an array of Comparable objects, returning a new array with the sorted items. The original array is left untouched. */
    public static Comparable[] sortCopy(Comparable[] objects) {
        int len = objects.length;
        Comparable[] copy = new Comparable[len];
        System.arraycopy(objects, 0, copy, 0, len);
        sort(copy);
        return copy;
    }

    /** Sorts an array of Strings, returning a new array with the sorted items. The original array is left untouched. */
    public static Object[] sortCopy(Object[] objects, Comparer comparer) {
        int len = objects.length;
        Object[] copy = new Object[len];
        System.arraycopy(objects, 0, copy, 0, len);
        sort(copy, comparer);
        return copy;
    }

    /** Sorts an array of Strings, returning a new array with the sorted items. The original array is left untouched. */
    public static String[] sortCopy(String[] objects) {
        int len = objects.length;
        String[] copy = new String[len];
        System.arraycopy(objects, 0, copy, 0, len);
        sort(copy);
        return copy;
    }

    /** Converts a char[][] to String, where segments are separated by '.'. */
    public static String toString(char[][] c) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, max = c.length; i < max; ++i) {
            if (i != 0)
                sb.append('.');
            sb.append(c[i]);
        }
        return sb.toString();
    }

    private static void appendArrayTypeSignature(char[] string, int start, StringBuffer buffer, boolean compact) {
        int length = string.length;
        // need a minimum 2 char
        if (start >= length - 1) {
            throw new IllegalArgumentException();
        }
        char c = string[start];
        if (c != Signature.C_ARRAY) {
            throw new IllegalArgumentException();
        }

        int index = start;
        c = string[++index];
        while (c == Signature.C_ARRAY) {
            // need a minimum 2 char
            if (index >= length - 1) {
                throw new IllegalArgumentException();
            }
            c = string[++index];
        }

        appendTypeSignature(string, index, buffer, compact);

        for (int i = 0, dims = index - start; i < dims; i++) {
            buffer.append('[').append(']');
        }
    }

    private static void appendClassTypeSignature(char[] string, int start, StringBuffer buffer, boolean compact) {
        char c = string[start];
        if (c != Signature.C_RESOLVED) {
            return;
        }
        int p = start + 1;
        int checkpoint = buffer.length();
        while (true) {
            c = string[p];
            switch (c) {
                case Signature.C_SEMICOLON:
                    // all done
                    return;
                case Signature.C_DOT:
                case '/':
                    // erase package prefix
                    if (compact) {
                        buffer.setLength(checkpoint);
                    } else {
                        buffer.append('.');
                    }
                    break;
                case Signature.C_DOLLAR:
                    /**
                     * Convert '$' in resolved type signatures into '.'. NOTE: This assumes that the type signature is an inner type
                     * signature. This is true in most cases, but someone can define a non-inner type name containing a '$'.
                     */
                    buffer.append('.');
                    break;
                default:
                    buffer.append(c);
            }
            p++;
        }
    }

    static void appendTypeSignature(char[] string, int start, StringBuffer buffer, boolean compact) {
        char c = string[start];
        switch (c) {
            case Signature.C_ARRAY:
                appendArrayTypeSignature(string, start, buffer, compact);
                break;
            case Signature.C_RESOLVED:
                appendClassTypeSignature(string, start, buffer, compact);
                break;
            case Signature.C_TYPE_VARIABLE:
                int e =
                        com.codenvy.ide.ext.java.jdt.internal.compiler.util.Util.scanTypeVariableSignature(string, start);
                buffer.append(string, start + 1, e - start - 1);
                break;
            case Signature.C_BOOLEAN:
                buffer.append(BOOLEAN);
                break;
            case Signature.C_BYTE:
                buffer.append(BYTE);
                break;
            case Signature.C_CHAR:
                buffer.append(CHAR);
                break;
            case Signature.C_DOUBLE:
                buffer.append(DOUBLE);
                break;
            case Signature.C_FLOAT:
                buffer.append(FLOAT);
                break;
            case Signature.C_INT:
                buffer.append(INT);
                break;
            case Signature.C_LONG:
                buffer.append(LONG);
                break;
            case Signature.C_SHORT:
                buffer.append(SHORT);
                break;
            case Signature.C_VOID:
                buffer.append(VOID);
                break;
        }
    }

    /*
     * Returns the unresolved type parameter signatures of the given method e.g. {"QString;", "[int", "[[Qjava.util.Vector;"}
     */
    public static String[] typeParameterSignatures(AbstractMethodDeclaration method) {
        Argument[] args = method.arguments;
        if (args != null) {
            int length = args.length;
            String[] signatures = new String[length];
            for (int i = 0; i < args.length; i++) {
                Argument arg = args[i];
                signatures[i] = typeSignature(arg.type);
            }
            return signatures;
        }
        return CharOperation.NO_STRINGS;
    }

    /*
     * Returns the unresolved type signature of the given type reference, e.g. "QString;", "[int", "[[Qjava.util.Vector;"
     */
    public static String typeSignature(TypeReference type) {
        String signature = null;
        if ((type.bits & com.codenvy.ide.ext.java.jdt.internal.compiler.ast.ASTNode.IsUnionType) != 0) {
            // special treatment for union type reference
            UnionTypeReference unionTypeReference = (UnionTypeReference)type;
            TypeReference[] typeReferences = unionTypeReference.typeReferences;
            int length = typeReferences.length;
            String[] typeSignatures = new String[length];
            for (int i = 0; i < length; i++) {
                char[][] compoundName = typeReferences[i].getParameterizedTypeName();
                char[] typeName = CharOperation.concatWith(compoundName, '.');
                typeSignatures[i] = Signature.createTypeSignature(typeName, false/*
                                                                              * don 't resolve
                                                                              */);
            }
            signature = Signature.createIntersectionTypeSignature(typeSignatures);
        } else {
            char[][] compoundName = type.getParameterizedTypeName();
            char[] typeName = CharOperation.concatWith(compoundName, '.');
            signature = Signature.createTypeSignature(typeName, false/*
                                                                   * don't resolve
                                                                   */);
        }
        return signature;
    }

    /** Asserts that the given method signature is valid. */
    public static void validateMethodSignature(String sig) {
        Assert.isTrue(isValidMethodSignature(sig));
    }

    /** Asserts that the given type signature is valid. */
    public static void validateTypeSignature(String sig, boolean allowVoid) {
        Assert.isTrue(isValidTypeSignature(sig, allowVoid));
    }

    /**
     * Get all type arguments from an array of signatures.
     * <p/>
     * Example: For following type X<Y<Z>,V<W>,U>.A<B> signatures is: [
     * ['L','X','<'
     * ,'L','Y','<','L','Z',';'>',';','L','V','<','L','W',';'>',';','L'
     * ,'U',';',>',';'], ['L','A','<','L','B',';','>',';'] ]
     *
     * @param typeSignatures
     *         Array of signatures (one per each type levels)
     * @return char[][][] Array of type arguments for each signature
     * @throws IllegalArgumentException
     *         If one of provided signature is malformed
     * @see #splitTypeLevelsSignature(String) Then, this method returns: [ [
     *      ['L','Y','<','L','Z',';'>',';'], ['L','V','<','L','W',';'>',';'],
     *      ['L','U',';'] ], [ ['L','B',';'] ] ]
     */
    public final static char[][][] getAllTypeArguments(char[][] typeSignatures) {
        if (typeSignatures == null)
            return null;
        int length = typeSignatures.length;
        char[][][] typeArguments = new char[length][][];
        for (int i = 0; i < length; i++) {
            typeArguments[i] = Signature.getTypeArguments(typeSignatures[i]);
        }
        return typeArguments;
    }

    /**
     * Split signatures of all levels from a type unique key.
     * <p/>
     * Example: For following type X<Y<Z>,V<W>,U>.A<B>, unique key is: "LX<LY<LZ;>;LV<LW;>;LU;>.LA<LB;>;"
     * <p/>
     * The return splitted signatures array is: [ ['L','X','<','L','Y','<','L','Z'
     * ,';'>',';','L','V','<','L','W',';'>',';','L','U','>',';'], ['L','A','<','L','B',';','>',';']
     *
     * @param typeSignature
     *         ParameterizedSourceType type signature
     * @return char[][] Array of signatures for each level of given unique key
     */
    public final static char[][] splitTypeLevelsSignature(String typeSignature) {
        // In case of IJavaElement signature, replace '$' by '.'
        char[] source = Signature.removeCapture(typeSignature.toCharArray());
        CharOperation.replace(source, '$', '.');

        // Init counters and arrays
        char[][] signatures = new char[10][];
        int signaturesCount = 0;
        // int[] lengthes = new int [10];
        int paramOpening = 0;

        // Scan each signature character
        for (int idx = 0, ln = source.length; idx < ln; idx++) {
            switch (source[idx]) {
                case '>':
                    paramOpening--;
                    if (paramOpening == 0) {
                        if (signaturesCount == signatures.length) {
                            System.arraycopy(signatures, 0, signatures = new char[signaturesCount + 10][], 0, signaturesCount);
                        }
                    }
                    break;
                case '<':
                    paramOpening++;
                    break;
                case '.':
                    if (paramOpening == 0) {
                        if (signaturesCount == signatures.length) {
                            System.arraycopy(signatures, 0, signatures = new char[signaturesCount + 10][], 0, signaturesCount);
                        }
                        signatures[signaturesCount] = new char[idx + 1];
                        System.arraycopy(source, 0, signatures[signaturesCount], 0, idx);
                        signatures[signaturesCount][idx] = Signature.C_SEMICOLON;
                        signaturesCount++;
                    }
                    break;
                case '/':
                    source[idx] = '.';
                    break;
            }
        }

        // Resize signatures array
        char[][] typeSignatures = new char[signaturesCount + 1][];
        typeSignatures[0] = source;
        for (int i = 1, j = signaturesCount - 1; i <= signaturesCount; i++, j--)//NOSONAR
        {
            typeSignatures[i] = signatures[j];
        }
        return typeSignatures;
    }

    public static char[] toAnchor(int startingIndex, char[] methodSignature, char[] methodName, boolean isVargArgs) {
        int firstParen = CharOperation.indexOf(Signature.C_PARAM_START, methodSignature);
        if (firstParen == -1) {
            throw new IllegalArgumentException();
        }

        StringBuffer buffer = new StringBuffer(methodSignature.length + 10);

        // selector
        if (methodName != null) {
            buffer.append(methodName);
        }

        // parameters
        buffer.append('(');
        char[][] pts = Signature.getParameterTypes(methodSignature);
        for (int i = startingIndex, max = pts.length; i < max; i++) {
            if (i == max - 1) {
                appendTypeSignatureForAnchor(pts[i], 0, buffer, isVargArgs);
            } else {
                appendTypeSignatureForAnchor(pts[i], 0, buffer, false);
            }
            if (i != pts.length - 1) {
                buffer.append(',');
                buffer.append(' ');
            }
        }
        buffer.append(')');
        char[] result = new char[buffer.length()];
        buffer.getChars(0, buffer.length(), result, 0);
        return result;
    }

    private static int appendTypeSignatureForAnchor(char[] string, int start, StringBuffer buffer, boolean isVarArgs) {
        // need a minimum 1 char
        if (start >= string.length) {
            throw new IllegalArgumentException();
        }
        char c = string[start];
        if (isVarArgs) {
            switch (c) {
                case Signature.C_ARRAY:
                    return appendArrayTypeSignatureForAnchor(string, start, buffer, true);
                case Signature.C_RESOLVED:
                case Signature.C_TYPE_VARIABLE:
                case Signature.C_BOOLEAN:
                case Signature.C_BYTE:
                case Signature.C_CHAR:
                case Signature.C_DOUBLE:
                case Signature.C_FLOAT:
                case Signature.C_INT:
                case Signature.C_LONG:
                case Signature.C_SHORT:
                case Signature.C_VOID:
                case Signature.C_STAR:
                case Signature.C_EXTENDS:
                case Signature.C_SUPER:
                case Signature.C_CAPTURE:
                default:
                    throw new IllegalArgumentException(); // a var args is an array type
            }
        } else {
            switch (c) {
                case Signature.C_ARRAY:
                    return appendArrayTypeSignatureForAnchor(string, start, buffer, false);
                case Signature.C_RESOLVED:
                    return appendClassTypeSignatureForAnchor(string, start, buffer);
                case Signature.C_TYPE_VARIABLE:
                    int e =
                            com.codenvy.ide.ext.java.jdt.internal.compiler.util.Util.scanTypeVariableSignature(string, start);
                    buffer.append(string, start + 1, e - start - 1);
                    return e;
                case Signature.C_BOOLEAN:
                    buffer.append(BOOLEAN);
                    return start;
                case Signature.C_BYTE:
                    buffer.append(BYTE);
                    return start;
                case Signature.C_CHAR:
                    buffer.append(CHAR);
                    return start;
                case Signature.C_DOUBLE:
                    buffer.append(DOUBLE);
                    return start;
                case Signature.C_FLOAT:
                    buffer.append(FLOAT);
                    return start;
                case Signature.C_INT:
                    buffer.append(INT);
                    return start;
                case Signature.C_LONG:
                    buffer.append(LONG);
                    return start;
                case Signature.C_SHORT:
                    buffer.append(SHORT);
                    return start;
                case Signature.C_VOID:
                    buffer.append(VOID);
                    return start;
                case Signature.C_CAPTURE:
                    return appendCaptureTypeSignatureForAnchor(string, start, buffer);
                case Signature.C_STAR:
                case Signature.C_EXTENDS:
                case Signature.C_SUPER:
                    return appendTypeArgumentSignatureForAnchor(string, start, buffer);
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private static int appendTypeArgumentSignatureForAnchor(char[] string, int start, StringBuffer buffer) {
        // need a minimum 1 char
        if (start >= string.length) {
            throw new IllegalArgumentException();
        }
        char c = string[start];
        switch (c) {
            case Signature.C_STAR:
                return start;
            case Signature.C_EXTENDS:
                return appendTypeSignatureForAnchor(string, start + 1, buffer, false);
            case Signature.C_SUPER:
                return appendTypeSignatureForAnchor(string, start + 1, buffer, false);
            default:
                return appendTypeSignatureForAnchor(string, start, buffer, false);
        }
    }

    private static int appendCaptureTypeSignatureForAnchor(char[] string, int start, StringBuffer buffer) {
        // need a minimum 2 char
        if (start >= string.length - 1) {
            throw new IllegalArgumentException();
        }
        char c = string[start];
        if (c != Signature.C_CAPTURE) {
            throw new IllegalArgumentException();
        }
        return appendTypeArgumentSignatureForAnchor(string, start + 1, buffer);
    }

    private static int appendArrayTypeSignatureForAnchor(char[] string, int start, StringBuffer buffer, boolean isVarArgs) {
        int length = string.length;
        // need a minimum 2 char
        if (start >= length - 1) {
            throw new IllegalArgumentException();
        }
        char c = string[start];
        if (c != Signature.C_ARRAY) {
            throw new IllegalArgumentException();
        }

        int index = start;
        c = string[++index];
        while (c == Signature.C_ARRAY) {
            // need a minimum 2 char
            if (index >= length - 1) {
                throw new IllegalArgumentException();
            }
            c = string[++index];
        }

        int e = appendTypeSignatureForAnchor(string, index, buffer, false);

        for (int i = 1, dims = index - start; i < dims; i++) {
            buffer.append('[').append(']');
        }

        if (isVarArgs) {
            buffer.append('.').append('.').append('.');
        } else {
            buffer.append('[').append(']');
        }
        return e;
    }

    private static int appendClassTypeSignatureForAnchor(char[] string, int start, StringBuffer buffer) {
        // need a minimum 3 chars "Lx;"
        if (start >= string.length - 2) {
            throw new IllegalArgumentException();
        }
        // must start in "L" or "Q"
        char c = string[start];
        if (c != Signature.C_RESOLVED && c != Signature.C_UNRESOLVED) {
            throw new IllegalArgumentException();
        }
        int p = start + 1;
        while (true) {
            if (p >= string.length) {
                throw new IllegalArgumentException();
            }
            c = string[p];
            switch (c) {
                case Signature.C_SEMICOLON:
                    // all done
                    return p;
                case Signature.C_GENERIC_START:
                    int e = scanGenericEnd(string, p + 1);
                    // once we hit type arguments there are no more package prefixes
                    p = e;
                    break;
                case Signature.C_DOT:
                    buffer.append('.');
                    break;
                case '/':
                    buffer.append('/');
                    break;
                case Signature.C_DOLLAR:
                    // once we hit "$" there are no more package prefixes
                    /**
                     * Convert '$' in resolved type signatures into '.'. NOTE: This assumes that the type signature is an inner type
                     * signature. This is true in most cases, but someone can define a non-inner type name containing a '$'.
                     */
                    buffer.append('.');
                    break;
                default:
                    buffer.append(c);
            }
            p++;
        }
    }

    private static int scanGenericEnd(char[] string, int start) {
        if (string[start] == Signature.C_GENERIC_END) {
            return start;
        }
        int length = string.length;
        int balance = 1;
        start++;
        while (start <= length) {
            switch (string[start]) {
                case Signature.C_GENERIC_END:
                    balance--;
                    if (balance == 0) {
                        return start;
                    }
                    break;
                case Signature.C_GENERIC_START:
                    balance++;
                    break;
            }
            start++;
        }
        return start;
    }

    /**
     * @param get_PLUS
     * @return
     */
    public static int[] clone(int[] array) {
        int result[] = new int[array.length];
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }
}
