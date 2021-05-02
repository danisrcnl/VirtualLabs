import { Assignment } from "./assignment.model";
import { Paper } from "./paper.model";
import { PaperStatusTime } from "./paperStatusTime.model";
import { StudentDTO } from "./studentDTO.model";

export class PaperWithHistory {
    paper: Paper;
    creator: StudentDTO;
    history: PaperStatusTime[];
  }
  
export class AssignmentWithPapers {
    assignment: Assignment;
    papersWithHistory: PaperWithHistory[];
  }