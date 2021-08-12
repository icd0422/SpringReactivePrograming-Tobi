package com.example.springreactiveprograming.duality;

import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

@Slf4j
public class PullAndPush {

    public static void main(String[] args) {
        pull();
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
}
