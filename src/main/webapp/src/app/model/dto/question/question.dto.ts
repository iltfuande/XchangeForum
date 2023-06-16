export interface QuestionDto {
  id: number;
  createdUserId: number;
  moderatorId?: number;
  close: boolean;
  title: string;
  description: string;
  tag: string[];
  viewed: number;
  timeCreated: Date;
  timeModify?: Date;
  hidden: boolean;
}
