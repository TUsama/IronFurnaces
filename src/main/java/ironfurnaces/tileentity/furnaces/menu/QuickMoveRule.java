package ironfurnaces.tileentity.furnaces.menu;

import java.util.function.Predicate;

public final class QuickMoveRule {
    private final Partition from;
    private final Partition to;
    private final Predicate<QuickMoveContext> contextPredicate;
    private final boolean reverse;

    public QuickMoveRule(
            Partition from,
            Partition to,
            Predicate<QuickMoveContext> contextPredicate,
            boolean reverse
    ) {
        this.from = from;
        this.to = to;
        this.contextPredicate = contextPredicate;
        this.reverse = reverse;
    }

    public Partition from() {
        return from;
    }

    public Partition to() {
        return to;
    }

    public boolean reverse() {
        return reverse;
    }

    public QuickMoveRule reversed(){
        return new QuickMoveRule(to, from, contextPredicate, reverse);
    }

    public boolean matches(QuickMoveContext context) {
        return this.from == context.sourcePartition() && this.contextPredicate.test(context);
    }
}
