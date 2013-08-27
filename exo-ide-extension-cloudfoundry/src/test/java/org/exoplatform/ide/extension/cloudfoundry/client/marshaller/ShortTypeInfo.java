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
 * Short information about class or interface. Contain fqn, short name,
 * modifiers Example : { "name": "String", "qualifiedName": "java.lang.String",
 * "modifiers": 0, "type": "CLASS" }
 * <p/>
 * <p/>
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class ShortTypeInfo extends Member implements IShortType {
    /** Full Qualified Class Name */
    private String qualifiedName;

    /** Means this is CLASS, INTERFACE or ANNOTATION */
    private String type;

    public ShortTypeInfo() {
    }

    public ShortTypeInfo(Integer modifiers, String name, String qualifiedName, String type) {
        super(modifiers, name);
        this.qualifiedName = qualifiedName;
        this.type = type;
    }

    /** {@inheritDoc} */
    @Override
    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    /** {@inheritDoc} */
    @Override
    public String getQualifiedName() {
        return qualifiedName;
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

}
