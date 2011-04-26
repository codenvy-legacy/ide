------------------------
Twitter Flickr Mashup
------------------------

This demo application displays picture galleries of the current Twitter trends in an iGoogle gadget.
It uses the Twitter API to look-up the current trends, and then performs a picture search using the Flickr API.

------------------------
Setup Instructions
------------------------

In order to run the application, you must first deploy the Groovy service (in the upper right corner, Deploy REST Service).
Change host name in the URL(http://www.cloud-ide.com/rest/twitter_trends/images?length=4&t) on your tenant name
For example, if your tenant called "exo" URL must be (http://exo.cloud-ide.com/rest/twitter_trends/images?length=4&t).
You can deploy your gadget in your iGoogle dashboard.
To do so, copy the URL of the gadget from the View menu, and remove "/private" from the URL. Use that URL in your iGoogle dashboard.