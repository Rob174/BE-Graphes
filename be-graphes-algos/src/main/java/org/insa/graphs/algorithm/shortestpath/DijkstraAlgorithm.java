package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.Node;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.*;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }
    public Label createLabel(Node sommet_courant, boolean marque, double cout, Arc pere,Node destination) {
    	return new Label(sommet_courant,marque,cout,pere);
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
    			labels[i] = this.createLabel(graph.getNodes().get(i),false,Double.POSITIVE_INFINITY,null,data.getDestination());
		}
        labels[data.getOrigin().getId()].setCost(0);
        tas.insert(labels[data.getOrigin().getId()]);
        this.notifyOriginProcessed(data.getOrigin());
        while (labels[data.getDestination().getId()].estMarque() == false) {
        	Label x = tas.deleteMin();
        	x.marquer();
        	System.out.println(x.getCurrentNode().getId()+" marqué avec cout "+x.getTotalCost());
        	notifyNodeMarked(x.getCurrentNode());
        	try
        	{
        		tas.remove(labels[x.getCurrentNode().getId()]);
				//tas.isValid();
        	}
        	catch(ElementNotFoundException e)
        	{
        		System.out.println("Cet élément n'existe pas");
        	}
        	for (Arc arc : x.getCurrentNode().getSuccessors()) {
        		Label successeur = labels[arc.getDestination().getId()];
        		double precCout = successeur.getTotalCost();
        		
                double w = data.getCost(arc);
                double oldDistance = successeur.getTotalCost();
                double newDistance = x.getCost()+w+successeur.getCoutEstime();
                
                if (Double.isInfinite(oldDistance) && Double.isFinite(newDistance)) {
                    notifyNodeReached(arc.getDestination());
                }
				if(data.isAllowed(arc)==true && successeur.estMarque()==false &&  oldDistance > newDistance) {
		        	
					successeur.setCost(x.getCost()+w);
					successeur.setFather(arc);
					if(precCout != Double.POSITIVE_INFINITY) {
						tas.remove(successeur);
						tas.insert(successeur);
						//tas.isValid();
						
					}
					else {
						tas.insert(successeur);
						//tas.isValid();
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
            ArrayList<Node>  nodes_path = new ArrayList<Node>();
            System.out.println("********/////");
            while (arc != null) {
            	if(nodes_path.contains(arc.getDestination())==false) {
            		nodes_path.add(arc.getDestination());
            	}
            	if(nodes_path.contains(arc.getOrigin())==false) {
            		nodes_path.add(arc.getOrigin());
            	}
            	
                arcs.add(arc);
                arc = labels[arc.getOrigin().getId()].getFather();
            }

            // Reverse the path...
            Collections.reverse(arcs);

            // Create the final solution.
            solution = new ShortestPathSolution(data, Status.OPTIMAL, new Path(graph, arcs));
			/*
			 * Path p = new Path(graph); Path s_d; System.out.println(" "); for(Node n :
			 * nodes_path) { System.out.println("indice : "+n.getId()); }
			 * System.out.println(" -*********"); try { s_d =
			 * Path.createFastestPathFromNodes(graph, nodes_path); if (s_d.isValid()) {
			 * System.out.println("Chemin ok"); }
			 * if(labels[data.getDestination().getId()].get != s_d.getLength()) {
			 * System.out.println("Le calcul : "+labels[data.getDestination().getId()].
			 * getTotalCost()+" et th : "+s_d.getLength()); } } catch (IllegalArgumentException
			 * e) { System.out.println("Erreur"); e.printStackTrace(); }
			 */
        }
        return solution;
    }

}
