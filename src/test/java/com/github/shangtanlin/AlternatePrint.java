package com.github.shangtanlin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AlternatePrint {
    private int count = 1;
    private final Object lock = new Object();

        @Test
        void testPrint() {
            Thread t1 = new Thread(() -> {
                while (count <= 100) {
                    synchronized (lock) {
                        if (count <= 100) {
                            System.out.println(Thread.currentThread().getName() + ": " + count++);
                        }
                        lock.notify(); // 唤醒另一个等待的线程
                        try {
                            if (count <= 100) {
                                lock.wait(); // 释放锁，自己进入等待
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, "线程1");

            Thread t2 = new Thread(() -> {
                while (count <= 100) {
                    synchronized (lock) {
                        if (count <= 100) {
                            System.out.println(Thread.currentThread().getName() + ": " + count++);
                        }
                        lock.notify();
                        try {
                            if (count <= 100) {
                                lock.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, "线程2");

            t1.start();
            t2.start();
        }
}
