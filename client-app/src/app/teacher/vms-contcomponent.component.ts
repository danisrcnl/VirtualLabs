import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Group } from '../model/group.model';
import { StudentService } from '../services/student.service';
import { Vms } from '../model/vms.model';
import { vmModelDTO } from 'app/model/vmModelDTO.model';
import { VmService } from 'app/services/vm.service';
import { YourDialog } from 'app/student/vms-contcomponent.component';
import { MatDialog } from '@angular/material/dialog';

export interface DialogDataVm {
  nvcpu  : number;
  RAM : number;
  Disksize : number;
  OperatingSystem : String;
  ActiveVms :number;
  TotalVms : number;
}

@Component({
  selector: 'app-vms-contcomponent',
  templateUrl: './vms-contcomponent.component.html',
  styleUrls: ['./vms-contcomponent.component.css']
})
export class VmsContcomponentComponent implements OnInit {

  constructor(
    private studentservice : StudentService, 
    private route: ActivatedRoute, 
    private router : Router, 
    private vmService : VmService,
    private dialog : MatDialog) { 

    this.hreff = router.url;
    this.subject = this.hreff.substring(this.hreff.lastIndexOf('/')+1 );
    this.href = '/teacher/course/'+ this.subject;
     this.href2 = this.href ;
  }


  
   
   vms : Vms[];
   vmModel : vmModelDTO;
   vmModel2 : vmModelDTO;
   groups : Group[];
   selectedegroups : Group[];
   public hreff : string ="";
   public href :string ="";
   public href2 : string ="";
   public href3 : string ="";
   public subject : string ="";
   firstParam : string = "";

   
  
   ngOnInit() {

     this.firstParam = this.route.snapshot.queryParamMap.get('name');
     this.route.params.subscribe (routeParams => {
     this.hreff = this.router.url;
     this.subject = this.hreff.substring(0,this.hreff.lastIndexOf('?'));
     this.hreff = this.hreff.substring(0,this.hreff.lastIndexOf('/'));
     this.href = this.subject; 
     this.href2 = this.hreff + '/students';
     this.href3 = this.hreff + '/elaborati';
     
;
});

    
    //Ricevo il VmModel dal servizio 
    this.vmService.getVmModelforCourse(this.firstParam).subscribe(data => {

    this.vmModel = data;
  
   })


 
 
   
    
  }

  //Ricevo il VmModel da settare dal componente figlio 
  receivevmModel($event)
    {
      this.vmModel = $event;
      this.vmModel2 = $event;
      this.vmService.setVmModel(this.vmModel2,this.firstParam).subscribe(data =>
        
        
        {
        
        }
        , (error) => {
          let dialogRef = this.dialog.open(YourDialog, {
                            data: { name: error },
                                });
        }
        );

    }
}
