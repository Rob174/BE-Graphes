package org.insa.graphs.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.insa.graphs.model.Graph;
import org.insa.graphs.model.IllegalArgumentException;
import org.junit.Test;
//Voir https://junit.org/junit4/javadoc/4.12/org/junit/runners/Parameterized.html pour le détail d'utilisation
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
import org.insa.graphs.algorithm.AbstractInputData.Mode;

@RunWith(Parameterized.class)
public class AlgorithmParamTest extends AlgorithmTest {
    private static ArrayList<Graph> graph_map;
    private static String[] graphs_names = new String[] { "carre", "insa", "haute-garonne", "guadeloupe" };

    @Parameters(name = "{index}: Carte {0}, de {1} vers {2}")
    public static Collection<Object[]> data() {
        graph_map = new ArrayList<Graph>();
        for (String name : graphs_names) {
            Graph g = open_graph("/home/robin/Documents/Cours/BE-Graphes/" + name + ".mapgr");
            graph_map.add(g);
        }
        return Arrays.asList(new Object[][] { { 0, 5, 4 }, // Carte carrée
                { 1, 259, 814 }, // Carte insa
                { 1, 1052, 656 }, 
                { 2, 55947, 100306 }, // Carte haute-garonne
                { 2, 153997, 101286 }, 
                { 2, 55947, 56506 }, 
                { 2, 60630, 144268 }, 
                { 3, 29816, 14715 }, // Carte
                                                                                                       // guadeloupe
                { 3, 8942, 12531 }, { 3, 32670, 33072 }, { 3, 33068, 27536 }, { 3, 26278, 27536 } });
    }

    public AlgorithmParamTest(int index_graph, int origine, int destination) {
        this.graph = graph_map.get(index_graph);
        this.origine = origine;
        this.destination = destination;
    }
    public void checkResults(List<Double> ecarts){ 
        String statut_path = "", statut_bellman = "";
        if (ecarts.get(0).doubleValue() > PRECISION) {
            statut_path = "Echec comparaison avec méthodes path : écart "+ecarts.get(0);
        }
        if (ecarts.get(1).doubleValue() > PRECISION) {
            statut_bellman = "Echec comparaison Bellman : écart "+ecarts.get(1);
        }
        if((ecarts.get(0).doubleValue() > PRECISION) || (ecarts.get(1).doubleValue() > PRECISION)){
            fail(statut_path+"\n"+statut_bellman+"\n avec une précision de " + PRECISION);
        }
    }
    @Test
    public void testDijkstraShortest() {
        List<Double> ecarts = Arrays.asList(new Double []{Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY});
        try {
            ecarts = cheminPratique(graph, origine, destination, Mode.LENGTH, TypeAlgorithme.Dijkstra);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception : "+e.toString());
        }
        checkResults(ecarts);
    }

    @Test
    public void testDijkstraFastest() {
        List<Double> ecarts = Arrays.asList(new Double []{Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY});
        try {
            ecarts = cheminPratique(graph, origine, destination, Mode.TIME, TypeAlgorithme.Dijkstra);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception : "+e.toString());
        } 
        checkResults(ecarts);
    }

    @Test
    public void testAStarShortest() {
        List<Double> ecarts = Arrays.asList(new Double []{Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY});
        try {
            ecarts = cheminPratique(graph, origine, destination, Mode.LENGTH, TypeAlgorithme.AStar);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception : "+e.toString());
        } 
        checkResults(ecarts);
    }
    @Test
    public void testAStarFastest(){
        List<Double> ecarts = Arrays.asList(new Double []{Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY});
        try {
            ecarts = cheminPratique(graph, origine, destination, Mode.TIME, TypeAlgorithme.AStar);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception : "+e.toString());
        } 
        checkResults(ecarts);
    }
    
}
