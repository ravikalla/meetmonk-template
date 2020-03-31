import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IAppointments } from 'app/shared/model/appointments.model';

type EntityResponseType = HttpResponse<IAppointments>;
type EntityArrayResponseType = HttpResponse<IAppointments[]>;

@Injectable({ providedIn: 'root' })
export class AppointmentsService {
  public resourceUrl = SERVER_API_URL + 'api/appointments';

  constructor(protected http: HttpClient) {}

  create(appointments: IAppointments): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(appointments);
    return this.http
      .post<IAppointments>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(appointments: IAppointments): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(appointments);
    return this.http
      .put<IAppointments>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IAppointments>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IAppointments[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(appointments: IAppointments): IAppointments {
    const copy: IAppointments = Object.assign({}, appointments, {
      date: appointments.date && appointments.date.isValid() ? appointments.date.toJSON() : undefined
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? moment(res.body.date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((appointments: IAppointments) => {
        appointments.date = appointments.date ? moment(appointments.date) : undefined;
      });
    }
    return res;
  }
}
