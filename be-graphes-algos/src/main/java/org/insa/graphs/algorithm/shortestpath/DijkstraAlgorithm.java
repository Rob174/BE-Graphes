package org.insa.graphs.algorithm.shortestpath;

import java.util.Arrays;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.algorithm.shortestpath.Label;
import org.insa.graphs.algorithm.utils.*;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun() {
        final ShortestPathData data = getInputData();
        Graph graph = data.getGraph();
        ShortestPathSolution solution = null;
        final int nbNodes = graph.size();
        BinaryHeap<Label> tas = new BinaryHeap<Label>();
        
        // Initialize array of distances.
        Label[] labels = new Label[nbNodes];
        for (int i = 0; i < nbNodes; i++) {
        	if(i==data.getOrigin().getId()) {
    			labels[i] = new Label(graph.getNodes().get(i),false,0,null);
    			tas.insert(labels[i]);
        	}
        	else {
    			labels[i] = new Label(graph.getNodes().get(i),false,Double.POSITIVE_INFINITY,null);
        	}
		}

        while (labels[data.getDestination().getId()].estMarque() == false) {
        	Label x = tas.findMin();
        	x.marquer();
        	for (Arc arc : x.getCurrentNode().getSuccessors()) {
        		Label successeur = labels[arc.getDestination().getId()];
				if(successeur.estMarque()==false && successeur.getCost() > x.getCost()+arc.getMinimumTravelTime()) {
					successeur.setCost(Math.min(successeur.getCost(), x.getCost()+arc.getMinimumTravelTime()));
					if(successeur.getCost() != Double.POSITIVE_INFINITY) {
						tas.remove(successeur);
						tas.insert(successeur);
					}
					else {
						tas.insert(successeur);
					}
				}
			}
        }
        
        return solution;
    }

}
