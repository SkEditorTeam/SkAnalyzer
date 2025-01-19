package me.glicz.skanalyzer.logback.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;

import java.util.Iterator;

public class MultiAppender extends AppenderBase<ILoggingEvent> implements AppenderAttachable<ILoggingEvent> {
    private final AppenderAttachable<ILoggingEvent> appenders = new AppenderAttachableImpl<>();

    @Override
    protected void append(ILoggingEvent eventObject) {
        Iterator<Appender<ILoggingEvent>> it = appenders.iteratorForAppenders();
        while (it.hasNext()) {
            it.next().doAppend(eventObject);
        }
    }

    @Override
    public void addAppender(Appender<ILoggingEvent> newAppender) {
        appenders.addAppender(newAppender);
    }

    @Override
    public Iterator<Appender<ILoggingEvent>> iteratorForAppenders() {
        return appenders.iteratorForAppenders();
    }

    @Override
    public Appender<ILoggingEvent> getAppender(String name) {
        return appenders.getAppender(name);
    }

    @Override
    public boolean isAttached(Appender<ILoggingEvent> appender) {
        return appenders.isAttached(appender);
    }

    @Override
    public void detachAndStopAllAppenders() {
        appenders.detachAndStopAllAppenders();
    }

    @Override
    public boolean detachAppender(Appender<ILoggingEvent> appender) {
        return appenders.detachAppender(appender);
    }

    @Override
    public boolean detachAppender(String name) {
        return appenders.detachAppender(name);
    }
}
