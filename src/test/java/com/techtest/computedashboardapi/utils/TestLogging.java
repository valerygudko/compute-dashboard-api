package com.techtest.computedashboardapi.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.mockito.Mockito.*;

public class TestLogging {

    protected final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    protected Appender appender = mock(Appender.class);

    protected ArgumentCaptor<LoggingEvent> argumentCaptor = ArgumentCaptor.forClass(LoggingEvent.class);

    @BeforeEach
    public void before() {
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
    }

    @AfterEach
    public void after() {
        logger.detachAppender(appender);
    }

    protected List<LoggingEvent> capture() {
        verify(appender, atLeastOnce()).doAppend(argumentCaptor.capture());
        return argumentCaptor.getAllValues();
    }

    protected boolean messageHasBeenLogged(List<LoggingEvent> loggingEvents, String message) {
        return loggingEvents.stream()
                .anyMatch(loggingEvent -> loggingEvent.getFormattedMessage().contains(message));
    }
}
