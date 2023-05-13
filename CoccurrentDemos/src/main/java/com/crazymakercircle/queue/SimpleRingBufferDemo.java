package com.crazymakercircle.queue;


public class SimpleRingBufferDemo {
    public static void main(String[] args) {

        //创建一个环形队列
        SimpleRingBuffer queue = new SimpleRingBuffer(4);
        queue.offer(11);
        queue.offer(12);
        queue.offer(13);
        queue.offer(14);
        System.out.println("queue = " + queue);
        int temp = queue.poll();
        System.out.println("temp = " + temp);
        System.out.println("queue = " + queue);
        temp = queue.poll();
        System.out.println("temp = " + temp);
        System.out.println("queue = " + queue);
        temp = queue.poll();
        System.out.println("temp = " + temp);
        System.out.println("queue = " + queue);
    }

}

class SimpleRingBuffer {
    private int maxSize;//表示数组的最大容量
    private int head;  // 模拟 缓存之王 Caffeine 源码命名
    //head就指向队列的第一个元素，也就是arr[head]就是队列的第一个元素
    //head的初始值=0
    private int tail; // 模拟 缓存之王 Caffeine 源码命名
    //tail指向队列的最后一个元素的后一个位置，因为希望空出一个空间做为约定
    //tail的初始化值=0
    private int[] buffer;//该数据用于存放数据

    public SimpleRingBuffer(int arrMaxSize) {
        maxSize = arrMaxSize;
        buffer = new int[maxSize];
    }

    //判断队列是否满
    public boolean isFull() {
        return (tail + 1) % maxSize == head;
    }

    //判断队列是否为空
    public boolean isEmpty() {
        return tail == head;
    }

    //添加数据到队列
    public void offer(int n) {
        //判断队列是否满
        if (isFull()) {
            System.out.println("队列满，不能加入数据");
            return;
        }
        //直接将数据加入
        buffer[tail] = n;
        //将tail后移，这里必须考虑取模
        tail = (tail + 1) % maxSize;
    }

    //获取队列的数据，出队列
    public int poll() {
        //判断队列是否空
        if (isEmpty()) {
            //通过抛出异常
            throw new RuntimeException("队列空，不能取数据");
        }
        //这里需要分析出head是指向队列的第一个元素
        //1.先把head对应的值保留到一个临时变量
        //2.将head后移，考虑取模
        //3.将临时保存的变量返回
        int value = buffer[head];
        head = (head + 1) % maxSize;
        return value;
    }

    //求出当前队列有效数据的个数
    public int size() {
        return (tail + maxSize - head) % maxSize;
    }

    @Override
    public String toString() {
       return   String.format("head=%d , tail =%d\n",head,tail);

    }
}