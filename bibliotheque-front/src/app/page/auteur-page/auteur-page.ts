import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuteurService } from '../../service/auteur-service';
import { Title } from '@angular/platform-browser';
import { rxResource } from '@angular/core/rxjs-interop';

@Component({
  imports: [FormsModule, AsyncPipe ],
  selector: 'app-auteur-page',
  styleUrl: './auteur-page.css',
  templateUrl: './auteur-page.html',
})
export class AuteurPage {
  protected auteurs = rxResource({
    stream: () => this.auteurService.findAll()
  });
  constructor(private title: Title, private auteurService: AuteurService) {
    this.title.setTitle("Liste des auteurs");
    
    
  }




}
