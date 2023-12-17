Copyright (C) 2023 Cosmin-Alexandru Dima
#   ____ _       _           ___        __
#  / ___| | ___ | |__   __ _| \ \      / /_ ___   _____  ___ 
# | |  _| |/ _ \| '_ \ / _` | |\ \ /\ / / _` \ \ / / _ \/ __|
# | |_| | | (_) | |_) | (_| | | \ V  V / (_| |\ V /  __/\__ \
#  \____|_|\___/|_.__/ \__,_|_| _\_/\_/ \__,_| \_/ \___||___/
#             | ____| |_ __ _ _ __   __ _  |___ \                        
#             |  _| | __/ _` | '_ \ / _` |   __) |                       
#             | |___| || (_| | |_) | (_| |  / __/                        
#             |_____|\__\__,_| .__/ \__,_| |_____|                       
#                            |_|
#                Am folosit scheleteul oficial

    
In etapa 2 am implementat functiile de paginatie si am adaugat noi entitati pe
platforma. 

- Userii pot fi online sau offline, statusul lor se schimba prin comanda
"SwitchConnectionStatus". Daca un user este online acesta nu poate asculta
muzica sau podcasturi si nu poate folosi alte comenzi.

- Fiecare artist are pagina lui specifica, la fel pentru fiecare host. Fiecare
user are doua pagini -HomePage- si -LikedPage- . In momentul in care un user
cauta un artist sau un host pe platforma si il selecteaza, acesta ajunge pe
pagina artistului/hostului. Am implementat asta cu ajutorul unei variabile care
se schimba la fiecare select al fiecarei cautari. Userul poate schimba pagina
la Home sau LikedPage folosind comanda "ChangePage". Acest lucru este posibil
deoarece fiecare user are o proprietate in care se retine pagina pe care se 
afla.

- Un artist poate adauga noi albume pe platforma. Cand se adauga un album
programul verifica daca artistul mai are inca un album cu acelasi nume in 
lista cu albume. Pe acelasi principiu functioneaza si adaugarea de podcasturi
pentru un host.

- Un host poate sa stearga din podcasturile sale de pe platforma. Pentru asta
programul verifica ca podcastul ce trebuie sa fie sters sa nu fie ascultat de
niciun user. Un album poate fi si el sters de catre artistul care l-a adaugat
atata timp cat niciun user nu asculta nicio melodie din album si niciun user
nu are niciun playlist loaded cu una din melodii, sau nu are niciun playlist cu 
una din melodii.

- Artistul poate adauga evenimente sau merch pe platforma si poate sterge din
ele. Un host poate adauga announcementuri si le poate sterge.

- Adminul poate sa elimine un user de pe platforma. Daca userul eliminat este
un artist sau host, se elimina toate cantecele, albumele respectiv podcasturile
pe care acesta le are. Un artist sau host poate fi eliminat doar daca nu este
ascultat de un user.

- Programul creeaza si clasamente ale albumelor sau ale artistilor. Albumele
sortate dupa suma dintre like-urile fiecarui cantec. Astfel se afiseaza primele
5 albume cu cel mai mare nr de like-uri. Daca au numar egal de like-uri, se
afiseaza in ordine lexicografica. Artistii sunt sortati dupa aceeasi metoda.

- Cand se adauga elemente noi pe platforma aceasta verifica daca userul care
doreste sa adauge elemente are permisiunile necesare, si verifica daca
elemente cu acelasi nume au mai fost incarcate.
