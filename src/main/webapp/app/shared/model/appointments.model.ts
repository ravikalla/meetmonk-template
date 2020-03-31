import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';

export interface IAppointments {
  id?: number;
  subject?: string;
  description?: string;
  date?: Moment;
  duration?: number;
  notes?: string;
  users?: IUser[];
}

export class Appointments implements IAppointments {
  constructor(
    public id?: number,
    public subject?: string,
    public description?: string,
    public date?: Moment,
    public duration?: number,
    public notes?: string,
    public users?: IUser[]
  ) {}
}
