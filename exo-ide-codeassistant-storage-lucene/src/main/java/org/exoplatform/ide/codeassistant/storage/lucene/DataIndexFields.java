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
package org.exoplatform.ide.codeassistant.storage.lucene;

/** Keep all field names in lucene Document */
public final class DataIndexFields {
    public static final String MODIFIERS = "modifiers";

    public static final String CLASS_NAME = "class-name";

    public static final String FQN = "fqn";

    public static final String ENTITY_TYPE = "entity-type";

    public static final String SUPERCLASS = "superclass";

    public static final String INTERFACES = "interfaces";

    public static final String TYPE_INFO = "type-info";

    public static final String JAVA_DOC = "doc";

    public static final String SIGNATURE = "signature";

    public static final String PACKAGE = "package";

    public static final String ARTIFACT = "artifact";
//   public static final String DESCRIPTOR = "descriptor";

    private DataIndexFields() {
    }

}
