/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.git.server.nativegit;

/**
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public abstract class CredentialItem {

    public abstract void clear();

    public static class Password extends CredentialItem {

        private char[] value;

        public char[] getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value.toCharArray();
        }

        public void setValue(char[] value) {
            this.value = new char[value.length];
            System.arraycopy(value, 0, this.value, 0, value.length);
        }

        @Override
        public void clear() {
            value = null;
        }

        @Override
        public String toString() {
            return new String(value);
        }
    }

    public static class Username extends CredentialItem {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public void clear() {
            value = null;
        }
    }

    public static class AuthenticatedUserName extends CredentialItem {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public void clear() {
            value = null;
        }
    }

    public static class AuthenticatedUserEmail extends CredentialItem {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public void clear() {
            value = null;
        }
    }
}
