import { finalize } from 'rxjs';
import { Component, signal } from '@angular/core';
import { Auth } from '../../model/auth';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../service/auth-service';
import { RouterLink, Router } from '@angular/router';

@Component({
  selector: 'app-login-page',
  imports: [ ReactiveFormsModule, RouterLink ],
  templateUrl: './login-page.html',
  styleUrl: './login-page.css',
})
export class LoginPage {
  protected formAuth = new FormGroup({
  username: new FormControl('', [Validators.required]),
  password: new FormControl('', [Validators.required]),
});

  constructor(private authService: AuthService, private router: Router) { }

  readonly error = signal('');
  readonly saving = signal(false);
  protected login() {
    this.formAuth.markAllAsTouched();
    if (this.formAuth.invalid || this.saving()) return;
    const auth: Auth = {
      username: this.formAuth.value.username!.trim(),
      password: this.formAuth.value.password!,
    };
    this.error.set(''); this.saving.set(true);
    this.authService.login(auth).pipe(finalize(() => this.saving.set(false))).subscribe({
      next: resp => {
        this.authService.token = resp.token;
        this.router.navigate(['/home']);
      },
      error: err => this.error.set(err.status === 401 ? 'Identifiant ou mot de passe incorrect.' : 'Connexion impossible. Vérifie le serveur.'),
    });
  }
}
