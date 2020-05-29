import pandas as pd
import plotly.express as px

colonnes = ["nom_algo","carte","mode","origine","destination","cout","temps_calc","nb_noeuds_explores","nb_noeuds_marques","taille_max_tas","distance","distance_max_marque","distance_max_explo"]
df = pd.DataFrame(columns=colonnes)

#Met les données en RAM
with open("/home/robin/Documents/Cours/BE-Graphes/tests_performance/output_detaillee.csv","r") as f:
    tests = f.readlines()
for i,test in enumerate(tests):
    df.loc[i] = test.strip().split(",")
df = df.drop(df[df["nom_algo"]=="bellmanFord"].index)
df = df.drop(df[df["mode"]=="temps"].index)
df = df.drop("mode",axis=1)
df = df.loc[df["carte"]=="INSA"]
df["temps_calc"]=df["temps_calc"].apply(lambda x:int(x))
df["distance_max_marque"]=df["distance_max_marque"].apply(lambda x:float(x))
df["distance_max_explo"]=df["distance_max_explo"].apply(lambda x:float(x))
print(df.columns.values)
df = df.apply(lambda x: (x[0],x[1],x[2],x[3],x[4],(int(x[6])-int(x[7]))/float(int(x[6]))), result_type='expand',axis=1)
df.columns = ["nom_algo","carte","origine","destination","cout","diff_explores_marques"]
# df_traj = df.copy()
# df_traj['Trajet'] = df_traj[['carte', 'origine', "destination"]].agg('-'.join, axis=1)
# df_traj = df_traj.drop(["carte","origine","destination"],axis=1)
df_dij = df.loc[df["nom_algo"]=="dijikstra"]
df_star = df.loc[df["nom_algo"]=="astar"]
# df_comp = df_dij.merge(df_star,on="Trajet")
# print(df_comp.columns.values)
# df_comp = df_comp.apply(lambda x: (x[0],x[1],int(x[2])-int(x[11])), result_type='expand',axis=1)
# df_comp.columns = ["Trajet","cout","Difference_nb_explorés"]
dij_desc = df_dij.describe()
astar_desc = df_star.describe()
fig = px.scatter(df,x="cout",
                    y="diff_explores_marques",
                    title="AStar : moyenne %f ; ecart type : %f<br>Dijkstra : moyenne : %f ; ecart type : %f"%(astar_desc.loc["mean"],astar_desc.loc["std"],dij_desc.loc["mean"],dij_desc.loc["std"]),
                    color="nom_algo",
                    hover_name="carte",
                    hover_data=["origine","destination"])
fig.show()