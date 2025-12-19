package org.example.bot.api;

import org.json.JSONObject;

public interface RateExtractor {
    JSONObject extract(JSONObject root);
}
