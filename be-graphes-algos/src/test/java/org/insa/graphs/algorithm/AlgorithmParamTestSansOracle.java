package org.insa.graphs.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
public class AlgorithmParamTestSansOracle extends AlgorithmTest {
    private static ArrayList<Graph> graph_map;
    private static String[] graphs_names = new String[] { "carre", "insa", "haute-garonne", "guadeloupe" };
    private int nb_noeuds;
    private int NB_PTS_INTERM = 25;

    @Parameters(name = "{index}: Carte {0}, de {1} vers {2}")
    public static Collection<Object[]> data() {
        graph_map = new ArrayList<Graph>();
        for (String name : graphs_names) {
            Graph g = open_graph("/home/robin/Documents/Cours/BE-Graphes/cartes/" + name + ".mapgr");
            graph_map.add(g);
        }
        return Arrays.asList(new Object[][] { { 0, 5, 4 }, // Carte carrée
                { 1, 259, 814 }, // Carte insa
                { 1, 1052, 656 }, 
                { 2, 55947, 100306 }, // Carte haute-garonne
                { 2, 153997, 101286 }, 
                { 2, 55947, 56506 }, 
                { 2, 60630, 144268 }, 
                { 3, 29816, 14715 }, // Carte guadeloupe
                { 3, 8942, 12531 }, 
                { 3, 32670, 33072 }, 
                { 3, 33068, 27536 }, 
                { 3, 26278, 27536 } });
    }

    public AlgorithmParamTestSansOracle(int index_graph, int origine, int destination) {
        this.graph = graph_map.get(index_graph);
        this.nb_noeuds = graph_map.size();
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
    public void test(Graph graph,int num_or,int num_dest, Mode mode_rapidite, TypeAlgorithme algorithme){
        for (int i = 0; i < NB_PTS_INTERM; i++) {
            int pt_interm = ThreadLocalRandom.current().nextInt(0, this.nb_noeuds);
            AlgorithmTest.ExecutionAlgo direct,partiel_1,partiel_2;
            try {
                direct = execution_algo(graph, origine, destination, Mode.LENGTH, algorithme);
            } catch (NullPointerException e) {
                continue;
            } catch (Exception e) {
                e.printStackTrace();
                fail("Unexpected Exception : "+e.toString());
                continue;
            }
            try {
                partiel_1 = execution_algo(graph, origine, pt_interm, Mode.LENGTH, algorithme);
            } catch (NullPointerException e) {
                continue;
            } catch (Exception e) {
                e.printStackTrace();
                fail("Unexpected Exception : "+e.toString());
                continue;
            }
            try {
                partiel_2 = execution_algo(graph, pt_interm,destination , Mode.LENGTH, algorithme);
            } catch (NullPointerException e) {
                continue;
            } catch (Exception e) {
                e.printStackTrace();
                fail("Unexpected Exception : "+e.toString());
                continue;
            }
            if(direct.solution().isFeasible()==false || partiel_1.solution().isFeasible()==false || partiel_2.solution().isFeasible()==false)
                continue;
            double cout_direct = direct.solution().getPath().getLength();
            double cout_indirect = partiel_1.solution().getPath().getLength()+partiel_2.solution().getPath().getLength();
            if((cout_direct-cout_indirect>AlgorithmTest.PRECISION)){
                if(mode_rapidite==Mode.LENGTH)
                    fail("Le chemin "+num_or+" -> "+pt_interm+" -> "+num_dest+" est plus court en longueur ("+cout_indirect+") que "+num_or+" -> "+num_dest+" ("+cout_direct+")");
                else
                    fail("Le chemin "+num_or+" -> "+pt_interm+" -> "+num_dest+" est plus court en longueur ("+(cout_indirect/graph.getGraphInformation().getMaximumSpeed())+" que "+num_or+" -> "+num_dest+" ("+(cout_direct/graph.getGraphInformation().getMaximumSpeed())+")");
            }
        }
    }
    @Test
    public void testDijkstraShortestSansOracle() {
        test(graph, origine, destination, Mode.LENGTH, TypeAlgorithme.Dijkstra);
    }

    @Test
    public void testDijkstraFastestSansOracle() {
        test(graph, origine, destination, Mode.TIME, TypeAlgorithme.Dijkstra);
    }

    @Test
    public void testAStarShortestSansOracle() {
        test(graph, origine, destination, Mode.LENGTH, TypeAlgorithme.AStar);
    }
    @Test
    public void testAStarFastestSansOracle(){
        test(graph, origine, destination, Mode.TIME, TypeAlgorithme.AStar);
    }
    
}
