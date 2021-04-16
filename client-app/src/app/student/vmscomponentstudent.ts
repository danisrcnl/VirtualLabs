import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Group } from '../model/group.model';
import { StudentService } from '../services/student.service';
import { Vms } from '../assets/vms.model';
import { LimitDialogComponent } from './limit-dialog.component';
import { vmModelDTO } from 'app/model/vmModelDTO.model';


@Component({
  selector: 'app-vmscomponentstudent',
  templateUrl: './vmscomponent.component.html',
  styleUrls: ['./vmscomponent.component.css']
})
export class VmscomponentComponent2 implements OnInit {
  href : string ="";
  //vms : Vms[] = new Array<Vms>();
  groups : Group[] = [];

  @Input ('vmspercourse')
  vms : Vms[];
  vmModel : vmModelDTO;

   


  constructor(public dialog: MatDialog, private studentservice: StudentService,private router: Router, private activeRoute: ActivatedRoute) 
  
  {
    
    
  }

  ngOnInit(){

    console.log(this.vms);


  }

  openlimitdialog()
  {
   
    this.dialog.open (LimitDialogComponent, { height: '300px',
    width: '400px',
    data : {
      dataKey: this.vms
  
    }
  
    });

  }

}
