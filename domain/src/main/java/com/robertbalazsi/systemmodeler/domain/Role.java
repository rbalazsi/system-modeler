package com.robertbalazsi.systemmodeler.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Defines the role of an entity in an interaction. It consists of its name and multiplicity.
 */
public class Role {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Multiplicity multiplicity;

    public Role(String name, Multiplicity multiplicity) {
        this.name = name;
        this.multiplicity = multiplicity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;

        Role role = (Role) o;

        return new EqualsBuilder().append(name, role.name).append(multiplicity, role.multiplicity).isEquals();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(multiplicity).hashCode();
    }
}
