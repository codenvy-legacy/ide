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

package org.exoplatform.ide.extension.cloudfoundry.client.marshaller;


/**
 * Represent information about class field. Can be transform to JSON. Example of
 * JSON: <code>
 * {
 * "declaringClass": "java.lang.String",
 * "name": "CASE_INSENSITIVE_ORDER",
 * "modifiers": 25,
 * "type": "Comparator"
 * }
 * </code>
 * <p/>
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class FieldInfo extends Member implements IFieldInfo {
    /** Short Class Name <code>Comparator</code> */
    private String type;

    /** Full Qualified Class Name where field declared */
    private String declaringClass;

    public FieldInfo(String type, Integer modifiers, String name, String declaringClass) {
        super(modifiers, name);
        this.type = type;
        this.declaringClass = declaringClass;
    }

    public FieldInfo() {

    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return type;
    }

    /** {@inheritDoc} */
    @Override
    public void setType(String type) {
        this.type = type;
    }

    /** {@inheritDoc} */
    @Override
    public String getDeclaringClass() {
        return declaringClass;
    }

    /** {@inheritDoc} */
    @Override
    public void setDeclaringClass(String declaringClass) {
        this.declaringClass = declaringClass;
    }


}
