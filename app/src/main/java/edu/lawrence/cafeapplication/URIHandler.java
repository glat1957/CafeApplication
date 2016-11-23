// Giorgio Latour
// Cafe Application
// IHRTLUHC

/*
Launcher Image: espresso cup by Symbolon from the Noun Project
 */

package edu.lawrence.cafeapplication;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class URIHandler {
    public static final String hostName = "10.0.2.2:8080";

    public static String doGet(String uri, String failure) {
        InputStream is = null;

        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Starts the query.
            conn.connect();
            int response = conn.getResponseCode();
            if (response != 200)
                return failure;
            is = conn.getInputStream();
            // Read the response as an array of chars.
            Reader reader = null;
            reader = new InputStreamReader(is, "UTF-8");
            char[] buffer = new char[conn.getContentLength()];
            reader.read(buffer);
            String result = new String(buffer);
            return result;

        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
        }
    return failure;
    }

    public static String doPost(String uri, String data) {
        InputStream is = null;

        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
            os.flush();

            // Start the query.
            conn.connect();

            is = conn.getInputStream();
            // Read the response as an array of char.
            Reader reader = null;
            reader = new InputStreamReader(is, "UTF-8");
            char[] buffer = new char[conn.getContentLength()];
            reader.read(buffer);
            // Convert the array of chars to a String and return it.
            String result = new String(buffer);
            return result;
        } catch (Exception ex) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
        }
        return "";
    }

    public static String doPut(String uri, String data) {
        InputStream is = null;
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream out = conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();

            // Start the query
            conn.connect();

            is = conn.getInputStream();
            // Read the response as an array of char
            Reader reader = null;
            reader = new InputStreamReader(is, "UTF-8");
            char[] buffer = new char[conn.getContentLength()];
            reader.read(buffer);
            // Convert the array of chars to a String and return that array.
            String result = new String(buffer);
            return result;
        } catch (Exception ex) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
        }
        return "";
    }

    public static void doDelete(String uri) {
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            // Start the query
            conn.connect();
            int responseCode = conn.getResponseCode();
        } catch (Exception ex) {

        }
    }
}


