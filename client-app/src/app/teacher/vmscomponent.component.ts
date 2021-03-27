import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Group } from '../model/group.model';
import { StudentService } from '../services/student.service';
import { Vms } from '../assets/vms.model';
import { LimitDialogComponent } from './limit-dialog.component';


@Component({
  selector: 'app-vmscomponent',
  templateUrl: './vmscomponent.component.html',
  styleUrls: ['./vmscomponent.component.css']
})
export class VmscomponentComponent implements OnInit {
  href : string ="";
  vms : Vms[] = [];
  groups : Group[] = [];
  constructor(public dialog: MatDialog, private studentservice: StudentService,private router: Router, private activeRoute: ActivatedRoute) 
  
  {
    

  }

  ngOnInit(){


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
