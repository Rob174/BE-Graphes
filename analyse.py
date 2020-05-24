import matplotlib.pyplot as plt
import numpy as np
import re
import os
import matplotlib.pyplot as plt
import numpy as np
import traceback
from mpl_toolkits.mplot3d import Axes3D
""" 
On sait : 
- Complexité Bellman > Dijkstra > A* --> doit se voir avec le temps d'exécution
- Pas de préjugés sur le reste

On veut observer : 
- Une efficacité croissante en fonction de la distance pour A* par rapport aux autres
- Un cout à peu près proportionnel à la distance à vol d'oiseau
- L'évolution des autres paramètres en fonction de la distance des noeuds

"""
liste_trajets = []
class Trajet:
    def __init__(self,trajet,distance):
        self.trajet = trajet
        self.distance = distance
        self.algo = {"bellmanFord":None,"dijikstra":None,"astar":None}
    def check_all_set(self):
        for k,v in self.algo.items():
            if v == None:
                return False
        return True
    def insert_res(self,algo,mode,resultat):
        if self.algo[algo] != None:
            self.algo[algo].insert_res(mode,resultat)
        else:
            self.algo[algo] = Algorithme(algo)
            self.algo[algo].insert_res(mode,resultat)
        resultat.algo = self.algo[algo]
        resultat.trajet = self
    def get_val(self,algo,mode,value):
        try:
            ret = self.distance,self.algo[algo].modes[mode].get_val(value)
            # print("ok d=",self.distance)
            return ret
        except Exception as e:
            pass
            # print("Error ",e," : not found continue...")
            # traceback.print_exc()
        return None
class Algorithme:
    def __init__(self,type):
        self.type = type
        self.modes = {"longueur":None,"temps":None}
    def insert_res(self,mode,resultat):
        if self.modes[mode] != None:
            self.modes[mode].insert_res(resultat)
        else:
            self.modes[mode] = Mode(mode)
            self.modes[mode].insert_res(resultat)
        resultat.mode = self.modes[mode]
class Mode:
    def __init__(self,mode):
        self.mode = mode
        self.resultats = [] 
    def insert_res(self,resultat):
        self.resultats.append(resultat)
    def get_val(self,val):
        if len(self.resultats) == 0:
            raise Exception("No value")
        elif len(self.resultats) == 1:
            return self.resultats[0].valeurs[val]
        else:
            Lval = []
            for r in self.resultats:
                Lval.append(r.valeurs[val])
            return np.mean(Lval)
class Resultat:
    def __init__(self,cout,temps_calc,nb_noeuds_explores,nb_noeuds_marques,taille_max_tas):
        self.valeurs = {"cout":cout,
                        "temps_calc":temps_calc,
                        "nb_noeuds_explores":nb_noeuds_explores,
                        "nb_noeuds_marques":nb_noeuds_marques,
                        "taille_max_tas":taille_max_tas,
                        "algo":None,
                        "mode":None,
                        "trajet": None
                        }
def search_trajet(nom_trajet):
    for i,o in enumerate(liste_trajets):
        if o.trajet == nom_trajet:
            return i
    return False
#Met les données en RAM
with open("/home/robin/Documents/Cours/BE-Graphes/tests_performance/output.csv","r") as f:
    tests = f.readlines()
#Parse les données
for test in tests:
    nom_algo,carte,mode,origine,destination,cout,temps_calc,nb_noeuds_explores,nb_noeuds_marques,taille_max_tas,distance = test.split(",")
    #print(distance)
    nom_trajet = carte+","+origine+","+destination
    resultat = Resultat(float(cout),int(temps_calc),int(nb_noeuds_explores),int(nb_noeuds_marques),int(taille_max_tas))
    found = search_trajet(nom_trajet)
    if type(found)==bool:
        liste_trajets.append(Trajet(nom_trajet,float(distance.strip())))
        liste_trajets[-1].insert_res(nom_algo,mode,resultat)
    else:
        liste_trajets[found].insert_res(nom_algo,mode,resultat)

## Analyse les données
fig = plt.figure()
ax_cout_distance = fig.add_subplot(111, xlabel="distance_chemin", ylabel="temps_calc")
ax_cout_temps = ax_cout_distance.twinx()
for mode,ax,marker,loc_leg in zip(["longueur","temps"],[ax_cout_distance,ax_cout_temps],["o","v"],["upper left","lower right"]):
    for algo in ["bellmanFord","dijikstra","astar"]:
        Lpts_cout = [traj.get_val(algo,mode,"cout") for traj in liste_trajets]
        x_coord = [pt[0] for pt in Lpts_cout if pt != None]
        y_coord = [pt[1] for pt in Lpts_cout if pt != None]
        print(algo," : \t",np.mean(y_coord),"\t",np.std(y_coord),"\t",np.min(y_coord),"\t",np.max(y_coord))
        ax.plot(x_coord,y_coord,label=algo+" cout "+mode,linestyle='',marker=marker,alpha=0.5)

        deltaX = (max(x_coord)-min(x_coord))/10.
        deltaY = (max(y_coord)-min(y_coord))/10.

        x_tend = np.array([deltaX/100.*i for i in range(10*100)])
        z = np.polyfit(x_coord, y_coord, 1)
        p = np.poly1d(z)
        ax.plot(x_tend,p(x_tend),linestyle="--",label="tendance "+algo+" cout "+mode+" : "+"y=%.6fx+%.6f"%(z[0],z[1]))
        ax.set_xticks([int(deltaX*i) for i in range(10)])
        ax.set_yticks([int(deltaY*i) for i in range(10)])
        ax.set_xticklabels([int(deltaX*i) for i in range(10)])
        ax.set_yticklabels([int(deltaY*i) for i in range(10)])
        ax.set_xlim(min(x_coord),max(x_coord))
        ax.set_ylim(min(y_coord),max(y_coord))
        ax.set_ylabel("cout en "+mode)
        ax.legend(loc=loc_leg)
plt.show()