import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogConsegna } from '../elaboratiteacher/elaboratiteacher.component';


@Component({
  selector: 'app-consegnadialog',
  templateUrl: './consegnadialog.component.html',
  styleUrls: ['./consegnadialog.component.css']
})
export class ConsegnadialogComponent implements OnInit {


  consegnaForm : FormGroup;

  constructor(
    private formBuilder: FormBuilder, 
    public dialogRef: MatDialogRef<ConsegnadialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogConsegna) { }

  months: String[] = ["Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"];
  submitted = false;
  ngOnInit(): void {



    this.consegnaForm = this.formBuilder.group({


        giorno : ['',[Validators.required, Validators.max(31)]],
        mese : ['',Validators.required],
        anno : ['',Validators.required],
        content : ['',Validators.required]
    })

  }


  get f() { return this.consegnaForm.controls; }

  onSubmit() {

    console.log (this.consegnaForm.get(['giorno']).value);

     this.submitted = true;

      if(this.consegnaForm.invalid) {
        console.log('Form invalid');
        return;
      }

      else
      this.dialogRef.close (this.consegnaForm.value);



  }

}
