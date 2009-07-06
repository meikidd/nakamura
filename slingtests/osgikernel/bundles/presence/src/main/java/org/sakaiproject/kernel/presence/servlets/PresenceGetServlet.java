/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.sakaiproject.kernel.presence.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.sakaiproject.kernel.api.presence.PresenceService;
import org.sakaiproject.kernel.util.ExtendedJSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet deals with JSON output only and the presence GET requests
 * 
 * @scr.component metatype="no" immediate="true"
 * @scr.service interface="javax.servlet.Servlet"
 * @scr.property name="sling.servlet.resourceTypes" value="sakai/presence"
 * @scr.property name="sling.servlet.methods" value="GET"
 * No selector currently //scr.property name="sling.servlet.selectors" value="status"
 * @scr.property name="sling.servlet.extensions" value="json"
 * 
 * @scr.reference name="PresenceService"
 *                interface="org.sakaiproject.kernel.api.presence.PresenceService"
 */
public class PresenceGetServlet extends SlingAllMethodsServlet {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(PresenceGetServlet.class);

  private static final long serialVersionUID = 11111111L;

  protected PresenceService presenceService;

  protected void bindPresenceService(PresenceService presenceService) {
    this.presenceService = presenceService;
  }

  protected void unbindPresenceService(PresenceService presenceService) {
    this.presenceService = null;
  }

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    // get current user
    String user = request.getRemoteUser();
    if (user == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User must be logged in to check their status");
    }
    LOGGER.info("GET to PresenceServlet ("+user+")");

    try {
      ExtendedJSONWriter output = new ExtendedJSONWriter(response.getWriter());
      output.object();
      output.key("user");
      output.value(user);
      output.key(PresenceService.PRESENCE_STATUS_PROP);
      String status = presenceService.getStatus(user);
      output.value( status );
      output.key(PresenceService.PRESENCE_LOCATION_PROP);
      String location = presenceService.getLocation(user);
      output.value( location );
      output.endObject();
    } catch (JSONException e) {
      LOGGER.error(e.getMessage(), e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }

    return;
  }

}
