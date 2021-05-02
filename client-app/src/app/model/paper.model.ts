import {PaperStatus} from './paperStatus.model';

export class Paper {
    
    id: number;
    content: String;
    creator: String;
    currentStatus: PaperStatus;
    mark: number;
    editable: Boolean;

}