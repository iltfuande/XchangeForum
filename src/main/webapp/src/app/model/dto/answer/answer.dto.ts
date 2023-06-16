export interface AnswerDto {
  id: number;
  questionId: number;
  userId: number;
  respond: string;
  timeCreated: Date;
  timeModify?: Date;
  hidden: boolean;
}
