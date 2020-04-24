package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Path;
import org.insa.graphs.algorithm.AbstractInputData;
import org.insa.graphs.algorithm.AbstractSolution.Status;
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
        	Label x = tas.deleteMin();
        	x.marquer();
        	notifyNodeMarked(x.getCurrentNode());
        	if(x.getCurrentNode().getId()==data.getOrigin().getId()) {
                // Notify observers about the first event (origin processed).
                notifyOriginProcessed(data.getOrigin());
        	}
        	
        	for (Arc arc : x.getCurrentNode().getSuccessors()) {
        		Label successeur = labels[arc.getDestination().getId()];
        		double precCout = successeur.getCost();

                // Retrieve weight of the arc.
                double w = data.getCost(arc);
                double oldDistance = successeur.getCost();
                double newDistance = x.getCost()+data.getCost(arc);
                
                if (Double.isInfinite(oldDistance) && Double.isFinite(newDistance)) {
                    notifyNodeReached(arc.getDestination());
                }
				if(data.isAllowed(arc)==true && successeur.estMarque()==false &&  oldDistance > newDistance) {
		        	
					successeur.setCost(Math.min(oldDistance, newDistance));
					successeur.setFather(arc);
					if(precCout != Double.POSITIVE_INFINITY) {
						tas.remove(successeur);
						tas.insert(successeur);
						
					}
					else {
						tas.insert(successeur);
					}
				}
			}
        	if(labels[data.getDestination().getId()].estMarque() == true) {
                notifyNodeReached(x.getFather().getDestination());
        	}
        }

        // Destination has no predecessor, the solution is infeasible...
        if (labels[data.getDestination().getId()] == null) {
            solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {

            // The destination has been found, notify the observers.
            notifyDestinationReached(data.getDestination());

            // Create the path from the array of predecessors...
            ArrayList<Arc> arcs = new ArrayList<>();
            Arc arc = labels[data.getDestination().getId()].getFather();
            while (arc != null) {
                arcs.add(arc);
                arc = labels[arc.getOrigin().getId()].getFather();
            }

            // Reverse the path...
            Collections.reverse(arcs);

            // Create the final solution.
            solution = new ShortestPathSolution(data, Status.OPTIMAL, new Path(graph, arcs));
        }

        return solution;
    }

}
