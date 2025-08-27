package com.example.demo1;

import com.example.demo1.MongoUtil;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/users")
public class FirstServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String name = request.getParameter("searchName");

        MongoCollection<Document> collection = MongoUtil.getDatabase().getCollection("pages");

        out.println("<h2>Pages List:</h2><ul>");
        FindIterable<Document> results;
        if (name != null && !name.isEmpty()){
            Document search = new Document("$text", new Document("$search", name));
            Document projection = new Document("score", new Document("$meta", "textScore"));
            results = collection.find(search)
                    .projection(projection)
                    .sort(projection)
                    .skip(0)
                    .limit(10);
        }
        else {
            results = collection.find()
                    .limit(10);
        }

        for (Document doc : results) {
            out.println("<li><a href=" + doc.getString("URL") + ">" + doc.getString("Title") + "</a></li>");
        }
        out.println("</ul>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");

        MongoCollection<Document> collection = MongoUtil.getDatabase().getCollection("users");
        Document doc = new Document("name", name);
        collection.insertOne(doc);

        response.sendRedirect("users"); // redirect back to list
    }
}
