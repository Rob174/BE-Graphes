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
import org.insa.graphs.algorithm.AbstractInputData.Mode;
public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
		super(data);
		this.nom = "dijikstra";
    }
    public Label createLabel(Node sommet_courant, boolean marque, double cout, double coutEstime, Arc pere,Node destination) {
    	return new Label(sommet_courant,marque,cout,pere);
	}
	 
    @Override
    protected ShortestPathSolution doRun() throws NullPointerException {
		final ShortestPathData data = getInputData();
		if(data.getOrigin().equals(null))
			throw new NullPointerException("Il manque l'origine du trajet");
		if(data.getDestination().equals(null))
			throw new NullPointerException("Il manque la destination du trajet");
		
		Graph graph = data.getGraph();
        ShortestPathSolution solution = null;
        final int nbNodes = graph.size();
		BinaryHeap<Label> tas = new BinaryHeap<Label>();
        
		long debut = System.nanoTime();
		resultat.distance_vol_oiseau = data.getOrigin().getPoint().distanceTo(data.getDestination().getPoint());
        // Initialize array of distances.
        Label[] labels = new Label[nbNodes];
    	if(data.getMode() == Mode.LENGTH) {
    		System.out.println("Mode distance");
    		for (int i = 0; i < nbNodes; i++) {
				double distance = graph.getNodes().get(i).getPoint().distanceTo(data.getDestination().getPoint());
				if(Double.isNaN(distance)==true)//Si bug de la méthode distanceTo on écarte le chemin et le met comme infaisable
					return new ShortestPathSolution(data, Status.INFEASIBLE);
    			labels[i] = this.createLabel(graph.getNodes().get(i),
    										false,
    										Double.POSITIVE_INFINITY,
    										distance,
    										null,
    										data.getDestination());
    		}
    	}
    	else {
    		System.out.println("Mode temps");
    		for (int i = 0; i < nbNodes; i++) {
				double distance = graph.getNodes().get(i).getPoint().distanceTo(data.getDestination().getPoint());
				if(Double.isNaN(distance)==true)//Si bug de la méthode distanceTo on écarte le chemin et le met comme infaisable
					return new ShortestPathSolution(data, Status.INFEASIBLE);
        		labels[i] = this.createLabel(graph.getNodes().get(i),
						false,
						Double.POSITIVE_INFINITY,
						distance/graph.getGraphInformation().getMaximumSpeed(),
						null,
						data.getDestination());
    		}        		
		}
        labels[data.getOrigin().getId()].setCost(0);
        tas.insert(labels[data.getOrigin().getId()]);
		this.notifyOriginProcessed(data.getOrigin());
		boolean arret = false;
        while (labels[data.getDestination().getId()].estMarque() == false && arret == false) {
        	Label x;
        	try {
        		x = tas.deleteMin();
        		//System.out.println(x.getCurrentNode().getId());
        	}
        	catch(EmptyPriorityQueueException e) {
        		System.out.println("Pas de chemin possible");
        		arret = true;
        		break;
        	}
			x.marquer();
			resultat.nb_noeuds_marques++;
			//Elements pour les tests de performance
			double distance_dest = Math.min(x.getCurrentNode().getPoint().distanceTo(data.getDestination().getPoint()),
									 		x.getCurrentNode().getPoint().distanceTo(data.getOrigin().getPoint()));
			resultat.distance_max_marque = resultat.distance_max_marque > distance_dest ? resultat.distance_max_marque : distance_dest;
			resultat.taille_max_tas = Math.max(tas.getCurrentSize(),resultat.taille_max_tas);
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
			
        	for (Arc arc : x.getCurrentNode().getSuccessors()) {
        		Label successeur = labels[arc.getDestination().getId()];
        		double precCout = successeur.getTotalCost();
        		
                double w = data.getCost(arc);
                double oldDistance = successeur.getCost();
                double newDistance = x.getCost()+w;
                
                if (Double.isInfinite(oldDistance) && Double.isFinite(newDistance)) {
					notifyNodeReached(arc.getDestination());
					resultat.nb_noeuds_explores++;
					distance_dest = Math.min(x.getCurrentNode().getPoint().distanceTo(data.getDestination().getPoint()),
											 x.getCurrentNode().getPoint().distanceTo(data.getOrigin().getPoint()));
	    			resultat.distance_max_explo = resultat.distance_max_explo > distance_dest ? resultat.distance_max_explo : distance_dest;
					
                }
				if(data.isAllowed(arc)==true && successeur.estMarque()==false &&  oldDistance > newDistance) {
		        	//si le successeur est accessible et non marqué
					successeur.setCost(x.getCost()+w);
					successeur.setFather(arc);
					if(precCout != Double.POSITIVE_INFINITY) {
						//Si on avait déjà exploré le noeud, on actualise le Label du tas
						tas.remove(successeur);
						tas.insert(successeur);
						//tas.isValid();
						
					}
					else {
						//Sinon on l'insert
						tas.insert(successeur);
						//tas.isValid();
					}
				}
			}
        	if(labels[data.getDestination().getId()].estMarque() == true) {
                notifyNodeReached(x.getFather().getDestination());
			}
		}
		resultat.temps_calcul = System.nanoTime()-debut;
        // Destination has no predecessor, the solution is infeasible...
        if (labels[data.getDestination().getId()].getFather() == null || arret == true) {
            solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {

            // The destination has been found, notify the observers.
            notifyDestinationReached(data.getDestination());
			resultat.cout = labels[data.getDestination().getId()].getCost();
			write_results(data);
			// Create the path from the array of predecessors...
			if(data.getOrigin().getId()==data.getDestination().getId())
				return new ShortestPathSolution(data, Status.OPTIMAL, new Path(graph, data.getOrigin()));
            ArrayList<Arc> arcs = new ArrayList<>();
            Arc arc = labels[data.getDestination().getId()].getFather();
			ArrayList<Node>  nodes_path = new ArrayList<Node>();
			nodes_path.add(data.getOrigin());
            while (arc != null) {
            	if(nodes_path.contains(arc.getDestination())==false) {
            		nodes_path.add(arc.getDestination());
            	}
            	
                arcs.add(arc);
                arc = labels[arc.getOrigin().getId()].getFather();
            }

            // Reverse the path...
            Collections.reverse(arcs);
            Collections.reverse(nodes_path);

            // Create the final solution.
            solution = new ShortestPathSolution(data, Status.OPTIMAL, new Path(graph, arcs));			
		}
		System.out.println(resultat.toString());
        return solution;
    }

}
