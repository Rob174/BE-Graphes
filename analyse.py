import matplotlib.pyplot as plt
import numpy as np
import re
import os
import matplotlib.pyplot as plt
import numpy as np
import traceback
from mpl_toolkits.mplot3d import Axes3D
from scipy.stats import pearsonr
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
    def get_val(self,algo,mode,value,info_trajet=False):
        try:
            ret = self.distance,self.algo[algo].modes[mode].get_val(value)
            # print("ok d=",self.distance)
            return ret if info_trajet==False else self.trajet,ret[1]
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
x_lab,y_lab = "cout","nb_noeuds_explores"
ax_cout_temps = fig.add_subplot(111, xlabel=x_lab, ylabel=y_lab)
mode,ax,marker,loc_leg = "longueur",ax_cout_temps,"v","lower right"
print("         \t\tMean\t\t\tStd\t\tMin\t\tMax\t\t\tR²")
for algo in ["dijikstra","astar"]:
    condition = lambda x:"INSA" in x.trajet
    x_coord = [traj.get_val(algo,mode,x_lab) for traj in liste_trajets if condition(traj)]
    y_coord = [traj.get_val(algo,mode,y_lab) for traj in liste_trajets if condition(traj)]

    condition = lambda x:x!= None
    x_coord = [pt[1] for pt in x_coord if condition(pt)]
    y_coord = [pt[1] for pt in y_coord if condition(pt)]
    print(algo," : \t",np.mean(y_coord),"\t",np.std(y_coord),"\t",np.min(y_coord),"\t",np.max(y_coord),"\t",np.corrcoef(x_coord,y_coord)[0,1])
    ax.plot(x_coord,y_coord,label=algo+" "+y_lab+" "+mode,linestyle='',marker=marker,alpha=0.5)
    sortedx,sortedy = np.sort(x_coord),np.sort(y_coord)
    minx,maxx,miny,maxy = sortedx[0],sortedx[-1],sortedy[0],sortedy[-10]
    deltaX = (maxx-minx)/10.
    deltaY = (maxy-miny)/10.

    # x_tend = np.array([deltaX/100.*i for i in range(10*100)])
    # z = np.polyfit(x_coord, y_coord, 1)
    # p = np.poly1d(z)
    # ax.plot(x_tend,p(x_tend),linestyle="--",label="tendance "+algo+" "+y_lab+" "+mode+" : "+"y=%.6fx+%.6f"%(z[0],z[1]))
    # ax.set_xticks([int(deltaX*i) for i in range(10)])
    # ax.set_yticks([int(deltaY*i) for i in range(10)])
    # ax.set_xticklabels([int(deltaX*i) for i in range(10)])
    # ax.set_yticklabels([int(deltaY*i) for i in range(10)])
    # ax.set_xlim(minx,maxx)
    # ax.set_ylim(miny,maxy)
    ax.set_ylabel(y_lab+" en mode "+mode)
    ax.legend(loc=loc_leg)
plt.show()
def dij_better(traj):
    if "Haute" not in traj.trajet:
        return None
    astar = traj.get_val("dijikstra","longueur",y_lab,True)
    dij = traj.get_val("astar","longueur",y_lab,True)
    if astar == None or dij == None or astar[1] > dij[1]:
        return None
    else:
        return  dij[0],dij[1],astar[1]
dij_plus_perf_traj = list(filter(lambda x:x!=None,list(map(dij_better,liste_trajets))))
if len(dij_plus_perf_traj) > 0:
    Lsorted = sorted(dij_plus_perf_traj,key=lambda x:x[2]-x[1])
    for traj,val_dij,val_astar in dij_plus_perf_traj:
        print("Trajet : ",traj," dij ",val_dij," astar ",val_astar," diff ",val_astar-val_dij)

    print("Trajet : ",Lsorted[-1][0]," dij ",Lsorted[-1][1]," astar ",Lsorted[-1][2]," diff ",Lsorted[-1][2]-Lsorted[-1][1])
else:
    print("Pas meilleur")