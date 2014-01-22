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
package org.exoplatform.ide.codeassistant.jvm.client;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 5:45:20 PM evgen $
 */
public enum TokenType {
    CLASS, METHOD, FIELD, ANNOTATION, INTERFACE, ARRAY, ENUM, CONSTRUCTOR, KEYWORD, TEMPLATE, VARIABLE, FUNCTION,
    /** Property type for JSON */
    PROPERTY,

    /** HTML or XML tag. */
    TAG,

    /** HTML or XML attribute; */
    ATTRIBUTE, CDATA,

    /** Property type for JavaScript */
    BLOCK,

    /** Property type for Groovy code */
    GROOVY_TAG, PACKAGE, IMPORT, PARAMETER, TYPE,

    /** Property type for Java code */
    JSP_TAG,

    /** Property type for Ruby code * */
    ROOT, MODULE, LOCAL_VARIABLE, GLOBAL_VARIABLE, CLASS_VARIABLE, INSTANCE_VARIABLE, CONSTANT,

    /** Propperty type for Php code * */
    PHP_TAG, CLASS_CONSTANT, NAMESPACE;
}