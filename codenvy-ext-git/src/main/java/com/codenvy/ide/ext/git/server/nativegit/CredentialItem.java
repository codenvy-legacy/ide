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
}
