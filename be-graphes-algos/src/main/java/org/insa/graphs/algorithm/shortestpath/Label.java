package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Node;

public class Label {
    private Node sommet_courant;
    private boolean marque;
    private double cout;
    private Node pere;
    private int id;
    public Label(Node sommet_courant, boolean marque, double cout, Node pere){
        this.sommet_courant = sommet_courant;
        this.marque = marque;
        this.cout = cout;
        this.pere = pere;
        this.id = sommet_courant.getId();
    }
    public double getCost() {
        return this.cout;
    }
}
