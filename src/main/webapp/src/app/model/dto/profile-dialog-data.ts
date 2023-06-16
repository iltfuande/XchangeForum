import { UserDto } from './user/user.dto';

export interface ProfileDialogData {
  title: string;
  user: UserDto;
  editText?: string;
  deleteText?: string;
  moderateText?: string;
  banText?: string;
  unbanText?: string;
}
