import { Component, EventEmitter, Inject, Input, OnInit, Output } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Group } from '../model/group.model';
import { StudentService } from '../services/student.service';
import { Vms } from '../model/vms.model';
import { LimitDialogComponent } from './limit-dialog.component';
import { vmModelDTO } from 'app/model/vmModelDTO.model';
import { StudentDTO } from 'app/model/studentDTO.model';


export interface DialogDataVm {
  VCPU  : number;
  RAM : number;
  Disksize : number;
  ActiveVms :number;
  OperatingSystem: String;
  TotalVms : number;
}

@Component({
  selector: 'app-vmscomponent',
  templateUrl: './vmscomponent.component.html',
  styleUrls: ['./vmscomponent.component.css']
})
export class VmscomponentComponent implements OnInit {


  displayedColumns: string[] = ['maxNVCpu', 'maxDisk', 'maxRam', 'operatingSystem','maxVmsForCourse','maxActiveForCourse'];
  

   @Output() addvmModelEvent = new EventEmitter<vmModelDTO>();
  
  href : string ="";
  vmModel : vmModelDTO;
  datasource;
  vmarray : vmModelDTO[] = new Array<vmModelDTO>();

  @Input('vmModel')
  set vmmodel (model : vmModelDTO)
  {
    this.vmModel = model;
    this.vmarray.push(this.vmModel);
    
    
  }


 


  vms : Vms[] = [];
  groups : Group[] = [];
  constructor(public dialog: MatDialog, private studentservice: StudentService,private router: Router, private activeRoute: ActivatedRoute,
    public dialogRef : MatDialogRef<LimitDialogComponent>, @Inject(MAT_DIALOG_DATA) public data : DialogDataVm) 
  
  {
    

  }

  ngOnInit(){

   console.log(this.vmarray);

  }

  openlimitdialog()
  {
   
    const dialogRef = this.dialog.open (LimitDialogComponent, { height: '300px',
    width: '400px',
    data : {
     
  
    }
  
    });


    dialogRef.afterClosed().subscribe( data => {

      console.log(data);


   if(data != undefined)
   {
      
      this.vmModel.maxNVCpu = data.VCPU;
      this.vmModel.maxRam = data.RAM;
      this.vmModel.maxVmsForTeam = data.ActiveVms;
      this.vmModel.maxDisk = data.Disksize;
      this.vmModel.operatingSystem = data.OperatingSystem;
      this.vmModel.maxVmsForCourse= data.TotalVms;

      this.addvmModelEvent.emit (this.vmModel);

   }
  })

  }

}
