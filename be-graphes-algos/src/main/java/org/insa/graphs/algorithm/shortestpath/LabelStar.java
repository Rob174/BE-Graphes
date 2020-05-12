package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.algorithm.shortestpath.Label;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class LabelStar extends Label{
	private double coutEstime;
	public LabelStar(Node sommet_courant, boolean marque, double cout, double coutEstime, Arc pere, Node destination) {
		super(sommet_courant, marque, cout, pere);
		this.coutEstime = coutEstime;/*A modifier en temps si mode temps*/
	}
    @Override
    public double getTotalCost() {
    	return this.cout+this.coutEstime;
    }
    @Override
	public double getCoutEstime() {
		return this.coutEstime;
	}
}
