import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-elaboraticontteacher',
  templateUrl: './elaboraticontteacher.component.html',
  styleUrls: ['./elaboraticontteacher.component.css']
})
export class ElaboraticontteacherComponent implements OnInit {

  constructor(private route: ActivatedRoute, private router : Router,) { }


   firstParam : string = "";
   public hreff : string ="";
   public href :string ="";
   public href2 : string ="";
   public href3 : string ="";
   public subject : string ="";

  ngOnInit() {



 this.firstParam = this.route.snapshot.queryParamMap.get('name');
  
     this.route.params.subscribe (routeParams => {
     this.hreff = this.router.url;
     this.subject = this.hreff.substring(0,this.hreff.lastIndexOf('?'));
     this.hreff = this.hreff.substring(0,this.hreff.lastIndexOf('/'));
     this.href = this.subject; console.log(this.href);
     this.href2 = this.hreff + '/students';
     this.href3 = this.hreff + '/vms';
    

  });

}

}