import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAppointments } from 'app/shared/model/appointments.model';

@Component({
  selector: 'jhi-appointments-detail',
  templateUrl: './appointments-detail.component.html'
})
export class AppointmentsDetailComponent implements OnInit {
  appointments: IAppointments | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appointments }) => {
      this.appointments = appointments;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
