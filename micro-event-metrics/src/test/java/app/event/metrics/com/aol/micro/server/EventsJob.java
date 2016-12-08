package app.event.metrics.com.aol.micro.server;

import org.springframework.stereotype.Component;

import com.aol.micro.server.events.ScheduledJob;
import com.aol.micro.server.events.SystemData;

@Component
public class EventsJob implements ScheduledJob<EventsJob> {

    @Override
    public SystemData<String, String> scheduleAndLog() {
        return SystemData.<String, String> builder()
                         .errors(0)
                         .processed(2)
                         .build();
    }

}
