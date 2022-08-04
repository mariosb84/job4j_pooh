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
    public void testOfPosting2Messages() {
        Service service = new TopicService();
        Req req = new Req("POST", "topic", "weather", "temp=30");
        Req req2 = new Req("POST", "topic", "weather", "temp=35");
        Resp resp = service.process(req);
        Resp resp2 = service.process(req2);
        assertThat(resp.text(), is("temp=30"));
        assertThat(resp2.text(), is("temp=35"));
    }

    @Test
    public void testOfPosting2MessagesAnd2GetRequestsWithSameId() {
        Service service = new TopicService();
        service.process(new Req("POST", "topic", "weather", "temp=30"));
        service.process(new Req("POST", "topic", "weather", "temp=35"));
        Resp resp3 = service.process(new Req("GET", "topic", "weather", ""));
        Resp resp4 = service.process(new Req("GET", "topic", "weather", ""));
        assertThat(resp3.text(), is("temp=30"));
        assertThat(resp4.text(), is("temp=35"));
    }

    @Test
    public void testOfPosting2MessagesAnd2GetRequestsWithDiffId() {
        Service service = new TopicService();
        service.process(new Req("POST", "topic", "weather", "temp=30"));
        service.process(new Req("POST", "topic", "weather", "temp=35"));
        Resp resp3 = service.process(new Req("GET", "topic", "weather", ""));
        Resp resp4 = service.process(new Req("GET", "topic", "weather", ""));
        assertThat(resp3.text(), is("temp=30"));
        assertThat(resp4.text(), is("temp=30"));
    }

    @Test
    public void testOfPosting3MessagesDiffThemesAnd4GetRequestsWithDiffId() {
        Service service = new TopicService();
        service.process(new Req("POST", "topic", "weather", "temp=30"));
        service.process(new Req("POST", "topic", "weather", "temp=35"));
        service.process(new Req("POST", "topic", "season", "summer"));
        Resp resp3 = service.process(new Req("GET", "topic", "weather", ""));
        Resp resp4 = service.process(new Req("GET", "topic", "weather", ""));
        Resp resp5 = service.process(new Req("GET", "topic", "weather", ""));
        Resp resp6 = service.process(new Req("GET", "topic", "season", ""));
        assertThat(resp3.text(), is("temp=30"));
        assertThat(resp4.text(), is("temp=30"));
        assertThat(resp5.text(), is("temp=35"));
        assertThat(resp6.text(), is("summer"));
    }
}
