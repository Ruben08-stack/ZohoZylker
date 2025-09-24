package com.pagesense.servlets;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.JSONObject;
import com.zoho.pagesense.PageSenseClient;
import com.zoho.pagesense.PageSenseClientBuilder;

@WebServlet("/")
public class FullStackJavaApp extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static PageSenseClient pageSenseClient;

    //Initailazing the PageSense Java SDK
    static {
             String projectSettings = PageSenseClientBuilder.getProjectSettings("e50ayjzj-US", "1jGrdUngcPEOjjO", "REPLICATION");
        pageSenseClient = PageSenseClientBuilder.getBuilder(projectSettings).buildClient();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userId = request.getSession().getId(); //Get the browser session as a userID
        String buttonClicked = request.getParameter("button");

        //Collecting and adding the user attributes to a Hasmap
        HashMap<String, String> visitorData = new HashMap<>();
        visitorData.put("Browser", getBrowserInfo(request));
        visitorData.put("Location", getClientCity(request));
        visitorData.put("Device", getDeviceType(request));
        visitorData.put("OperatingSystem", getOSDetails(request));
        visitorData.put("DayOfTheWeek", getDayOfTheWeek(request));
        visitorData.put("HourOfTheDay", getHourOfTheDay(request));

        //Printing the user attribute on the console
        visitorData.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));

        if (pageSenseClient == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "PageSenseClient not initialized.");
            return;
        }

        //activating the PageSense SDK client
        String variationName = pageSenseClient.activateExperiment("ZohoCode", userId, visitorData);
        System.out.println(variationName + " is served"); //Printing the name of the variation served

        // Track Add to Cart or Cart CTA only
        if (buttonClicked != null && buttonClicked.startsWith("AddToCart-")) {
            String productId = buttonClicked.replace("AddToCart-", "");
            pageSenseClient.trackGoal("ZohoCode", userId, "AddToCart", visitorData);
            System.out.println("Add to Cart clicked for Product ID: " + productId);
        } else if ("Cart".equals(buttonClicked)) {
            pageSenseClient.trackGoal("ZohoCode", userId, "ViewCart", visitorData);
            System.out.println("Cart CTA clicked");
        }

        // Set button color based on variation
        String addToCartButtonColor;
        if ("Original".equals(variationName)) {
            addToCartButtonColor = "bg-blue-600";
        } else if ("Variation 1".equals(variationName)) {
            addToCartButtonColor = "bg-orange-500";
        } else {
            addToCartButtonColor = "bg-red-500";
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Start of HTML rendering
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("    <title>E-commerce Store</title>");
        out.println("    <script src=\"https://cdn.tailwindcss.com\"></script>");
        out.println("    <style>");
        out.println("        body { font-family: 'Inter', sans-serif; background-color: #f3f4f6; }");
        out.println("        input[type=\"number\"]::-webkit-inner-spin-button,");
        out.println("        input[type=\"number\"]::-webkit-outer-spin-button { -webkit-appearance: none; margin: 0; }");
        out.println("        input[type=\"number\"] { -moz-appearance: textfield; }");
        out.println("    </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <div class=\"min-h-screen flex flex-col\">");
        out.println("        <header class=\"bg-gradient-to-r from-blue-500 to-indigo-600 text-white p-4 shadow-lg\">");
        out.println("            <div class=\"container mx-auto flex justify-between items-center\">");
        out.println("                <h1 class=\"text-3xl font-bold\">Zylker Store</h1>");
        out.println("                <nav>");
        out.println("                    <ul class=\"flex space-x-6\">");
        out.println("                        <li><button id=\"products-link\" class=\"text-white hover:text-blue-200 transition duration-300 font-semibold text-lg\">Products</button></li>");
        out.println("                        <li><button id=\"cart-link\" class=\"text-white hover:text-blue-200 transition duration-300 font-semibold text-lg relative\"");
        out.println("                            onclick=\"trackCartClick()\">Cart");
        out.println("                            <span id=\"cart-count\" class=\"absolute -top-2 -right-3 bg-red-500 text-white text-xs font-bold rounded-full h-5 w-5 flex items-center justify-center\">0</span>");
        out.println("                        </button></li>");
        out.println("                    </ul>");
        out.println("                </nav>");
        out.println("            </div>");
        out.println("        </header>");
        out.println("        <main class=\"container mx-auto p-6 flex-grow\">");
        out.println("            <section id=\"products-section\" class=\"grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8\">");

        // Product Cards Loop
        String[][] products = {
                {"1", "Stylish Watch", "120.00", "A78BFA"},
                {"2", "Premium Headphones", "250.00", "60A5FA"},
                {"3", "Smart Home Speaker", "99.99", "34D399"},
                {"4", "Ergonomic Mouse", "45.00", "FCD34D"},
                {"5", "Portable SSD", "85.00", "EC4899"},
                {"6", "Wireless Charger", "30.00", "F97316"},
                {"7", "Bluetooth Keyboard", "75.00", "10B981"},
                {"8", "Action Camera", "199.99", "EF4444"},
                {"9", "Gaming Headset", "110.00", "FBBF24"}
        };

        for (String[] product : products) {
            out.println("                <div class=\"bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 overflow-hidden\">");
            out.println("                    <img src=\"https://placehold.co/400x300/" + product[3] + "/ffffff?text=Product+" + product[0] + "\" alt=\"" + product[1] + "\" class=\"w-full h-48 object-cover rounded-t-xl\">");
            out.println("                    <div class=\"p-6\">");
            out.println("                        <h2 class=\"text-xl font-semibold text-gray-800 mb-2\">" + product[1] + "</h2>");
            out.println("                        <p class=\"text-gray-600 mb-4\">$" + product[2] + "</p>");
            out.println("                        <div class=\"flex items-center justify-between mb-4\">");
            out.println("                            <label for=\"qty-" + product[0] + "\" class=\"text-gray-700\">Quantity:</label>");
            out.println("                            <input type=\"number\" id=\"qty-" + product[0] + "\" value=\"1\" min=\"1\" class=\"w-20 p-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-center\">");
            out.println("                        </div>");
            out.println("                        <button data-product-id=\"" + product[0] + "\" data-product-name=\"" + product[1] + "\" data-product-price=\"" + product[2] + "\" class=\"add-to-cart-btn w-full " + addToCartButtonColor + " text-white py-3 rounded-lg font-bold hover:bg-opacity-90 transition duration-300 shadow-md\" onclick=\"trackAddToCart('" + product[1] + "', '" + product[0] + "')\">Add to Cart</button>");
            out.println("                    </div>");
            out.println("                </div>");
        }

        // End of Products Section
        out.println("            </section>");

        // Cart Section
        out.println("            <section id=\"cart-section\" class=\"hidden bg-white p-8 rounded-xl shadow-lg\">");
        out.println("                <h2 class=\"text-2xl font-bold text-gray-800 mb-6\">Your Shopping Cart</h2>");
        out.println("                <div id=\"cart-items\" class=\"space-y-4\"></div>");
        out.println("                <p class=\"text-gray-500 text-center\" id=\"empty-cart-message\">Your cart is empty.</p>");
        out.println("                <div id=\"cart-summary\" class=\"mt-8 pt-6 border-t border-gray-200 flex justify-between items-center\">");
        out.println("                    <p class=\"text-xl font-semibold text-gray-800\">Total:</p>");
        out.println("                    <p class=\"text-2xl font-bold text-blue-600\" id=\"cart-total\">$0.00</p>");
        out.println("                </div>");
        out.println("                <div class=\"mt-8 flex justify-end\">");
        out.println("                    <button class=\"bg-orange-600 text-white py-3 px-6 rounded-lg font-bold hover:bg-orange-700 transition duration-300 shadow-md\">Proceed to Checkout</button>");
        out.println("                </div>");
        out.println("            </section>");
        out.println("        </main>");
        out.println("        <footer class=\"bg-gray-800 text-white p-4 text-center mt-8\">");
        out.println("            <div class=\"container mx-auto\">");
        out.println("                <p>&copy; 2025 My Awesome Store. All rights reserved.</p>");
        out.println("            </div>");
        out.println("        </footer>");
        out.println("    </div>");

        // JavaScript with Tracking
        out.println("    <script>");
        out.println("        let cart = [];");

        // Track Add to Cart
        out.println("        function trackAddToCart(productName, productId) {");
        out.println("            fetch('submitForm', {");
        out.println("                method: 'POST',");
        out.println("                headers: {'Content-Type': 'application/x-www-form-urlencoded'},");
        out.println("                body: 'button=AddToCart-' + encodeURIComponent(productId)");
        out.println("            });");
        out.println("        }");

        // Track Cart Click
        out.println("        function trackCartClick() {");
        out.println("            fetch('submitForm', {");
        out.println("                method: 'POST',");
        out.println("                headers: {'Content-Type': 'application/x-www-form-urlencoded'},");
        out.println("                body: 'button=Cart'");
        out.println("            });");
        out.println("        }");

        // Cart logic
        out.println("        const cartItemsContainer = document.getElementById('cart-items');");
        out.println("        const cartTotalElement = document.getElementById('cart-total');");
        out.println("        const cartCountElement = document.getElementById('cart-count');");
        out.println("        const emptyCartMessage = document.getElementById('empty-cart-message');");
        out.println("        const productsSection = document.getElementById('products-section');");
        out.println("        const cartSection = document.getElementById('cart-section');");
        out.println("        const productsLink = document.getElementById('products-link');");
        out.println("        const cartLink = document.getElementById('cart-link');");

        out.println("        function updateCartDisplay() {");
        out.println("            cartItemsContainer.innerHTML = ''; let total = 0;");
        out.println("            if (cart.length === 0) {");
        out.println("                emptyCartMessage.classList.remove('hidden');");
        out.println("            } else {");
        out.println("                emptyCartMessage.classList.add('hidden');");
        out.println("                cart.forEach(item => {");
        out.println("                    const itemTotal = item.price * item.quantity;");
        out.println("                    total += itemTotal;");
        out.println("                    const cartItemDiv = document.createElement('div');");
        out.println("                    cartItemDiv.className = 'flex justify-between items-center bg-gray-50 p-4 rounded-lg shadow-sm';");
        out.println("                    cartItemDiv.innerHTML = `<div class='flex-grow'><h3 class='text-lg font-semibold text-gray-700'>${item.name}</h3><p class='text-gray-500'>Price: $${item.price.toFixed(2)}</p><p class='text-gray-500'>Quantity: ${item.quantity}</p></div><p class='text-lg font-bold text-blue-600'>$${itemTotal.toFixed(2)}</p>`;");
        out.println("                    cartItemsContainer.appendChild(cartItemDiv);");
        out.println("                });");
        out.println("            }");
        out.println("            cartTotalElement.textContent = `$${total.toFixed(2)}`;");
        out.println("            cartCountElement.textContent = cart.reduce((sum, item) => sum + item.quantity, 0);");
        out.println("        }");

        out.println("        document.querySelectorAll('.add-to-cart-btn').forEach(button => {");
        out.println("            button.addEventListener('click', event => {");
        out.println("                const productId = event.target.dataset.productId;");
        out.println("                const productName = event.target.dataset.productName;");
        out.println("                const productPrice = parseFloat(event.target.dataset.productPrice);");
        out.println("                const quantityInput = document.getElementById(`qty-${productId}`);");
        out.println("                const quantity = parseInt(quantityInput.value);");
        out.println("                if (quantity > 0) {");
        out.println("                    const existingItemIndex = cart.findIndex(item => item.id === productId);");
        out.println("                    if (existingItemIndex > -1) {");
        out.println("                        cart[existingItemIndex].quantity += quantity;");
        out.println("                    } else {");
        out.println("                        cart.push({id: productId, name: productName, price: productPrice, quantity: quantity});");
        out.println("                    }");
        out.println("                    quantityInput.value = 1;");
        out.println("                    updateCartDisplay();");
        out.println("                }");
        out.println("            });");
        out.println("        });");

        out.println("        productsLink.addEventListener('click', () => {");
        out.println("            productsSection.classList.remove('hidden');");
        out.println("            cartSection.classList.add('hidden');");
        out.println("        });");

        out.println("        cartLink.addEventListener('click', () => {");
        out.println("            productsSection.classList.add('hidden');");
        out.println("            cartSection.classList.remove('hidden');");
        out.println("            updateCartDisplay();");
        out.println("        });");

        out.println("        updateCartDisplay();");
        out.println("    </script>");

        out.println("</body>");
        out.println("</html>");
    }

    // Helper methods 
    private String getBrowserInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown Browser";
        if (userAgent.contains("Chrome") && userAgent.contains("Safari") && !userAgent.contains("Edg")) return "GoogleChrome";
        if (userAgent.contains("Edg")) return "MicrosoftEdge";
        if (userAgent.contains("Firefox")) return "MozillaFirefox";
        if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) return "AppleSafari";
        if (userAgent.contains("Opera") || userAgent.contains("OPR")) return "Opera";
        if (userAgent.contains("MSIE") || userAgent.contains("Trident")) return "InternetExplorer";
        return "Unknown Browser";
    }

    private String getClientCity(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            return "Localhost";
        }
        try {
            URL url = new URL("http://ip-api.com/json/" + ip);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() != 200) return "Unknown";
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) json.append(output);
            conn.disconnect();
            return new JSONObject(json.toString()).optString("city", "Unknown");
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String getDeviceType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";
        if (userAgent.matches(".*(Mobile|iPhone|Android).*")) return "Mobile";
        if (userAgent.matches(".*(iPad|Tablet).*")) return "Tablet";
        return "Desktop";
    }

    private String getOSDetails(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Windows")) {
            if (userAgent.contains("Windows NT 10.0")) return "Windows10";
            if (userAgent.contains("Windows NT 6.3")) return "Windows8.1";
            if (userAgent.contains("Windows NT 6.2")) return "Windows8";
            if (userAgent.contains("Windows NT 6.1")) return "Windows7";
            if (userAgent.contains("Windows NT 6.0")) return "WindowsVista";
            if (userAgent.contains("Windows NT 5.1")) return "WindowsXP";
            return "Windows(Other)";
        } else if (userAgent.contains("Mac")) {
            if (userAgent.contains("Macintosh")) return "macOS";
            if (userAgent.contains("Mac OS X")) return "macOS";
            return "Mac(Other)";
        } else if (userAgent.contains("Linux")) {
            if (userAgent.contains("Android")) return "Android";
            return "Linux";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iPod")) {
            return "iOS";
        } else {
            return "Unknown OS";
        }
    }

    private String getDayOfTheWeek(HttpServletRequest request) {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        return dayOfWeek.toString().charAt(0) + dayOfWeek.toString().substring(1).toLowerCase();
    }

    private String getHourOfTheDay(HttpServletRequest request) {
        LocalDateTime now = LocalDateTime.now();
        return Integer.toString(now.getHour());
    }
}