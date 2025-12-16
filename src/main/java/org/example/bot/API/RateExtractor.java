package org.example.bot.API;

import org.json.JSONObject;


public interface RateExtractor {
    JSONObject extract(JSONObject root);
}
