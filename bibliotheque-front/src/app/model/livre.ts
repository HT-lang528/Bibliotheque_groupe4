import { Collection } from './collection';

// Types utilisés par Livre : les CRUD des autres membres restent indépendants.
export interface AuteurLivre { id: number; nom: string; prenom: string; nationalite?: string; }
export interface EditeurLivre { id: number; nom: string; pays?: string; }
export interface Livre {
  id: number;
  titre: string;
  resume: string | null;
  annee: number | null;
  auteur: AuteurLivre;
  editeur: EditeurLivre;
  collection: Collection | null;
}
export interface LivreRequest {
  titre: string;
  resume: string | null;
  annee: number | null;
  auteur: { id: number };
  editeur: { id: number };
  collection: { id: number } | null;
}
export interface LivreReferences {
  auteurs: AuteurLivre[];
  editeurs: EditeurLivre[];
  collections: Collection[];
}
