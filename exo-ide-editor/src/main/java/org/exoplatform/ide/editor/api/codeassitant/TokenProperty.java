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
package org.exoplatform.ide.editor.api.codeassitant;

/**
 * Abstract {@link Token} property class. <br>
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public abstract class TokenProperty {

    /** @return instance of {@link StringProperty} or <code>null</code> */
    public abstract StringProperty isStringProperty();

    /** @return instance of {@link NumericProperty} or <code>null</code> */
    public abstract NumericProperty isNumericProperty();

    /** @return instance of {@link ArrayProperty} or <code>null</code> */
    public abstract ArrayProperty isArrayProperty();

    /** @return instance of {@link ObjectProperty} or <code>null</code> */
    public abstract ObjectProperty isObjectProperty();

}
