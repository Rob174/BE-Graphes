package org.insa.graphs.algorithm.shortestpath;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.insa.graphs.algorithm.AbstractAlgorithm;
import org.insa.graphs.algorithm.AbstractInputData.Mode;
import org.insa.graphs.model.Node;

public abstract class ShortestPathAlgorithm extends AbstractAlgorithm<ShortestPathObserver> {
    public class Resultat {
        public double cout = -1;// lire sur le dernier Label
        public long temps_calcul = 0;
        public int nb_noeuds_explores = 0;
        public int nb_noeuds_marques = 0;
        public int taille_max_tas = 0;
        public double distance_vol_oiseau = 0;
        public double distance_max_marque = 0;
        public double distance_max_explo = 0;
        public String toString(){
            return "cout : "+this.cout+" temps_calcul : "+this.temps_calcul+" nb_noeuds_explores : "+this.nb_noeuds_explores+" nb_noeuds_marques : "+this.nb_noeuds_marques+" taille_max_tas : "+this.taille_max_tas+" distance_vol_oiseau : "+this.distance_vol_oiseau;
        }
    }

    public Resultat resultat = new Resultat();
    protected String nom;

    protected ShortestPathAlgorithm(ShortestPathData data) {
        super(data);
    }

    void write_results(ShortestPathData data) {
        try {
            FileWriter writer = new FileWriter("/home/robin/Documents/Cours/BE-Graphes/tests_performance/output_detaillee.csv", true);
            List<String> liste = Arrays.asList(new String[] {
                                nom,
                                data.getGraph().getMapName(),
                                data.getMode() == Mode.LENGTH ? "longueur" : "temps",
                                String.valueOf(data.getOrigin().getId()),
                                String.valueOf(data.getDestination().getId()),
                                String.valueOf(resultat.cout),
                                String.valueOf(resultat.temps_calcul),
                                String.valueOf(resultat.nb_noeuds_explores),
                                String.valueOf(resultat.nb_noeuds_marques),
                                String.valueOf(resultat.taille_max_tas),
                                String.valueOf(resultat.distance_vol_oiseau),
                                String.valueOf(resultat.distance_max_marque),
                                String.valueOf(resultat.distance_max_explo)

            });
            String ligne = String.join(",", liste);
            writer.append(ligne+"\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Erreur d'ouverture du fichier de sauvegarde");
            e.printStackTrace();
        }
        
    }
    @Override
    public ShortestPathSolution run() {
        return (ShortestPathSolution) super.run();
    }

    @Override
    protected abstract ShortestPathSolution doRun();
    

    @Override
    public ShortestPathData getInputData() {
        return (ShortestPathData) super.getInputData();
    }

    /**
     * Notify all observers that the origin has been processed.
     * 
     * @param node Origin.
     */
    public void notifyOriginProcessed(Node node) {
        for (ShortestPathObserver obs: getObservers()) {
            obs.notifyOriginProcessed(node);
        }
    }

    /**
     * Notify all observers that a node has been reached for the first time.
     * 
     * @param node Node that has been reached.
     */
    public void notifyNodeReached(Node node) {
        for (ShortestPathObserver obs: getObservers()) {
            obs.notifyNodeReached(node);
        }
    }

    /**
     * Notify all observers that a node has been marked, i.e. its final value has
     * been set.
     * 
     * @param node Node that has been marked.
     */
    public void notifyNodeMarked(Node node) {
        for (ShortestPathObserver obs: getObservers()) {
            obs.notifyNodeMarked(node);
        }
    }

    /**
     * Notify all observers that the destination has been reached.
     * 
     * @param node Destination.
     */
    public void notifyDestinationReached(Node node) {
        for (ShortestPathObserver obs: getObservers()) {
            obs.notifyDestinationReached(node);
        }
    }
}
