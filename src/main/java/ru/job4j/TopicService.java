package ru.job4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {
    ConcurrentHashMap<String, ConcurrentHashMap<String,
            ConcurrentLinkedQueue<String>>> topicMap = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        String topicName = req.getSourceName();
        String text = req.getParam();
        String status = "200";
        String listMapText = "";
        if ("GET".equals(req.httpRequestType())) {
            if (topicMap.containsKey(topicName)
                    && topicMap.get(topicName) != null
                    && topicMap.get(topicName).containsKey(text)
                    && topicMap.get(topicName).get(text) != null) {
                listMapText = topicMap.get(topicName).get(text).poll();
            }
            topicMap.putIfAbsent(topicName, new ConcurrentHashMap<>());
            topicMap.get(topicName).putIfAbsent(text, new ConcurrentLinkedQueue<>());
            if (listMapText == null) {
                listMapText = "";
                status = "204";
            }
            text = listMapText;
        }
        if ("POST".equals(req.httpRequestType())
                && topicMap.containsKey(topicName)) {
            listMapText = text;
            for (String s : topicMap.get(topicName).keySet()) {
                topicMap.get(topicName).get(s).add(listMapText);
            }
        }
        return new Resp(text, status);
    }
}

