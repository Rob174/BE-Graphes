package org.insa.graphs.algorithm.shortestpath;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
        this.nom = "astar";
    }
    @Override
    public LabelStar createLabel(Node sommet_courant, boolean marque, double cout, double coutEstime, Arc pere, Node destination) {
    	return new LabelStar(sommet_courant,marque,cout,coutEstime,pere,destination);
    }
}
