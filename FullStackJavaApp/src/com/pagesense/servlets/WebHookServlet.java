package com.pagesense.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.zoho.pagesense.PageSenseClientBuilder;


@WebServlet("/webhook")
public class WebHookServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(WebHookServlet.class.getName());
    private static final String SECRET_KEY = "8vhqdRPNJvEsnZL"; //"ISGLvPa1p5wu34l"; //toa8yXGJkFDZcai"; // Replace with actual secret key
    private static final String JAVA_SDK_KEY = "oJG32I8B97Yrqby"; // "XaTXb1eyvJyIAME"; // Replace with actual sdk key

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /* Function to handle incoming webhook requests  
     * @parameter request (HttpServletRequest), response (HttpServletResponse)  
     * @return JSON response with success status  
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter outputWriter = response.getWriter();
        
        LOGGER.info("Received a new webhook request.");
        
        // Validate authentication token
        String authToken = request.getHeader("X-OAuth-Token");
        if (authToken == null || !authToken.equals(SECRET_KEY)) {
            LOGGER.warning("Unauthorized request received.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            outputWriter.print("{\"success\": false, \"message\": \"Unauthorized: Invalid token\"}");
            return;
        }
        else
        {
            LOGGER.info("Authentication Token: " + authToken + " matched.");
        }

        // Read request payload
        StringBuilder jsonPayload = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                jsonPayload.append(inputLine);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading request payload", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            outputWriter.print("{\"success\": false, \"message\": \"Error reading request payload\"}");
            return;
        }

        // Parse JSON payload
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonPayload.toString());
            LOGGER.info("Received payload: " + jsonObject.toString());
        } catch (JSONException e) {
            LOGGER.log(Level.WARNING, "Invalid JSON format", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            outputWriter.print("{\"success\": false, \"message\": \"Invalid JSON format\"}");
            return;
        }

        // Validate JSON payload
        if (!isValidPayload(jsonObject)) {
            LOGGER.warning("Invalid payload received.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            outputWriter.print("{\"success\": false, \"message\": \"Invalid Payload\"}");
            return;
        }

        // Extract required fields
        String accountName = jsonObject.getString("AccountName");
        String projectName = jsonObject.getString("ProjectName");

        try {
            // Fetch project settings
            String projectSettingString = PageSenseClientBuilder.getProjectSettings(accountName, JAVA_SDK_KEY, projectName);
            if (projectSettingString != null) {
                LOGGER.info("Project settings retrieved successfully.");
                outputWriter.print("{\"success\": true, \"message\": \"API Call processed successfully.\"}");
            } else {
                LOGGER.warning("Failed to retrieve project settings.");
                outputWriter.print("{\"success\": false, \"message\": \"Failed to get the Project Settings.\"}");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving project settings", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            outputWriter.print("{\"success\": false, \"message\": \"Error retrieving project settings\"}");
        }
    }

    /* Function to validate payload fields  
     * @parameter jsonObject (JSONObject) - JSON payload received in webhook  
     * @return boolean - true if payload is valid, false otherwise  
     */
    private boolean isValidPayload(JSONObject jsonObject) {
        return jsonObject.has("ProjectName") && isValidString(jsonObject.optString("ProjectName")) &&
               jsonObject.has("AccountName") && isValidString(jsonObject.optString("AccountName")) &&
               jsonObject.has("EnvironmentName") && isValidString(jsonObject.optString("EnvironmentName")) &&
               jsonObject.has("EventName") && isValidString(jsonObject.optString("EventName")) &&
               jsonObject.has("LastModifiedTime") && isValidDateTime(jsonObject.optString("LastModifiedTime"));
    }

    /* Function to validate a string field (non-null, non-empty, non-whitespace)  
     * @parameter value (String) - Field value to validate  
     * @return boolean - true if valid, false otherwise  
     */
    private boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /* Function to validate LastModifiedTime as "yyyy-MM-dd HH:mm:ss"  
     * @parameter dateTimeStr (String) - DateTime string to validate  
     * @return boolean - true if valid, false otherwise  
     */
    private boolean isValidDateTime(String dateTimeStr) {
        try {
            LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            LOGGER.log(Level.WARNING, "Invalid LastModifiedTime format", e);
            return false;
        }
    }
}