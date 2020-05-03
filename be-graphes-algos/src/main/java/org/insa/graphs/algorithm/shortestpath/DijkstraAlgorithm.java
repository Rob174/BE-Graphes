package org.insa.graphs.algorithm.shortestpath;


import java.util.ArrayList;
import java.util.Collections;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.Node;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.*;
import org.insa.graphs.algorithm.shortestpath.Fraction;

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
		long nb_moyen_parc = 0;
		Fraction tmp_num = new Fraction((long)0,(long)1);
		long nb_noeuds_tot = data.getGraph().size();
		long nb = 0;
		boolean arret = false;
        while (labels[data.getDestination().getId()].estMarque() == false && arret == false) {
        	Label x;
        	try {
        		x = tas.deleteMin();
        		System.out.println(x.getCurrentNode().getId());
        	}
        	catch(EmptyPriorityQueueException e) {
        		System.out.println("Pas de chemin possible");
        		arret = true;
        		break;
        	}
        	x.marquer();
        	notifyNodeMarked(x.getCurrentNode());
        	try
        	{
        		tas.remove(labels[x.getCurrentNode().getId()]);
				//tas.isValid();
        	}
        	catch(ElementNotFoundException e)
        	{
        		//System.out.println("Cet élément n'existe pas");
			}
			Fraction moy_crte = new Fraction((long)0,(long)(x.getCurrentNode().getSuccessors().size()));
        	for (Arc arc : x.getCurrentNode().getSuccessors()) {
        		Label successeur = labels[arc.getDestination().getId()];
        		double precCout = successeur.getTotalCost();
        		
                double w = data.getCost(arc);
                double oldDistance = successeur.getTotalCost();
                double newDistance = x.getCost()+w+successeur.getCoutEstime();
                
                if (Double.isInfinite(oldDistance) && Double.isFinite(newDistance)) {
					notifyNodeReached(arc.getDestination());
					moy_crte.incr_num();
					nb++;
                }
				if(data.isAllowed(arc)==true && successeur.estMarque()==false &&  oldDistance > newDistance) {
		        	
					successeur.setCost(x.getCost()+w);
					successeur.setFather(arc);
					if(precCout != Double.POSITIVE_INFINITY) {
						tas.remove(successeur);
						tas.insert(successeur);
						tas.isValid();
						
					}
					else {
						tas.insert(successeur);
						tas.isValid();
					}
				}
			}
        	if(moy_crte.get_denom()!=0)
			{
        		tmp_num.add(moy_crte);
    		}
        	if(labels[data.getDestination().getId()].estMarque() == true) {
                notifyNodeReached(x.getFather().getDestination());
			}
			nb_moyen_parc++;
        }
        // Destination has no predecessor, the solution is infeasible...
        if (labels[data.getDestination().getId()] == null || arret == true) {
            solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {

            double res = tmp_num.calcul()/(double)(nb_moyen_parc);
    		System.out.println("Total en moy "+res+" prct");
    		System.out.println("Total du total "+((double)(nb)/(double)(nb_noeuds_tot))+" prct");
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
            Collections.reverse(nodes_path);

            // Create the final solution.
            solution = new ShortestPathSolution(data, Status.OPTIMAL, new Path(graph, arcs));
			
			Path p = new Path(graph); 
			
			Path s_d;
			try { 
				s_d = Path.createFastestPathFromNodes(graph, nodes_path); 
				if (s_d.isValid()) {
					System.out.println("Chemin ok"); 
					}
				if(labels[data.getDestination().getId()].getCost() != s_d.getLength()) {
					System.out.println("Le calcul : "+labels[data.getDestination().getId()].getTotalCost()+" et th : "+s_d.getLength()); 
					} 
				} 
			catch (org.insa.graphs.model.IllegalArgumentException e) 
			{
				System.out.println("Erreur"); e.printStackTrace();
			}
			
        }
        return solution;
    }

}
