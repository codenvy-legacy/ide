------------------------
Shopping Cart Project
------------------------

This demo application displays a shopping cart in an iGoogle gadget. You can display the list of objects, add them to your cart and create your own objects.
The logic of the application is deployed as REST service while the content of the cart is stored in the Java Content Repository thanks to the Chromattic framework, mapping the Groovy objects to JCR nodetypes.

------------------------
Setup Instructions
------------------------

In order to run the application, you first need to create the new node types in the JCR. 
In the data folder, create node types (upper right corner) for file: ShoppingCart.groovy.
In the logic folder, deploy the ShoppingCartRestService.grs (Deploy as REST service in upper right corner).
In the UI folder, you need change value of variable "serverUrl" (by default it set to "http://www.cloud-ide.com/IDE/rest/shop") on your tenant name in ShoppingCartGadget.xml.
(for example if your tenant called "exo" you "serverUrl" must be set to "http://exo.cloud-ide.com/IDE/rest/shop")
Now you can now view the ShoppingCartGadget.xml.
To deploy your gadget in your iGoogle dashboard. Get the URL of the gadget from the View menu, removing "/private" from the URL.
