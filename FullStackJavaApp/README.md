Follow the below steps to modify and run this staging Java servlet app configured with your PageSense account

**********************************************
Updating the codes on FullStackJavaApp.java
**********************************************

1. From the PageSense application, copy the 'projectSettings' code from the Summary page of the experiment you are desired to test and run. The sample code looks like the one attached below.
` String projectSettings = PageSenseClientBuilder.getProjectSettings("5lam9bei-US", "yC8SSSlMBkR0ueC", "master/ABTEST_2.4.542");`
2. Navigate to *FullStackJavaApp > src > com > pagesense > servlet > FullStackJavaApp.java*
3. Find and replace the `projectSettings` line
4. Replace the FullSStack A/B test experiment name on the `Track Add to Cart or Cart CTA only`section (look for the code below this comment section). Below is the desired code:
`// Track Add to Cart or Cart CTA only`
        i`f (buttonClicked != null && buttonClicked.startsWith("AddToCart-")) {`
          `  String productId = buttonClicked.replace("AddToCart-", "");`
           ` pageSenseClient.trackGoal("ZohoCode", userId, "AddToCart", visitorData);`
           ` System.out.println("Add to Cart clicked for Product ID: " + productId);`
        `} else if ("Cart".equals(buttonClicked)) {`
           `` pageSenseClient.trackGoal("ZohoCode", userId, "ViewCart", visitorData);`
            `System.out.println("Cart CTA clicked");``
        `}`
In the above code, replace `ZohoCode` with your actual experiment name from the PageSense interface. 
5. Navigate to `File > Save All`

*********************************
Running / Debugging the app
*********************************

1. On the top pane click `Run` > `Debug` > `Tomcat Debug Launch` (for debugging) / `Tomcat Run Launch` (for running the app and to a demo or a learning). 
2. Click on the `Preview` button on the top right pane, that will open the web app on a default browser with the *local host URL*
3. To stop the server, navigate back to `Run` > `Debug` > `stop server`. 

--------------------------------------------------------------------------------------------
Terminal tab - Prints the console logs, standard outputs, SDK responses. Webhook response. 
Logs tab - Gives am detailed stack of Tomcat server
---------------------------------------------------------------------------------------------

` All the required PageSense JARs to run the FullStack A/B test have been added to this staging app. This README.md file will be updated with further steps to configure and test the Webhook option. `
