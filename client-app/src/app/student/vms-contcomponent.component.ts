import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Group } from '../model/group.model';
import { StudentService } from '../services/student.service';
import { Vms } from '../assets/vms.model';
import { VmService } from 'app/services/vm.service';
import { Observable } from 'rxjs';
import { AuthService } from 'app/auth/authservices/auth.service';
import { User } from 'app/auth/user';
import { CourseService } from 'app/services/course.service';

@Component({
  selector: 'app-vms-contcomponent',
  templateUrl: './vms-contcomponent.component.html',
  styleUrls: ['./vms-contcomponent.component.css']
})
export class VmsContcomponentComponent2 implements OnInit {

  constructor(private route: ActivatedRoute, private studentservice : StudentService, private router : Router,private vmService: VmService ) { }

   vmspercourse : Vms[] = new Array<Vms>();
   vms$ : Observable <Vms[]>;
   studentId : string;
   groups : Group[];
   courseId :string;
   selectedegroups : Group[];
   public href :string ="";
   public href2 : string ="";
   currentUser : User;
   
  
   ngOnInit() {


    this.route.queryParams.subscribe(params => { this.courseId = params.name
      
      
      
       this.courseId.replace('%20', " ");
       console.log (this.courseId);
       });
    

    this.vmService.getVmsByCourse(this.courseId).subscribe ( vmss => {

      vmss.forEach ( v => {

        this.vmspercourse.push(v);
      })

    })

     console.log(this.vmspercourse);
    
  

    this.studentservice.getgroups().subscribe (s => this.groups = s);
 
 

    this.href = this.router.url;
    this.href2 = this.href;
    
  }
}
