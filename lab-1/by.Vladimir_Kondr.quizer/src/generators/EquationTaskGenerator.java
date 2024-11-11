package generators;

import generators.math.AbstractMathTaskGenerator;
import tasks.EquationTask;
import tasks.math.MathTask;


import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;

public class EquationTaskGenerator extends AbstractMathTaskGenerator<EquationTask> {
    public EquationTaskGenerator(int minNumber, int maxNumber, EnumSet<MathTask.Operation> operations) {
        super(minNumber, maxNumber, operations);
    }

    protected Optional<EquationTask> tryGenerate(Random random) {
        try {
            EquationTask task = new EquationTask(getRandomNumber(random),
                    (MathTask.Operation) operations.toArray()[random.nextInt(operations.size())],
                    getRandomNumber(random),
                    random.nextBoolean());
            return Optional.of(task);
        } catch (IllegalArgumentException _) {
            return Optional.empty();
        }
    }
}
