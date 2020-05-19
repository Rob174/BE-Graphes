package org.insa.graphs.algorithm;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.insa.graphs.algorithm.AbstractInputData.Mode;
import org.insa.graphs.algorithm.shortestpath.AStarAlgorithm;
import org.insa.graphs.algorithm.shortestpath.BellmanFordAlgorithm;
import org.insa.graphs.algorithm.shortestpath.DijkstraAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathData;
import org.insa.graphs.algorithm.shortestpath.ShortestPathSolution;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.io.BinaryGraphReader;
import org.insa.graphs.model.io.GraphReader;
import org.insa.graphs.model.Path;

import org.insa.graphs.model.Graph;

public class TestsPerformances {
    public List<ShortestPathData> donnees;

    public static void parse_input(){
        ArrayList<ShortestPathData> donnees = new ArrayList<ShortestPathData>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("/home/robin/Documents/Cours/BE-Graphes/tests_performance/inputs.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Graph graph = open_graph(values[0]);
                int num_or = Integer.parseInt(values[1]), num_dest = Integer.parseInt(values[2]);
                Mode[] modes_rapidite = new Mode[]{Mode.LENGTH, Mode.TIME};
                Node origine,destination;
                //Préparation des cas limite où il manque un des noeuds
                if(num_or== -1)
                    origine = null;
                else
                    origine = graph.get(num_or);
                if(num_dest == -1)
                    destination = null;
                else
                    destination = graph.get(num_dest);
                for (int i = 0; i < 2; i++) {
                    Mode mode_rapidite = modes_rapidite[i];
                    //Choix des modes de transports autorisés
                    ArcInspector filtre_arc_autorises;
                    if(mode_rapidite == Mode.LENGTH)
                        filtre_arc_autorises = ArcInspectorFactory.getAllFilters().get(0);
                    else
                        filtre_arc_autorises = ArcInspectorFactory.getAllFilters().get(2);
                    ShortestPathData data = new ShortestPathData(graph, origine,destination, filtre_arc_autorises);
                    (new BellmanFordAlgorithm(data)).run();
                    (new DijkstraAlgorithm(data)).run();
                    (new AStarAlgorithm(data)).run();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static String[] graph_names = new String[]{"insa","haute-garonne","guadeloupe"};
    public static void process_random(){
        for (String name : graph_names) {
            Graph graph = open_graph(name);
            for (int tentative = 0; tentative < 100; tentative++) {
                int num_or = ThreadLocalRandom.current().nextInt(0, graph.size()), num_dest = ThreadLocalRandom.current().nextInt(0, graph.size());
                Mode[] modes_rapidite = new Mode[]{Mode.LENGTH, Mode.TIME};
                Node origine,destination;
                //Préparation des cas limite où il manque un des noeuds
                if(num_or== -1)
                    origine = null;
                else
                    origine = graph.get(num_or);
                if(num_dest == -1)
                    destination = null;
                else
                    destination = graph.get(num_dest);
                for (int i = 0; i < 2; i++) {
                    Mode mode_rapidite = modes_rapidite[i];
                    //Choix des modes de transports autorisés
                    ArcInspector filtre_arc_autorises;
                    if(mode_rapidite == Mode.LENGTH)
                        filtre_arc_autorises = ArcInspectorFactory.getAllFilters().get(0);
                    else
                        filtre_arc_autorises = ArcInspectorFactory.getAllFilters().get(2);
                    ShortestPathData data = new ShortestPathData(graph, origine,destination, filtre_arc_autorises);
                    
                    System.out.println("Echec bellman "+graph.getMapName()+", "+num_or+", "+num_dest+", en "+(i==0 ? "longueur" : "temps"));
                    (new BellmanFordAlgorithm(data)).run();
                    System.out.println("Echec dijkstra "+graph.getMapName()+", "+num_or+", "+num_dest+", en "+(i==0 ? "longueur" : "temps"));
                    (new DijkstraAlgorithm(data)).run();
                    System.out.println("Echec A* "+graph.getMapName()+", "+num_or+", "+num_dest+", en "+(i==0 ? "longueur" : "temps"));
                    (new AStarAlgorithm(data)).run();
                }
            }
        }
        
    }
    /**
     * @param chemin_absolu Absolute path to the map file
     * 
     * @return The object graph desired
     * 
     */
    public static Graph open_graph(String nom) {
        String chemin_absolu = "/home/robin/Documents/Cours/BE-Graphes/cartes/" + nom;
        chemin_absolu = chemin_absolu.contains(".") ? chemin_absolu : chemin_absolu + ".mapgr";
        Graph graph;
        final DataInputStream stream;
        try {
            stream = new DataInputStream(new BufferedInputStream(new FileInputStream(chemin_absolu)));
        } catch (IOException e1) {
            System.out.println("Problème d'ouverture du fichier " + chemin_absolu);
            e1.printStackTrace();
            return null;
        }
        GraphReader reader = new BinaryGraphReader(stream);
        try {
            graph = reader.read();
            reader.close();
        } catch (Exception exception) {
            System.out.println("Impossible de construire le graph");
            exception.printStackTrace(System.out);
            return null;
        }
        System.out.println("Graph " + chemin_absolu + " ouvert");
        return graph;
    }
}