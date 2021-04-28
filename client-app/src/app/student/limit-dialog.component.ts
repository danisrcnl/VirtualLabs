import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Vms } from 'app/model/vms.model';
import {MatDialog, MAT_DIALOG_DATA} from '@angular/material/dialog';
import { Inject } from '@angular/core';

@Component({
  selector: 'app-limit-dialog',
  templateUrl: './limit-dialog.component.html',
  styleUrls: ['./limit-dialog.component.css']
})
export class LimitDialogComponent implements OnInit {


  

  limitForm: FormGroup;

  vms: Vms[] = []; 
  alertACTIVEVMS : string = "";
  activevms : number = 0;
  alertRAM : string = "";
  RAMtotal : number = 0;
  alertVCPU : string = "";
  VCPUtotal : number = 0;
  alertTOTALVMS : string = "";
  TOTALVMS : number = 0;
  alertDISKSIZE : string = "";
  DISKSIZE : number = 0;
  VCPU: number;
  RAM: number;
  Disksize: number;
  ActiveVms: number;
  TotalVms: number;
  vmstemp: Vms[];
  length : number;

  ram_consumption = 50;
  vcpu_consumption = 45;
  disk_consumption = 70;
  
  constructor(private formBuilder: FormBuilder, @Inject(MAT_DIALOG_DATA) public data: Vms, public dialog: MatDialog) {

  }

  ngOnInit() {

    this.limitForm = this.formBuilder.group({
      VCPU: [''],
      RAM: [''],
      Disksize: [''],
      
    });

  


    Object.entries(this.data).forEach( s => {

    this.length = (s.length);
    console.log(s[1]);
    this.vms = Object.assign(s[1]);
    })
    
  }

close()
{
this.dialog.closeAll();
}



createvm() {

  
  



  this.alertACTIVEVMS = "";
  this.alertDISKSIZE = "";
  this.alertRAM = "";
  this.alertTOTALVMS = "";
  this.alertVCPU ="";

  this.activevms = 0;
  this.DISKSIZE = 0;
  this.RAMtotal = 0;
  this.TOTALVMS = 0;
  this.VCPUtotal = 0;
 

if (this.VCPU < this.VCPUtotal)
{
  this.alertVCPU = "Limite non consentito"
}

if (this.RAM < this.RAMtotal)
{
  this.alertRAM = "Limite non consentito"
}

if (this.ActiveVms < this.activevms)
{

this.alertACTIVEVMS = "Limite non consentito"

}

if (this.TotalVms< this.TOTALVMS)
{
  this.alertTOTALVMS = "Limite non consentito"

}
}
}