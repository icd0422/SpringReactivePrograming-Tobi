package com.example.springreactiveprograming.duality;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.Iterator;

@Slf4j
public class PublishAndSubscribe {
    public static void main(String[] args) {
        Publisher publisher = new Publisher() {

            final Iterable<Integer> iterable = Arrays.asList(1, 2, 3, 4, 5);

            @Override
            public void subscribe(Subscriber subscriber) {
                log.debug("subscribe");
                subscriber.onSubscribe(new Subscription() {

                    Iterator iterator = iterable.iterator();

                    @Override
                    public void request(long n) {
                        try {
                            while (n-- > 0) {
                                if (iterator.hasNext()) {
                                    subscriber.onNext(iterator.next());
                                } else {
                                    subscriber.onComplete();
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Subscriber subscriber = new Subscriber() {
            @Override
            public void onSubscribe(Subscription s) {
                log.debug("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Object o) {
                log.debug("onNext : {}", o);
                throw new RuntimeException();
            }

            @Override
            public void onError(Throwable t) {
                log.debug("onError");
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        };

        publisher.subscribe(subscriber);
    }
}
