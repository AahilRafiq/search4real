package com.aahilrafiq;

import com.aahilrafiq.helpers.search.QueryHelpers;
import com.aahilrafiq.helpers.search.ResponseObj;

import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public"); // Serve files from 'src/main/resources/public'
        }).start(3000);

        app.get("/", ctx -> ctx.redirect("/index.html"));

        app.get("/search", ctx -> {
            String query = ctx.queryParam("q");
            ResponseObj[] searchResults = QueryHelpers.getResults(query).toArray(new ResponseObj[0]);
            ctx.json(searchResults);
        });
    }
}