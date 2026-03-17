package ironfurnaces.tileentity.furnaces.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class QuickMoveRuleBuilder {
    private final List<QuickMoveRule> rules = new ArrayList<>();

    private QuickMoveRuleBuilder() {
    }

    public static QuickMoveRuleBuilder create() {
        return new QuickMoveRuleBuilder();
    }

    public RuleStage rule() {
        return new RuleStage(this);
    }

    public List<QuickMoveRule> build() {
        return List.copyOf(this.rules);
    }

    private void addRule(QuickMoveRule rule) {
        this.rules.add(rule);
    }

    public static final class RuleStage {
        private final QuickMoveRuleBuilder parent;
        private Predicate<QuickMoveContext> predicate = ctx -> true;
        private boolean reverse = false;

        private RuleStage(QuickMoveRuleBuilder parent) {
            this.parent = parent;
        }

        public RuleStage when(Predicate<QuickMoveContext> predicate) {
            this.predicate = Objects.requireNonNull(predicate, "predicate");
            return this;
        }

        public RuleStage and(Predicate<QuickMoveContext> predicate) {
            Objects.requireNonNull(predicate, "predicate");
            this.predicate = this.predicate.and(predicate);
            return this;
        }

        public RuleStage reverse(boolean reverse) {
            this.reverse = reverse;
            return this;
        }

        /**
         * 单向：from -> to
         */
        public QuickMoveRuleBuilder oneWay(Partition from, Partition to) {
            parent.addRule(new QuickMoveRule(
                    Objects.requireNonNull(from, "from"),
                    Objects.requireNonNull(to, "to"),
                    predicate,
                    reverse
            ));
            return parent;
        }

        /**
         * 双向：a <-> b
         */
        public QuickMoveRuleBuilder bidirectional(Partition a, Partition b) {
            Objects.requireNonNull(a, "a");
            Objects.requireNonNull(b, "b");

            parent.addRule(new QuickMoveRule(a, b, predicate, reverse));
            parent.addRule(new QuickMoveRule(b, a, predicate, reverse));
            return parent;
        }

        /**
         * 单向：group 中每个分区 -> to
         */
        public QuickMoveRuleBuilder oneWay(PartitionGroup fromGroup, Partition to) {
            Objects.requireNonNull(fromGroup, "fromGroup");
            Objects.requireNonNull(to, "to");

            for (Partition from : fromGroup.partitions()) {
                parent.addRule(new QuickMoveRule(from, to, predicate, reverse));
            }
            return parent;
        }

        /**
         * 单向：from -> group 中每个分区
         */
        public QuickMoveRuleBuilder oneWay(Partition from, PartitionGroup toGroup) {
            Objects.requireNonNull(from, "from");
            Objects.requireNonNull(toGroup, "toGroup");

            for (Partition to : toGroup.partitions()) {
                parent.addRule(new QuickMoveRule(from, to, predicate, reverse));
            }
            return parent;
        }

        public QuickMoveRuleBuilder oneWay(PartitionGroup fromGroup, PartitionGroup toGroup) {
            Objects.requireNonNull(fromGroup, "fromGroup");
            Objects.requireNonNull(toGroup, "toGroup");

            for (Partition from : fromGroup.partitions()) {
                for (Partition to : toGroup.partitions()) {
                    parent.addRule(new QuickMoveRule(from, to, predicate, reverse));
                }
            }
            return parent;
        }

        /**
         * 双向：group 中每个分区 <-> target
         */
        public QuickMoveRuleBuilder bidirectional(PartitionGroup group, Partition target) {
            Objects.requireNonNull(group, "group");
            Objects.requireNonNull(target, "target");

            for (Partition partition : group.partitions()) {
                parent.addRule(new QuickMoveRule(partition, target, predicate, reverse));
                parent.addRule(new QuickMoveRule(target, partition, predicate, reverse));
            }
            return parent;
        }

        /**
         * 双向：source <-> group 中每个分区
         */
        public QuickMoveRuleBuilder bidirectional(Partition source, PartitionGroup group) {
            Objects.requireNonNull(source, "source");
            Objects.requireNonNull(group, "group");

            for (Partition partition : group.partitions()) {
                parent.addRule(new QuickMoveRule(source, partition, predicate, reverse));
                parent.addRule(new QuickMoveRule(partition, source, predicate, reverse));
            }
            return parent;
        }

        /**
         * 双向：aGroup 和 bGroup 两两互通
         */
        public QuickMoveRuleBuilder bidirectional(PartitionGroup aGroup, PartitionGroup bGroup) {
            Objects.requireNonNull(aGroup, "aGroup");
            Objects.requireNonNull(bGroup, "bGroup");

            for (Partition a : aGroup.partitions()) {
                for (Partition b : bGroup.partitions()) {
                    parent.addRule(new QuickMoveRule(a, b, predicate, reverse));
                    parent.addRule(new QuickMoveRule(b, a, predicate, reverse));
                }
            }
            return parent;
        }

        /**
         * 玩家组 -> target
         */
        public QuickMoveRuleBuilder playerTo(PartitionGroup playerGroup, Partition target) {
            return oneWay(playerGroup, target);
        }

        /**
         * source -> 玩家组
         */
        public QuickMoveRuleBuilder toPlayer(Partition source, PartitionGroup playerGroup) {
            return oneWay(source, playerGroup);
        }

        /**
         * 玩家组 <-> target
         */
        public QuickMoveRuleBuilder playerBidirectional(PartitionGroup playerGroup, Partition target) {
            return bidirectional(playerGroup, target);
        }

        /**
         * 玩家组 <-> targetGroup
         */
        public QuickMoveRuleBuilder playerBidirectional(PartitionGroup playerGroup, PartitionGroup targetGroup) {
            return bidirectional(playerGroup, targetGroup);
        }
    }
}
