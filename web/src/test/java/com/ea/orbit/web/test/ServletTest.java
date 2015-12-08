/*
 Copyright (C) 2015 Electronic Arts Inc.  All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1.  Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2.  Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3.  Neither the name of Electronic Arts, Inc. ("EA") nor the names of
     its contributors may be used to endorse or promote products derived
     from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY ELECTRONIC ARTS AND ITS CONTRIBUTORS "AS IS" AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL ELECTRONIC ARTS OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ea.orbit.web.test;

import com.ea.orbit.container.Container;
import com.ea.orbit.util.IOUtils;
import com.ea.orbit.util.NetUtils;
import com.ea.orbit.web.WebModule;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.DeploymentException;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ServletTest
{
    private static Container container;
    private static String serverPath = null;

    @Path("/servlet1")
    public static class Servlet1 extends HttpServlet
    {
        @Override
        protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
        {
            resp.getOutputStream().print("hello");
        }
    }

    @BeforeClass
    public static void setupServer() throws URISyntaxException, IOException, DeploymentException, InterruptedException
    {
        container = new Container();
        final int port = NetUtils.findFreePort();
        Map<String, Object> props = new HashMap<>();
        props.put("orbit.http.port", port);
        props.put("orbit.components", Arrays.asList(WebModule.class, Module1.class));
        container.setProperties(props);
        container.start();
        serverPath = "localhost:" + port;
    }

    @AfterClass
    public static void stopServer()
    {
        container.stop();
    }

    private static String getHttpPath()
    {
        return "http://" + serverPath;
    }

    @Test
    public void rawTest() throws IOException
    {
        // IDK anyone would use servlets now days, but there you go.

        Response servlet1 = ClientBuilder.newClient().target(getHttpPath())
                .path("servlet1").request(MediaType.TEXT_PLAIN)
                .get();
        assertEquals(200, servlet1.getStatus());
        assertEquals("hello", IOUtils.toString((InputStream) servlet1.getEntity()));
    }

}
