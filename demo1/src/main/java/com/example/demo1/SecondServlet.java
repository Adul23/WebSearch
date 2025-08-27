package com.example.demo1;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import redis.clients.jedis.*;
import org.json.*;


@WebServlet("/search")
public class SecondServlet extends HttpServlet {
    private Jedis jedis;

    @Override
    public void init() {
        jedis = new Jedis("localhost", 6379);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String query = request.getParameter("q");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Send query to Python
        JSONObject reqJson = new JSONObject();
        reqJson.put("query", query);
        jedis.publish("search_requests", reqJson.toString());

        out.println("<h2>Search results for: " + query + "</h2><ul>");

        // Wait for Python results
        JedisPubSub listener = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if ("search_results".equals(channel)) {
                    JSONArray results = new JSONObject(message).getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject r = results.getJSONObject(i);
                        out.println("<li><a href='" + r.getString("url") + "'>" +
                                r.getString("title") + "</a> (score: " + r.getDouble("score") + ")</li>");
                    }
                    out.println("</ul>");
                    this.unsubscribe();
                }
            }
        };
        jedis.subscribe(listener, "search_results");

    }

    @Override
    public void destroy() {
        jedis.close();
    }
}

