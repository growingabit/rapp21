/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.growingabit.rapp21;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

// [START example]
import com.google.appengine.api.utils.SystemProperty;

import java.io.IOException;
import java.util.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.*;
import com.google.appengine.api.datastore.*;

// With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
@WebServlet(
    name = "EndorsementAPI",
    description = "EndorsementAPI: show all the rapp21 Endorsement",
    urlPatterns = "/endorsement"
)
public class Endorsement extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
        UserService userService = UserServiceFactory.getUserService();

        String thisUrl = req.getRequestURI();

        String userName = "";

        resp.setContentType("text/html");
        if (req.getUserPrincipal() != null) {
            userName = req.getUserPrincipal().getName();
            req.setAttribute("username", userName);
            req.setAttribute("logoutUrl", userService.createLogoutURL(thisUrl));
        } else {
            resp.sendRedirect(userService.createLoginURL(thisUrl));
            return;
        }


        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Long id_topic = Long.parseLong(req.getParameter("id_topic"));
        req.setAttribute("id_topic", id_topic);
        req.setAttribute("topic_title", this.getTopicTitle(id_topic));

        Long id_skill = Long.parseLong(req.getParameter("id_skill"));
        req.setAttribute("id_skill", id_skill);
        req.setAttribute("skill_title", this.getSkillTitle(id_skill));

        Long id_student = Long.parseLong(req.getParameter("id_student"));
        req.setAttribute("id_student", id_student);
        req.setAttribute("student_name", this.getStudentName(id_student));

        this.saveEndorsement(id_topic, id_skill, id_student, userName);

        RequestDispatcher rd = req.getRequestDispatcher("Endorsement.jsp");
        rd.forward(req, resp);
    }

    private String getStudentName(Long id_topic) {
        return "Mario Rossi";
    }

    private String getTopicTitle(Long id_topic) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        try {
            Entity topic = datastore.get(KeyFactory.createKey("RApP21Topic", id_topic));
            return (String) topic.getProperty("title");
        } catch (EntityNotFoundException e) {
            return "";
        }
    }

    private String getSkillTitle(Long id_skill) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        try {
            Entity skill = datastore.get(KeyFactory.createKey("RApP21Skill", id_skill));
            return (String) skill.getProperty("title");
        } catch (EntityNotFoundException e) {
            return "";
        }
    }


    private void saveEndorsement(Long id_topic, Long id_skill, Long id_student, String professor) {

        String endorsementKey = professor + "_" + id_topic + "_" + id_skill + "_" + id_student;
        Entity endorsement = new Entity(KeyFactory.createKey("RApP21Endorsement", endorsementKey));
        endorsement.setProperty("id_topic", id_topic);
        endorsement.setProperty("id_skill", id_skill);
        endorsement.setProperty("id_student", id_student);
        endorsement.setProperty("professor", professor);
        endorsement.setProperty("updated_at", new Date());

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        datastore.put(endorsement);
    }
}
// [END example]
