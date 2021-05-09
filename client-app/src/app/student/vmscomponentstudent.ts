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
import { UsedResources } from 'app/model/UsedResources.model';
import { Team } from 'app/model/team.model';



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
  roles$ : Observable<String[]>;
  usedResources$ : Observable <UsedResources>;
  team: Team;

  @Input ('vmsperteam$')
  set Vms (vmss: Observable<Vms[]>)
  {
    this.vmsperteam$ = vmss;
    console.log(this.vmsperteam$);
  }

  @Input ('team')
  set _team (team: Team) {
    this.team = team;
    console.log("||" + team)
  }

  @Input ('vmModel')
  set Vmmodel (vmmodel : vmModelDTO)
  {
    this.vmModel = vmmodel;
    console.log(this.vmModel);
  }

  @Input ('roles$')
  set Roles (roles: Observable<String[]>)
  {
    this.roles$ = roles;
    console.log(this.roles$);
  }

  @Input ('usedResources$')
  set Usedresources (value : Observable <UsedResources>)
  {
    this.usedResources$ = value;
    console.log("!!!" + this.usedResources$)
    
  }


  @Output() addvmEvent = new EventEmitter<Vms>();

  @Output() editvmEvent = new EventEmitter<Vms>();

  @Output() changestateEvent = new EventEmitter<any>();
  

  
  vm : Vms = new Vms;
   


  constructor(public dialog: MatDialog,private router: Router, private activeRoute: ActivatedRoute) 
  
  {
    
    
  }

  ngOnInit(){

   


  }

  createvm()
  {
   
    const dialogRef = this.dialog.open (LimitDialogComponent, { height: '350px',
   width: '400px',
    data : { 
      resources : this.usedResources$, vmModel : this.vmModel
  
          }
  
    });
    
    dialogRef.afterClosed().subscribe( data => {

       if(data != undefined)
     {
      
      this.vm.nvcpu = data.nvcpu;
      console.log(data.nvcpu);
      this.vm.ram = data.ram;
      this.vm.disk = data.disk;
      
      console.log(this.vm);
      this.addvmEvent.emit(this.vm);

   }

  })
}


editvm(vmss) {

  this.vm = vmss;
  let ed = true;

  const dialogRef = this.dialog.open (LimitDialogComponent, { height: '350px',
   width: '400px',
    data : { 
      resources : this.usedResources$, vmModel : this.vmModel, edit : ed
  
          }
  
    });
    
    dialogRef.afterClosed().subscribe( data => {

       if(data != undefined)
     {
      
     
      this.vm.nvcpu = data.nvcpu;
      console.log(data.nvcpu);
      this.vm.ram = data.ram;
      this.vm.disk = data.disk;
      
      console.log(this.vm);
      this.editvmEvent.emit(this.vm);

   }

  })



}

set(commandstring,vmid) {


this.changestateEvent.emit({vmId:vmid,command:commandstring})
  
}

isOn (stat: String) {
  
  if(stat == "ACTIVE") return true;
  return false;
}
isOff (stat: String) {
 
  if (stat == "OFF") return true;
  return false;
}
isFreezed (stat: String) {
  
  if (stat == "FREEZED") return true;
  return false;
}

}


