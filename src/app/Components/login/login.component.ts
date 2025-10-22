import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RegistrationService } from '../../Services/registration.service';
import { NotificationService } from 'nzrm-ng';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  LoginForm!: FormGroup;
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private service: RegistrationService,
    private _notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.LoginForm = this.fb.group({
      email: ['', [Validators.required, Validators.minLength(3), Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    this.successMessage = '';
    this.errorMessage = '';

    if (this.LoginForm.invalid) {
      return;
    }

    const loginData = this.LoginForm.value;

    this.service.login(loginData).subscribe({
      next: (res: any) => {
         localStorage.setItem('email',this.LoginForm.value.email);
        this.LoginForm.reset();
        this.successMessage = res?.message || 'Login successful!';
        this._notificationService.success("Success", this.successMessage);
        this.router.navigate(['homepage']);
      },
      error: (err) => {
        // Extract backend message if available
        const backendMessage = err?.error?.message || 'An unknown error occurred';
        this.errorMessage = backendMessage;
        this._notificationService.error("Error", backendMessage);
      }
    });
  }

  goToRegister(): void {
    this.router.navigate(['/register']);
  }
}
