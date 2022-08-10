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
        String listMapText;
        if ("GET".equals(req.httpRequestType())) {
            topicMap.putIfAbsent(topicName, new ConcurrentHashMap<>());
            topicMap.get(topicName).putIfAbsent(text, new ConcurrentLinkedQueue<>());
            listMapText = topicMap.get(topicName).get(text).poll();
            if (listMapText == null) {
                listMapText = "";
                status = "204";
            }
            text = listMapText;
        }
        if ("POST".equals(req.httpRequestType())) {
            listMapText = text;
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>
                    topic = topicMap.get(topicName);
            if (topic != null) {
                for (ConcurrentLinkedQueue<String> queue : topic.values()) {
                    queue.add(listMapText);
                }
            }
        }
        return new Resp(text, status);
    }
}

