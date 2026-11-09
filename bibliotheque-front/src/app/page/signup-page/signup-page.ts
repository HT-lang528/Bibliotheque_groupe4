import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../service/auth-service';

@Component({
  selector: 'app-signup-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './signup-page.html',
  styleUrl: './signup-page.css',
})
export class SignupPage {
  private readonly auth = inject(AuthService);
  readonly saving = signal(false);
  readonly error = signal('');
  readonly created = signal(false);
  readonly form = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.pattern(/\S/), Validators.maxLength(100)] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(8), Validators.maxLength(72)] }),
    confirmation: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });
  submit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.saving()) return;
    const value = this.form.getRawValue();
    this.error.set('');
    if (value.password !== value.confirmation) {
      this.error.set('Les mots de passe ne correspondent pas.');
      return;
    }
    if (new TextEncoder().encode(value.password).length > 72) {
      this.error.set('Mot de passe trop long : choisis une version plus courte.');
      return;
    }
    this.saving.set(true);
    this.auth.subscribe({ username: value.username.trim(), password: value.password })
      .pipe(finalize(() => this.saving.set(false))).subscribe({
        next: () => { this.created.set(true); this.form.reset(); },
        error: err => this.error.set(err.status === 409 ? 'Cet identifiant est déjà utilisé.' : err.status === 400 ? 'Vérifie les champs : mot de passe de 8 à 72 caractères.' : 'Inscription impossible. Vérifie la connexion au serveur.'),
      });
  }
}
