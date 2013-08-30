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
package org.exoplatform.ide.editor.java.client.codeassistant.services.marshal;

import org.exoplatform.ide.editor.api.codeassitant.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 24, 2010 3:18:00 PM evgen $
 */
public class JavaClass {

    private List<Token> methods = new ArrayList<Token>();

    private List<Token> fields = new ArrayList<Token>();

    private List<Token> constructors = new ArrayList<Token>();

    private List<Token> abstractMethods = new ArrayList<Token>();

    /** @return the {@link List} of public methods */
    public List<Token> getPublicMethods() {
        return methods;
    }

    /** @return the {@link List} of public fields */
    public List<Token> getPublicFields() {
        return fields;
    }

    /** @return the {@link List} of public constructors */
    public List<Token> getPublicConstructors() {
        return constructors;
    }

    public List<Token> getAbstractMethods() {
        return abstractMethods;
    }

}
