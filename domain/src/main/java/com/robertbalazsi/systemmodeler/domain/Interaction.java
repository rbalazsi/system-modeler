package com.robertbalazsi.systemmodeler.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Value object that defines an interaction between two entities.
 */
public class Interaction {

    /**
     * Defines the navigability of the interaction (uni- or bi-directional).
     */
    public enum Direction {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        TWO_WAY
    }

    @Getter
    @Setter
    private Direction direction;

    @Getter
    @Setter
    private Role leftSide;

    @Getter
    @Setter
    private Role rightSide;

    public Interaction(Direction direction, Role leftSide, Role rightSide) {
        this.direction = direction;
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Interaction)) return false;

        Interaction interaction = (Interaction) o;

        return new EqualsBuilder().append(direction, interaction.direction)
                .append(leftSide, interaction.leftSide)
                .append(rightSide, interaction.rightSide).isEquals();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(direction).append(leftSide).append(rightSide).hashCode();
    }
}
