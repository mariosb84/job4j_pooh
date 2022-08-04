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
                clientMap.putIfAbsent(text, new ConcurrentLinkedQueue<>());
                listMap.putIfAbsent(topicName, listMapText);
            clientMap.get(text).add(listMap.get(topicName));
            text = clientMap.get(text).poll();
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
        System.out.println(topicService.listMap);
        System.out.println(topicService.clientMap);
        /* Режим topic. Добавляем данные в топик weather. */
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher)
        );
        System.out.println();
        System.out.println(topicService.listMap);
        System.out.println(topicService.clientMap);
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client407. */
        Resp result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        System.out.println();
        System.out.println(topicService.listMap);
        System.out.println(topicService.clientMap);
        System.out.println(result1.text());
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client6565.
        Очередь отсутствует, т.к. еще не был подписан - получит пустую строку */
        Resp result2 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );
        System.out.println();
        System.out.println(topicService.listMap);
        System.out.println(topicService.clientMap);
        System.out.println(result2.text());


    }
}

