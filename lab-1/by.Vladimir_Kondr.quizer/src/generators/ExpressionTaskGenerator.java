package generators;

import generators.math.AbstractMathTaskGenerator;
import tasks.ExpressionTask;
import tasks.math.MathTask;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;

public class ExpressionTaskGenerator extends AbstractMathTaskGenerator<ExpressionTask> {
    public ExpressionTaskGenerator(int minNumber, int maxNumber, EnumSet<MathTask.Operation> operations) {
        super(minNumber, maxNumber, operations);
    }

    public IllegalArgumentException isValid() {
        if (isOnlyDivision() && getMinNumber() == 0 && getMaxNumber() == 0) {
            return new IllegalArgumentException("Test will always have zero division");
        }
        return super.isValid();
    }

    protected Optional<ExpressionTask> tryGenerate(Random random) {
        try {
            ExpressionTask task = new ExpressionTask(getRandomNumber(random),
                    getRandomNumber(random),
                    (MathTask.Operation) operations.toArray()[random.nextInt(operations.size())]);
            return Optional.of(task);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}