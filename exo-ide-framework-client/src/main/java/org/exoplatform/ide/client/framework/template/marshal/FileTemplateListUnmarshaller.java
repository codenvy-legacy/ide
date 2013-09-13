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
package org.exoplatform.ide.client.framework.template.marshal;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.template.FileTemplate;
import org.exoplatform.ide.client.framework.template.TemplateAutoBeanFactory;

import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: FileTemplateListUnmarshaller.java Jul 27, 2011 2:56:00 PM vereshchaka $
 */
public class FileTemplateListUnmarshaller implements Unmarshallable<List<FileTemplate>> {
    private List<FileTemplate> fileTemplates;

    public FileTemplateListUnmarshaller(List<FileTemplate> fileTemplates) {
        this.fileTemplates = fileTemplates;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        JSONArray jsonArray = JSONParser.parseStrict(response.getText()).isArray();
        if (jsonArray == null) {
            return;
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            String payload = jsonArray.get(i).isObject().toString();

            AutoBean<FileTemplate> fileTemplateBean =
                    AutoBeanCodex.decode(TemplateAutoBeanFactory.AUTO_BEAN_FACTORY, FileTemplate.class, payload);
            fileTemplates.add(fileTemplateBean.as());
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public List<FileTemplate> getPayload() {
        return fileTemplates;
    }
}
