import { Routes } from '@angular/router';
import { LoginComponent } from './Components/login/login.component';
import { RegisterionComponent } from './Components/registerion/registerion.component';
import { CheckOtpComponent } from './Components/check-otp/check-otp.component';
export const routes: Routes = [
    
    {path:'' , component : RegisterionComponent},
    
    {path:'register' , component : RegisterionComponent},


    { path:'login' , component : LoginComponent },
    
    { path:'checkOtp' , component : CheckOtpComponent }

];
