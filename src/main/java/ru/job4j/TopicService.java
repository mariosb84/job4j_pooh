package ru.job4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {
    ConcurrentHashMap<String, String> listMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> clientMap = new ConcurrentHashMap<>();
    @Override
    public Resp process(Req req) {
        String topicName = req.getSourceName();
        String text = req.getParam();
        String status = "200";
        String listMapText = "";
        if ("POST".equals(req.httpRequestType()) && listMap.containsKey(topicName)) {
            listMapText = text;
            listMap.put(topicName, listMapText);
        } else {
            if (clientMap.containsKey(text)) {
                clientMap.get(text).add(listMap.get(topicName));
                listMapText = clientMap.get(text).poll();
            }
            listMap.putIfAbsent(topicName, listMapText);
            clientMap.putIfAbsent(text, new ConcurrentLinkedQueue<>());
            if (listMapText == null) {
                listMapText = "";
               }
            text = listMapText;
            }
        return new Resp(text, status);
    }
}

