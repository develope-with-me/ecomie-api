package org.csbf.ecomie.utils.commons;

public interface Thing extends Identifiable {

    default String identifier() {
        return id().toString();
    }

    String name();

    String description();

    String alternateName();
}
