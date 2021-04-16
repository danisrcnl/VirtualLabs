import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Group } from '../model/group.model';
import { StudentService } from '../services/student.service';
import { Vms } from '../assets/vms.model';
import { LimitDialogComponent } from './limit-dialog.component';
import { vmModelDTO } from 'app/model/vmModelDTO.model';
import { StudentDTO } from 'app/model/studentDTO.model';


export interface DialogDataVm {
  VPCU  : number;
  RAM : number;
  Disksize : number;
  ActiveVms :number;
  TotalVms : number;
}

@Component({
  selector: 'app-vmscomponent',
  templateUrl: './vmscomponent.component.html',
  styleUrls: ['./vmscomponent.component.css']
})
export class VmscomponentComponent implements OnInit {

   @Output() addvmModelEvent = new EventEmitter<vmModelDTO>();
  
  href : string ="";
  vmModel : vmModelDTO = new vmModelDTO();
  vms : Vms[] = [];
  groups : Group[] = [];
  constructor(public dialog: MatDialog, private studentservice: StudentService,private router: Router, private activeRoute: ActivatedRoute,
    public dialogRef : MatDialogRef<LimitDialogComponent>, @Inject(MAT_DIALOG_DATA) public data : DialogDataVm) 
  
  {
    

  }

  ngOnInit(){


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
      this.vmModel.maxActiveForCourse = data.ActiveVms;
      this.vmModel.maxDisk = data.Disksize;
      this.vmModel.operatingSystem = data.OperatingSystem;
      this.vmModel.maxVmsForCourse= data.TotalVms;

      this.addvmModelEvent.emit (this.vmModel);

   }
  })

  }

}
