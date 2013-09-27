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
package org.exoplatform.ide.extension.java.jdi.server;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JdiType {
    /**
     * <table>
     * <tr><th>Type Signature</th><th>Java Type</th></tr>
     * <tr>Z<td></td><td>boolean</td></tr>
     * <tr>B<td></td><td>byte</td></tr>
     * <tr>C<td></td><td>char</td></tr>
     * <tr>S<td></td><td>short</td></tr>
     * <tr>I<td></td><td>int</td></tr>
     * <tr>J<td></td><td>long</td></tr>
     * <tr>F<td></td><td>float</td></tr>
     * <tr>D<td></td><td>double</td></tr>
     * </table>
     *
     * @param signature
     *         variable signature
     * @return <code>true</code> if primitive and <code>false</code> otherwise
     */
    public static boolean isPrimitive(String signature) {
        char t = signature.charAt(0);
        return t == 'Z' || t == 'B' || t == 'C' || t == 'S' || t == 'I' || t == 'J' || t == 'F' || t == 'D';
    }

    /**
     * @param signature
     *         variable signature
     * @return <code>true</code> if array and <code>false</code> otherwise
     */
    public static boolean isArray(String signature) {
        return signature.charAt(0) == '[';
    }

    private JdiType() {
    }
}
