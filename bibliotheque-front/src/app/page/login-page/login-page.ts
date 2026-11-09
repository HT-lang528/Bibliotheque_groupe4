import { Component, signal } from '@angular/core';
import { Auth } from '../../model/auth';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../service/auth-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-page',
  imports: [ ReactiveFormsModule  ],
  templateUrl: './login-page.html',
  styleUrl: './login-page.css',
})
export class LoginPage {
  protected formAuth = new FormGroup({
  username: new FormControl('', [Validators.required]),
  password: new FormControl('', [Validators.required]),
  
});

  protected error = signal('');

  constructor(private authService: AuthService, private router: Router) { }

  protected login() {
    const auth: Auth = {
    username: this.formAuth.value.username!,
    password: this.formAuth.value.password!,
  };

  this.authService.login(auth).subscribe({
  next: () => this.router.navigate(['home']),
  error: () => this.error.set('Identifiants incorrects'),
});
  }
}
