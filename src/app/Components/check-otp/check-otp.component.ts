import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RegistrationService } from '../../Services/registration.service';
@Component({
  selector: 'app-check-otp',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './check-otp.component.html',
  styleUrls: ['./check-otp.component.scss']
})
export class CheckOtpComponent {
  otp: string = '';
  email: string = '';
  isLoading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private router: Router, private service: RegistrationService) {
    const storedEmail = localStorage.getItem('email');
    if (storedEmail) {
      this.email = storedEmail;
    } else {
      // إذا ما في إيميل محفوظ، رجّع المستخدم لتسجيل الدخول
      this.router.navigate(['/login']);
    }
  }

  verifyOtp(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.otp.length !== 6 || !/^\d{6}$/.test(this.otp)) {
      this.errorMessage = 'OTP must be a 6-digit number.';
      return;
    }

    this.isLoading = true;

    const otpPayload = {
      email: this.email,
      otp: this.otp
    };

    this.service.checkOTP(otpPayload).subscribe({
      next: (response: string) => {
        if (response === 'The otp is valid') {
          this.successMessage = 'OTP verified successfully!';
          this.errorMessage = '';
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 1000);
        } else {
          this.errorMessage = 'Invalid OTP. Please try again.';
          this.successMessage = '';
        }
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Server error. Please try again later.';
        this.successMessage = '';
        this.isLoading = false;
      }
    });
  }
}
