package com.robertbalazsi.systemmodeler.domain;

import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Defines the multiplicity of a role in an interaction.
 */
public final class Multiplicity {

    public static final int UNBOUNDED = Integer.MAX_VALUE;

    private static final String UNBOUNDED_SYMBOL = "*";

    @Getter
    private int from;

    @Getter
    private int to;

    private Multiplicity() {
        /* hidden */
    }

    public static Multiplicity of(int from, int to) {
        Multiplicity multiplicity = new Multiplicity();
        if (from < 0 || to < 0) {
            throw new IllegalArgumentException("Bounds must be positive.");
        }
        multiplicity.from = from;
        multiplicity.to = to;

        return multiplicity;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(from).append(to).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null || !getClass().equals(obj.getClass())) &&
                new EqualsBuilder().append(from, ((Multiplicity) obj).from).append(to, ((Multiplicity) obj).to).isEquals();
    }

    @Override
    public String toString() {
        String fromStr = from != UNBOUNDED ? Integer.toString(from) : UNBOUNDED_SYMBOL;
        String toStr  = to != UNBOUNDED ? Integer.toString(to) : UNBOUNDED_SYMBOL;
        return String.format("%s..%s", fromStr, toStr);
    }
}
