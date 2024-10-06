package net.programmer.igoodie.node.predicate.comparator;

import net.programmer.igoodie.exception.TSLSyntaxException;

public class LteComparator extends TSLComparator {

    public LteComparator(Object right) throws TSLSyntaxException {
        super(right);
        this.right = this.tryParseNumber(right);
    }

    @Override
    public boolean compare(Object left) {
        if (left instanceof Number && right instanceof Number)
            return ((Number) left).doubleValue() <= ((Number) right).doubleValue();

        return false;
    }

}
