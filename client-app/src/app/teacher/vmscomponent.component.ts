import { Component, EventEmitter, Inject, Input, OnInit, Output } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Group } from '../model/group.model';
import { StudentService } from '../services/student.service';
import { Vms } from '../model/vms.model';
import { LimitDialogComponent2 } from './limit-dialog.component';
import { vmModelDTO } from 'app/model/vmModelDTO.model';
import { StudentDTO } from 'app/model/studentDTO.model';


export interface DialogDataVm {
  nvcpu  : number;
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


  displayedColumns: string[] = ['maxnvcpu', 'maxDisk', 'maxRam', 'operatingSystem','maxVmsForCourse','maxActiveForCourse'];
  

   @Output() addvmModel2Event = new EventEmitter<vmModelDTO>();
  
  href : string ="";
  vmModel2 : vmModelDTO = new vmModelDTO;
  vmModel : vmModelDTO;
  datasource;
  vmarray : vmModelDTO[] = new Array<vmModelDTO>();

  @Input('vmModel')
  set vmmodel2 (model : vmModelDTO)
  {
    this.vmModel = model;
    this.vmarray.push(this.vmModel);
    
  }


  vms : Vms[] = [];
  groups : Group[] = [];
  constructor(public dialog: MatDialog, private studentservice: StudentService,private router: Router, private activeRoute: ActivatedRoute,
    public dialogRef : MatDialogRef<LimitDialogComponent2>, @Inject(MAT_DIALOG_DATA) public data : DialogDataVm) 
  
  {
    

  }

  ngOnInit(){

  }


  //Apro lo specchietto che mi permette di settare il VmModel
  openlimitdialog()
  {
   
    const dialogRef = this.dialog.open (LimitDialogComponent2, { height: '500px',
    width: '400px',
    data : {}
  
    });


    dialogRef.afterClosed().subscribe( data => {



   if(data != undefined)
   {
      
      this.vmModel2.maxNVCpu = data.nvcpu;
      this.vmModel2.maxRam = data.RAM;
      this.vmModel2.maxActiveVms = data.ActiveVms;
      this.vmModel2.maxDisk = data.Disksize;
      this.vmModel2.operatingSystem = data.OperatingSystem;
      this.vmModel2.maxVmsForCourse= data.TotalVms;

      this.addvmModel2Event.emit (this.vmModel2);

   }
  })

  }

}
