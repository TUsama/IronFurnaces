package ironfurnaces.tileentity.furnaces.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class PartitionGroup {
    private final List<Partition> partitions;

    public PartitionGroup(Collection<Partition> partitions) {
        Objects.requireNonNull(partitions, "partitions");
        if (partitions.isEmpty()) {
            throw new IllegalArgumentException("PartitionGroup cannot be empty");
        }

        List<Partition> copy = new ArrayList<>(partitions.size());
        for (Partition partition : partitions) {
            copy.add(Objects.requireNonNull(partition, "partition is null"));
        }
        this.partitions = new ArrayList<>(copy);
    }

    public static PartitionGroup of(Partition... partitions) {
        Objects.requireNonNull(partitions, "partitions are null");
        return new PartitionGroup(Arrays.asList(partitions));
    }

    public PartitionGroup add(Partition partition){
        this.partitions.add(partition);
        return this;
    }

    public PartitionGroup addAll(Collection<Partition> partition){
        this.partitions.addAll(partition);
        return this;
    }

    public List<Partition> partitions() {
        return partitions;
    }

    public boolean contains(Partition partition) {
        return partitions.contains(partition);
    }
}
