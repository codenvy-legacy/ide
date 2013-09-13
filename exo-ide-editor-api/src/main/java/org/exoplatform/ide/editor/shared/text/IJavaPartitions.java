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
package org.exoplatform.ide.editor.shared.text;

/**
 * Definition of Java partitioning and its partitions.
 *
 * @since 3.1
 */
public interface IJavaPartitions {

    /** The identifier of the Java partitioning. */
    String JAVA_PARTITIONING = "___java_partitioning";  //$NON-NLS-1$

    /** The identifier of the single-line (JLS2: EndOfLineComment) end comment partition content type. */
    String JAVA_SINGLE_LINE_COMMENT = "__java_singleline_comment"; //$NON-NLS-1$

    /** The identifier multi-line (JLS2: TraditionalComment) comment partition content type. */
    String JAVA_MULTI_LINE_COMMENT = "__java_multiline_comment"; //$NON-NLS-1$

    /** The identifier of the Javadoc (JLS2: DocumentationComment) partition content type. */
    String JAVA_DOC = "__java_javadoc"; //$NON-NLS-1$

    /** The identifier of the Java string partition content type. */
    String JAVA_STRING = "__java_string"; //$NON-NLS-1$

    /** The identifier of the Java character partition content type. */
    String JAVA_CHARACTER = "__java_character";  //$NON-NLS-1$
}
