import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})

export class RegistrationService {

    constructor(
        private http: HttpClient) { }


    login(userDate: any) {
        return this.http.post("http://localhost:8081/api/v1/auth/authenticate", userDate);
    }

    registration(registration: any) {
        return this.http.post("http://localhost:8081/api/v1/auth/register", registration);
    }

    checkOTP(OTP: { email: string; otp: string }): Observable<string> {
        return this.http.post('http://localhost:8081/api/v1/auth/otpcheack', OTP, {
            responseType: 'text'
        });
    }




}