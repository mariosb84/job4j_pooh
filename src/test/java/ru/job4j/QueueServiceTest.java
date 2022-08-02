package ru.job4j;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class QueueServiceTest {

    @Test
    public void whenPostThenGetQueue() {
        QueueService queueService = new QueueService();
        String paramForPostMethod = "temperature=18";
        /* Добавляем данные в очередь weather. Режим queue */
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );
        /* Забираем данные из очереди weather. Режим queue */
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(result.text(), is("temperature=18"));
    }

    @Test
    public void testOfPosting2Messages() {
        Service service = new QueueService();
        Req req = new Req("POST", "queue", "weather", "temp=30");
        Req req2 = new Req("POST", "queue", "weather", "temp=35");
        Resp resp = service.process(req);
        Resp resp2 = service.process(req2);
        assertThat(resp.text(), is("temp=30"));
        assertThat(resp2.text(), is("temp=35"));
    }

    @Test
    public void testOfPosting3MessagesDiffThemesAnd3GetRequests() {
        Service service = new QueueService();
        service.process(new Req("POST", "queue", "weather", "temp=30"));
        service.process(new Req("POST", "queue", "weather", "temp=35"));
        service.process(new Req("POST", "queue", "season", "summer"));
        Resp resp = service.process(new Req("GET", "queue", "weather", ""));
        Resp resp2 = service.process(new Req("GET", "queue", "weather", ""));
        Resp resp3 = service.process(new Req("GET", "queue", "season", ""));
        assertThat(resp.text(), is("temp=30"));
        assertThat(resp2.text(), is("temp=35"));
        assertThat(resp3.text(), is("summer"));
    }
}
