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

@Component({ templateUrl: 'register.component.html',
styleUrls: ['register.component.css']})
export class RegisterComponent implements OnInit {
    registerForm: FormGroup;
    loading = false;
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
            email: ['', Validators.required],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });
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
        this.authService.signup(this.f.nome.value,this.f.cognome.value,this.f.matricola.value,this.f.email.value,this.f.password.value)
            .pipe(first())
            .subscribe(
                data => {
                    this.alertService.success('Registration successful', true);
                    this.router.navigate(['/login']);
                },
                error => {
                    this.alertService.error(error);
                    this.loading = false;
                });
    }

}