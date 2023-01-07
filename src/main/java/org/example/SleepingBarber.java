package org.example;

import org.w3c.dom.ranges.Range;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SleepingBarber {
    public static void main(String[] args) throws InterruptedException {
        Barber barber = new Barber();
        new Thread(barber).start();


        for(int i =0; i< 100 ; i++){
            Thread customer = new Thread(new Customer(barber,i));
            customer.setName("Customer "+i);
            customer.start();
        }
    }

    public static class Customer implements Runnable{
        public final int counter;
        Barber b;
        Semaphore canGoNow = new Semaphore(0);
        public Customer(Barber barber,int counter) {
            this.counter = counter;
            b = barber;
        }


        @Override
        public void run() {
            try {
                while(true){

                    if(b.enterShop(this)){
                        System.out.println("Customer "+Thread.currentThread().getName()+" is entered to the shop");

                        canGoNow.acquire();
                        System.out.println("Customer "+Thread.currentThread().getName()+" is going out");
                        break;
                    }else{
                        int walkAway = new Random().nextInt(50);
                        //System.out.println("Customer "+Thread.currentThread().getName()+" will come back after " + walkAway + " ms");
                        Thread.sleep(walkAway);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static class Barber implements Runnable{
        Queue<Customer> customerSeats = new LinkedList<>();

        public Barber(){}

        @Override
        public void run() {
            while(true){
                Customer customer;
                synchronized (customerSeats) {
                    customer = customerSeats.poll();
                }
                if(customer != null){
                    int workmSecs = new Random().nextInt(500);
                    System.out.println("Barber is working for "+workmSecs+" m.seconds on customer "+customer.counter);
                    try {
                        Thread.sleep(workmSecs);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    customer.canGoNow.release();
                    System.out.println("Barber is free");
                }else{
                    try {
                        System.out.println("Barber is sleeping");
                        Thread.sleep(new Random().nextInt(5) * 1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        private void PrintSeatsWithCustomers() {
            System.out.print("Seats : ");
            customerSeats.stream().forEach(el->{
                System.out.print(el.counter+" ");
            });
            System.out.println();
        }

        public boolean enterShop(Customer c) throws InterruptedException {
            synchronized (customerSeats) {
                if (customerSeats.size() == 4) {
                    return false;
                } else {
                    customerSeats.offer(c);
                    PrintSeatsWithCustomers();
                    return true;
                }
            }
        }
    }
}
