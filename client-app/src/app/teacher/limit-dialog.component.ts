import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Vms } from 'app/model/vms.model';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import { Inject } from '@angular/core';
import { DialogDataVm } from './vmscomponent.component';

@Component({
  selector: 'app-limit-dialog',
  templateUrl: './limit-dialog.component.html',
  styleUrls: ['./limit-dialog.component.css']
})
export class LimitDialogComponent2 implements OnInit {

  limitForm: FormGroup;

  vms: Vms[] = []; 
  alertACTIVEVMS : string = "";
  activevms : number = 0;
  alertRAM : string = "";
  RAMtotal : number = 0;
  alertnvcpu : string = "";
  nvcputotal : number = 0;
  alertTOTALVMS : string = "";
  TOTALVMS : number = 0;
  alertDISKSIZE : string = "";
  alertOperatingSystem : string = "";
  DISKSIZE : number = 0;
  nvcpu: number;
  RAM: number;
  Disksize: number;
  ActiveVms: number;
  TotalVms: number;
  vmstemp: Vms[];
  length : number;
  options = ["Windows", "Linux"];
  submitted = false;
  
  constructor(
    
    private formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<LimitDialogComponent2>,
     @Inject(MAT_DIALOG_DATA) public data: DialogDataVm, 
     public dialog: MatDialog) {

  }

  ngOnInit() {

    this.limitForm = this.formBuilder.group({
      nvcpu: ['',Validators.required],
      RAM:  ['',Validators.required],
      Disksize:  ['',Validators.required],
      OperatingSystem:  ['',Validators.required],
      ActiveVms:  ['',Validators.required],
      TotalVms:  ['',Validators.required]
      
    });

  


    Object.entries(this.data).forEach( s => {

    this.length = (s.length);
    
    this.vms = Object.assign(s[1]);
    })
    
  }

  get f() { return this.limitForm.controls; }

close()
{
this.dialog.closeAll();
}



setlimit() {

 this.submitted = true;

  if (this.limitForm.invalid) {


    return;
  }

  else
  this.dialogRef.close (this.limitForm.value);

}
}