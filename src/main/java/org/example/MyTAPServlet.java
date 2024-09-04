package org.example;

import java.io.IOException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tap.resource.TAP;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "tapService", value = "/tap")
public class MyTAPServlet extends HttpServlet {
    private TAP tap;

    public void init(ServletConfig config) throws ServletException {
        try {
            // 1. Create a TAP instance:
            tap = new TAP(new MyTAPServiceConnection());

            // 2. Initialize its resources:
            tap.init(config);

            // 3. Set the service as available:
            tap.getServiceConnection().setAvailable(true, "Service initialization in progress...");

            super.init(config);

            tap.getServiceConnection().setAvailable(true, "Service initialized.");
        } catch (Throwable e) {
            throw new ServletException("Can not initialize the TAP service",e);
        }
    }

    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException{
        // Forward the request to the TAP library:
        tap.executeRequest(request, response);
    }

    public void destroy() {
        // Free all resources attached to this TAP service (and its resources):
        /* note: after this call, no resources of the TAP service can be requested. */
        tap.destroy();
        super.destroy();
    }
}
