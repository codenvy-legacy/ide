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
 * Object implementation of {@link TokenProperty} Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class ObjectProperty extends TokenProperty {

    private Object value;

    /** @param value */
    public ObjectProperty(Object value) {
        super();
        this.value = value;
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.TokenProperty#isObjectProperty() */
    @Override
    public ObjectProperty isObjectProperty() {
        return this;
    }

    /** @return value of this property */
    public Object objectValue() {
        return value;
    }

    @Override
    public StringProperty isStringProperty() {
        return null;
    }

    @Override
    public NumericProperty isNumericProperty() {
        return null;
    }

    @Override
    public ArrayProperty isArrayProperty() {
        return null;
    }

}
