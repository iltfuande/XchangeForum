import { QuestionDto } from './question.dto';
import { Pageable } from '../pageable';

export interface QuestionListDto {
  content: QuestionDto[];
  pageable: Pageable;
  last: boolean;
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}
