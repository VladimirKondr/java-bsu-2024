package tasks.math;

import core.Result;

public abstract class AbstractMathTask implements MathTask {
    protected final Operation op;
    protected final int left;
    protected final int right;
    protected final int answer;

    protected AbstractMathTask(Operation op, int left, int right) {
        this.op = op;
        this.left = left;
        this.right = right;
        this.answer = computeAnswer();
        boolean e = isValid();
        if (!e) {
            throw new IllegalArgumentException("Task is invalid");
        }
    }

    /**
     * Проверяет ответ на задание и возвращает результат
     *
     * @param  answer ответ на задание
     * @return        результат ответа
     * @see           Result
     */
    public Result validate(String answer) {
        try {
            int ans = Integer.parseInt(answer);
            return this.answer == ans ? Result.OK : Result.WRONG;
        } catch (NumberFormatException e) {
            return Result.INCORRECT_INPUT;
        }
    }

    protected abstract int computeAnswer();
}