package com.example.springreactiveprograming.operators;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Operator {
    public static void main(String[] args) {

        Publisher<Integer> originPublisher = getOriginPublisher();
        Publisher<Integer> mapPublisher = getMapPublisher(originPublisher, i -> i + 10);
        Publisher<Integer> negativePublisher = getMapPublisher(mapPublisher, i -> i * -1);
//        Publisher<Integer> sumPublisher = getSumPublisher(negativePublisher);
        Publisher<Integer> reducePublisher = getReducePublisher(negativePublisher, 0, (a, b) -> a + b);
        Subscriber<Integer> logSubscriber = getLogSubscriber();

//        originPublisher.subscribe(logSubscriber);
//        negativePublisher.subscribe(logSubscriber);
//        sumPublisher.subscribe(logSubscriber);
        reducePublisher.subscribe(logSubscriber);
    }

    static Publisher<Integer> getMapPublisher(Publisher<Integer> originPublisher, Function<Integer, Integer> f) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> originSubscriber) {
                originPublisher.subscribe(new DelegateSubscriber(originSubscriber) {
                    @Override
                    public void onNext(Integer integer) {
                        super.onNext(f.apply(integer));
                    }
                });
            }
        };
    }

    static Publisher<Integer> getSumPublisher(Publisher<Integer> originPublisher) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> originSubscriber) {
                originPublisher.subscribe(new DelegateSubscriber(originSubscriber) {

                    int sum = 0;

                    @Override
                    public void onNext(Integer integer) {
                        sum += integer;
                    }

                    @Override
                    public void onComplete() {
                        super.onNext(sum);
                        super.onComplete();
                    }
                });
            }
        };
    }

    static Publisher<Integer> getReducePublisher(Publisher<Integer> originPublisher, Integer init, BiFunction<Integer, Integer, Integer> bf) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> originSubscriber) {
                originPublisher.subscribe(new DelegateSubscriber(originSubscriber) {

                    int result = init;

                    @Override
                    public void onNext(Integer integer) {
                        result = bf.apply(result, integer);
                    }

                    @Override
                    public void onComplete() {
                        super.onNext(result);
                        super.onComplete();
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
