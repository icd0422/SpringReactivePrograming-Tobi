package com.example.springreactiveprograming.duality;

import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class PullAndPush {

    public static void main(String[] args) {
//        pull();
        push();
//        옵져버 패턴의 한계
//        1.Complete??
//        2.Error??
    }

    public static void pull() {
        Iterable<Integer> iterableUntilTen = new Iterable<Integer>() {

            private static final int MAX = 10;

            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {

                    private int i = 0;

                    @Override
                    public boolean hasNext() {
                        return i < MAX;
                    }

                    @Override
                    public Integer next() {
                        return ++i;
                    }
                };
            }
        };

        Iterator<Integer> iterator = iterableUntilTen.iterator();
        while (iterator.hasNext()) {
            log.debug(String.valueOf(iterator.next()));
        }
    }

    public static void push() {
        log.debug("start");

        Observer observer = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                log.debug(arg.toString());
            }
        };

        IntObservable intObservable = new IntObservable();
        intObservable.addObserver(observer);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(intObservable);

        log.debug("end");
    }

    static class IntObservable extends Observable implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i <= 10; i++) {
                setChanged();
                notifyObservers(i);
            }
        }
    }
}
