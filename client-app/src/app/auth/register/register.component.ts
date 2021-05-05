import { Component, OnInit } from '@angular/core';
import {FormControl, Validators, FormGroup, FormBuilder, NgForm} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { first } from 'rxjs/operators';
import { User } from '../user';
import { UserService } from 'app/services/user.service';
import { AuthenticationService } from '../authentication.service';
import { AlertService } from '../authservices/alert.service';
import { AuthService } from '../authservices/auth.service';
import { Observable } from 'rxjs/internal/Observable';
import { of } from 'rxjs';

@Component({ templateUrl: 'register.component.html',
styleUrls: ['register.component.css']})
export class RegisterComponent implements OnInit {
    registerForm: FormGroup;
    loading : boolean = false;
    loading$ : Observable <boolean>;
    submitted = false;

    constructor(
        private formBuilder: FormBuilder,
        private router: Router,
        private authenticationService: AuthenticationService,
        private userService: UserService,
        private alertService: AlertService,
        private matDialog : MatDialog,
        private authService: AuthService
    ) {
        // redirect to home if already logged in
        if (this.authenticationService.currentUserValue) {
           // this.router.navigate(['/']);
        }
    }

    ngOnInit() {
        this.registerForm = this.formBuilder.group({
            nome: ['', Validators.required],
            cognome: ['', Validators.required],
            matricola: ['', Validators.required],
            email: ['', Validators.required,Validators.email],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });

  this.loading$ = of(this.loading);
        
    }

    // convenience getter for easy access to form fields
    get f() { return this.registerForm.controls; }

    onSubmit() {
        this.submitted = true;

        // reset alerts on submit
        this.alertService.clear();

        // stop here if form is invalid
        if (this.registerForm.invalid) {
            return;
        }
        this.loading = true;
        this.loading$ = of(this.loading);
        this.loading$.subscribe(data => {console.log(data);});


        this.authService.signup(this.f.nome.value,this.f.cognome.value,this.f.matricola.value,this.f.email.value,this.f.password.value)

            .subscribe(
                data => {
                    this.loading = false;
                    console.log(data);
                    this.alertService.success('Registration successful', true);
                    this.matDialog.closeAll();
                    this.router.navigate(['/auth/login']);
                },
                error => {
                    this.loading = false;
                    this.loading$ = of(this.loading);
                    this.alertService.error(error);
                    
                });
    }

    close()
    {
        this.matDialog.closeAll();
    }

}