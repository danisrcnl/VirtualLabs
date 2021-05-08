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
  file: File;

  ngOnInit(): void {

   
  


  }

  carica() {

    this.submitted = true;
    
   
    var formData = new FormData();
    formData.append("file", this.file);
    this.dialogRef.close(formData);


  }

  onFileSelected (event) {
    this.file = event.target.files[0];
  }

}
