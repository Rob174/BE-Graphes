package org.insa.graphs.algorithm;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.insa.graphs.algorithm.AbstractInputData.Mode;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.io.BinaryGraphReader;
import org.insa.graphs.model.io.GraphReader;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.IllegalArgumentException;
import org.insa.graphs.algorithm.shortestpath.ShortestPathData;
import org.insa.graphs.algorithm.shortestpath.ShortestPathSolution;
import org.insa.graphs.algorithm.shortestpath.DijkstraAlgorithm;
import org.insa.graphs.algorithm.shortestpath.BellmanFordAlgorithm;
import org.insa.graphs.algorithm.shortestpath.AStarAlgorithm;

public class AlgorithmTest {
    protected static final double PRECISION = 0.001;
    
    protected Graph graph;
    protected int origine;
    protected int destination;
    public enum TypeAlgorithme {Bellman, Dijkstra, AStar;}

    /**
     * @param chemin_absolu Absolute path to the map file
     * 
     * @return The object graph desired
     * 
     */
    public static Graph open_graph(String chemin_absolu){
        Graph graph;
        final DataInputStream stream;
        try {
            stream = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(chemin_absolu)));
        }
        catch (IOException e1) {
            System.out.println("Problème d'ouverture du fichier "+chemin_absolu);
            e1.printStackTrace();
            return null;
        }
        GraphReader reader = new BinaryGraphReader(stream);
        try {
            graph = reader.read();
            reader.close();
        }
        catch (Exception exception) {
            System.out.println("Impossible de construire le graph");
            exception.printStackTrace(System.out);
            return null;
        }
        return graph;
    }
    /**
     * Execute the algorithm algorithme to find a path between the node num_or and num_dest with the selected criterion mode_rapidite
     * Compare the result with the theory
     * @param graph Graph containing the nodes in the list.
     * @param num_or id of the origin node.
     * @param num_dest id of the destination node.
     * @param mode_rapidite Mode.TIME or Mode.LENGTH
     * @param algorithme algorithm chosen to solve the problem
     * 
     * @return A double reprsenting the difference of time/distance depending of the mode selected. -1 if no path is possible
     * @throws IllegalArgumentException If the list of nodes created by the algorithm is not valid, i.e. two consecutive nodes in the list are not connected in the graph
     * @throws NullPointerException If there is no origin or no destination node
     * 
     */
    public double cheminPratique(Graph graph,int num_or,int num_dest, Mode mode_rapidite, TypeAlgorithme algorithme ) throws IllegalArgumentException, NullPointerException {
        Node origine,destination;
        if(num_or == -1)
            origine = null;
        else
            origine = graph.get(num_or);
        if(num_dest == -1)
            destination = null;
        else
            destination = graph.get(num_dest);
        ArcInspector filtre_arc_autorises;
        if(mode_rapidite == Mode.LENGTH)
            filtre_arc_autorises = ArcInspectorFactory.getAllFilters().get(0);
        else
            filtre_arc_autorises = ArcInspectorFactory.getAllFilters().get(2);
        ShortestPathData data = new ShortestPathData(graph, origine,destination, filtre_arc_autorises);
        ShortestPathSolution solution;
        if(algorithme == TypeAlgorithme.Bellman)
            solution = (new BellmanFordAlgorithm(data)).run();
        else if(algorithme == TypeAlgorithme.Dijkstra)
            solution = (new DijkstraAlgorithm(data)).run();
        else if(algorithme == TypeAlgorithme.AStar)
            solution = (new AStarAlgorithm(data)).run();
        if(solution.isFeasible() == false)
            return -1;
        Path chemin_choisi = solution.getPath();
        ArrayList<Node>  nodes_path = new ArrayList<Node>();
        nodes_path.add(data.getOrigin());
        for (Arc arc : chemin_choisi.getArcs()) {
            nodes_path.add(arc.getDestination());
        }
        Path chemin_theorique;
        if(mode_rapidite == Mode.LENGTH)
            chemin_theorique = Path.createShortestPathFromNodes(graph, nodes_path); 
        else 
            chemin_theorique = Path.createFastestPathFromNodes(graph, nodes_path); 
        return (double)(Math.abs(chemin_choisi.getLength()-chemin_theorique.getLength()));
    }
}