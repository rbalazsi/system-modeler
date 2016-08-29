package com.robertbalazsi.systemmodeler.model;

import com.robertbalazsi.systemmodeler.diagram.Visual;
import com.robertbalazsi.systemmodeler.domain.Interaction;

/**
 * An interaction between two component or two sub-systems.
 */
public class VisualizedInteraction {

    private Interaction interaction;
    private Visual visual;

    public VisualizedInteraction(Interaction interaction, Visual visual) {
        this.interaction = interaction;
        this.visual = visual;
    }
}
