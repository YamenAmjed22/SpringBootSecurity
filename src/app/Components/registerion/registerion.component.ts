import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RegistrationService } from '../../Services/registration.service';

@Component({
  selector: 'app-registeration-page',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './registerion.component.html',
  styleUrls: ['./registerion.component.scss']
})
export class RegisterionComponent implements OnInit {
  title = 'Emarket Registration';
  registrationForm!: FormGroup;
  submitted = false;
  passwordStrength = 0;
  errorMessage = '';
  successMessage = '';
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private registrationService: RegistrationService
  ) { }

  ngOnInit(): void {
    this.buildForm();
  }

  private buildForm(): void {
    this.registrationForm = this.fb.group(
      {
        firstName: ['', [Validators.required, Validators.minLength(3)]],
        lastName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(15), this.noSpacesValidator()]],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(8), this.passwordValidator()]],
        confirmPassword: ['', Validators.required],
        agreeTerms: [false, Validators.requiredTrue]
      },
      { validators: this.mustMatch('password', 'confirmPassword') }
    );
  }

  private noSpacesValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) return null;
      return /\s/.test(control.value) ? { noSpaces: true } : null;
    };
  }

  private passwordValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) return null;
      const hasLetter = /[A-Za-z]/.test(value);
      const hasNumber = /\d/.test(value);
      return hasLetter && hasNumber ? null : { invalidPassword: true };
    };
  }

  private mustMatch(controlName: string, matchingControlName: string): ValidatorFn {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const group = formGroup as FormGroup;
      const control = group.get(controlName);
      const matchingControl = group.get(matchingControlName);
      if (!control || !matchingControl) return null;
      if (matchingControl.errors && !matchingControl.errors['mustMatch']) return null;
      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({ mustMatch: true });
      } else {
        matchingControl.setErrors(null);
      }
      return null;
    };
  }

  checkPasswordStrength(): void {
    const password = this.password?.value || '';
    let strength = 0;
    if (password.length >= 8) strength += 25;
    if (/[A-Z]/.test(password)) strength += 25;
    if (/\d/.test(password)) strength += 25;
    if (/[^A-Za-z0-9]/.test(password)) strength += 25;
    this.passwordStrength = strength;
  }

  getPasswordStrengthColor(): string {
    if (this.passwordStrength < 50) return '#e74c3c';
    if (this.passwordStrength < 75) return '#f39c12';
    return '#2ecc71';
  }

  get f(): { [key: string]: AbstractControl } {
    return this.registrationForm.controls;
  }

  get password(): AbstractControl | null {
    return this.registrationForm.get('password');
  }

  get confirmPassword(): AbstractControl | null {
    return this.registrationForm.get('confirmPassword');
  }

  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.registrationForm.invalid) {
      return;
    }

    const formData = { ...this.registrationForm.value };
    delete formData.agreeTerms;

    this.isSubmitting = true;

    this.registrationService.registration(formData).subscribe({
      next: (res: any) => {
        console.log("✅ Inside next()");
        localStorage.setItem('email', this.registrationForm.value.email);
        this.successMessage = res?.message || 'Registration successful!';
        this.router.navigate(['/checkOtp']);
         // OR navigate to OTP page here
      },
      error: (err) => {
        console.error("❌ Inside error()", err);
        this.errorMessage = err?.error?.message || 'Registration failed. Please try again.';
      },
      complete: () => {
        console.log("ℹ️ Inside complete()");
        this.isSubmitting = false;
      }
    });

  }

  goToLoginPage() {
    this.router.navigate(['/login']);
  }
}
