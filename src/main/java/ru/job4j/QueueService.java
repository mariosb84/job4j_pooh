package ru.job4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {
    ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> queueMap = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        String queueName = req.getSourceName();
        String text = req.getParam();
        String status = "200";
        ConcurrentLinkedQueue<String> linkedQueue;
        if ("POST".equals(req.httpRequestType())) {
            queueMap.putIfAbsent(queueName, new ConcurrentLinkedQueue<>());
            queueMap.get(queueName).add(text);
        } else {
            linkedQueue = queueMap.get(queueName);
            if (linkedQueue == null) {
                text = "incorrect GET request, no POST found";
                status = "204";
            } else {
                text = linkedQueue.poll();
            }
        }
        return new Resp(text, status);
    }
}
