package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Node;
import org.insa.graphs.model.Arc;

public class Label implements Comparable<Label>{
	protected Node sommet_courant;
    protected boolean marque;
    protected double cout;
    protected Arc pere;
    protected int id;
    public Label(Node sommet_courant, boolean marque, double cout, Arc pere){
        this.sommet_courant = sommet_courant;
        this.marque = marque;
        this.cout = cout;
        this.pere = pere;
        this.id = sommet_courant.getId();
    }
    public double getCost() {
        return this.cout;
    }
    public double getTotalCost() {
    	return this.cout;
    }
    public void setCost(double cout) {
    	this.cout = cout;
    }
    public boolean estMarque() {
    	return this.marque;
    }
	public double getCoutEstime() {
		return 0;
	}
    public void marquer() {
    	this.marque = true;
    }
    public Node getCurrentNode() {
    	return this.sommet_courant;
    }
    public void setFather(Arc a) {
    	this.pere = a;
    }
    public Arc getFather() {
    	return this.pere;
    }
    public int compareTo(Label otherLabel) {
    	if(this.getTotalCost() < otherLabel.getTotalCost())
    		return -1;
    	else if(this.getTotalCost() > otherLabel.getTotalCost())
    		return 1;
    	else {
    		if(this.getCost()<otherLabel.getCost())
    			return -1;
    		else if(this.getCost()>otherLabel.getCost()) 
    			return 1;
    		return 0;
    	}
    }
}
