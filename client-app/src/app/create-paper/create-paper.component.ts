import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-create-paper',
  templateUrl: './create-paper.component.html',
  styleUrls: ['./create-paper.component.css']
})
export class CreatePaperComponent implements OnInit {

 constructor( 
   public dialogRef: MatDialogRef<CreatePaperComponent>,
   public dialog: MatDialog,
   private formBuilder : FormBuilder,
   @Inject(MAT_DIALOG_DATA) public data: any, ) { }


  createForm : FormGroup;
  submitted : boolean = false;

  ngOnInit(): void {

   this.createForm = this.formBuilder.group({

    content : ['',Validators.required]

   })
  


  }

  onSubmit() {

  this.submitted = true;
    
      // Fermati qua se il form è invalido 
      if (this.createForm.invalid) {
        return;
      }
      else
       this.dialogRef.close(this.createForm.value);

  }

}
