import { Component, Input, OnInit, Output, EventEmitter} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Group } from '../model/group.model';
import { StudentService } from '../services/student.service';
import { Vms } from '../model/vms.model';
import { LimitDialogComponent } from './limit-dialog.component';
import { vmModelDTO } from 'app/model/vmModelDTO.model';
import { Observable } from 'rxjs';
import { MatCardModule } from '@angular/material/card';



@Component({
  selector: 'app-vmscomponentstudent',
  templateUrl: './vmscomponent.component.html',
  styleUrls: ['./vmscomponent.component.css']
})
export class VmscomponentComponent2 implements OnInit {
  href : string ="";
  //vms : Vms[] = new Array<Vms>();
  
  vmsperteam$ : Observable <Vms[]>;
  vmModel : vmModelDTO;
  

  @Input ('vmsperteam$')
  set Vms (vmss: Observable<Vms[]>)
  {
    this.vmsperteam$ = vmss;
    console.log(this.vmsperteam$);
  }

  @Input ('vmModel')
  set Vmmodel (vmmodel : vmModelDTO)
  {
    this.vmModel = vmmodel;
    console.log(this.vmModel);
  }
 
  @Output() addvmEvent = new EventEmitter<Vms>();

  

  
  vm : Vms = new Vms;
   


  constructor(public dialog: MatDialog, private studentservice: StudentService,private router: Router, private activeRoute: ActivatedRoute) 
  
  {
    
    
  }

  ngOnInit(){

   


  }

  createvm()
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
      
      this.vm.nVCpu = data.nVCpu;
      this.vm.ram = data.ram;
      this.vm.disk = data.disk;
      
      console.log(this.vm);
      this.addvmEvent.emit(this.vm);

   }



  })

    


}
}
