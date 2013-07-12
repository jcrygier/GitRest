/**
 * Copyright 2013 John Crygier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.crygier.git.rest;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Main class.
 *
 */
public class Main {

    public static final Logger logger = Logger.getLogger(Main.class.getName());

    // Base URI the Grizzly HTTP server will listen on
    public static String BASE_URI = "http://localhost:8080/gitrest/";
    public static final Main INSTANCE = new Main();
    public static final Properties configuration = new Properties();

    private HttpServer httpServer;
    private boolean running = false;

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.crygier.git.rest package
        final ResourceConfig rc = new ResourceConfig().packages("com.crygier.git.rest.resources")
                .register(JacksonFeature.class)
        ;

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(Configuration.BaseUri.getStringValue()), rc);
    }

    /**
     * Initialize the program.  Called by commons-deamon, possibly as a super-user in UNIX environments.
     *
     * @param arguments
     */
    public void init(String[] arguments) throws Exception {
        if (arguments.length > 0) {          // Attempt to load the properties file
            Configuration.loadProperties(new File(arguments[0]));
        }
    }

    /**
     * Start the program.  Called by commons-daemon.
     */
    public void start() {
        httpServer = startServer();
        running = true;

        while (running) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // This is okay, we're going to shut down the server
            }
        }
    }

    /**
     * Stop the program.
     */
    public void stop() {
        running = false;
        httpServer.stop();
    }

    /**
     * Destroy any resources that are still existing.  Called by commons-daemon.
     */
    public void destroy() {

    }

    /**
     * Main method.  Mimics the process commons-daemon jscv would do.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length == 0 || "start".equalsIgnoreCase(args[0])) {
            if (args == null || args.length == 0)
                INSTANCE.init(args);
            else
                INSTANCE.init(Arrays.copyOfRange(args, 1, args.length));
            INSTANCE.start();
        } else if ("stop".equalsIgnoreCase(args[0])) {
            INSTANCE.stop();
            INSTANCE.destroy();
        }
    }
}

