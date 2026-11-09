import { AuthService } from '../../service/auth-service';
import { Router } from '@angular/router';
import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  imports: [RouterLink, RouterLinkActive],
  selector: 'app-navigation',
  styleUrl: './navigation.css',
  templateUrl: './navigation.html',
})
export class Navigation {
  readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  logout(): void { this.auth.logout(); this.router.navigate(['/login']); }
}
