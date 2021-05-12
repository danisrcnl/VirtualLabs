import {Injectable} from '@angular/core';
import {CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import { AuthenticationService } from '../auth/authentication.service';
import { AuthService } from '../auth/authservices/auth.service';
import {Location} from '@angular/common';

@Injectable({

providedIn : 'root'

})
export class AuthGuard implements CanActivate {

constructor (private authenticationService : AuthService, private router: Router, private _location: Location) {}

canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const currentUser = this.authenticationService.currentUserValue;
    if (currentUser) {

      
        this.authenticationService.info().subscribe(data => {

            if(route.data.role && data.roles.indexOf(route.data.role)=== -1)
            {
                this.router.navigate(['/']);
                return false;
            }

        })

        // Loggato quindi ritorna true
        return true;
    }

    // Non loggato quindi ritorna alla pagina iniziale 
    this.router.navigate(['/']);
    return false;
}
}
