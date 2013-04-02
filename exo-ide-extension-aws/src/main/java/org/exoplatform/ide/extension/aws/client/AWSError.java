/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.extension.aws.client;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: AWSError.java Sep 24, 2012 vetal $
 */
public class AWSError {

    public static String STATUS_CODE = "Status Code";

    public static String AWS_SERVICE = "AWS Service";

    public static String AWS_REQUEST_ID = "AWS Request ID";

    public static String AWS_ERROR_CODE = "AWS Error Code";

    public static String AWS_ERROR_MESSAGE = "AWS Error Message";

    private String awsErrorMessage;

    private String awsErrorCode;

    private String statusCode;

    private String awsService;

    private String awsRequestID;

    public AWSError() {
    }

    public AWSError(String awsErrorMessage, String awsErrorCode, String statusCode, String awsService,
                    String awsRequestID) {
        super();
        this.awsErrorMessage = awsErrorMessage;
        this.awsErrorCode = awsErrorCode;
        this.statusCode = statusCode;
        this.awsService = awsService;
        this.awsRequestID = awsRequestID;
    }

    public AWSError(String error) {
        String[] split = error.split(",");
        for (int i = 0; i < split.length; i++) {
            String part = split[i];
            String[] tokens = part.split(":");
            if (tokens[0].trim().equals(AWSError.AWS_ERROR_CODE)) {
                awsErrorCode = tokens[1];
            } else if (tokens[0].trim().equals(AWSError.AWS_ERROR_MESSAGE)) {
                awsErrorMessage = tokens[1];
            } else if (tokens[0].trim().equals(AWSError.AWS_REQUEST_ID)) {
                awsRequestID = tokens[1];
            } else if (tokens[0].trim().equals(AWSError.AWS_SERVICE)) {
                awsService = tokens[1];
            } else if (tokens[0].trim().equals(AWSError.STATUS_CODE)) {
                statusCode = tokens[1];
            }
        }
    }

    public String getAwsErrorMessage() {
        return awsErrorMessage;
    }

    public void setAwsErrorMessage(String awsErrorMessage) {
        this.awsErrorMessage = awsErrorMessage;
    }

    public String getAwsErrorCode() {
        return awsErrorCode;
    }

    public void setAwsErrorCode(String awsErrorCode) {
        this.awsErrorCode = awsErrorCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getAwsService() {
        return awsService;
    }

    public void setAwsService(String awsService) {
        this.awsService = awsService;
    }

    public String getAwsRequestID() {
        return awsRequestID;
    }

    public void setAwsRequestID(String awsRequestID) {
        this.awsRequestID = awsRequestID;
    }

}
