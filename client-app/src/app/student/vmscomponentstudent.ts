import { Component, Input, OnInit, Output, EventEmitter} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Group } from '../model/group.model';
import { StudentService } from '../services/student.service';
import { Vms } from '../model/vms.model';
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
  
  vmsperteam : Vms[];

  

  @Input ('vmsperteam')
  set Vms (vmss: Vms[])
  {
    this.vmsperteam = vmss;
  }
 
  @Output() addvmEvent = new EventEmitter<Vms>();

  

  vmModel : vmModelDTO;
  vm : Vms = new Vms;
   


  constructor(public dialog: MatDialog, private studentservice: StudentService,private router: Router, private activeRoute: ActivatedRoute) 
  
  {
    
    
  }

  ngOnInit(){

    console.log(this.vmsperteam);


  }

  createvm()
  {
   
    const dialogRef = this.dialog.open (LimitDialogComponent, { height: '300px',
    width: '400px',
    data : {
      dataKey: this.vmsperteam
  
    }
  
    });

    dialogRef.afterClosed().subscribe( data => {

      console.log(data);


       if(data != undefined)
   {
      
      this.vm.nVCpu = data.nVCpu;
      this.vm.ram = data.ram;
      this.vm.disk = data.disk;
      
      console.log(this.vm);
      this.addvmEvent.emit(this.vm);

   }



  })

    


}
}
