package generators.math;

import exceptions.NumberGenerationException;
import tasks.math.MathTask;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;

abstract public class AbstractMathTaskGenerator<T extends MathTask> implements MathTaskGenerator<T> {

    protected final int minNumber;
    protected final int maxNumber;
    protected final EnumSet<MathTask.Operation> operations;
    protected final int tries = 1000;

    /**
     * @param minNumber минимальное число
     * @param maxNumber максимальное число
     */
    protected AbstractMathTaskGenerator(int minNumber, int maxNumber, EnumSet<MathTask.Operation> operations) {
        this.minNumber = minNumber;
        this.maxNumber = maxNumber;
        this.operations = operations;
        IllegalArgumentException e = isValid();
        if (e != null) {
            throw e;
        }
    }


    public int getMinNumber() {
        return this.minNumber;
    }

    public int getMaxNumber() {
        return this.maxNumber;
    }

    public IllegalArgumentException isValid() {
        if (getMinNumber() > getMaxNumber()) {
            return new IllegalArgumentException("Min value is greater than Max value");
        }
        return null;
    }

    protected int getRandomNumber(Random rand) {
        return rand.nextInt(getDiffNumber() + 1) + getMinNumber();
    }

    public boolean isOnlyDivision() {
        return operations.contains(MathTask.Operation.DIVISION) && operations.size() == 1;
    }

    protected abstract Optional<T> tryGenerate(Random random);

    public T generate() {
        Random random = new Random();
        for (int i = 0; i < tries; i++) {
            Optional<T> task = tryGenerate(random);
            if (task.isPresent()) {
                return task.get();
            }
        }
        throw new NumberGenerationException("Unable to generate a task that meets the criteria after 1000 attempts");
    }
}
