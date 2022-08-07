package ru.job4j;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TopicServiceTest {

    @Test
    public void whenTopic() {
        TopicService topicService = new TopicService();
        String paramForPublisher = "temperature=18";
        String paramForSubscriber1 = "client407";
        String paramForSubscriber2 = "client6565";
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
        assertThat(result1.text(), is("temperature=18"));
        assertThat(result2.text(), is(""));
    }

    @Test
    public void testOfPostingOneMessagesAndTwoGetRequests() {
        Service service = new TopicService();
        Resp resp = service.process(new Req("GET", "topic", "weather", "clientOne"));
        Resp resp2 = service.process(new Req("GET", "topic", "weather", "clientTwo"));
        service.process(new Req("POST", "topic", "weather", "temp=35"));
        Resp resp3 = service.process(new Req("GET", "topic", "weather", "clientOne"));
        Resp resp4 = service.process(new Req("GET", "topic", "weather", "clientTwo"));
        assertThat(resp3.text(), is("temp=35"));
        assertThat(resp4.text(), is("temp=35"));
    }

    @Test
    public void testOfPostingTwoMessagesAndTwoGetRequests() {
        Service service = new TopicService();
        Resp resp = service.process(new Req("GET", "topic", "weather", "clientOne"));
        Resp resp2 = service.process(new Req("GET", "topic", "weather", "clientTwo"));
        service.process(new Req("POST", "topic", "weather", "temp=35"));
        Resp resp3 = service.process(new Req("GET", "topic", "weather", "clientOne"));
        service.process(new Req("POST", "topic", "weather", "temp=25"));
        Resp resp4 = service.process(new Req("GET", "topic", "weather", "clientTwo"));
        Resp resp5 = service.process(new Req("GET", "topic", "weather", "clientTwo"));
        assertThat(resp3.text(), is("temp=35"));
        assertThat(resp5.text(), is("temp=25"));
    }

    @Test
    public void testOfPostingFourMessagesAndFourGetRequests() {
        Service service = new TopicService();
        Resp resp = service.process(new Req("GET", "topic", "weather", "clientOne"));
        Resp resp2 = service.process(new Req("GET", "topic", "weather", "clientTwo"));
        Resp resp3 = service.process(new Req("GET", "topic", "weather", "clientThree"));
        Resp resp4 = service.process(new Req("GET", "topic", "weather", "clientFour"));
        service.process(new Req("POST", "topic", "weather", "temp=15"));
        Resp resp5 = service.process(new Req("GET", "topic", "weather", "clientOne"));
        service.process(new Req("POST", "topic", "weather", "temp=25"));
        Resp resp6 = service.process(new Req("GET", "topic", "weather", "clientTwo"));
        service.process(new Req("POST", "topic", "weather", "temp=35"));
        Resp resp7 = service.process(new Req("GET", "topic", "weather", "clientThree"));
        service.process(new Req("POST", "topic", "weather", "temp=45"));
        Resp resp8 = service.process(new Req("GET", "topic", "weather", "clientFour"));
        assertThat(resp5.text(), is("temp=15"));
        assertThat(resp6.text(), is("temp=15"));
        assertThat(resp7.text(), is("temp=15"));
        assertThat(resp8.text(), is("temp=15"));
    }
}
