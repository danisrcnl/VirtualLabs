import { Component, Inject, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { AuthService } from './authservices/auth.service';
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
        private matDialog: MatDialog,
        public dialogRef: MatDialogRef<LoginDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data : DialogLogin,
       
    ) {
        // Redirect alla pagina iniziale se è già loggato
        if (this.authService.currentUserValue) {
            this.router.navigate(['/']);
        }
    }

    ngOnInit() {

        //Controllo che username e password non siano vuoti 
        this.loginForm = this.formBuilder.group({
            username: ['', Validators.required],
            password: ['', Validators.required]
        });

        this.returnUrl = this.route.snapshot.queryParams.returnUrl || '/';
     
    }

    get f() { return this.loginForm.controls; }

    onSubmit() {
      this.submitted = true;
     

      if (this.loginForm.invalid) {
        return;
      }
      else
       this.dialogRef.close(this.loginForm.value);

      var dummy = Promise.resolve();
      this.loading = true;
  
     }

    //Apri popup per la registrazione  
    openregister() {
        
    this.matDialog.open (RegisterComponent, { panelClass: 'custom-modalbox', disableClose : true });

    }

    closeregister() {

    this.matDialog.closeAll();
    }
}
