import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup,Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CreatePaperComponent } from 'app/create-paper/create-paper.component';
import { Assignment } from 'app/model/assignment.model';
import { PaperStatusTime } from 'app/model/paperStatusTime.model';
import { environment } from 'environments/environment';


@Component({
  selector: 'app-view-paper',
  templateUrl: './view-paper.component.html',
  styleUrls: ['./view-paper.component.css']
})
export class ViewPaperComponent implements OnInit {

 
  constructor( 
     public dialogRef: MatDialogRef<ViewPaperComponent>,
     public dialog: MatDialog,
     private formBuilder : FormBuilder,
     @Inject(MAT_DIALOG_DATA) public data: any ) { }

  history: PaperStatusTime[] = [];
  id: number;
  currentStatus: String;
  editable: Boolean;
  ratable: Boolean;
  step1: Boolean = true;
  goRate: Boolean = false;
  goReview: Boolean = false;
  teacher: Boolean = false;
  student: Boolean = false;
  rates: number[] = [];
  revisioni: Boolean = false;
  currentReview: String;
  currentSol: String = "";
  imageViewer: Boolean = false;
  soluzioneForm : FormGroup;
  valutaForm : FormGroup;
  submitted : boolean = false;
  reviewable: Boolean;
  assignment: Assignment;
  expired: Boolean = false;
  rated: Boolean = false;
  mark: number;

  ngOnInit(): void {
    this.id = this.data.id;
    this.history = this.data.history;
    this.currentStatus = this.data.currentStatus;
    this.editable = this.data.editable;
    this.reviewable = (this.currentStatus != "LETTO" && this.currentStatus != "NULL");
    this.ratable = (this.currentStatus == "RIVISTO" && this.editable == false);
    
    for(let i = 1; i<=30; i++) {
      this.rates.push(i);
    }
    this.teacher = this.data.teacher;
    this.student = this.data.student;
    this.assignment = this.data.assignment;
    if(this.isExpired(this.assignment.expiryDate))
      this.expired = true;
    if(this.currentStatus == "VALUTATO")
      this.rated = true;
    this.mark = this.data.mark;

    //Inizializzazione dei Form 
    this.soluzioneForm = this.formBuilder.group({

      soluzione : ['',Validators.required],
      check : ['',]

    })

    this.valutaForm = this.formBuilder.group({

      voto : null
    })
    


  }

  onSubmit() {
      this.submitted = true;
    
      // Fermati qua se il form è invalido 
      if (this.soluzioneForm.invalid) {
        return;
      }
      else
       this.dialogRef.close(this.soluzioneForm.value);
     }

   onSubmit2() {
      this.submitted = true;

      // Fermati qua se il form è invalido 
      if (this.valutaForm.invalid) {
        return;
      }
      else
       this.dialogRef.close(this.valutaForm.value);
     }




     displayDate(date: Date) {
      var newDate: Date = new Date(date);
      var dd = String(newDate.getDate()).padStart(2, '0');
      var mm = String(newDate.getMonth() + 1).padStart(2, '0'); //Gennaio è 0!
      var yyyy = newDate.getFullYear();
      return dd + "/" + mm + "/" + yyyy;
    }

  displayTime(date: Date) {
    var newDate: Date = new Date(date);
    return String(newDate.getHours()).padStart(2, '0') + ":" + String(newDate.getMinutes()).padStart(2, '0');
  }

  clickrate(){
    this.step1 = false;
    this.goRate = true;
  }

  clickreview(){
    this.step1 = false;
    this.goReview = true;


  }

  viewReview(content: String) {
    this.currentReview = content;
    console.log(content)
    this.step1 = false;
    this.revisioni = true;
  }

  exitReview() {
    this.step1 = true;
    this.revisioni = false;
  }

  goViewSol (relativePath: String) {
    var newImg: String = environment.baseimage + "/" + relativePath;
    this.currentSol = newImg;
    this.imageViewer = true;
  }

  exitImageViewer() {
    this.imageViewer = false;
  }

  setEdit() {
    this.dialogRef.close(true);
  }

  isExpired(date: Date) {
    var expiry = new Date(date);
    var now = new Date();
    if(now > expiry)
      return true;
    else
      return false;
  }


 
}
