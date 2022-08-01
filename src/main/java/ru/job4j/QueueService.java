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

            if (queueMap.containsKey(text)) {
                linkedQueue = queueMap.get(req.getParam());
            } else {
                linkedQueue = new ConcurrentLinkedQueue<>();
            }
            linkedQueue.add(req.getParam());
            queueMap.put(req.getParam(), linkedQueue);
        } else {
            linkedQueue = queueMap.get(req.getParam());
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
