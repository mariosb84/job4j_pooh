package ru.job4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {
    ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> queueMap = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        String text = req.getParam();
        String status = "200";
        ConcurrentLinkedQueue<String> linkedQueue;
        if ("POST".equals(req.httpRequestType())) {
            queueMap.putIfAbsent(req.getSourceName(), new ConcurrentLinkedQueue<>());
            queueMap.get(req.getSourceName()).add(req.getParam());
        } else {
            linkedQueue = queueMap.get(req.getSourceName());
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
