package net.programmer.igoodie.runtime.predicate.comparator;

import net.programmer.igoodie.exception.TSLSyntaxException;

public class PrefixComparator extends TSLComparator {

    public PrefixComparator(Object right) throws TSLSyntaxException {
        super(right);
    }

    @Override
    public boolean compare(Object left) {
        return left.toString().toLowerCase()
                .startsWith(right.toString().toLowerCase());
    }

}