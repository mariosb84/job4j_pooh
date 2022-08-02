package ru.job4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {
    ConcurrentHashMap<String, List<ConcurrentLinkedQueue<String>>> queueMap = new ConcurrentHashMap<>();
    @Override
    public Resp process(Req req) {
        String topicName = req.getSourceName();
        String topicParam = req.getParam();
        String text = req.getParam();
        String status = "200";
        ConcurrentLinkedQueue<String> linkedQueue;
        if ("POST".equals(req.httpRequestType()) && queueMap.containsKey(topicName)) {
            queueMap.get(topicName);
        } else {
            List<ConcurrentLinkedQueue<String>> list = queueMap.get(topicName);
                queueMap.putIfAbsent(topicName, list);
            linkedQueue = queueMap.get(topicName);
            text = linkedQueue.poll();
            }
        return new Resp(text, status);
    }

    public static void main(String[] args) {
        String paramForPublisher = "temperature=18";
        String paramForSubscriber1 = "client407";
        String paramForSubscriber2 = "client6565";
        TopicService topicService = new TopicService();
        /* Режим topic. Подписываемся на топик weather. client407. */
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        /* Режим topic. Добавляем данные в топик weather. */
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher)
        );
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client407. */
        Resp result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client6565.
        Очередь отсутствует, т.к. еще не был подписан - получит пустую строку */
        Resp result2 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );


    }
}

