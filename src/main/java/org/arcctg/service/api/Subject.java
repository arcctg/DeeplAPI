package org.arcctg.service.api;

public interface Subject {
    void subscribe(Observer observer);
    void unsubscribe(Observer observer);
    void notify(EventData eventData);
}
