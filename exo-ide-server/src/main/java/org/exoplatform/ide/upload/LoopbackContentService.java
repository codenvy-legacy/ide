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
package org.exoplatform.ide.upload;

import org.apache.commons.fileupload.FileItem;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Uses for receiving the content of local file through server.
 * <p/>
 * Created by The eXo Platform SAS
 *
 * @author <a href="work.visor.ck@gmail.com">Dmytro Katayev</a> ${date}
 */

@Path("{ws-name}/loopbackcontent")
public class LoopbackContentService {

    private static Log log = ExoLogger.getLogger(LoopbackContentService.class);

    /**
     * POST method that gets the request body and returns it wrapped in the JavaScript.
     *
     * @param items
     *         file items form the request body.
     * @return the request body content wrapped with JavaScript.
     * @throws UploadServiceException
     */
    @POST
    @Consumes({"multipart/*"})
    @Produces(MediaType.TEXT_HTML)
    public String post(Iterator<FileItem> items) throws UploadServiceException {
        InputStream stream = null;
        while (items.hasNext()) {
            FileItem fitem = items.next();
            if (!fitem.isFormField()) {
                try {
                    stream = fitem.getInputStream();
                } catch (IOException ioe) {
                    log.error(ioe.getMessage(), ioe);
                    throw new UploadServiceException(ioe.getMessage());
                }
            }
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();

        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                String str = URLEncoder.encode(line + "\n", "UTF-8");
                sb.append(str);
            }
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
            throw new UploadServiceException(ioe.getMessage());
        }

        String bodyString = sb.toString();

        return "<filecontent>" + bodyString + "</filecontent>";
    }

}
