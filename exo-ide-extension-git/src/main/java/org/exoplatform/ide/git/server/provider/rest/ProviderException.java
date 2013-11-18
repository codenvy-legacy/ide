package org.exoplatform.ide.git.server.provider.rest;

/**
 * Handle exception when we try to make requests to Git services.
 */
public class ProviderException extends Exception {
    private final int responseStatus;

    private final String contentType;

    private final String providerName;

    public ProviderException(int responseStatus, String message, String contentType, String providerName) {
        super(message);
        this.responseStatus = responseStatus;
        this.contentType = contentType;
        this.providerName = providerName;
    }

    /**
     * Get response code for exception.
     *
     * @return response code for result
     */
    public int getResponseStatus() {
        return responseStatus;
    }

    /**
     * Get content type of message if it exists.
     *
     * @return string representation of message mime type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Get Git service short name in which exception throwed.
     *
     * @return short name of Git service to identify where problem occurs.
     */
    public String getProviderName() {
        return providerName;
    }
}
