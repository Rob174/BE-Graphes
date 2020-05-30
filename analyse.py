import pandas as pd
import plotly.express as px
import numpy as np
import sys

# Barre de progression d'après https://gist.github.com/vladignatyev/06860ec2040cb497f0f3
def progress(count, total, status=''):
    bar_len = 60
    filled_len = int(round(bar_len * count / float(total)))

    percents = round(100.0 * count / float(total), 1)
    bar = '=' * filled_len + '-' * (bar_len - filled_len)

    sys.stdout.write('[%s] %s%s ...%s\r' % (bar, percents, '%', status))
    sys.stdout.flush()
converters = {"nom_algo":(lambda x:str(x)),
            "carte":(lambda x:str(x)),
            "mode":(lambda x:str(x)),
            "origine":(lambda x:np.int(x)),
            "destination":(lambda x:np.int(x)),
            "cout":(lambda x:np.float(x)),
            "temps_calc":(lambda x:float(np.int(int(x)*1e-6))),
            "nb_noeuds_explores":(lambda x:np.int(x)),
            "nb_noeuds_marques":(lambda x:np.int(x)),
            "taille_max_tas":(lambda x:np.int(x)),
            "distance":(lambda x:np.float(x)),
            "distance_max_marque":(lambda x:np.float(x)),
            "distance_max_explo":(lambda x:np.float(x))
            }
df = pd.DataFrame(columns=list(converters.keys()))

#Met les données en RAM
pd.read_csv("/home/robin/Documents/Cours/BE-Graphes/tests_performance/output_detaillee.csv",
            sep=",",names=converters.values(),
            converters=converters)
df = df.drop(df[df["nom_algo"]=="bellmanFord"].index)
df = df.drop(df[df["mode"]=="temps"].index)
df = df.drop("mode",axis=1)
df = df.apply(lambda x:x[8]/float(x[7])*100,axis=1,result_type="expand")
print(list(df.columns.values))
# for carte in ["INSA","Haute-Garonne","Guadeloupe",None]:
#     df_tmp = None
#     if carte != None:
#         df_tmp = df.loc[df["carte"]==carte]
#     fig = px.scatter(df_tmp if carte != None else df,x="cout",
#                         y=valeur,
#                         title="Comparaison Astar Dijkstra %s"%("carte "+carte if carte != None else "toutes cartes"),
#                         color="nom_algo",
#                         hover_name="carte",
#                         hover_data=[
#                             "origine",
#                             "destination",
#                             "cout",
#                             valeur,
#                             "temps_calc"
#                         ],
#                         size="temps_calc")
#     fig.write_html("/home/robin/Documents/Cours/BE-Graphes/tests_performance/analyse_output_detaillee/%s_%s.html"%(carte if carte != None else "toutes_cartes",valeur))