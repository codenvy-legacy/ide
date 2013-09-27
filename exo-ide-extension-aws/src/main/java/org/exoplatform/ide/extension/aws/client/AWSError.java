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
