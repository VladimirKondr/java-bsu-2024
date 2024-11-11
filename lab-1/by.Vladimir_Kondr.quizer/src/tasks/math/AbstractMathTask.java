package tasks.math;

public abstract class AbstractMathTask implements MathTask {
    protected final Operation op;
    protected final int left;
    protected final int right;


    protected AbstractMathTask() {
        this.op = null;
        this.left = 0;
        this.right = 0;
    }

    protected AbstractMathTask(Operation op, int left, int right) {
        this.op = op;
        this.left = left;
        this.right = right;
        boolean e = isValid();
        if (!e) {
            throw new IllegalArgumentException("Task is invalid");
        }

    }

    protected abstract int computeAnswer();
}