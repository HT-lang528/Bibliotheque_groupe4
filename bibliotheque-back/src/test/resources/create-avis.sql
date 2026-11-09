-- Création d'un auteur
INSERT INTO auteur (nom, prenom, nationalite)
VALUES ('Hugo', 'Victor', 'Française');

-- Création d'un éditeur
INSERT INTO editeur (nom, pays)
VALUES ('Gallimard', 'France');

-- Création d'un livre
INSERT INTO livre (titre, auteur_id, editeur_id)
VALUES ('Les Misérables', 1, 1);

-- Création des avis
INSERT INTO avis (note, commentaire, date, livre_id)
VALUES
(5, 'Très bon livre', '2026-01-10', 1),
(4, 'Livre intéressant', '2026-01-12', 1);