import { Component, Inject, OnInit } from '@angular/core';
import {FormControl, Validators, FormGroup, FormBuilder, NgForm} from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { first } from 'rxjs/operators';
import { User } from '../user';
import { UserService } from 'app/services/user.service';
import { AuthenticationService } from '../authentication.service';
import { AuthService } from '../authservices/auth.service';
import { Observable } from 'rxjs/internal/Observable';
import { of } from 'rxjs';

@Component({ templateUrl: 'register.component.html', styleUrls: ['register.component.css']})
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
        private matDialog : MatDialog,
        private authService: AuthService,
        public dialog : MatDialog,
        public dialogRef: MatDialogRef<RegisterComponent>,
    ) {}

    ngOnInit() {
        this.registerForm = this.formBuilder.group({
            nome: ['', Validators.required],
            cognome: ['', Validators.required],
            matricola: ['', [Validators.required,Validators.minLength(7),Validators.maxLength(7)]],
            email: ['', [Validators.required,Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });



        this.onChanges();

        //Parametro che servirà per visualizzare o meno la rotellina sulla vista una volta inoltrata la richiesta
        //di registrazione 
        this.loading$ = of(this.loading);
        
    }

    //La funzione ascolta il cambio di parametri del form (nello specifico il valore della matricola)
    //e in base a questo riesce a costruire passo dopo passo una mail istituzionale che dipenderà
    //dalla lettera iniziale della matricola 
   onChanges() : void {


    this.registerForm.valueChanges.subscribe ( val => {

      
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
          
          this.student = false;
          let n = val.matricola +'@polito.it';
          this.autoemail = of (n);
        }

        if (this.student == true && this.count!=0 )    {
             
            let s = val.matricola + '@studenti.polito.it';
            this.autoemail = of (s); 
        }

        if (this.student == false && this.count!=0)
        {
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

    // Getter per accedere più facilmente ai parametri del form 
    get f() { return this.registerForm.controls; }

    onSubmit() {
 
        this.submitted = true;
        let mail;
        this.autoemail.subscribe(data => {mail = data})
        this.registerForm.controls['email'].setValue(mail);

        //Se il form è invalido mi fermo 
         if (this.registerForm.invalid) {
            return;
        }
       
    
       
        this.loading = true;
        this.loading$ = of(this.loading);
        this.loading$.subscribe(data => {console.log(data);});

        //Taglio la lettera iniziale dalla matricola perché durante il programma la gestisco
        //solo come numero 
        this.numbermatricola = this.f.matricola.value;
        this.numbermatricola = this.numbermatricola.substring(1);
       


        
        //Chiamo il metodo per registrarmi con i campi presi dal form 
        this.authService.signup(this.f.nome.value,this.f.cognome.value,this.numbermatricola,this.f.email.value,this.f.password.value)

            .subscribe(
                data => {
                    this.loading = false;
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