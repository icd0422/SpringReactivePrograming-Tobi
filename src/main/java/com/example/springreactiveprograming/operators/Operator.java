package com.example.springreactiveprograming.operators;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Operator {
    public static void main(String[] args) {

        Publisher<Integer> originPublisher = getOriginPublisher();
        Publisher<Integer> mapPublisher = getMapPublisher(originPublisher, i -> i + 10);
        Publisher<Integer> negativePublisher = getMapPublisher(mapPublisher, i -> i * -1);
        Subscriber<Integer> logSubscriber = getLogSubscriber();

//        originPublisher.subscribe(logSubscriber);
        negativePublisher.subscribe(logSubscriber);
    }

    static Publisher<Integer> getMapPublisher(Publisher<Integer> originPublisher, Function<Integer, Integer> f) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> originSubscriber) {
                originPublisher.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        originSubscriber.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        originSubscriber.onNext(f.apply(integer));
                    }

                    @Override
                    public void onError(Throwable t) {
                        originSubscriber.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        originSubscriber.onComplete();
                    }
                });
            }
        };
    }

    static Publisher<Integer> getOriginPublisher() {
        Publisher<Integer> publisher = new Publisher<Integer>() {

            Iterable iterable = Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList());

            @Override
            public void subscribe(Subscriber s) {
                log.debug("subscribe");

                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {

                        log.debug("request");

                        iterable.forEach(i ->
                                s.onNext(i)
                        );

                        s.onComplete();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        return publisher;
    }

    static Subscriber<Integer> getLogSubscriber() {
        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.debug("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.debug("onNext : {}", integer);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        };

        return subscriber;
    }
}
