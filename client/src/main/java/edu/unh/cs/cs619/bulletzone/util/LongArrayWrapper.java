package edu.unh.cs.cs619.bulletzone.util;

public class LongArrayWrapper {
    private Long[] result;

    public LongArrayWrapper() {}

    public LongArrayWrapper(Long[] result) {
        this.result = result;
    }

    public Long[] getResult() {
        return result;
    }

    public void setResult(Long[] result) {
        this.result = result;
    }
}
