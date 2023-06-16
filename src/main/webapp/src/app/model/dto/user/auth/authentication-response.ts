import { UserDto } from '../user.dto';
import { Role } from '../../../enum/role.enum';

export interface AuthenticationResponse {
  token: string;
  tokenTimeExpiration: string;
  userDto: UserDto;
  role: Role;
}
