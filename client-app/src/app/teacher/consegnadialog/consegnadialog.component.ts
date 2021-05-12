import { Component, Inject, OnInit} from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogConsegna } from '../elaboratiteacher/elaboratiteacher.component';


class Outcome {
  formData: FormData;
  consegnaForm: FormGroup;
}

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
  file: File;
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


    let month : any = this.consegnaForm.controls['mese'].value;
     
      let month2 = this.months.indexOf(month);

      var dd = String(this.consegnaForm.controls['giorno'].value).padStart(2, '0');
      var mm = String(month2 + 1).padStart(2, '0'); //January is 0!
      var yyyy = this.consegnaForm.controls['anno'].value;

      let data = yyyy + "-" + mm + "-" + dd;
      

      //Switch per il controllo della correttezza della data inserita nel form 

      switch(mm)
      {

        case "04" : {
          if(this.consegnaForm.controls['giorno'].value > 30 )
          return;
            break;
        }

        case "06" : {
          if(this.consegnaForm.controls['giorno'].value > 30 )
          return;
            break;
        }

        case "09" : {
          if(this.consegnaForm.controls['giorno'].value > 30 )
          return;
             break;
        }

        case "11" : {
          if(this.consegnaForm.controls['giorno'].value > 30 )
          return;
             break;
        }

        case "02" : {
          
          if(this.consegnaForm.controls['giorno'].value > 28 )
          return;
             break;
        }

      }

    var outcome: Outcome = new Outcome();


    this.submitted = true;
    var formData = new FormData();
    formData.append("file", this.file);
    outcome.formData = formData;
    outcome.consegnaForm = this.consegnaForm;

    if(this.consegnaForm.invalid) {
    
      return;
    }
    else
      this.dialogRef.close (outcome);



  }

  onFileSelected (event) {
    this.file = event.target.files[0];
  }

   
}
