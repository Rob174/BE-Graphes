package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;

public class BellmanFordAlgorithm extends ShortestPathAlgorithm {

    public BellmanFordAlgorithm(ShortestPathData data) {
        super(data);
        this.nom = "bellmanFord";
    }

    @Override
    protected ShortestPathSolution doRun() {

        // Retrieve the graph.
        ShortestPathData data = getInputData();
        Graph graph = data.getGraph();

        final int nbNodes = graph.size();
        resultat.distance_vol_oiseau = data.getOrigin().getPoint().distanceTo(data.getDestination().getPoint());
		long debut = System.nanoTime();
        resultat.nb_noeuds_marques = -1;//il n'y a pas de notion de marquage ici
        resultat.taille_max_tas = -1;//et pas de notion de tas
        // Initialize array of distances.
        double[] distances = new double[nbNodes];
        Arrays.fill(distances, Double.POSITIVE_INFINITY);
        distances[data.getOrigin().getId()] = 0;

        // Notify observers about the first event (origin processed).
        notifyOriginProcessed(data.getOrigin());

        // Initialize array of predecessors.
        Arc[] predecessorArcs = new Arc[nbNodes];

        // Actual algorithm, we will assume the graph does not contain negative
        // cycle...
        boolean found = false;
        for (int i = 0; !found && i < nbNodes; ++i) {
            found = true;
            for (Node node: graph.getNodes()) {
                for (Arc arc: node.getSuccessors()) {

                    // Small test to check allowed roads...
                    if (!data.isAllowed(arc)) {
                        continue;
                    }

                    // Retrieve weight of the arc.
                    double w = data.getCost(arc);
                    double oldDistance = distances[arc.getDestination().getId()];
                    double newDistance = distances[node.getId()] + w;

                    if (Double.isInfinite(oldDistance) && Double.isFinite(newDistance)) {
                        notifyNodeReached(arc.getDestination());
                        resultat.nb_noeuds_explores++;
                        double distance_dest = Math.min(arc.getDestination().getPoint().distanceTo(data.getDestination().getPoint()),
                                                        arc.getDestination().getPoint().distanceTo(data.getOrigin().getPoint()));
			            resultat.distance_max_marque = resultat.distance_max_marque > distance_dest ? resultat.distance_max_marque : distance_dest;
                    }

                    // Check if new distances would be better, if so update...
                    if (newDistance < oldDistance) {
                        found = false;
                        distances[arc.getDestination().getId()] = distances[node.getId()] + w;
                        predecessorArcs[arc.getDestination().getId()] = arc;
                    }
                }
            }
        }
		resultat.temps_calcul = System.nanoTime()-debut;

        ShortestPathSolution solution = null;

        // Destination has no predecessor, the solution is infeasible...
        if (predecessorArcs[data.getDestination().getId()] == null) {
            solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {

            // The destination has been found, notify the observers.
            notifyDestinationReached(data.getDestination());
			resultat.cout = distances[data.getDestination().getId()];
			write_results(data);

            // Create the path from the array of predecessors...
            ArrayList<Arc> arcs = new ArrayList<>();
            Arc arc = predecessorArcs[data.getDestination().getId()];
            while (arc != null) {
                arcs.add(arc);
                arc = predecessorArcs[arc.getOrigin().getId()];
            }

            // Reverse the path...
            Collections.reverse(arcs);

            // Create the final solution.
            solution = new ShortestPathSolution(data, Status.OPTIMAL, new Path(graph, arcs));
        }

        return solution;
    }

}
