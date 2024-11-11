package tasks.math;

import core.Task;

public interface MathTask extends Task {
    boolean isValid();


    enum Operation {
        ADDITION("+"),
        SUBTRACTION("-"),
        MULTIPLICATION("*"),
        DIVISION("/");

        private final String symbol;
        private static final double accuracy = 2;
        public static double pow10() {
            return Math.pow(10.0, accuracy);
        }

        public static double getAccuracy() {
            return accuracy;
        }

        Operation(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        public int perform(Integer a, Integer b) {
            return switch (this) {
                case ADDITION -> (int) ((a + b) * pow10());
                case SUBTRACTION -> (int) ((a - b) * pow10());
                case MULTIPLICATION -> (int) (a * b * pow10());
                case DIVISION -> {
                    if (b == 0) {
                        throw new ArithmeticException("Division by zero is not allowed");
                    }
                    yield (int) (((double) a / (double) b) * pow10());
                }
            };
        }

        public Operation getOpposite () {
            return switch (this) {
                case ADDITION -> Operation.SUBTRACTION;
                case SUBTRACTION -> Operation.ADDITION;
                case MULTIPLICATION -> Operation.DIVISION;
                case DIVISION -> Operation.MULTIPLICATION;
            };
        }
    }
}
