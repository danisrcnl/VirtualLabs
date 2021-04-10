import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { AuthService } from './authservices/auth.service';
import { AlertService } from './authservices/alert.service';
import { MatDialog } from '@angular/material/dialog';
import { RegisterComponent } from './register/register.component';
import { JwtInterceptor } from './interceptor/jwt.interceptor';

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
        private matDialog: MatDialog
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
        console.log(this.returnUrl);
    }

    // convenience getter for easy access to form fields
    get f() { return this.loginForm.controls; }

    onSubmit() {
      this.submitted = true;

      // reset alerts on submit
      this.alertService.clear();

      // stop here if form is invalid
      if (this.loginForm.invalid) {

        console.log('Login invalid');
        return;
      }

      var dummy = Promise.resolve();
      this.loading = true;



      this.authService.login(this.f.username.value, this.f.password.value).subscribe
      (
        data => { console.log(data);
        this.authService.info().subscribe(
          data1 => {
            this.isTeacher= data1.isTeacher;
            if (this.isTeacher == false)
        {
          this.router.navigate([this.returnUrl + 'student'], {queryParams: {user: this.f.username.value}});
          this.matDialog.closeAll();
        }
        else 
         this.router.navigate([this.returnUrl + 'teacher'], {queryParams: {user: this.f.username.value}});
         this.matDialog.closeAll();
      }
        )}
        )
        
      


     /* this.authService.login(this.f.username.value, this.f.password.value).toPromise()
      .then( data => console.log (data)).then( ()=> this.authService.info().toPromise()
      .then(data => { this.isTeacher = data.isTeacher;
       
        if (this.isTeacher == false)
        {
          this.router.navigate([this.returnUrl + 'student'], {queryParams: {user: this.f.username.value}});
          this.matDialog.closeAll();
        }
        else 
         this.router.navigate([this.returnUrl + 'teacher'], {queryParams: {user: this.f.username.value}});
         this.matDialog.closeAll();
      }
      )).catch(error => console.log('errore'));
      

    */
  
     }

    openregister() {

        this.matDialog.open (RegisterComponent, { panelClass: 'custom-modalbox' });

    }

    closeregister() {

    this.matDialog.closeAll();
    }
}
