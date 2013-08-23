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
package org.exoplatform.ide.client.operation.uploadfile;

/**
 * Helper to parse response from server, that was got in SubmitCompleteHandler.
 * <p/>
 * Now it is used only for uploading files and zipped folders, but it can be used for other purposes.
 * <p/>
 * If while uploading was exception, than error message will be received in such form:
 * <code>&lt;pre&gt;Code: &lt;exit-code&gt; Text: &lt;error message&gt; &lt;/pre&gt;<code>
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: UploadHelper.java Nov 15, 2011 12:35:34 PM vereshchaka $
 */
public class UploadHelper {

    public static class ErrorData {
        public ErrorData(int code, String text) {
            this.code = code;
            this.text = text;
        }

        public int code;

        public String text;
    }

    /**
     * Parse error message in such form:
     * <p/>
     * <code>&lt;pre&gt;Code: &lt;exit-code&gt; Text: &lt;error message&gt; &lt;/pre&gt;<code>.
     *
     * @param errorMsg
     * @return
     */
    public static ErrorData parseError(String errorMsg) {
        String[] res = errorMsg.split("^<pre>Code: | Text: |</pre>$");
        return new ErrorData(Integer.valueOf(res[1]).intValue(), res[2]);
    }

}
