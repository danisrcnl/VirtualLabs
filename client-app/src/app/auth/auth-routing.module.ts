import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponentComponent } from '../home-component/home-component.component';
import { AuthGuard } from '../_helpers/auth.guard';
import { LoginDialogComponent } from './login-dialog.component';
import { RegisterComponent } from './register/register.component';



const routes: Routes = [
  {path:'auth', component : HomeComponentComponent},
  {
   path : 'login', 
   component: LoginDialogComponent 
  },

   {path :'register', 
    component: RegisterComponent 
    },

    

   



];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule { }
