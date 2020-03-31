import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IAppointments, Appointments } from 'app/shared/model/appointments.model';
import { AppointmentsService } from './appointments.service';
import { AppointmentsComponent } from './appointments.component';
import { AppointmentsDetailComponent } from './appointments-detail.component';
import { AppointmentsUpdateComponent } from './appointments-update.component';

@Injectable({ providedIn: 'root' })
export class AppointmentsResolve implements Resolve<IAppointments> {
  constructor(private service: AppointmentsService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAppointments> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((appointments: HttpResponse<Appointments>) => {
          if (appointments.body) {
            return of(appointments.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Appointments());
  }
}

export const appointmentsRoute: Routes = [
  {
    path: '',
    component: AppointmentsComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'Appointments'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: AppointmentsDetailComponent,
    resolve: {
      appointments: AppointmentsResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Appointments'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: AppointmentsUpdateComponent,
    resolve: {
      appointments: AppointmentsResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Appointments'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: AppointmentsUpdateComponent,
    resolve: {
      appointments: AppointmentsResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Appointments'
    },
    canActivate: [UserRouteAccessService]
  }
];
