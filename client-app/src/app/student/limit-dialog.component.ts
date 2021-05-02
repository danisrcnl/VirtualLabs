import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Vms } from 'app/model/vms.model';
import {MatDialog, MAT_DIALOG_DATA} from '@angular/material/dialog';
import { Inject } from '@angular/core';
import { AlertService } from 'app/auth/authservices/alert.service';
import { TeamService } from '../services/team.service';
import { UsedResources } from 'app/model/UsedResources.model';
import { vmModelDTO } from 'app/model/vmModelDTO.model';

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

  vmModel : vmModelDTO;
  used_resources: UsedResources;

  ram_consumption : Number;
  vcpu_consumption : Number;
  disk_consumption : Number;

  ram_left : number;
  vcpu_left: number;
  disk_left: number;

  submitted = false;

  
  constructor(private alertService: AlertService, private teamService: TeamService, private formBuilder: FormBuilder, @Inject(MAT_DIALOG_DATA) public data: Vms, public dialog: MatDialog) {

  }

  ngOnInit() {

    this.limitForm = this.formBuilder.group({
      VCPU: ['',Validators.required],
      RAM: ['',Validators.required],
      Disksize: ['',Validators.required],
      



      
    });
  
      
  let m = 0;

    Object.entries(this.data).forEach( s => {
    
    this.length = (s.length);
   if(m==0)
    this.used_resources = s[1];
    else
    this.vmModel = s[1];

    m++;
    })
    
    this.ram_left = (this.vmModel.maxRam - this.used_resources.ram);
    this.disk_left = (this.vmModel.maxDisk - this.used_resources.disk);
    this.vcpu_left = ((this.vmModel.maxNVCpu - this.used_resources.vCpu));


    this.ram_consumption = ((this.vmModel.maxRam - this.used_resources.ram)/this.vmModel.maxRam)*100;
    this.disk_consumption = ((this.vmModel.maxDisk - this.used_resources.disk)/this.vmModel.maxDisk)*100;
    this.vcpu_consumption = ((this.vmModel.maxNVCpu - this.used_resources.vCpu)/this.vmModel.maxNVCpu)*100;

    console.log(this.ram_consumption);
    
  }


  get f() { return this.limitForm.controls; }

close()
{
this.dialog.closeAll();
}



createvm() {
this.alertService.clear();
  
  this.submitted = true;

 if (this.limitForm.invalid) {
            return;
        }
    
        


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