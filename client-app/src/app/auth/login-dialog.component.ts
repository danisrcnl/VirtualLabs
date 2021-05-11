import { Component, Inject, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { AuthService } from './authservices/auth.service';
import { AlertService } from './authservices/alert.service';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { RegisterComponent } from './register/register.component';
import { JwtInterceptor } from './interceptor/jwt.interceptor';
import { DialogLogin } from 'app/app.component';

@Component({ templateUrl: 'login-dialog.component.html' ,
styleUrls: ['login-dialog.component.css']})
export class LoginDialogComponent implements OnInit {
    loginForm: FormGroup;
    loading = false;
    submitted = false;
    returnUrl: string;
    isTeacher: boolean;
    username : String;
    data1 : any;
    jwt : JwtInterceptor;
    constructor(
        private formBuilder: FormBuilder,
        private route: ActivatedRoute,
        private router: Router,
        private authService: AuthService,
        private alertService: AlertService,
        private matDialog: MatDialog,
        public dialogRef: MatDialogRef<LoginDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data : DialogLogin,
       
    ) {
        // redirect to home if already logged in
        if (this.authService.currentUserValue) {
            this.router.navigate(['/']);
        }
    }

    ngOnInit() {
        this.loginForm = this.formBuilder.group({
            username: ['', Validators.required],
            password: ['', Validators.required]
        });

        // get return url from route parameters or default to '/'
        this.returnUrl = this.route.snapshot.queryParams.returnUrl || '/';
     
    }

    // convenience getter for easy access to form fields
    get f() { return this.loginForm.controls; }

    onSubmit() {
      this.submitted = true;
     

      // reset alerts on submit
      this.alertService.clear();

      // stop here if form is invalid
      if (this.loginForm.invalid) {
        return;
      }
      else
       this.dialogRef.close(this.loginForm.value);

      var dummy = Promise.resolve();
      this.loading = true;
  
     }

    openregister() {
        
        this.matDialog.open (RegisterComponent, { panelClass: 'custom-modalbox', disableClose : true });

    }

    closeregister() {

    this.matDialog.closeAll();
    }
}
