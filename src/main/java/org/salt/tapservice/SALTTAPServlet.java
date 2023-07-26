package org.salt.tapservice;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import tap.resource.TAP;

@WebServlet(name = "tapService", value = "/tap")
public class SALTTAPServlet extends HttpServlet {

    // Object representation of the TAP service.
    private TAP tap;

    public void init(final ServletConfig config) throws ServletException{
        try{

            // 1. Create a TAP instance:
            tap = new TAP(new SALTTAPServiceConnection());

            // 2. Initialize its resources:
            tap.init(config);

            // 3. Set the service as available:
            tap.getServiceConnection().setAvailable(true, "Service initialization in progress...;");

            super.init(config);
        }catch(Throwable e){
            throw new ServletException("Can not initialize the TAP service!", e);
        }
    }

    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        // Forward the request to the TAP library:
        tap.executeRequest(request, response);
    }

    public void destroy(){

        // Free all resources attached to this TAP service (and its resources):
        /* note: after this call, no resources of the TAP service can be requested. */
        tap.destroy();

        super.destroy();
    }

}