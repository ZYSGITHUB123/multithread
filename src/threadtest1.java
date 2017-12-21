

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

//启动线程的两个方法 extends Thread ,重载run（）方法；implements Runnable(),实现run（）方法
class MyThread extends Thread{                 //这里是继承
    private int tid;
    public MyThread(int tid){
        this.tid=tid;
    }

    @Override
    public void run() {
        try{
          for(int i=0;i<10;i++){
              Thread.sleep(1000);
              System.out.println(String.format("T%d:%d",tid,i));
          }
        }catch(Exception e){
           e.printStackTrace();
        }

    }
}

class Producer implements Runnable{                 //同步队列的测试
    private BlockingQueue<String> q;
    public Producer(BlockingQueue<String> q){
        this.q=q;
    }

    @Override
    public void run() {
        try{
            for(int i=0;i<10;i++){
                Thread.sleep(1000);
                q.put(String.valueOf(i));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
class Consumer implements Runnable{
    private BlockingQueue<String> q;
    public Consumer(BlockingQueue<String> q){
        this.q=q;
    }

    @Override
    public void run() {
        try{
           while(true){
               System.out.println(Thread.currentThread().getName() + ":" + q.take());
           }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
public class threadtest1 {
    public static void threadtest11(){
       for(int i=0;i<10;i++){                      //启动十个线程
          // new MyThread(i).start();
       }
     for(int i=0;i<10;i++){                               //另一个方法开启十个线程
           final int tid=i;
           new Thread(new Runnable() {                   //这里用了匿名类实现runnable接口开启线程
               @Override                                 //且开启要用大的线程名开启
               public void run() {
                   try{
                   for(int i=0;i<10;i++){
                       Thread.sleep(1000);
                       System.out.println(String.format("T2%d,%d",tid,i));

                   }
                   }catch(Exception e){
                       e.printStackTrace();
                   }
               }
           }).start();
     }
    }


    private static Object obj=new Object();
    public static void testSynchronized1(){
        synchronized (obj){                                 //同步锁，这里锁的同一个对象
            try{                                            //可以用来锁对象，也可以用来锁方法
                for(int i=0;i<10;i++){
                    Thread.sleep(1000);
                    System.out.println(String.format("T3%d",i));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }
    public static void testSynchronized2(){
        synchronized (obj){                    //内置同步锁，这里锁的同一个对象
            try{
                for(int i=0;i<10;i++){
                    Thread.sleep(1000);
                    System.out.println(String.format("T4%d",i));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    private static int counter = 0;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    public static void sleep(int mills) {
        try {                                 //原子不会让多个线程重复写入一个值
            //Thread.sleep(new Random().nextInt(mills));
            Thread.sleep(mills);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void testWithAtomic() {  //
        for (int i = 0; i < 10; ++i) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sleep(1000);
                    for (int j = 0; j < 10; ++j) {
                        System.out.println(atomicInteger.incrementAndGet());
                    }
                }
            }).start();
        }
    }

    public static void testWithoutAtomic() {
        for (int i = 0; i < 10; ++i) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sleep(1000);
                    for (int j = 0; j < 10; ++j) {
                        counter++;
                        System.out.println(counter);
                    }
                }
            }).start();
        }
    }
    private static ThreadLocal<Integer> threadLocalUserIds = new ThreadLocal<>();
    private static int userId;

    public static void testThreadLocal() {   //这里是一个本地局部变量，每个线程取到的值不一样
        for (int i = 0; i < 10; ++i) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    threadLocalUserIds.set(finalI);
                    sleep(1000);
                    System.out.println("ThreadLocal: " + threadLocalUserIds.get());
                }
            }).start();
        }

        for (int i = 0; i < 10; ++i) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    userId = finalI;
                    sleep(1000);
                    System.out.println("NonThreadLocal: " + userId);
                }
            }).start();
        }
    }
    public static void testExecutor() {            //这里有一个线程池的概念，可以处理一种并发的线程
        //ExecutorService service = Executors.newSingleThreadExecutor();
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(new Runnable() {                //executorservice是一个任务框架
            @Override                                  //线程池任务框架
            public void run() {
                for (int i = 0; i < 10; ++i) {
                    sleep(1000);
                    System.out.println("Execute1 " + i);
                }
            }
        });

        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; ++i) {
                    sleep(1000);
                    System.out.println("Execute2 " + i);
                }
            }
        });

        service.shutdown();
        while (!service.isTerminated()) {
            sleep(1000);
            System.out.println("Wait for termination.");
        }
    }
    public static void testAtomic() {
        testWithAtomic();
        testWithoutAtomic();
    }
    public static void testSynchronized(){          //同步锁测试
        for(int i=0;i<10;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    testSynchronized1();
                    testSynchronized2();
                }
            }).start();
        }
    }

    public static void testBlockingQueue(){
        BlockingQueue<String> q=new ArrayBlockingQueue<String>(10);
       new Thread (new Producer(q)).start();
        new Thread (new Consumer(q),"consumer1").start();
        new Thread (new Consumer(q),"consumer2").start();
    }
    public static void testFutrue() {                  //通过future框架可以获取未来可能返回的值
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                sleep(1000);
                return 1;
                //throw new IllegalArgumentException("异常");
            }
        });

        service.shutdown();                       //这是一个异步框架
                                                  //可以进行捕获
        try {
            //System.out.println(future.get());
            System.out.println(future.get(100, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
           threadtest11();
        //testSynchronized();
       // testBlockingQueue();
       // testAtomic();
        //testThreadLocal();
        // testExecutor();
        //testFutrue();
    }
}
