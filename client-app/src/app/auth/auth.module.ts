import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {NgForm,FormsModule} from '@angular/forms';

import { AuthRoutingModule } from './auth-routing.module';
import { LoginDialogComponent } from './login-dialog.component';
import { RegisterComponent } from './register/register.component';
import { MatFormFieldModule} from '@angular/material/form-field';
import { ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { ErrorInterceptor } from './interceptor/error.interceptor';
import { LimitDialogComponent } from '../teacher/limit-dialog.component';
import { AuthGuard } from '../_helpers/auth.guard';
import { AuthService } from './authservices/auth.service';
import { JwtInterceptor } from './interceptor/jwt.interceptor';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatProgressBarModule } from '@angular/material/progress-bar';


@NgModule({
  declarations: [LoginDialogComponent, RegisterComponent,LimitDialogComponent],
  providers: [
    AuthGuard,
    AuthService,
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },

  

  ],
  
  imports: [
    CommonModule,
    AuthRoutingModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
  
  ],

  exports : [
    LoginDialogComponent, RegisterComponent,LimitDialogComponent
  ]})

  export class AuthModule{}

