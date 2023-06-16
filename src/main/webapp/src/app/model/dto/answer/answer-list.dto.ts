import { Pageable } from '../pageable';
import { AnswerDto } from './answer.dto';

export interface AnswerListDto {
  content: AnswerDto[];
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
