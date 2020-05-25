import pandas as pd
import plotly.express as px
liste_trajets = []
df = pd.DataFrame(columns=["nom_algo","carte","mode","origine","destination","cout","temps_calc","nb_noeuds_explores","nb_noeuds_marques","taille_max_tas","distance"])

#Met les données en RAM
with open("/home/robin/Documents/Cours/BE-Graphes/tests_performance/output.csv","r") as f:
    tests = f.readlines()
for i,test in enumerate(tests):
    df.loc[i] = test.strip().split(",")
df = df.drop(df[df["nom_algo"]=="bellmanFord"].index)
df = df.drop(df[df["mode"]=="temps"].index)
df = df.drop("mode",axis=1)
df = df.loc[df["carte"]=="Guadeloupe"]
df["temps_calc"]=df["temps_calc"].apply(lambda x:int(x))
df_traj = df.copy()
df_traj['Trajet'] = df_traj[['carte', 'origine', "destination"]].agg('_'.join, axis=1)
df_traj = df_traj.drop(["carte","origine","destination"],axis=1)
df_dij = df_traj.loc[df_traj["nom_algo"]=="dijikstra"]
df_star = df_traj.loc[df_traj["nom_algo"]=="astar"]
df_comp = df_dij.merge(df_star,on="Trajet")
df_comp = df_comp.apply(lambda x: (x[7],x[1],int(x[4])-int(x[12]),abs(int(x[2])-int(x[10])),True if int(x[2])-int(x[10]) >=0 else False), result_type='expand',axis=1)
df_comp.columns = ["Trajet","cout","Difference_nb_marqués","Difference_temps_calc","Dijkstra_plus_long_texec"]
fig = px.scatter(df_comp,x="cout",
                    y="Difference_nb_marqués",
                    hover_data=["Trajet"],
                    color="Dijkstra_plus_long_texec",
                    size="Difference_temps_calc")
fig.show()