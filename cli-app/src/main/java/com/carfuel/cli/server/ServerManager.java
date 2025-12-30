package com.carfuel.cli.server;

import java.io.File;
import java.io.IOException;

/**
 * Manages the backend server process.
 * Can detect if server is running and start it if needed.
 */
public class ServerManager {
    
    private final String apiBaseUrl;
    private final String serverJarPath;
    private Process serverProcess;
    
    public ServerManager(String apiBaseUrl, String serverJarName) {
        this.apiBaseUrl = apiBaseUrl;
        this.serverJarPath = findServerJar(serverJarName);
    }
    
    /**
     * Find the server JAR in common locations.
     */
    private String findServerJar(String jarName) {
        // Check current directory
        File currentDir = new File(jarName);
        if (currentDir.exists()) {
            return currentDir.getAbsolutePath();
        }
        
        // Check ../build/libs (if CLI is run from cli-app directory)
        File parentBuild = new File("../build/libs/" + jarName);
        if (parentBuild.exists()) {
            return parentBuild.getAbsolutePath();
        }
        
        // Check build/libs (if run from project root)
        File buildDir = new File("build/libs/" + jarName);
        if (buildDir.exists()) {
            return buildDir.getAbsolutePath();
        }
        
        return null;
    }
    
    /**
     * Check if the server can be started (JAR exists).
     */
    public boolean canStartServer() {
        return serverJarPath != null && new File(serverJarPath).exists();
    }
    
    /**
     * Check if the backend server is running.
     */
    public boolean isServerRunning() {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(apiBaseUrl + "/api/cars"))
                    .timeout(java.time.Duration.ofSeconds(2))
                    .GET()
                    .build();
            
            java.net.http.HttpResponse<String> response = client.send(request, 
                    java.net.http.HttpResponse.BodyHandlers.ofString());
            
            return response.statusCode() >= 200 && response.statusCode() < 500;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Start the backend server.
     */
    public void startServer() throws IOException, InterruptedException {
        if (!canStartServer()) {
            throw new IOException("Server JAR not found: " + serverJarPath);
        }
        
        ProcessBuilder processBuilder = new ProcessBuilder(
            "java",
            "-jar",
            serverJarPath
        );
        
        // Run server in background - redirect output
        processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        processBuilder.redirectError(ProcessBuilder.Redirect.DISCARD);
        
        serverProcess = processBuilder.start();
        
        // Wait for server to be ready (max 30 seconds)
        int maxAttempts = 30;
        for (int i = 0; i < maxAttempts; i++) {
            Thread.sleep(1000);
            if (isServerRunning()) {
                return;
            }
        }
        
        throw new IOException("Server failed to start within 30 seconds");
    }
    
    /**
     * Stop the server if it was started by this manager.
     */
    public void stopServer() {
        if (serverProcess != null && serverProcess.isAlive()) {
            serverProcess.destroy();
            try {
                serverProcess.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);
                if (serverProcess.isAlive()) {
                    serverProcess.destroyForcibly();
                }
            } catch (InterruptedException e) {
                serverProcess.destroyForcibly();
            }
        }
    }
}
