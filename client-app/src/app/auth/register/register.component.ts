import { Component, Inject, OnInit } from '@angular/core';
import {FormControl, Validators, FormGroup, FormBuilder, NgForm} from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
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
    matricolis : String ='';
    numbermatricola : String;
    autoemail : Observable <String>;
    student : boolean = false;
    count : number = 0;
    matr : String ='';
   
    constructor(
        private formBuilder: FormBuilder,
        private router: Router,
        private authenticationService: AuthenticationService,
        private userService: UserService,
        private alertService: AlertService,
        private matDialog : MatDialog,
        private authService: AuthService,
        public dialog : MatDialog,
        public dialogRef: MatDialogRef<RegisterComponent>,
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
            matricola: ['', [Validators.required,Validators.minLength(7),Validators.maxLength(7)]],
            email: ['', [Validators.required,Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });

        //this.registerForm.controls['email'].setValue(this.matricolis);

        this.onChanges();
  
  this.loading$ = of(this.loading);
        
    }

   onChanges() : void {


    this.registerForm.valueChanges.subscribe ( val => {

      console.log(val);
        
      

        if(val.matricola == 's' && this.count==0)
        {
            this.student = true;
            this.count ++;
            let s = val.matricola + '@studenti.polito.it';
            this.autoemail = of (s);
        }
        else if (val.matricola == 'd' && this.count==0)
        {
          this.count++;
          console.log("d");
          this.student = false;
          let n = val.matricola +'@polito.it';
          this.autoemail = of (n);
        }

        if (this.student == true && this.count!=0 )    {
             console.log("continuo s");
            let s = val.matricola + '@studenti.polito.it';
            this.autoemail = of (s); 
        }

        if (this.student == false && this.count!=0)
        {
            console.log("continuo d");
            let n = val.matricola +'@polito.it';
          this.autoemail = of (n);
        }


        this.matr = this.registerForm.controls['matricola'].value;

        if(this.matr.length == 0 && this.count!=0)
        {
            
            this.count = 0;
        }  
   
    })
   }

    // convenience getter for easy access to form fields
    get f() { return this.registerForm.controls; }

    onSubmit() {
 
this.submitted = true;
        let mail;
        this.autoemail.subscribe(data => {mail = data})
        this.registerForm.controls['email'].setValue(mail);
        console.log(this.registerForm.controls['email'].value);


  
         if (this.registerForm.invalid) {
            return;
        }
       
      

        // reset alerts on submit
        this.alertService.clear();

        // stop here if form is invalid
       
        this.loading = true;
        this.loading$ = of(this.loading);
        this.loading$.subscribe(data => {console.log(data);});

       this.numbermatricola = this.f.matricola.value;
       this.numbermatricola = this.numbermatricola.substring(1);
       


        

        this.authService.signup(this.f.nome.value,this.f.cognome.value,this.numbermatricola,this.f.email.value,this.f.password.value)

            .subscribe(
                data => {
                    this.loading = false;
                    console.log(data);
                    this.alertService.success('Registration successful', true);
                    this.matDialog.closeAll();
                    let dialogRef = this.dialog.open(YourDialog, {
                            data: { name: 'Registrazione effettuata! Hai ricevuto una mail con un link per attivare il tuo profilo.' },
                                });
                    this.router.navigate(['/']);
                },
                (error) => {
                    let dialogRef = this.dialog.open(YourDialog, {
                            data: { name: error },
                                });
                    this.loading = false;
                    this.loading$ = of(this.loading);
                    this.alertService.error(error);
                    
                });
    }

    close()
    {
       this.dialogRef.close();
    }

    

}

@Component({
  selector: 'your-dialog',
  template: '{{ data.name }}',
})
export class YourDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: {name: string}) { }
}