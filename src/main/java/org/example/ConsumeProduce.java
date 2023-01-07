package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConsumeProduce {

    static Queue<Integer> dataQueue = new LinkedList();
    static ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);


    public static void main(String[] args) {

        Thread producer = new Thread(new Producer());
        producer.start();

        for (int i = 0; i < 100; i++) {
            Thread consumer = new Thread(new Consumer());
            consumer.setName("Consumer-"+i);
            consumer.start();
        }
    }

    public static class Producer implements Runnable{

        @Override
        public void run() {
            while (true) {
                readWriteLock.writeLock().lock();
                if(dataQueue.size() < 10) {
                    System.out.println("Produce Entered");
                    dataQueue.offer(4);
                    System.out.println("Produce Exited");

                }
                readWriteLock.writeLock().unlock();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public static class Consumer implements Runnable{

        @Override
        public void run() {
            while (true) {
                readWriteLock.readLock().lock();
                if(dataQueue.size() ==10) {
                    dataQueue.poll();
                    System.out.println("Consumer["+Thread.currentThread().getName()+"] taking... ( size "+dataQueue.size()+")");
                }
                readWriteLock.readLock().unlock();

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}