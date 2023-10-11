package com.simplecounter;

import javafx.concurrent.Task;

public class counterTask extends Task<Long> {

    private final long limit;

    public counterTask(long limit) {
        this.limit = limit;
    }

    @Override
    protected Long call() throws Exception {
        long sum = 0, percent = 0;
        double percDivider = (double) limit / 100;
        for (int i = 0; i <= limit; i++) {
            if (isCancelled()) {
                updateValue(0L);
                break;
            }
            sum += i;
            percent = (long) (i / percDivider);
            updateValue(sum);
            updateProgress(i, limit);
            updateMessage(Long.toString(percent));
        }

        Thread.sleep(400);
        return sum;
    }
    
}
