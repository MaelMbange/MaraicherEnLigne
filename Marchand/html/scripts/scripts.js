var url = "http://localhost:8080/api/";
var urlimage = "http://localhost:8080/images/";


(function() {
    console.log("Ceci est une fonction auto-exécutante !");
    miseAJourTable();
})();


document.addEventListener("DOMContentLoaded", function () {
    // Variable pour stocker la ligne sélectionnée précédemment
    var selectedRow;

    // Ajout d'un gestionnaire d'événement au clic sur une ligne de la table
    var table = document.getElementById("tableArticle");
    table.addEventListener("click", function (event) {
        var clickedRow = event.target.closest("tr");

        // Réinitialiser la couleur de la ligne précédemment sélectionnée
        if (selectedRow) {
            selectedRow.style.backgroundColor = "";
        }

        // Vérifier si la ligne sélectionnée n'est pas l'en-tête de la table
        if (clickedRow && !clickedRow.cells[0].textContent.includes("Id")) {
            // Marquer la ligne en jaune
            clickedRow.style.backgroundColor = "yellow";
            selectedRow = clickedRow;

            // Mettre à jour les champs id, Articles, Prix, Quantite et l'image
            var idInput = document.getElementById("Id");
            var articlesInput = document.getElementById("Articles");
            var prixInput = document.getElementById("Prix");
            var quantiteInput = document.getElementById("Quantite");
            var image = document.getElementById("image");

            idInput.value = selectedRow.cells[0].textContent;
            articlesInput.value = selectedRow.cells[1].textContent;
            prixInput.value = selectedRow.cells[2].textContent;
            quantiteInput.value = selectedRow.cells[3].textContent;
            image.src = urlimage + selectedRow.cells[1].textContent.toLowerCase() + ".jpg";
        } else {
            // Si la ligne sélectionnée est l'en-tête ou une autre partie de la table, réinitialiser les champs
            var idInput = document.getElementById("Id");
            var articlesInput = document.getElementById("Articles");
            var prixInput = document.getElementById("Prix");
            var quantiteInput = document.getElementById("Quantite");
            var image = document.getElementById("image");

            idInput.value = "";
            articlesInput.value = "";
            prixInput.value = "";
            quantiteInput.value = "";
            image.src = "";
        }
    });
});

document.getElementById('modifier').addEventListener("click",function(e) {
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function()
    {
        console.log(this);
        if (this.readyState == 4 && this.status == 201)
        {
            console.log(this.response);
            miseAJourTable();
        }
        else if (this.readyState == 4) {
            alert("Une erreur est survenue...");
        }
    };
    xhr.open("POST",url,true);
    xhr.responseType = "json";
    xhr.setRequestHeader("Content-type","application/json");


    var id = document.getElementById("Id").value;
    var prix = document.getElementById("Prix").value;
    var quantite = document.getElementById("Quantite").value;

    console.log(JSON.stringify({
        id: id,
        prix: prix,
        quantite: quantite
    }));

    xhr.send(JSON.stringify({
        id: id,
        prix: prix,
        quantite: quantite
    }));
});


function miseAJourTable()
{
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function()
    {
        console.log(this);
        if (this.readyState == 4 && this.status == 200)
        {
            console.log(this);
            //articles = JSON.parse(this.response);
            let articles = this.response;
            console.log("Reception MAJ -> " + articles)
            if(articles!=null){
                videTable();
                articles.forEach(function(article) {
                    ajouteLigne(article.id,article.intitule,article.prix,article.quantite);
                });
            }
        }
        else if (this.readyState == 4) {
            alert("Une erreur est survenue...");
        }
    };
    xhr.open("GET",url,true);
    xhr.responseType = "json";
    xhr.send();
}

function ajouteLigne(id,intitule,prix,quantite)
{
    var maTable = document.getElementById("tableArticle");
// Créer une nouvelle ligne
    var nouvelleLigne = document.createElement("tr");
// Créer des cellules
    celluleId = document.createElement("td");
    celluleId.textContent = id;
    celluleIntitule = document.createElement("td");
    celluleIntitule.textContent = intitule;

    cellulePrix = document.createElement("td");
    cellulePrix.textContent = prix;
    celluleQuantite = document.createElement("td");
    celluleQuantite.textContent = quantite;


// Ajouter les cellules à la ligne
    nouvelleLigne.appendChild(celluleId);
    nouvelleLigne.appendChild(celluleIntitule);
    nouvelleLigne.appendChild(cellulePrix);
    nouvelleLigne.appendChild(celluleQuantite);
// Ajouter la nouvelle ligne au tableau
    maTable.appendChild(nouvelleLigne);
}

function videTable()
{
    var maTable = document.getElementById("tableArticle");
    while (maTable.rows.length > 1) {
        maTable.deleteRow(-1);
    }
}