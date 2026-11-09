import { Livre } from './livre';

export interface Avis {
    id: number;
    note: number;
    commentaire: string;
    date: string;
    livre: Livre;
}
