package generators;

import core.Task;
import core.TaskGenerator;
import exceptions.EndOfPoolException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

public class PoolTaskGenerator implements TaskGenerator<Task> {
    private final boolean allowDuplicate;
    private final ArrayList<Task> tasks;
    public PoolTaskGenerator(
            boolean allowDuplicate,
            Task... tasks
    ) {
        this.allowDuplicate = allowDuplicate;
        this.tasks = new ArrayList<>(Arrays.asList(tasks));
    }

    public PoolTaskGenerator(
            boolean allowDuplicate,
            Collection<Task> tasks
    ) {
        this.allowDuplicate = allowDuplicate;
        this.tasks = (ArrayList<Task>) tasks;
    }

    public int getSize() {
        return tasks.size();
    }

    public boolean getAllowDuplicates() {
        return allowDuplicate;
    }

    @Override
    public Task generate() {
        Random random = new Random();
        if (tasks.isEmpty()) {
            throw new EndOfPoolException("Generation of task is impossible, pool of tasks is empty");
        }
        int index = random.nextInt(tasks.size());
        Task task = tasks.get(index);
        if (!allowDuplicate) {
            tasks.remove(index);
        }
        return task;
    }
}