import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Group } from '../model/group.model';
import { StudentService } from '../services/student.service';
import { Vms } from '../model/vms.model';
import { vmModelDTO } from 'app/model/vmModelDTO.model';
import { VmService } from 'app/services/vm.service';

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

  constructor(private studentservice : StudentService, private route: ActivatedRoute, private router : Router, private vmService : VmService) { 

    this.hreff = router.url;
    this.subject = this.hreff.substring(this.hreff.lastIndexOf('/')+1 );
    console.log(this.subject);
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
     console.log(this.firstParam);

     this.route.params.subscribe (routeParams => {
     this.hreff = this.router.url;
     this.subject = this.hreff.substring(0,this.hreff.lastIndexOf('?'));
     this.hreff = this.hreff.substring(0,this.hreff.lastIndexOf('/'));
     this.href = this.subject; console.log(this.href);
     this.href2 = this.hreff + '/students';
     this.href3 = this.hreff + '/elaborati';
     console.log(this.href2);
;
});

    

    this.vmService.getVmModelforCourse(this.firstParam).subscribe(data => {

    this.vmModel = data;
    console.log(this.vmModel);
   })


 
 
   
    
  }

  receivevmModel($event)
    {
      this.vmModel = $event;
      this.vmModel2 = $event;
      console.log(this.vmModel);
      console.log(this.vmModel2);
      this.vmService.setVmModel(this.vmModel2,this.firstParam).subscribe(data => console.log(data));

    }
}
