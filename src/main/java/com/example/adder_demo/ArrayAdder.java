package com.example.adder_demo;

import java.lang.Thread;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
import java.util.Random;
import java.util.Arrays;

@SuppressWarnings("serial")
class ArrayAdder extends RecursiveTask<Integer>
{
    private static final int MIN_RECURSE_LEN = 5000;
    private final int[] arr;

    private int startIndex;
    private int endIndex;
    private boolean balanced;

    ArrayAdder(int[] arr, int startIndex, int endIndex, boolean balanced, boolean copy)
    {
        if (copy) {
            this.arr = Arrays.copyOf(arr, arr.length);
        } else {
            this.arr = arr;
        }
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.balanced = balanced;
    }

    ArrayAdder(int[] arr, int startIndex, int endIndex, boolean balanced)
    {
        this(arr, startIndex, endIndex, balanced, false);
    }

    ArrayAdder(int[] arr, int startIndex, int endIndex)
    {
        this(arr, startIndex, endIndex, true);
    }

    private Integer calculateSum() {
        if (startIndex >= endIndex) {
            return 0;
        }

        int sum = 0;
        int sliceLen = endIndex - startIndex;
        if (sliceLen <= MIN_RECURSE_LEN) {
            for (int i = startIndex; i < endIndex; i++) {
                sum += arr[i];
            }
        } else {
            int partition = 0;
            if (balanced) {
                partition = sliceLen / 2;
            } else {
                partition = new Random().nextInt(sliceLen);
            }
            
            ArrayAdder leftAdder = new ArrayAdder(arr, startIndex, startIndex + partition);
            int leftLen = (startIndex + partition) - startIndex;
            leftAdder.fork();
            ArrayAdder rightAdder = new ArrayAdder(arr, startIndex + partition, endIndex);
            int rightLen = endIndex - (startIndex + partition);
            sum = Integer.valueOf(rightAdder.compute() + leftAdder.join());
        }
        return sum;
    }

    private void loop(long milliseconds) {
        long startTime = System.nanoTime();
        int i = 0;
        while (((System.nanoTime() - startTime) / 1000000) < milliseconds) {
            i = i++ % 100;
        }
    }

    private void shortBusyLoop() {
        loop(3);
    }

    private void longBusyLoop() {
        loop(7);
    }

    @Override
    protected Integer compute() {
        // shortBusyLoop();
        Integer sum = calculateSum();
        // longBusyLoop();
        return sum;
    }
}